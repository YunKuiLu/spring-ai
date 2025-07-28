package org.springframework.ai.zhipuai;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class ZhiPuAiChatOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(ZhiPuAiChatOptions.class).usingGetClass().verify();
	}

}
