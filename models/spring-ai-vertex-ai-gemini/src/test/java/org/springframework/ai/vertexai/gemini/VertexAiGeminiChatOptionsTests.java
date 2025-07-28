package org.springframework.ai.vertexai.gemini;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class VertexAiGeminiChatOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(VertexAiGeminiChatOptions.class).usingGetClass().verify();
	}

}
