package org.springframework.ai.mcp.server.autoconfigure;

import org.springframework.boot.autoconfigure.condition.AllNestedConditions;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;

/**
 * This class defines a condition met when the MCP server is enabled and the STDIO
 * Transport is disabled.
 *
 * @since 1.0.0
 * @author YunKui Lu
 */
public class McpServerStdioDisabledCondition extends AllNestedConditions {

	public McpServerStdioDisabledCondition() {
		super(ConfigurationPhase.PARSE_CONFIGURATION);
	}

	@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "enabled", havingValue = "true",
			matchIfMissing = true)
	static class McpServerEnabledCondition {

	}

	@ConditionalOnProperty(prefix = McpServerProperties.CONFIG_PREFIX, name = "stdio", havingValue = "false",
			matchIfMissing = true)
	static class StdioDisabledCondition {

	}

}
