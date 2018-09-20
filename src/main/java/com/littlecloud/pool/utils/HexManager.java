package com.littlecloud.pool.utils;

import java.math.BigInteger;

import org.apache.commons.codec.binary.Hex;

public class HexManager {
	
	public static String hexToString(final String hex) {
		return new String(new BigInteger(hex, 16).toByteArray());
	}

	public static String stringToHex(final String str) {
		return Hex.encodeHexString(str.getBytes(/* charset */));
	}
	
	public static void main(String args[])
	{
		String str = "12345!@#$%^&*()_+<>?\"\'";
		System.out.println(str);
		String hexString = Hex.encodeHexString(str.getBytes(/* charset */));
		System.out.println(hexString);

		System.out.println(hexToString(hexString));
	}
}
