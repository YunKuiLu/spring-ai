package org.springframework.ai.deepseek;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class DeepSeekChatOptionsTests {
	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(DeepSeekChatOptions.class)
				.usingGetClass()
				.verify();
	}
}
