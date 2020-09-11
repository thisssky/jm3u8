package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import application.dto.EXTINF;

public class M3U8 {

	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String TS_FILE = "ts.txt";

	// #EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=800000,RESOLUTION=1080x608
	public static String EXTM3U = "#EXTM3U";
	// 加密的
	// #EXT-X-KEY:METHOD=AES-128,URI=""
	public static String KEY = "#EXT-X-KEY";
	public static String VERSION = "#EXT-X-VERSION";
	public static String TARGETDURATION = "#EXT-X-TARGETDURATION";
	public static String SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
	public static String EXTINF = "#EXTINF";
	public static String ENDLIST = "#EXT-X-ENDLIST";

	public static List<String> index(String urlPath) {
		BufferedReader bufferedReader = null;
		String read = "";
		ArrayList<String> list = new ArrayList<String>();
		try {
			URL url = new URL(urlPath);
			bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
			while ((read = bufferedReader.readLine()) != null) {
				list.add(read);
			}
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return list;
	}

	public static List<EXTINF> ts(String m3u8, String dir) {
		List<String> list = index(m3u8);
		String last = list.get(list.size() - 1);
		while (!ENDLIST.equalsIgnoreCase(last)) {
			// 如果不是#EXT-X-ENDLIST結尾还需继续
			String prefix = m3u8.substring(0, m3u8.lastIndexOf("/") + 1);
			if (last.startsWith("/")) {
				m3u8 = prefix + last.substring(1);
			} else {
				m3u8 = prefix + last;
			}
			list = index(m3u8);
			last = list.get(list.size() - 1);
		}

		// 验证文件夹
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}
		// 删除已有文件
		String filePath = dir + File.separator + TS_FILE;
		File file = new File(filePath);
		file.delete();

		// 处理ts序列

		ArrayList<EXTINF> tsList = new ArrayList<EXTINF>();
		EXTINF extinf = null;
		int index = 0;
		boolean encrypted = false;

		String m3u8Prefix = m3u8.substring(0, m3u8.lastIndexOf("/"));
		String ts = "";
		for (String data : list) {
			if (!encrypted && data.contains(KEY)) {
				encrypted = true;
			}
			if (data.contains(".ts")) {

				if (data.startsWith(HTTP) || data.startsWith(HTTPS)) {
					ts = data;
				} else {
					String[] tsArray = data.split("/");
					for (int i = 0; i < tsArray.length; i++) {
						if (!"".equals(tsArray[i]) && !m3u8Prefix.contains(tsArray[i])) {
							ts = m3u8Prefix + "/" + tsArray[i];
						}
					}
				}

				extinf = new EXTINF(m3u8, dir, index);
				extinf.setTs(ts);
				extinf.setTsName(ts.substring(ts.lastIndexOf("/") + 1));
				extinf.setEncrypt(encrypted);
				tsList.add(extinf);

				index++;
			}
		}

		if (encrypted) {
			// 加密视频创建index.m3u8,uri修改成本地，extinf也修改成本地
			writeCIndex(m3u8, list, dir);
		} else {
			// 写下ts文件
			writeTS(tsList);
		}
		return tsList;
	}

	private static void writeCIndex(String m3u8, List<String> list, String dir) {
		int index = 0;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;
		BufferedWriter cindexWriter = null;

		try {
			cindexWriter = new BufferedWriter(new FileWriter(dir + File.separator + "cindex.m3u8"));
			for (int i = 0, len = list.size(); i < len; i++) {
				String line = list.get(i);
				if (line.contains(KEY)) {
					String prefix = m3u8.substring(0, m3u8.lastIndexOf("/") + 1);
					String key = line.substring(line.lastIndexOf("=") + 2, line.lastIndexOf("\""));
					URL url = null;
					if (key.startsWith(HTTP) || key.startsWith(HTTPS)) {
						url = new URL(key);
					} else {
						url = new URL(prefix + key);
					}
					bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
					String read;
					bufferedWriter = new BufferedWriter(new FileWriter(dir + File.separator + "key.key"));
					while ((read = bufferedReader.readLine()) != null) {
						bufferedWriter.write(read);
					}

					// 修改
					String redir = dir.replace("\\", "/");
					line = line.substring(0, line.lastIndexOf("=") + 1) + "\"" + redir + "/" + "key.key\"";

				}
				if (line.endsWith(".ts")) {
					String reLine = "";
					int lastIndexOf = line.lastIndexOf("/");
					if (-1 == lastIndexOf) {
						reLine = line;
					} else {
						reLine = line.substring(lastIndexOf + 1);
					}
					String replace = line.replace(line, dir + File.separator + index + "-" + reLine);
					line = replace;
					index++;
				}
				cindexWriter.write(line);
				cindexWriter.newLine();
			}

		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			if (null != bufferedReader) {
				try {
					bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			if (null != bufferedWriter) {
				try {
					bufferedWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			if (null != cindexWriter) {
				try {
					cindexWriter.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
		}

	}

	private static String writeTS(ArrayList<EXTINF> ts) {
		StringBuilder tsBuilder = new StringBuilder();
		for (EXTINF extinf : ts) {
			tsBuilder.append("file ");
			tsBuilder.append("'");
			tsBuilder.append(extinf.getDir() + File.separator + extinf.getIndex() + "-" + extinf.getTsName());
			tsBuilder.append("'");
			tsBuilder.append("\r\n");
		}
		String filePath = ts.get(0).getDir() + File.separator + TS_FILE;

		File tsfile = new File(filePath);
		FileWriter fileWriter = null;
		try {
			fileWriter = new FileWriter(tsfile);
			fileWriter.write(tsBuilder.toString());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fileWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return filePath;
	}

	@Deprecated
	public static void tsFile(String fileName, String content) {
		RandomAccessFile randomFile = null;
		try {
			// 打开一个随机访问文件流，按读写方式
			randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				randomFile.close();
			} catch (IOException e) {
				e.printStackTrace();
			}

		}
	}

	@Deprecated
	public static List<String> filter(String content) {
		Pattern pattern = Pattern.compile(".*ts");
		Matcher ma = pattern.matcher(content);
		List<String> list = new ArrayList<String>();
		while (ma.find()) {
			String s = ma.group();
			list.add(s);
		}
		return list;
	}

}