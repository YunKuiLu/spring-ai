package org.springframework.ai.google.genai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class GoogleGenAiChatOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(GoogleGenAiChatOptions.class).usingGetClass().verify();
	}

}
