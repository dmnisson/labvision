package io.github.dmnisson.labvision.utils;

/**
 * Convert between byte-array and string representations
 * @author davidnisson
 *
 */
public class ByteArrayStringConverter {
	public static String toHexString(byte[] a) {
		StringBuilder builder = new StringBuilder();
		for (byte b : a) {
			builder.append(String.format("%02x", b));
		}
		return builder.toString();
	}
	
	public static byte[] hexToByteArray(String s) {
		int length = s.length();
		byte[] array = new byte[length / 2];
		for (int i = 0; i < length; i += 2) {
			array[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
					+ Character.digit(s.charAt(i + 1), 16));
		}
		return array;
	}
}
