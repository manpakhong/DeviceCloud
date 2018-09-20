package com.littlecloud.pool.utils;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.codec.binary.Hex;
 
public class MD5Manager { 

	public static String md5(String text) throws NoSuchAlgorithmException, UnsupportedEncodingException{ 
        MessageDigest md;
        md = MessageDigest.getInstance("MD5");
        byte[] md5hash = new byte[32];
        md.update(text.getBytes("UTF-8"), 0, text.length());
        md5hash = md.digest();
        return convertToHex(md5hash);
    }
    
	public static String md5File(String filePath){
		MessageDigest md;
		String digest = null;
		try {
			md = MessageDigest.getInstance("MD5");
			digest = getDigest(new FileInputStream(filePath), md, 2048);
		} catch (Exception e) {
			e.printStackTrace();
		}

		return digest;
	}
	
	private static String getDigest(InputStream is, MessageDigest md, int byteArraySize)
			throws NoSuchAlgorithmException, IOException {

		md.reset();
		byte[] bytes = new byte[byteArraySize];
		int numBytes;
		while ((numBytes = is.read(bytes)) != -1) {
			md.update(bytes, 0, numBytes);
		}
		byte[] digest = md.digest();
		String result = new String(Hex.encodeHex(digest));
		return result;
	}	
	
    private static String convertToHex(byte[] data) { 
        StringBuilder buf = new StringBuilder();
        for (int i = 0; i < data.length; i++) { 
            int halfbyte = (data[i] >>> 4) & 0x0F;
            int two_halfs = 0;
            do { 
                if ((0 <= halfbyte) && (halfbyte <= 9)){
                    buf.append((char) ('0' + halfbyte));
                }else{ 
                    buf.append((char) ('a' + (halfbyte - 10)));
                }
                halfbyte = data[i] & 0x0F;
            } while(two_halfs++ < 1);
        }
        return buf.toString();
    }     
} 
