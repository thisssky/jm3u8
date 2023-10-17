package application.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.security.spec.AlgorithmParameterSpec;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.List;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import application.dto.EXTINF;
import application.dto.XMLRoot;

/**
 *
 * @description 128位解密
 * AES-128 CBC with PKCS7 padding
 */
public class AES {

	public static byte[] decrypt(byte[] sSrc, byte[] key, String iv) throws Exception {
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
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
		Cipher cipher = Cipher.getInstance("AES/CBC/PKCS7Padding");
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

		File file = new File(dir + File.separator + "extinf.xml");
		XMLRoot xmlRoot = JAXBUtils.read(file);
		List<EXTINF> list = xmlRoot.getList();
		for (EXTINF extinf : list) {
			decryptFile(dir + File.separator + extinf.getIndex() + "-" + extinf.getTsName(),
					dir + File.separator + +extinf.getIndex() + "-" + extinf.getTsName(),
					dir + File.separator + "key.key");
			System.out.println(extinf.getDir() + "/" + extinf.getIndex() + "-" + extinf.getTsName() + "---"
					+ (list.size() - extinf.getIndex() - 1));

		}

	}

	public static void merge(String dir) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		List<String> command = new ArrayList<>();
		command.add("ffmpeg");
		command.add("-f");
		command.add("concat");
		command.add("-safe");
		command.add("0");
		command.add("-i");
		command.add(dir + File.separator + M3U8.TS_TXT);
		command.add("-c");
		command.add("copy");
		command.add(dir + File.separator + date + ".mp4");

		try {
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable inputRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getInputStream()));
					try {
						// 将进程的输出流封装成缓冲读者对象

					} finally {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			};
			Runnable errorRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getErrorStream()));
					String line = "";
					try {
						while ((line = bufferedReader.readLine()) != null) {
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							bufferedReader.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}
				}
			};
			new Thread(inputRunnable).start();
			new Thread(errorRunnable).start();
			videoProcess.waitFor();
			videoProcess.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static String encrypt(final String secret, final String data) {


        byte[] decodedKey = Base64.getDecoder().decode(secret);

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.ENCRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(data.getBytes("UTF-8"));
            return Base64.getEncoder().encodeToString(cipherText);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error occured while encrypting data", e);
        }

    }

    public static String decrypt(final String secret,
            final String encryptedString) {


        byte[] decodedKey = Base64.getDecoder().decode(secret);

        try {
            Cipher cipher = Cipher.getInstance("AES");
            // rebuild key using SecretKeySpec
            SecretKey originalKey = new SecretKeySpec(Arrays.copyOf(decodedKey, 16), "AES");
            cipher.init(Cipher.DECRYPT_MODE, originalKey);
            byte[] cipherText = cipher.doFinal(Base64.getDecoder().decode(encryptedString));
            return new String(cipherText);
        } catch (Exception e) {
            throw new RuntimeException(
                    "Error occured while decrypting data", e);
        }
    }
	public static void main(String[] args) {
//		String rootS = "E:\\xxx\\d";
//		File root = new File(rootS);
//		String[] list = root.list();
//		for (int i = 0; i < list.length; i++) {
////			System.out.println(list[i]);
//			File dirF = new File(rootS + File.separator + list[i]);
//			String[] mp4 = dirF.list(new FilenameFilter() {
//
//				@Override
//				public boolean accept(File dir, String name) {
//					if (name.contains(".mp4"))
//						return true;
//					return false;
//				}
//			});
//
//			if (mp4.length == 0) {
//				System.out.println(dirF.getAbsolutePath());
//				merge(dirF.getAbsolutePath());
//				System.err.println(dirF.getAbsolutePath());
//			}
//
//		}
//		System.out.println(list.length);

		String dir = "C:\\Users\\zhouyu\\Desktop\\lxzc\\20211031230850643";
		File file = new File(dir + File.separator + "extinf.xml");
		XMLRoot xmlRoot = JAXBUtils.read(file);
		List<EXTINF> list = xmlRoot.getList();
		for (EXTINF extinf : list) {
			if (extinf.getIndex() >= 429) {

			decryptFile(dir + File.separator + extinf.getIndex() + "-" + extinf.getTsName(),
					dir + File.separator + +extinf.getIndex() + "-" + extinf.getTsName(),
					dir + File.separator + "key.key");
			System.out.println(
					extinf.getIndex() + "-" + extinf.getTsName() + "---" + (list.size() - extinf.getIndex() - 1));
			}

		}

	}

}
