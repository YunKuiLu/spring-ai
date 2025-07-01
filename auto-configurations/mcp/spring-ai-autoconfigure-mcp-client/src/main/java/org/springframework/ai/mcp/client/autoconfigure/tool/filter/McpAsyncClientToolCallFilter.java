package org.springframework.ai.mcp.client.autoconfigure.tool.filter;

import java.util.function.BiPredicate;

import io.modelcontextprotocol.client.McpAsyncClient;
import io.modelcontextprotocol.client.McpSyncClient;
import io.modelcontextprotocol.spec.McpSchema;

/**
 * @author YunKui Lu
 */
public interface McpAsyncClientToolCallFilter extends BiPredicate<McpAsyncClient, McpSchema.Tool> {

}
