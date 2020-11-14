package application.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
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

	public static void decryptDir(String dir) {
		File file = new File(dir);
		String[] list = file.list(new FilenameFilter() {

			@Override
			public boolean accept(File dir, String name) {
				if (name.contains(".ts")) {
					return true;
				}
				return false;
			}
		});
		for (int i = 0, len = list.length; i < len; i++) {
			decryptFile(dir + File.separator + list[i], dir + File.separator + list[i],
					dir + File.separator + "key.key");
			System.out.println(len-1-i);
		}
	}

	public static void main(String[] args) {
//		encryptFile();
		String keyfile = "E:\\xxx\\5101\\20201111155629499\\key.key";
		String infile = "E:\\xxx\\5101\\20201111155629499\\541-index541-copy.ts";
		String outfile = "E:\\xxx\\5101\\20201111155629499\\541-index541-copy.ts";
//		decryptFile(infile, outfile, keyfile);
		decryptDir("E:\\xxx\\5101\\20201112001814089");
	}
}
