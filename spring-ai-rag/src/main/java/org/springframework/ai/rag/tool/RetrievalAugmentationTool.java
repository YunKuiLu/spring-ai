package org.springframework.ai.rag.tool;

import org.springframework.ai.chat.model.ToolContext;
import org.springframework.ai.tool.ToolCallback;
import org.springframework.ai.tool.definition.ToolDefinition;
import org.springframework.ai.tool.metadata.ToolMetadata;
import org.springframework.ai.tool.support.ToolDefinitions;

/**
 * @author YunKui Lu
 */
public class RetrievalAugmentationTool implements ToolCallback {
	private final String toolName;
	private final String toolDescription;

	private RetrievalAugmentationTool(String toolName, String toolDescription) {
		this.toolName = toolName;
		this.toolDescription = toolDescription;
	}

	@Override
	public ToolDefinition getToolDefinition() {
		return ToolDefinition.builder()
				.name(toolName)
				.description(toolDescription)
				.description(ToolDefinitions.from(this))
				.build();
	}

	@Override
	public String call(String toolInput) {
		return "";
	}

	@Override
	public String call(String toolInput, ToolContext toolContext) {
		return call(toolInput);
	}
}
