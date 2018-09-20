package com.littlecloud.pool.utils;

import org.apache.commons.codec.binary.Base64;

public class CipherUtil {
	
	private static final String KEY = "CHv8WO7llsZD9hQ0hnfkQxxLal/cvOJgdxe7SlsQXzQhtfapnu9XmZN7MDDhIK+w\\nk6bVXW2Tu1u30FxVIWWkghTPp7Ju/ront9kdctsx8Cg4oEgLAajqgsYPb1rKkMrJ\\nXhTZtOXFL63hlUO70aErP/UuP3GVMFutcBIw39XK7sM=";
	
	public String encrypt(final String text) {
		return Base64.encodeBase64String(this.xor(text.getBytes()));
	}

	public String decrypt(final String hash) {
		try {
			return new String(this.xor(Base64.decodeBase64(hash.getBytes())), "UTF-8");
		} catch (java.io.UnsupportedEncodingException ex) {
			throw new IllegalStateException(ex);
		}
	}

	private byte[] xor(final byte[] input) {
		final byte[] output = new byte[input.length];
		final byte[] secret = this.KEY.getBytes();
		int spos = 0;
		for (int pos = 0; pos < input.length; ++pos) {
			output[pos] = (byte) (input[pos] ^ secret[spos]);
			spos += 1;
			if (spos >= secret.length) {
				spos = 0;
			}
		}
		return output;
	}
	
	public static void main(String args[])
	{
		String testStr = "";
		
		CipherUtil cp = new CipherUtil();
		String enc = cp.encrypt(testStr);
		System.out.println("enc="+enc);
		
		String dec = cp.decrypt(enc);
		System.out.println("dec="+dec);
	}
}