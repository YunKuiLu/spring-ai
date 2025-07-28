package org.springframework.ai.openai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class OpenAiEmbeddingOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(OpenAiEmbeddingOptions.class).usingGetClass().verify();
	}

}
