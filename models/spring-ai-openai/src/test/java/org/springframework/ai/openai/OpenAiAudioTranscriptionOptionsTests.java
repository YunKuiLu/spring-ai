package org.springframework.ai.openai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class OpenAiAudioTranscriptionOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(OpenAiAudioTranscriptionOptions.class).usingGetClass().verify();
	}

}
