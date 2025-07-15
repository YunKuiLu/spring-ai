package org.springframework.ai.tool.method;

import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

/**
 * @author YunKui Lu
 */
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Thread)
public class MethodToolCallbackProviderBenchmark {

	public static void main(String[] args) throws RunnerException {
		Options opt = new OptionsBuilder().include(MethodToolCallbackProviderBenchmark.class.getSimpleName())
			.forks(1)
			.build();

		new Runner(opt).run();
	}

	@Benchmark
	public void testGetToolCallbacksCache() {
		Object[] sources = new Object[] { new MethodToolCallbackProviderTests.ValidToolObject()};
		MethodToolCallbackProvider.builder()
				.toolObjects(sources)
				.build().setCacheToolCallback(true)
				.getToolCallbacks();
	}

	@Benchmark
	public void testGetToolCallbacksNoCache() {
		Object[] sources = new Object[] { new MethodToolCallbackProviderTests.ValidToolObject()};
		MethodToolCallbackProvider.builder()
				.toolObjects(sources)
				.build().setCacheToolCallback(false)
				.getToolCallbacks();
	}

}
