package com.littlecloud.ac.health.util;

import java.io.IOException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.InputStream;
import java.util.Scanner;

public class Cli {
	
	public static String getOutput(String cmd) throws IOException {
		StringBuilder sb = new StringBuilder();
		Runtime aRT = Runtime.getRuntime();
		Process aProc = aRT.exec(cmd);
		InputStream is = aProc.getInputStream();
		Scanner sc = new Scanner(is);
		while (sc.hasNextLine()) {
			sb.append(sc.nextLine());
			sb.append("\n");
		}
		sc.close();
		return sb.toString();
	}

	public static String parseOutput(String cmd) throws IOException {
		StringBuilder sb = new StringBuilder();
		Runtime aRT = Runtime.getRuntime();
		Process aProc = aRT.exec(cmd);
		BufferedReader br = new BufferedReader(new InputStreamReader(aProc.getInputStream()));
		String line = "";
		while ((line=br.readLine())!=null) {
			sb.append(line);
			sb.append("\n");
		}
		br.close();
		return sb.toString();
	}

	public static void main(String[] args)
	{
		try {
			System.out.printf("Result = %s", Cli.parseOutput("c:\\windows\\system32\\ipconfig"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
