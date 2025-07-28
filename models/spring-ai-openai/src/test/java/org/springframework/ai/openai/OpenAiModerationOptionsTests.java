package org.springframework.ai.openai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class OpenAiModerationOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(OpenAiModerationOptions.class).usingGetClass().verify();
	}

}
