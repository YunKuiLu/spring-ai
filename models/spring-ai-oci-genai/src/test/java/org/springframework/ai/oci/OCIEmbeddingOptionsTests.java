package org.springframework.ai.oci;

import nl.jqno.equalsverifier.EqualsVerifier;
import org.junit.jupiter.api.Test;

/**
 * @author YunKui Lu
 */
class OCIEmbeddingOptionsTests {

	@Test
	void testEqualsAndHashCode() {
		EqualsVerifier.simple().forClass(OCIEmbeddingOptions.class).usingGetClass().verify();
	}

}
