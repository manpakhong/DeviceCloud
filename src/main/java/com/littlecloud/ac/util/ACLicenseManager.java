package com.littlecloud.ac.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.GeneralSecurityException;

import org.apache.commons.codec.binary.Base64;
import org.jboss.logging.Logger;

import com.littlecloud.ac.ACService;
import com.littlecloud.control.json.util.DateUtils;
import com.littlecloud.control.json.util.JsonUtils;
import com.littlecloud.pool.object.ACLicenseObject;
import com.littlecloud.pool.utils.CipherUtil;
import com.littlecloud.pool.utils.PropertyService;
import com.littlecloud.pool.utils.RSAManager;

public class ACLicenseManager {

	private static final Logger log = Logger.getLogger(ACLicenseManager.class);

	private static PropertyService<ACLicenseManager> ps = new PropertyService<ACLicenseManager>(ACLicenseManager.class);
	// private static String PUBLIC_KEY_PATH = System.getProperty("public_key_path");
	private static String cipherKey = "bmVbFXoNcislPXoUbCodeStOLS4IVVVhTEElLj8ILColSCJ0AB00AhEYYiwlIyMxPyB4GVkdAHYJBwYBGABpEA0tDA46GxNufGQtQEcEBAUyAT1lIy1eXn8UCj1+A2UrBBYfeh9WBzYwSQF6NB9bIQYtAykpNy4hGzgPYyAvWnwEYWwaHg4HHAEbaT44bklaAyUhT0EgHAADHCw7WlJsL35+HiRMEh88Cmltf04gOHA7EDRoYB0BKCVEOQNDAxJgLyFNWwchDAtYJ35UMh8DLSkoJwY8NQFlMUg7KzhsVjcKFmABBy8sT3gDPFwrGm8AFSoqZyMUUnofeVgcMH10ZigDCxseKEcjEQldGmdYAg==";
	private static ACLicenseManager instance;
	
	private static boolean LicenseModeActivated = false; 
	
	private Base64 base64;
	private RSAManager rsa;
	
	public static boolean isLicenseModeActivated() {
		return LicenseModeActivated;
	}
	
	public static boolean isLicenseExpired() {
		boolean isLicenseExpired = false;
		
		if (ACLicenseManager.isLicenseModeActivated())
		{
			ACLicenseObject acLicObj = new ACLicenseObject();			
			acLicObj.setServer(ACService.getServerName());					
			try {
				acLicObj = ACUtil.getPoolObjectBySn(acLicObj, ACLicenseObject.class);
			} catch (Exception e) {
				log.error(e);
				isLicenseExpired = true;
			}
			
			if (acLicObj==null)
				isLicenseExpired = true;
			
			/* requirement on 2014-09-16 - null expiry_date means no expiry date limit */
			if (acLicObj.getExpiry_date()!=null && acLicObj.getExpiry_date() < DateUtils.getUnixtime() && 
					(DateUtils.getUnixtime() - acLicObj.getExpiry_date())/60/60/24 - 15 > 0
					)
				isLicenseExpired = true;
		}
		
		return isLicenseExpired;
	}

	public static void setLicenseModeActivated(boolean licenseModeActivated) {
		LicenseModeActivated = licenseModeActivated;
	}
	
	private ACLicenseManager() {
		rsa = new RSAManager();
		base64 = new Base64();
		try {
			// if (PUBLIC_KEY_PATH==null)
			// {
			// log.error("FATAL: PUBLIC_KEY_PATH is missing!!!");
			// System.out.println("FATAL: PUBLIC_KEY_PATH is missing!!!");
			// }
			String publicKeyStr = decCipherKey(cipherKey);
			rsa.loadPublicKey(publicKeyStr);

		} catch (IOException | GeneralSecurityException e) {
			log.error(e, e);
		}
	}

	public static ACLicenseManager getInstance() {
		if (instance != null)
			return instance;

		instance = new ACLicenseManager();
		return instance;
	}

	public ACLicenseObject decryptACLicenseObject(ACLicenseObject encObj) {
		ACLicenseObject result = null;
		String decData = null;
		byte[] base64dec = null;
		try {
			base64dec = base64.decode(encObj.getEnc());
			decData = rsa.decryptDataWithPublicKey(base64dec);
			result = JsonUtils.fromJson(decData, ACLicenseObject.class);
		} catch (IOException e) {
			log.error(e, e);
		} catch (Exception e)
		{
			log.error("decData="+decData, e);
		}

		return result;
	}

	private String decCipherKey(String cipher) {
		CipherUtil cp = new CipherUtil();
		return cp.decrypt(cipher);
	}

	private String loadCipherKeyFromFile(String filename) {
		InputStream is = null;
		String decKey = null;
		BufferedReader br = null;
		StringBuilder builder = new StringBuilder();
		try {
			is = new FileInputStream(new File(filename));
			// fileName.getClass().getResourceAsStream(fileName);
			br = new BufferedReader(new InputStreamReader(is));			
			for (String line = br.readLine(); line != null; line = br.readLine()) {
				builder.append(line);
			}
			CipherUtil cp = new CipherUtil();
			decKey = cp.decrypt(builder.toString());

			log.debugf("decKey=%s", decKey);
		} catch (Exception e) {
			log.error(e, e);
		} finally {
			closeSilent(is);
			try {
				br.close();
			} catch (IOException e) {
				log.error(e, e);
			}
		}
		return decKey;
	}

	public static void closeSilent(final InputStream is) {
		if (is == null)
			return;
		try {
			is.close();
		} catch (Exception ign) {
		}
	}
}
