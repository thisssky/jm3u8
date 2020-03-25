package application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.spec.AlgorithmParameterSpec;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

/**
 * 
 * @description 128位解密
 *
 */
public class AES {

	public static byte[] decrypt(byte[] sSrc, byte[] key, String iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		byte[] ivByte;
		if (iv.startsWith("0x")) {
			ivByte = hexStringToByteArray(iv.substring(2));
		} else {
			ivByte = iv.getBytes();
		}
		if (ivByte.length != 16) {
			ivByte = new byte[16];
		}
		// 如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
		cipher.init(Cipher.DECRYPT_MODE, keySpec, paramSpec);
		return cipher.doFinal(sSrc, 0, sSrc.length);
	}

	public static byte[] encrypt(byte[] sSrc, byte[] key, String iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
		SecretKeySpec keySpec = new SecretKeySpec(key, "AES");
		byte[] ivByte;
		if (iv.startsWith("0x")) {
			ivByte = hexStringToByteArray(iv.substring(2));
		} else {
			ivByte = iv.getBytes();
		}
		if (ivByte.length != 16) {
			ivByte = new byte[16];
		}
		// 如果m3u8有IV标签，那么IvParameterSpec构造函数就把IV标签后的内容转成字节数组传进去
		AlgorithmParameterSpec paramSpec = new IvParameterSpec(ivByte);
		cipher.init(Cipher.ENCRYPT_MODE, keySpec, paramSpec);
		return cipher.doFinal(sSrc, 0, sSrc.length);
	}

	public static byte[] hexStringToByteArray(String s) {
		int len = s.length();
		if ((len & 1) == 1) {
			s = "0" + s;
			len++;
		}
		byte[] data = new byte[len / 2];
		for (int i = 0; i < len; i += 2) {
			data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
		}
		return data;
	}

	public static byte[] getKeyBytes(String key) {
		File file = new File(key);
		FileInputStream fileInputStream = null;
		try {
			fileInputStream = new FileInputStream(file);
			byte[] bf = new byte[16];
			fileInputStream.read(bf, 0, bf.length);
			String string = new String(bf);
			System.out.println(string);
			return bf;
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileInputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return null;
	}

	public static void decryptFile(String infile, String outfile, String keyfile) {
		byte[] keyBytes = getKeyBytes(keyfile);
		FileInputStream fileInputStream;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(infile));
			int available = fileInputStream.available();
			byte[] src = new byte[available];
			fileInputStream.read(src);
			byte[] decrypt = decrypt(src, keyBytes, "");
			fileOutputStream = new FileOutputStream(new File(outfile));
			fileOutputStream.write(decrypt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void encryptFile(String infile, String outfile, String keyfile) {
		byte[] keyBytes = getKeyBytes(keyfile);
		FileInputStream fileInputStream;
		FileOutputStream fileOutputStream = null;
		try {
			fileInputStream = new FileInputStream(new File(infile));
			int available = fileInputStream.available();
			byte[] src = new byte[available];
			fileInputStream.read(src);
			byte[] decrypt = encrypt(src, keyBytes, "");
			fileOutputStream = new FileOutputStream(new File(outfile));
			fileOutputStream.write(decrypt);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} catch (Exception e) {
			e.printStackTrace();
		} finally {
			try {
				fileOutputStream.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

	public static void main(String[] args) {
//		encryptFile();
		String keyfile = "C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\01\\key.key";
		String infile = "C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\01\\75-Ezg3PH6a.ts";
		String outfile = "C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\01\\75-Ezg3PH6a-decrypt.ts";
		decryptFile(infile, outfile, keyfile);
	}
}
