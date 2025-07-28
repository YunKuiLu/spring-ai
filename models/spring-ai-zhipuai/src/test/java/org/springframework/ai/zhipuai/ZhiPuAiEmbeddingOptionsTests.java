package org.springframework.ai.zhipuai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class ZhiPuAiEmbeddingOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(ZhiPuAiEmbeddingOptions.class)
				.usingGetClass()
				.verify();
	}

}
