package com.littlecloud.ac;

public class DebugManager {
	private static boolean PROD_MODE = true;

	public static boolean isPROD_MODE() {
		return PROD_MODE;
	}

	public static void setPROD_MODE(boolean pROD_MODE) {
		PROD_MODE = pROD_MODE;
	}
}
