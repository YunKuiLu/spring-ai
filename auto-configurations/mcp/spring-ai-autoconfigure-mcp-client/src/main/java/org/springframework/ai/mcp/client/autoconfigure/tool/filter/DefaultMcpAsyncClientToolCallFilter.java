package org.springframework.ai.mcp.client.autoconfigure.tool.filter;

import java.util.Set;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * @author YunKui Lu
 */
public class DefaultMcpAsyncClientToolCallFilter implements McpAsyncClientToolCallFilter {

	private final Set<String> excludedToolNames;

	public DefaultMcpAsyncClientToolCallFilter() {
		this.excludedToolNames = Set.of();
	}

	public DefaultMcpAsyncClientToolCallFilter(Set<String> excludedToolNames) {
		this.excludedToolNames = excludedToolNames;
	}

	@Override
	public boolean test(McpAsyncClient mcpAsyncClient, McpSchema.Tool tool) {
		return !excludedToolNames.contains(tool.name());
	}
}
