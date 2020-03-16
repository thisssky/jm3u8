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

//一次的
//	http://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8
//	#EXTM3U
//	#EXT-X-VERSION:3
//	#EXT-X-TARGETDURATION:8
//	#EXT-X-MEDIA-SEQUENCE:0
//	#EXTINF:4.000000,
//	9660a92c9d8000000.ts
//	#EXTINF:6.400000,
//	9660a92c9d8000001.ts
//	#EXTINF:3.560000,
//	9660a92c9d8000002.ts
//	#EXTINF:2.680000,
//	9660a92c9d8000003.ts
//	#EXTINF:4.000000,
//	#EXT-X-ENDLIST

//https://video.huishenghuo888888.com/jingpin/20200118/kpxNMN5Q/index.m3u8
//	#EXTM3U
//	#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=500000,RESOLUTION=720x1252
//	500kb/hls/index.m3u8
//https://video.huishenghuo888888.com/jingpin/20200118/kpxNMN5Q/500kb/hls/index.m3u8
//	#EXTM3U
//	#EXT-X-VERSION:3
//	#EXT-X-TARGETDURATION:1
//	#EXT-X-MEDIA-SEQUENCE:0
//	#EXT-X-KEY:METHOD=AES-128,URI="key.key"//eebb9 243f9 debe8c
//	#EXTINF:0.66,
//	cvbQeA52.ts
//	#EXTINF:0.74,
//	7P16iiM4.ts
//	#EXT-X-ENDLIST

//https://videozmcdn.stz8.com:8091/20200116/0jk8H2mb/index.m3u8
//	#EXTM3U
//	#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=1000000,RESOLUTION=720x406
//	1000kb/hls/index.m3u8
//https://videozmcdn.stz8.com:8091/20200116/0jk8H2mb/1000kb/hls/index.m3u8
//	#EXTM3U
//	#EXT-X-VERSION:3
//	#EXT-X-TARGETDURATION:9
//	#EXT-X-MEDIA-SEQUENCE:0
//	#EXTINF:8.342,
//	6xxzl6Me.ts
//	#EXTINF:4.171,
//	CQzQ95OV.ts
//	#EXT-X-ENDLIST
//https://videozmcdn.stz8.com:8091/20200116/0jk8H2mb/1000kb/hls/6xxzl6Me.ts

// #EXT-X-KEY:METHOD=AES-128,URI="https://j-island.net/movie/hls_key/s/857401e309d8a032c3bb18f4b09b8db2/?f=jj_20190401_hihijets_004",IV=0xaa3dcf6a7acb92ff4fb08d9b3b3d6f51
// 有加密的
// https://www.jianshu.com/p/1b0adcc7b426
public class M3U8 {
	public static final String TS_FILE = "ts.txt";

	public static String EXTM3U = "#EXTM3U";
//	#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=800000,RESOLUTION=1080x608

	public static String KEY = "#EXT-X-KEY";
	public static String VERSION = "#EXT-X-VERSION";
	public static String TARGETDURATION = "#EXT-X-TARGETDURATION";
	public static String SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
	public static String EXTINF = "#EXTINF";
	public static String ENDLIST = "#EXT-X-ENDLIST";

	public static void test() {
//		https://cn7.7639616.com/hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804/index.m3u8
//		   /hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804/film_00000.ts
//https://cn7.7639616.com/hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804/film_00001.ts

//https://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8
//                                              9660a92c9d8000000.ts
//https://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/9660a92c9d8000000.ts

		String m3u8 = "https://cn7.7639616.com/hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804/index.m3u8";
		System.out.println(m3u8);
// https://cn7.7639616.com/hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804
		String m3u8Prefix = m3u8.substring(0, m3u8.lastIndexOf("/"));
		System.out.println(m3u8Prefix);

		String data = "film_00000.ts";
		System.out.println(data);
//https://cn7.7639616.com/hls/20191126/eebb68accf60bff8adffe64c7ac68219/1574769804/film_00000.ts

		if (data.contains("/")) {
			String rts = data.substring(data.lastIndexOf("/"));
			System.out.println(rts);
			String tsPrefix = data.substring(0, data.lastIndexOf("/"));
			System.out.println(tsPrefix);
			int containPrefix = m3u8Prefix.indexOf(tsPrefix);
			if (containPrefix > -1) {
				data = m3u8Prefix + rts;
			} else {
				if (!tsPrefix.startsWith("/")) {
					tsPrefix = "/" + tsPrefix;
				}
				data = m3u8Prefix + tsPrefix + rts;
			}
		} else {
			data = m3u8Prefix + "/" + data;
		}

	}

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
		ArrayList<EXTINF> ts = new ArrayList<EXTINF>();
		EXTINF extinf = null;
		int index = 0;
		boolean encrypted = false;
		String m3u8Prefix = m3u8.substring(0, m3u8.lastIndexOf("/"));
		String redata = "";
		for (String data : list) {
			if (!encrypted && data.contains(KEY)) {
				encrypted = true;
			}
			if (data.endsWith(".ts")) {
				if (data.contains("/")) {
					String rts = data.substring(data.lastIndexOf("/"));
					String tsPrefix = data.substring(0, data.lastIndexOf("/"));
					int containPrefix = m3u8Prefix.indexOf(tsPrefix);
					if (containPrefix > -1) {
						redata = m3u8Prefix + rts;
					} else {
						if (!tsPrefix.startsWith("/")) {
							tsPrefix = "/" + tsPrefix;
						}
						redata = m3u8Prefix + tsPrefix + rts;
					}
				} else {
					redata = m3u8Prefix + "/" + data;
				}
				extinf = new EXTINF(m3u8, dir, index);
				extinf.setTs(redata);
				extinf.setTsName(redata.substring(redata.lastIndexOf("/") + 1));
				extinf.setEncrypt(encrypted);
				ts.add(extinf);

				index++;
			}
		}
		if (encrypted) {
			// 加密视频创建index.m3u8,uri修改成本地，extinf也修改成本地
			writeCIndex(m3u8, list, dir);
		} else {
			// 写下ts文件
			writeTS(ts);
		}
		return ts;
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
					URL url = new URL(prefix + key);
					bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
					String read;
					bufferedWriter = new BufferedWriter(new FileWriter(dir + File.separator + "key.key"));
					while ((read = bufferedReader.readLine()) != null) {
						bufferedWriter.write(read);
					}
					// 修改
					String redir = dir.replace("\\", "/");
					line = line.substring(0, line.lastIndexOf("=") + 1) + "\"" + redir + "/" + key + "\"";
				}
				if (line.endsWith(".ts")) {
					String replace = line.replace(line, dir + File.separator + index + "-" + line);
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