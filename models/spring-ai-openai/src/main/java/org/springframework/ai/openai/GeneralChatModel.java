package org.springframework.ai.openai;

import java.util.List;
import java.util.Map;

import io.micrometer.observation.ObservationConvention;
import io.micrometer.observation.ObservationRegistry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.openai.metadata.support.OpenAiResponseHeaderExtractor;
import reactor.core.publisher.Flux;

import org.springframework.ai.chat.metadata.EmptyUsage;
import org.springframework.ai.chat.metadata.RateLimit;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.ai.chat.model.AiApi;
import org.springframework.ai.chat.model.ChatModel;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.model.Generation;
import org.springframework.ai.chat.observation.ChatModelObservationContext;
import org.springframework.ai.chat.observation.ChatModelObservationConvention;
import org.springframework.ai.chat.observation.ChatModelObservationDocumentation;
import org.springframework.ai.chat.observation.DefaultChatModelObservationConvention;
import org.springframework.ai.chat.prompt.ChatOptions;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.model.ModelOptionsUtils;
import org.springframework.ai.model.tool.ToolCallingChatOptions;
import org.springframework.ai.model.tool.ToolExecutionResult;
import org.springframework.ai.openai.api.OpenAiApi;
import org.springframework.ai.retry.RetryUtils;
import org.springframework.ai.support.UsageCalculator;
import org.springframework.http.ResponseEntity;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.util.StringUtils;

/**
 * @author YunKui Lu
 */
public abstract class GeneralChatModel<O extends ChatOptions, R, P, M, C> implements ChatModel {

	private static final Logger logger = LoggerFactory.getLogger(GeneralChatModel.class);

	private static final ChatModelObservationConvention DEFAULT_OBSERVATION_CONVENTION = new DefaultChatModelObservationConvention();

	private O defaultOptions;

	private Class<O> chatOptionsClass;

	private Class<R> requestClass;

	private Class<P> responseClass;

	@Override
	public ChatResponse call(Prompt prompt) {
		Prompt requestPrompt = buildRequestPrompt(prompt);
		return internalCall(requestPrompt, null);
	}

	public ChatResponse internalCall(Prompt prompt, ChatResponse previousChatResponse) {

		R request = createRequest(prompt, false);

		ChatModelObservationContext observationContext = createObservationContext(prompt, request,
				previousChatResponse);

		ChatResponse respnse = ChatModelObservationDocumentation.CHAT_MODEL_OPERATION
			.observation(getObservationConvention(), DEFAULT_OBSERVATION_CONVENTION, () -> observationContext,
					getObservationRegistry())
			.observe(() -> {

				ResponseEntity<C> completionEntity = getRestTemplate()
					.execute(ctx -> getAiApi().chatCompletionEntity(request, getAdditionalHttpHeaders(prompt)));

				var chatCompletion = completionEntity.getBody();

				if (chatCompletion == null) {
					logger.warn("No chat completion returned for prompt: {}", prompt);
					return new ChatResponse(List.of());
				}

				List<Choice> choices = chatCompletion.choices();
				if (choices == null) {
					logger.warn("No choices returned for prompt: {}", prompt);
					return new ChatResponse(List.of());
				}

			// @formatter:off
					List<Generation> generations = choices.stream().map(choice -> {
						Map<String, Object> metadata = Map.of(
								"id", chatCompletion.id() != null ? chatCompletion.id() : "",
								"role", choice.message().role() != null ? choice.message().role().name() : "",
								"index", choice.index(),
								"finishReason", choice.finishReason() != null ? choice.finishReason().name() : "",
								"refusal", StringUtils.hasText(choice.message().refusal()) ? choice.message().refusal() : "",
								"annotations", choice.message().annotations() != null ? choice.message().annotations() : List.of(Map.of()));
						return buildGeneration(choice, metadata, request);
					}).toList();
					// @formatter:on

				RateLimit rateLimit = OpenAiResponseHeaderExtractor.extractAiResponseHeaders(completionEntity);

				// Current usage
				OpenAiApi.Usage usage = chatCompletion.usage();
				Usage currentChatResponseUsage = usage != null ? getDefaultUsage(usage) : new EmptyUsage();
				Usage accumulatedUsage = UsageCalculator.getCumulativeUsage(currentChatResponseUsage,
						previousChatResponse);
				ChatResponse chatResponse = new ChatResponse(generations,
						from(chatCompletion, rateLimit, accumulatedUsage));

				observationContext.setResponse(chatResponse);

				return chatResponse;

			});

		if (this.toolExecutionEligibilityPredicate.isToolExecutionRequired(prompt.getOptions(), response)) {
			var toolExecutionResult = this.toolCallingManager.executeToolCalls(prompt, response);
			if (toolExecutionResult.returnDirect()) {
				// Return tool execution result directly to the client.
				return ChatResponse.builder()
					.from(response)
					.generations(ToolExecutionResult.buildGenerations(toolExecutionResult))
					.build();
			}
			else {
				// Send the tool execution result back to the model.
				return this.internalCall(new Prompt(toolExecutionResult.conversationHistory(), prompt.getOptions()),
						response);
			}
		}

		return response;
	}

