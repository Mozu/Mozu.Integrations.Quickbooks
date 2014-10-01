package com.mozu.qbintegration.handlers;


import org.jasypt.util.text.BasicTextEncryptor;
import org.springframework.stereotype.Component;

import com.mozu.api.security.AppAuthenticator;

@Component
public class EncryptDecryptHandler {
	
	public String encrypt( String value)  {
		
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret());
		
		return textEncryptor.encrypt(value);
	}
	
	public String decrypt( String encryptedStr)  {
	
		BasicTextEncryptor textEncryptor = new BasicTextEncryptor();
		textEncryptor.setPassword(AppAuthenticator.getInstance().getAppAuthInfo().getSharedSecret());
		
		return textEncryptor.decrypt(encryptedStr);
	}
	
}
