package uk.co.mafew.file.kryptos;

import java.security.Key;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;

public class Encryption
{
	static String SYSTEM_ENCRYPTION_KEY = "hds%YKw82HZL&kwsJ";
	
	static public String getSystemEncryptionKey()
	{
		return SYSTEM_ENCRYPTION_KEY;
	}
	
	static public String encrypt(String text, String key) throws Exception
	{
		// As we can only use 128bit encryption at the moment we
		// need to make sure the key is 16 characters long
		if (key.length() > 16)
		{
			key = key.substring(0, 16);
		}
		else if (key.length() < 16)
		{
			key = key.substring(key.length()) + key;
		}
		
		// Create key and cipher
		Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");

		// encrypt the text
		cipher.init(Cipher.ENCRYPT_MODE, aesKey);
		byte[] encrypted = cipher.doFinal(text.getBytes());
		String encodedString = new Base64().encodeToString(encrypted);

		return encodedString;
	}

	static public String decrypt(String text, String key) throws Exception
	{
		// As we can only use 128bit encryption at the moment we
		// need to make sure the key is 16 characters long
		if (key.length() > 16)
		{
			key = key.substring(0, 16);
		}
		else if (key.length() < 16)
		{
			key = key.substring(key.length()) + key;
		}
		
		// Create key and cipher
		Key aesKey = new SecretKeySpec(key.getBytes(), "AES");
		Cipher cipher = Cipher.getInstance("AES");
		cipher.init(Cipher.DECRYPT_MODE, aesKey);
		String decrypted = new String(cipher.doFinal(new Base64().decode(text)));

		return decrypted;
	}

}
