package application.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
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

	private boolean encrypt = false;
	private String EXTM3U = "#EXTM3U";
	private String KEY = "#EXT-X-KEY";
	private String VERSION = "#EXT-X-VERSION";
	private String TARGETDURATION = "#EXT-X-TARGETDURATION";
	private String SEQUENCE = "#EXT-X-MEDIA-SEQUENCE";
	private String EXTINF = "#EXTINF";
	private static String ENDLIST = "#EXT-X-ENDLIST";

	public static void main(String[] args) {
		// https://www2.800-cdn.com/20200117/oEPSuMue/index.m3u8
		ts("C:\\Users\\kyh\\Desktop\\m3u8\\xxx-",
				"http://youku.cdn4-okzy.com/20191126/2980_2373c5f5/1000k/hls/index.m3u8");
	}

	public static List<EXTINF> ts(String dir, String indexUrl) {
		List<String> list = index(indexUrl);
		String last = list.get(list.size() - 1);
		while (!ENDLIST.equalsIgnoreCase(last)) {
			// 如果不是#EXT-X-ENDLIST結尾还需继续
			String prefix = indexUrl.substring(0, indexUrl.lastIndexOf("/") + 1);
			if (last.startsWith("/")) {
				indexUrl = prefix + last.substring(1);
			} else {
				indexUrl = prefix + last;
			}
			list = index(indexUrl);
			last = list.get(list.size() - 1);
		}
		// 处理ts序列
		ArrayList<EXTINF> ts = new ArrayList<EXTINF>();
		EXTINF extinf;
		int index = 0;
		for (String e : list) {
			String prefix = indexUrl.substring(0, indexUrl.lastIndexOf("/") + 1);
			if (e.endsWith(".ts")) {
				if (e.startsWith("/")) {
					extinf = new EXTINF(index, prefix + e.substring(1), e.substring(e.lastIndexOf("/") + 1));
					ts.add(extinf);
				} else {
					extinf = new EXTINF(index, prefix + e, e.substring(e.lastIndexOf("/") + 1));
					ts.add(extinf);
				}
				index++;
				// 写下ts文件
				String filePath = dir + File.separator + "tsFile.txt";

				StringBuilder sb = new StringBuilder();
				sb.append("file ");
				sb.append("'");
				sb.append(dir + File.separator + extinf.getIndex() + "-" + extinf.getName());
				sb.append("'");
				sb.append("\r\n");
				tsFile(filePath, sb.toString());
			}
		}
		return ts;
	}

	public static void tsFile(String fileName, String content) {
		try {
			// 打开一个随机访问文件流，按读写方式
			RandomAccessFile randomFile = new RandomAccessFile(fileName, "rw");
			// 文件长度，字节数
			long fileLength = randomFile.length();
			// 将写文件指针移到文件尾。
			randomFile.seek(fileLength);
			randomFile.writeBytes(content);
			randomFile.close();
		} catch (IOException e) {
			e.printStackTrace();
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