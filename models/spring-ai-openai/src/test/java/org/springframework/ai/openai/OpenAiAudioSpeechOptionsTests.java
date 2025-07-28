package org.springframework.ai.openai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class OpenAiAudioSpeechOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(OpenAiAudioSpeechOptions.class).usingGetClass().verify();
	}

}
