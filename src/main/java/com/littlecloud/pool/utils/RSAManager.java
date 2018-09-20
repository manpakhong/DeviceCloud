package com.littlecloud.pool.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import javax.crypto.Cipher;
import javax.xml.bind.DatatypeConverter;

import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

public class RSAManager {
	private static final Logger log = Logger.getLogger(RSAManager.class);

	/* Requirement: Swap the encryption key by design (private encrypt and public decrypt) =_=! */
	private String public_key_file;
	private String private_key_file;

	private PrivateKey privateKey;
	private PublicKey publicKey;

	public RSAManager() {
		super();
	}

	public String getPublic_key_file() {
		return public_key_file;
	}

	public String getPrivate_key_file() {
		return private_key_file;
	}

	public PrivateKey getPrivateKey() {
		return privateKey;
	}

	public PublicKey getPublicKey() {
		return publicKey;
	}

	public void loadKeyFromFile(String public_key_file, String private_key_file) throws IOException, GeneralSecurityException {
		this.public_key_file = public_key_file;
		this.private_key_file = private_key_file;

		if (public_key_file != null)
			this.publicKey = loadPublicKeyFromFile(public_key_file);

		if (private_key_file != null)
			this.privateKey = loadPrivateKeyFromFile(private_key_file);
	}
	
	public void loadPublicKey(String keyStr) throws IOException, GeneralSecurityException {
		String[] keyArr = keyStr.split("\n");
		
		StringBuilder builder = new StringBuilder();
		boolean inKey = false;
		for (String line:keyArr) {
			if (!inKey) {
				if (line.startsWith("-----BEGIN ") && line.endsWith(" PUBLIC KEY-----")) {
					inKey = true;
				}
				continue;
			} else {
				if (line.startsWith("-----END ") && line.endsWith(" PUBLIC KEY-----")) {
					inKey = false;
					break;
				}
				builder.append(line);
			}
		}
		
		byte[] encoded = DatatypeConverter.parseBase64Binary(builder.toString());
		X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encoded);
		KeyFactory kf = KeyFactory.getInstance("RSA");
		this.publicKey = kf.generatePublic(x509Spec);		
	}
	
	private byte[] encryptData(String data) throws IOException {
		

		
		byte[] dataToEncrypt = data.getBytes();
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, publicKey);
			encryptedData = cipher.doFinal(dataToEncrypt);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return encryptedData;
	}

	private String decryptData(byte[] data) throws IOException {
		
		byte[] descryptedData = null;

		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, privateKey);
			descryptedData = cipher.doFinal(data);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		return new String(descryptedData);
	}

	public String decryptDataWithPublicKey(byte[] data) throws IOException {
		
		byte[] descryptedData = null;

		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.DECRYPT_MODE, publicKey);
			descryptedData = cipher.doFinal(data);
			
		} catch (Exception e) {
			e.printStackTrace();
		}

		return new String(descryptedData);
	}

	public byte[] encryptDataWithPrivateKey(String data) throws IOException {
		

		
		byte[] dataToEncrypt = data.getBytes();
		byte[] encryptedData = null;
		try {
			Cipher cipher = Cipher.getInstance("RSA");
			cipher.init(Cipher.ENCRYPT_MODE, privateKey);
			encryptedData = cipher.doFinal(dataToEncrypt);
			

		} catch (Exception e) {
			e.printStackTrace();
		}

		
		return encryptedData;
	}

	public static PrivateKey loadPrivateKeyFromFile(String fileName) throws IOException, GeneralSecurityException {
		PrivateKey key = null;
		InputStream is = null;
		try {
			is = new FileInputStream(new File(fileName));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			boolean inKey = false;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (!inKey) {
					if (line.startsWith("-----BEGIN ") && line.endsWith(" PRIVATE KEY-----")) {
						inKey = true;
					}
					continue;
				} else {
					if (line.startsWith("-----END ") && line.endsWith(" PRIVATE KEY-----")) {
						inKey = false;
						break;
					}
					builder.append(line);
				}
			}
			//
			byte[] encoded = DatatypeConverter.parseBase64Binary(builder.toString());
			PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(encoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			key = kf.generatePrivate(keySpec);
		} finally {
			closeSilent(is);
		}
		return key;
	}

	public static PublicKey loadPublicKeyFromFile(String fileName) throws IOException, GeneralSecurityException {
		PublicKey key = null;
		InputStream is = null;
		try {			
			is = new FileInputStream(new File(fileName));
			BufferedReader br = new BufferedReader(new InputStreamReader(is));
			StringBuilder builder = new StringBuilder();
			boolean inKey = false;
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				if (!inKey) {
					if (line.startsWith("-----BEGIN ") && line.endsWith(" PUBLIC KEY-----")) {
						inKey = true;
					}
					continue;
				} else {
					if (line.startsWith("-----END ") && line.endsWith(" PUBLIC KEY-----")) {
						inKey = false;
						break;
					}
					builder.append(line);
				}
			}

			byte[] encoded = DatatypeConverter.parseBase64Binary(builder.toString());
			X509EncodedKeySpec x509Spec = new X509EncodedKeySpec(encoded);
			KeyFactory kf = KeyFactory.getInstance("RSA");
			key = kf.generatePublic(x509Spec);
		} finally {
			closeSilent(is);
		}
		return key;
	}

	public static void closeSilent(final InputStream is) {
		if (is == null)
			return;
		try {
			is.close();
		} catch (Exception ign) {
		}
	}

	public static void main(String[] args) throws IOException {

		RSAManager rsa = new RSAManager();
		try {
			rsa.loadKeyFromFile("c:\\Public.pem8", "c:\\Private.pem8");
		} catch (GeneralSecurityException e1) {
			e1.printStackTrace();
		}
		Base64 base64 = new Base64();
		byte[] base64dec = null;

		byte[] encTestStrByte;
		String encTestStr;

		String encodedStr = "Fvp6siDf6xMM4k/NCTV28LTOAm1xiFzh26alV3WgwWiic7naZjwhMBu/kGQ5HlAQ"
				+ "sHKkGl1DyzTYwpiLxHcPRr7prMX97XOXfdPrGZebRRVlUbO0l7jil5TgZhUJlfa0"
				+ "IloqIz8SHP/7wSMswme/nlSgE0YgVLd9H+HM7RarA2s=";
		
		base64dec = base64.decode(encodedStr);
		rsa.decryptDataWithPublicKey(base64dec);

		/*************/
		String testStr = "This is a test string buffer for private key encrypt and base64 encoded";
		encTestStrByte = rsa.encryptDataWithPrivateKey(testStr);
		

		encTestStr = javax.xml.bind.DatatypeConverter.printBase64Binary(encTestStrByte);
		
		base64dec = base64.decode(encTestStr);
		// Descypt Data using Private Key
		rsa.decryptDataWithPublicKey(base64dec);

		encTestStrByte = rsa.encryptDataWithPrivateKey(testStr);
		encTestStr = javax.xml.bind.DatatypeConverter.printBase64Binary(encTestStrByte);
		
	}
}