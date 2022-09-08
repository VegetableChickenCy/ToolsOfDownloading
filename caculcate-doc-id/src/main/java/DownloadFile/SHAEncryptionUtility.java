package DownloadFile;


import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.util.ObjectUtils;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

public class SHAEncryptionUtility {
	
	public static String encrypt(String strToEncrypt, SecretKeySpec shaSaltInstance, String cipherInstance) 
    {
        try {
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.ENCRYPT_MODE, shaSaltInstance);
            return Base64.getEncoder().encodeToString(cipher.doFinal(strToEncrypt.getBytes(StandardCharsets.UTF_8)));
        } catch (Exception e) {
            return null;
        }        
    }
	
	public static String decrypt(String strToDecrypt, SecretKeySpec shaSaltInstance, String cipherInstance) 
    {
        try {
            Cipher cipher = Cipher.getInstance(cipherInstance);
            cipher.init(Cipher.DECRYPT_MODE, shaSaltInstance);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {
        	return null;
        }        
    }
	
	/*
	 * This method generates a SHA-1 hash based on the input
	 */
	public static String generateSHA1Hash(String stringToBeConverted) {
		return ObjectUtils.isEmpty(stringToBeConverted) ? "" : DigestUtils.sha1Hex(stringToBeConverted);
	}


}
