package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
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

	public static final String UTF8 = "UTF-8";
	public static final String HTTP = "http";
	public static final String HTTPS = "https";
	public static final String TS_TXT = "ts.txt";
	public static final String INDEX_M3U8 = "index.m3u8";
	public static final String CINDEX_M3U8 = "cindex.m3u8";

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
			bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), UTF8));
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

	public static List<String> downloadIndex(String m3u8, String dir) {
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
		File dirFile = new File(dir);
		if (!dirFile.exists()) {
			dirFile.mkdirs();
		}

		BufferedWriter indexWriter = null;
		try {
			indexWriter = new BufferedWriter(new FileWriter(dir + File.separator + INDEX_M3U8));
			for (int i = 0, len = list.size(); i < len; i++) {
				indexWriter.write(list.get(i));
				if (i < len - 1) {
					indexWriter.newLine();
				}
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				indexWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
		return list;
	}

	public static void localIndex(String dir) {
		// extinf
		File file = new File(dir + File.separator + INDEX_M3U8);
		BufferedReader bufferedReader;
		BufferedWriter bufferedWriter = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
			String read;
			EXTINF extinf = null;
			ArrayList<EXTINF> tsList = new ArrayList<EXTINF>();
			int index = 0;
			int c = 0;
			bufferedWriter = new BufferedWriter(new FileWriter(dir + File.separator + CINDEX_M3U8));
			while ((read = bufferedReader.readLine()) != null) {
				if (read.contains(".ts")) {
					extinf = new EXTINF(dir + File.separator + INDEX_M3U8, dir, index);
					extinf.setTs(read);
					read = read.substring(read.lastIndexOf("/") + 1);
					extinf.setTsName(read);
					extinf.setEncrypt(false);
					tsList.add(extinf);

					read = dir.replace("\\", "/") + "/" + index + "-" + read;
					index++;
				}
				bufferedWriter.write(read);
				bufferedWriter.newLine();

				System.out.println(c);
				c++;
			}
			JAXBUtils.extinf(dir, tsList);
			writeTS(tsList);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				bufferedWriter.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

	}

	public static void downloadByLocalM3u8(String file) {
		String dir=file.substring(0,file.lastIndexOf(File.separator));
		String read = "";
		ArrayList<String> list = new ArrayList<String>();
		BufferedReader bufferedReader = null;
		boolean encrypted = false;

		// 处理ts
		ArrayList<EXTINF> tsList = new ArrayList<EXTINF>();
		EXTINF extinf = null;
		int index = 0;
			
				
		try {
			bufferedReader = new BufferedReader(new FileReader(file));
		while ((read = bufferedReader.readLine()) != null) {
//			&&read.endsWith(".ts")
			if (!encrypted && read.contains(KEY)) {
				encrypted = true;
			}
			if(read.endsWith(".ts")) {
				
			list.add(read);
			extinf = new EXTINF();
			extinf.setDir(dir);
			extinf.setIndex(index);
			extinf.setTs(read);
			extinf.setTsName(read.substring(read.lastIndexOf("/") + 1));
			extinf.setEncrypt(encrypted);
			tsList.add(extinf);

			index++;

			}
		}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}finally {
			if(null!=bufferedReader) {
				
			try {
				bufferedReader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
			}
		}
		
		
					
				
				// ts.txt
				writeTS(tsList);
				// extinf.xml
				JAXBUtils.extinf(dir, tsList);

//				if (encrypted) {
//					// 加密视频创建index.m3u8,uri修改成本地，extinf也修改成本地
//					writeCIndex(m3u8, list, dir);
//				}
	}
	public static void main(String[] args) {
//		downloadByLocalM3u8("C:\\Users\\zhouyu\\Desktop\\完美世界\\20210812185223175\\index.m3u8");
//		String dir = "E:\\xxx\\20201114223602297";
//		File file = new File(dir + File.separator + "extinf.xml");
//		XMLRoot xmlRoot = JAXBUtils.read(file);
//		String m3u8 = xmlRoot.getM3u8();
//		downloadIndex(m3u8, dir);
//		localIndex(dir);

//		ts("https://cdn-khzy-i-bofang.com/20201217/cFxyXDHB/800kb/hls/index.m3u8", "D:\\xxx\\0112\\test");
		ts("https://vod8.wenshibaowenbei.com/20210813/fAeXYRvR/1000kb/hls/index.m3u8", "C:\\Users\\zhouyu\\Desktop\\testm3u8");

	}

	public static List<EXTINF> ts(String m3u8, String dir) {
		// index.m3u8
		List<String> list = downloadIndex(m3u8, dir);
		// 处理ts
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
				// 删除xxx.ts?xxxx,后缀
				if (ts.contains("?")) {
					ts = ts.substring(0, ts.indexOf("?"));
				}
				extinf = new EXTINF(m3u8, dir, index);
				extinf.setTs(ts);
				extinf.setTsName(ts.substring(ts.lastIndexOf("/") + 1));
				extinf.setEncrypt(encrypted);
				tsList.add(extinf);

				index++;
			}
		}
		// ts.txt
		writeTS(tsList);
		// extinf.xml
		JAXBUtils.extinf(dir, tsList);

		if (encrypted) {
			// 加密视频创建index.m3u8,uri修改成本地，extinf也修改成本地
			writeCIndex(m3u8, list, dir);
		}
		return tsList;
	}

	private static void writeCIndex(String m3u8, List<String> list, String dir) {
		int index = 0;
		BufferedReader bufferedReader = null;
		BufferedWriter bufferedWriter = null;

		BufferedWriter cindexWriter = null;

		try {
			cindexWriter = new BufferedWriter(new FileWriter(dir + File.separator + CINDEX_M3U8));
			for (int i = 0, len = list.size(); i < len; i++) {
				String line = list.get(i);
				if (line.contains(KEY)) {
					String prefix = m3u8.substring(0, m3u8.lastIndexOf("/") + 1);
					int keyIndexStart = line.indexOf("URI=");
					if (-1 == keyIndexStart) {
						line.indexOf("uri=");
					}
					String key = line.substring(keyIndexStart + 5, line.lastIndexOf("\""));
					// 修改 uri=dir/key.key
					String redir = dir.replace("\\", "/");
					String keyPrefix = line.substring(0, keyIndexStart + 5);
					String suffix = line.substring(line.lastIndexOf("\""));
					line = keyPrefix + redir + "/" + "key.key" + suffix;

					URL url = null;
					if (key.startsWith(HTTP) || key.startsWith(HTTPS)) {
						url = new URL(key);
					} else {
						url = new URL(prefix + key);
					}

					bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), UTF8));
					String read;
					bufferedWriter = new BufferedWriter(new FileWriter(dir + File.separator + "key.key"));
					while ((read = bufferedReader.readLine()) != null) {
						bufferedWriter.write(read);
					}

				}
				if (line.contains(".ts")) {
					String reLine = "";
					if (line.startsWith(HTTP) || line.startsWith(HTTPS)) {
						line = dir.replace("\\", "/") + "/" + index + "-" + line.substring(line.lastIndexOf("/") + 1);
					} else {

						int lastIndexOf = line.lastIndexOf("/");
						if (-1 == lastIndexOf) {
							reLine = line;
						} else {
							reLine = line.substring(lastIndexOf + 1);
						}
						String replace = line.replace(line, dir + File.separator + index + "-" + reLine);
						line = replace;
					}
					// 删除xxx.ts?xxxx,后缀
					if (line.contains("?")) {
						line = line.substring(0, line.indexOf("?"));
					}
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

	public static String writeTS(List<EXTINF> ts) {
		StringBuilder tsBuilder = new StringBuilder();
		for (EXTINF extinf : ts) {
			tsBuilder.append("file ");
			tsBuilder.append("'");
			tsBuilder.append(extinf.getDir() + File.separator + extinf.getIndex() + "-" + extinf.getTsName());
			tsBuilder.append("'");
			tsBuilder.append("\r\n");
		}
		String filePath = ts.get(0).getDir() + File.separator + TS_TXT;

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