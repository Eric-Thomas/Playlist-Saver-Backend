package com.psb.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

// yoinked from https://stackoverflow.com/questions/51332314/java-byte-array-compression
public class Compresser {
	
	private static Logger logger = LoggerFactory.getLogger(Compresser.class);

	private Compresser() {
		throw new IllegalStateException("Static utility class should not be instantiated");
	}

	public static byte[] compress(byte[] in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DeflaterOutputStream defl = new DeflaterOutputStream(out);
			defl.write(in);
			defl.flush();
			defl.close();

			return out.toByteArray();
		} catch (Exception e) {
			logger.error("Error in Compresser: {}", e.getMessage());
			return new byte[0];
		}
	}

	public static byte[] decompress(byte[] in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			InflaterOutputStream infl = new InflaterOutputStream(out);
			infl.write(in);
			infl.flush();
			infl.close();

			return out.toByteArray();
		} catch (Exception e) {
			logger.error("Error in Compresser: {}", e.getMessage());
			return new byte[0];
		}
	}

}
