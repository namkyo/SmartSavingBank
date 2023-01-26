package kr.co.smartbank.app.util;

import android.util.Base64;
import java.security.Key;
import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;


public class CryptoUtil {
	private static volatile CryptoUtil INSTANCE;
	private final byte[] ENC_CHARSET = "UTF-8".getBytes();
	private final byte[] ENC_FORMAT  = "AES/CBC/PKCS5Padding".getBytes(); // AES/
	private final byte[] SECRET_KEY  = "FT3456789JT23456".getBytes(); // 16byte
	private final byte[] iv 		 = "FT87654321JT4321".getBytes();
	private final byte[] alroism 	 = "AES".getBytes();

	public static CryptoUtil getInstace() {
		if (null == INSTANCE) {
			synchronized (CryptoUtil.class) {
				if (null == INSTANCE) INSTANCE = new CryptoUtil();
			}
		}
		return INSTANCE;
	}

	private CryptoUtil() {
	}

	private Key getAESKey() throws Exception {

		byte[] keyBytes = new byte[16];
		byte[] b = SECRET_KEY;
		int len = b.length;
		if(len > keyBytes.length) {
			len = keyBytes.length;
		}
		System.arraycopy(b, 0, keyBytes, 0, len);
		return new SecretKeySpec(keyBytes, new String(alroism,new String(ENC_CHARSET)));
	}

	/**
	 * 암호화
	 * @param decStr
	 * @return
	 * @throws Exception
	 */

	public String encrypt(String decStr) throws Exception {
		Cipher cipher = Cipher.getInstance(new String(ENC_FORMAT));
		cipher.init(Cipher.ENCRYPT_MODE, getAESKey(), new IvParameterSpec(iv));
		//return new String(Base64.encodeBase64(cipher.doFinal(decStr.getBytes(ENC_CHARSET))),ENC_CHARSET);
		return Base64.encodeToString(cipher.doFinal(decStr.getBytes()),Base64.NO_WRAP);
	}
	/**
	 * 암호화
	 * @param decStr
	 * @return
	 * @throws Exception
	 */

	public String encrypt(byte[] decStr) throws Exception {
		Cipher cipher = Cipher.getInstance(new String(ENC_FORMAT));
		cipher.init(Cipher.ENCRYPT_MODE, getAESKey(), new IvParameterSpec(iv));
		//return new String(Base64.encodeBase64(cipher.doFinal(decStr.getBytes(ENC_CHARSET))),ENC_CHARSET);
		return Base64.encodeToString(cipher.doFinal(decStr),Base64.NO_WRAP);
	}

	/**
	 * 복호화
	 * @param encStr
	 * @return
	 * @throws Exception
	 */

	public String decrypt(String encStr) throws Exception {
		Cipher cipher = Cipher.getInstance(new String(ENC_FORMAT));
		cipher.init(Cipher.DECRYPT_MODE, getAESKey(), new IvParameterSpec(iv));
		//return new String(cipher.doFinal(Base64.decodeBase64(encStr.getBytes(ENC_CHARSET))),ENC_CHARSET);
		return new String(cipher.doFinal(Base64.decode(encStr.getBytes(),Base64.NO_WRAP)));
	}

}



