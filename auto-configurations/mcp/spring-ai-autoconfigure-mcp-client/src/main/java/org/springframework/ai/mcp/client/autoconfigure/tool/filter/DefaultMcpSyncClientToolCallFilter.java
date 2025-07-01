package org.springframework.ai.mcp.client.autoconfigure.tool.filter;

import java.util.Set;

import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * @author YunKui Lu
 */
public class DefaultMcpSyncClientToolCallFilter implements McpSyncClientToolCallFilter {

	private final Set<String> excludedToolNames;

	public DefaultMcpSyncClientToolCallFilter() {
		this.excludedToolNames = Set.of();
	}

	public DefaultMcpSyncClientToolCallFilter(Set<String> excludedToolNames) {
		this.excludedToolNames = excludedToolNames;
	}

	@Override
	public boolean test(McpSyncClient mcpAsyncClient, McpSchema.Tool tool) {
		return !excludedToolNames.contains(tool.name());
	}
}