	abstract Api getApi();

	protected RetryTemplate getRestTemplate() {
		return RetryUtils.DEFAULT_RETRY_TEMPLATE;
	}

	protected ObservationRegistry getObservationRegistry() {
		return ObservationRegistry.NOOP;
	}

	protected ObservationConvention<ChatModelObservationContext> getObservationConvention() {
		return DEFAULT_OBSERVATION_CONVENTION;
	}

	protected abstract ChatModelObservationContext createObservationContext(Prompt prompt, R request,
			ChatResponse previousChatResponse);

	abstract R createRequest(Prompt prompt, boolean stream);

	Prompt buildRequestPrompt(Prompt prompt) {
		O runtimeOptions = extractRuntimeOptions(prompt);
		O requestOptions = ModelOptionsUtils.merge(runtimeOptions, this.defaultOptions, chatOptionsClass);

		if (requestOptions instanceof ToolCallingChatOptions requestToolCallingChatOptions) {
			mergeToolCallingChatOptions(requestToolCallingChatOptions, runtimeOptions, this.defaultOptions);
			ToolCallingChatOptions.validateToolCallbacks(requestToolCallingChatOptions.getToolCallbacks());
		}

		return new Prompt(prompt.getInstructions(), requestOptions);
	}

	private O extractRuntimeOptions(Prompt prompt) {
		if (prompt.getOptions() == null) {
			return null;
		}

		if (prompt.getOptions() instanceof ToolCallingChatOptions toolCallingChatOptions) {
			if (!ToolCallingChatOptions.class.isAssignableFrom(chatOptionsClass)) {
				throw new IllegalArgumentException("The chatOptionsClass must be ToolCallingChatOptions");
			}
			// noinspection unchecked
			return (O) ModelOptionsUtils.copyToTarget(toolCallingChatOptions, ToolCallingChatOptions.class,
					(Class<? extends ToolCallingChatOptions>) chatOptionsClass);
		}

		return ModelOptionsUtils.copyToTarget(prompt.getOptions(), ChatOptions.class, chatOptionsClass);
	}

	private void mergeToolCallingChatOptions(ToolCallingChatOptions targetOptions, O runtimeOptions, O defaultOptions) {
		if (runtimeOptions != null) {
			if (runtimeOptions instanceof ToolCallingChatOptions runtimeToolCallingChatOptions
					&& defaultOptions instanceof ToolCallingChatOptions defaultToolCallingChatOptions) {
				mergeToolCallingFields(targetOptions, runtimeToolCallingChatOptions, defaultToolCallingChatOptions);
			}
		}
		else if (defaultOptions instanceof ToolCallingChatOptions defaultToolCallingChatOptions) {
			copyDefaultToolCallingFields(targetOptions, defaultToolCallingChatOptions);
		}
	}

	private void mergeToolCallingFields(ToolCallingChatOptions target, ToolCallingChatOptions runtime,
			ToolCallingChatOptions defaults) {
		target.setInternalToolExecutionEnabled(ModelOptionsUtils.mergeOption(runtime.getInternalToolExecutionEnabled(),
				defaults.getInternalToolExecutionEnabled()));
		target.setToolNames(ToolCallingChatOptions.mergeToolNames(runtime.getToolNames(), defaults.getToolNames()));
		target.setToolCallbacks(
				ToolCallingChatOptions.mergeToolCallbacks(runtime.getToolCallbacks(), defaults.getToolCallbacks()));
		target.setToolContext(
				ToolCallingChatOptions.mergeToolContext(runtime.getToolContext(), defaults.getToolContext()));
	}

	private void copyDefaultToolCallingFields(ToolCallingChatOptions target, ToolCallingChatOptions defaults) {
		target.setInternalToolExecutionEnabled(defaults.getInternalToolExecutionEnabled());
		target.setToolNames(defaults.getToolNames());
		target.setToolCallbacks(defaults.getToolCallbacks());
		target.setToolContext(defaults.getToolContext());
	}

	@Override
	public Flux<String> stream(Prompt prompt) {
	}

}
