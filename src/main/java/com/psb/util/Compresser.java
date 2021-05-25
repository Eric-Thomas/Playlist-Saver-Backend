package com.psb.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.DeflaterOutputStream;
import java.util.zip.InflaterOutputStream;

// yoinked from https://stackoverflow.com/questions/51332314/java-byte-array-compression
public class Compresser {

	public static byte[] compress(byte[] in) {
		try {
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			DeflaterOutputStream defl = new DeflaterOutputStream(out);
			defl.write(in);
			defl.flush();
			defl.close();

			return out.toByteArray();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(150);
			return null;
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
			e.printStackTrace();
			System.exit(150);
			return null;
		}
	}

}
