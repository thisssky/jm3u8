package application.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public   class M3U8 {
		String key="#EXT-X-KEY";
		boolean encrypt=false;
		//#EXT-X-KEY:METHOD=AES-128,URI="https://j-island.net/movie/hls_key/s/857401e309d8a032c3bb18f4b09b8db2/?f=jj_20190401_hihijets_004",IV=0xaa3dcf6a7acb92ff4fb08d9b3b3d6f51
		//有加密的
//https://www.jianshu.com/p/1b0adcc7b426
		public static List<String> getList(String url) {
			url = index(url);
			String indexFile = getIndexFile(url);
			// 解析索引文件
			List<String> videoUrlList = analysisIndex(indexFile);
			return videoUrlList;
		}

		public static String index(String urlPath) {
			BufferedReader bufferedReader = null;
			String read = "";
			String lastLine = "";
			try {
				URL url = new URL(urlPath);
				bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
				while ((read = bufferedReader.readLine()) != null) {
					lastLine = read;
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

			if (lastLine.endsWith("m3u8")) {
				urlPath = urlPath.substring(0, urlPath.lastIndexOf("/") + 1) + lastLine;
			}
			return urlPath;
		}

		public static List<String> analysisIndex(String content) {
			Pattern pattern = Pattern.compile(".*ts");
			Matcher ma = pattern.matcher(content);
			List<String> list = new ArrayList<String>();
			while (ma.find()) {
				String s = ma.group();
				list.add(s);
			}
			return list;
		}

		public static String getIndexFile(String urlPath) {
//		url = "https://youku.cdn7-okzy.com/20191126/15946_b6e68816/index.m3u8";
//		url = "https://youku.cdn7-okzy.com/20191126/15946_b6e68816/1000k/hls/index.m3u8";
			BufferedReader bufferedReader = null;
			String content = "";
			String line = "";
			try {
				URL url = new URL(urlPath);
				bufferedReader = new BufferedReader(new InputStreamReader(url.openStream(), "utf-8"));
				while ((line = bufferedReader.readLine()) != null) {
					content += line + "\n";
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
			return content;
		}

	}