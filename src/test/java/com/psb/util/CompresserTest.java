package com.psb.util;

import static org.junit.Assert.assertEquals;

import org.junit.jupiter.api.Test;

class CompresserTest {
	
	@Test
	void testCompressNullBytes() {
		byte[] compressed = Compresser.compress(null);
		assertEquals(0, compressed.length);
	}
	
	@Test
	void testDecompressNullBytes() {
		byte[] deCompressed = Compresser.decompress(null);
		assertEquals(0, deCompressed.length);
	}

}
