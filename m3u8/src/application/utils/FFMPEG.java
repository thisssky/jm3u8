package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;

import application.component.FileSizeTask;

public class FFMPEG {

	/**
	 * @param command
	 * @throws Exception
	 */
	private static boolean process(List<String> command) {
		try {
			if (null == command || command.size() == 0) {
				return false;
			}
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable target = new Runnable() {

				@Override
				public void run() {
					try {
						while (this != null) {
							while (this != null) {
								int _ch = videoProcess.getInputStream().read();
								if (_ch == -1) {
									break;
								} else {
//									System.out.print((char) _ch);
								}
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			};
			Runnable target2 = new Runnable() {

				@Override
				public void run() {
					try {
						while (this != null) {
							int _ch;
							_ch = videoProcess.getErrorStream().read();
							if (_ch == -1) {
								break;
							} else {
								System.err.print((char) _ch);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			};
			new Thread(target).start();
			new Thread(target2).start();
			int exitcode = videoProcess.waitFor();
			if (exitcode == 1) {
				return false;
			}
			return true;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return false;
	}

	/**
	 * 
	 * @Title: readInputStream @Description: 完成进度百分比 @param @return String @throws
	 */
	private static String readInputStream(InputStream is, String f) throws IOException {
		// 将进程的输出流封装成缓冲读者对象
		BufferedReader br = new BufferedReader(new InputStreamReader(is));
		StringBuffer lines = new StringBuffer();// 构造一个可变字符串
		long totalTime = 0;

		// 对缓冲读者对象进行每行循环
		for (String line = br.readLine(); line != null; line = br.readLine()) {
			lines.append(line);// 将每行信息字符串添加到可变字符串中
			int positionDuration = line.indexOf("Duration:");// 在当前行中找到第一个"Duration:"的位置
			int positionTime = line.indexOf("time=");
			System.out.println(line + "\n");
//			if (positionDuration > 0) {// 如果当前行中有"Duration:"
//				String dur = line.replace("Duration:", "");// 将当前行中"Duration:"替换为""
//				dur = dur.trim().substring(0, 8);// 将替换后的字符串去掉首尾空格后截取前8个字符
//				int h = Integer.parseInt(dur.substring(0, 2));// 封装成小时
//				int m = Integer.parseInt(dur.substring(3, 5));// 封装成分钟
//				int s = Integer.parseInt(dur.substring(6, 8));// 封装成秒
//				totalTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
//			}
//			int COMPLETE = 0;
//			if (positionTime > 0) {// 如果所用时间字符串存在
//				// 截取包含time=的当前所用时间字符串
//				String time = line.substring(positionTime, line.indexOf("bitrate") - 1);
//				time = time.substring(time.indexOf("=") + 1, time.indexOf("."));// 截取当前所用时间字符串
//				int h = Integer.parseInt(time.substring(0, 2));// 封装成小时
//				int m = Integer.parseInt(time.substring(3, 5));// 封装成分钟
//				int s = Integer.parseInt(time.substring(6, 8));// 封装成秒
//				long hasTime = h * 3600 + m * 60 + s;// 得到总共的时间秒数
//				float t = (float) hasTime / (float) totalTime;// 计算所用时间与总共需要时间的比例
//				COMPLETE = (int) Math.ceil(t * 100);// 计算完成进度百分比
//			}
//			System.out.println("完成：" + COMPLETE + "%");
		}
		br.close();// 关闭进程的输出流
		return lines.toString();
	}

	private static List<String> getFfmpegCommand(String tsfilepath, String out) {
		List<String> command = new ArrayList<>();
		// ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
		command.add("ffmpeg");
		command.add("-f");
		command.add("concat");
		command.add("-safe");
		command.add("0");
		command.add("-i");
		command.add(tsfilepath);
		command.add("-c");
		command.add("copy");
		command.add(out);
		return command;
	}

	public static void merge(String dir) {
//		String tsfilepath = tsfile(dir);
		List<String> command = getFfmpegCommand(dir + File.separator + "tsFile.txt", dir + File.separator + "out.mp4");
		process(command);
	}

	public static void merge(String dir, FileSizeTask task) {
		List<String> command = getFfmpegCommand(dir + File.separator + "tsFile.txt", dir + File.separator + "out.mp4");

		try {
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable target = new Runnable() {

				@Override
				public void run() {
					BufferedReader br = null;
					try {
						// 将进程的输出流封装成缓冲读者对象
						br = new BufferedReader(new InputStreamReader(videoProcess.getInputStream()));
						StringBuffer lines = new StringBuffer();// 构造一个可变字符串

						// 对缓冲读者对象进行每行循环
						for (String line = br.readLine(); line != null; line = br.readLine()) {
							lines.append(line);// 将每行信息字符串添加到可变字符串中
							if (line.indexOf("size=") > -1) {
								String substring = line.substring(line.indexOf("size=") + 5, line.indexOf("time="));
								substring = substring.trim();
								substring = substring.substring(0, substring.length() - 2);
								long l = Long.valueOf(substring);
								if (l / 1024 < 1) {
									substring = l + "KB";
								} else if (l / 1024 > 1 && l / (1024 * 1024) < 1) {
									substring = (l / 1024) + "MB";
								} else if (l / (1024 * 1024) > 1 && l / (1024 * 1024 * 1024) < 1) {
									substring = l / (1024 * 1024) + "." + (l / 1024) + "GB";
								}
								task.updateFileSize(substring);
							}
						}
						// 通知完成
						task.done();
						br.close();// 关闭进程的输出流
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						try {
							br.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
					}

				}
			};
			Runnable target2 = new Runnable() {

				@Override
				public void run() {
					try {
						while (this != null) {
							int read = videoProcess.getErrorStream().read();
							if (read == -1) {
								break;
							} else {
//								System.err.print((char) _ch);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}

				}
			};
			new Thread(target).start();
			new Thread(target2).start();
			videoProcess.waitFor();
			videoProcess.destroy();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void multiDownload(String m3u8, String dir) {
//		普通降低分辨率，码率的命令：
//		 ffmpeg -y -i in.mp4 -s 320x240 -b 290000 out290.mp4
//		带thread参数的命令：    
//		ffmpeg -y -threads 2 -i in.mp4 -s 320x240 -b 290000 out290.mp4

		// 下载
//		-threads count      thread count 
//		 ffmpeg -i http://...m3u8 -c copy out.mkv 将视频流下载并合并成 out.mkv。
		List<String> command = new ArrayList<>();
		// ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
		command.add("ffmpeg");
		command.add("-i");
		command.add(m3u8);
		command.add("-y");
//		command.add("-thread");
//		command.add("4");
		command.add("-c");
		command.add("copy");
		command.add(dir + File.separator + "out.mp4");
		process(command);
	}

	public static void cut() {
		// 很有用
//https://blog.csdn.net/Yao_2333/article/details/82910560
		String m3u8;
		String dir;
//		ffmpeg	-y -i D:\aa\bb\test.mp4 
//		-hls_time 6
//		-hls_playlist_type vod 
//		-hls_segment_filename "D:\aa\bb\file%d" D:\aa\bb\playlist.m3u8

		List<String> command = new ArrayList<>();
		// ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
		command.add("ffmpeg");
		command.add("-y");
		command.add("-i");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\in.mp4");
		command.add("-hls_time");
		command.add("10");
		command.add("-hls_playlist_type");
		command.add("vod");
		command.add("-hls_segment_filename");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\cut%05d.ts");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\index.m3u8");
		process(command);
	}

	public static void encryCut() {
		// 很有用
//https://blog.csdn.net/Yao_2333/article/details/82910560
		String m3u8;
		String dir;
//		ffmpeg	-y -i D:\aa\bb\test.mp4 
//		-hls_time 6
//		-hls_playlist_type vod 
//		-hls_segment_filename "D:\aa\bb\file%d" D:\aa\bb\playlist.m3u8

		List<String> command = new ArrayList<>();
		// ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
		command.add("ffmpeg");
		command.add("-y");
		command.add("-i");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\in.mp4");
		command.add("-hls_time");
		command.add("10");
		command.add("-hls_playlist_type");
		command.add("vod");
		command.add("");
		command.add("");
		command.add("-hls_segment_filename");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\cut%05d.ts");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\test\\index.m3u8");
		process(command);
	}

//	https://blog.csdn.net/guanxiao1989/article/details/90529865
//	https://blog.csdn.net/cquptvlry/article/details/94185316
	public static void decryptedMerge() {
//		ffmpeg -allowed_extensions ALL -i HdNz1kaz.m3u8 -c copy new.mp4
//		因此，可以知道，当使用本地的ts和key文件时，m3u8的路径格式是：
//		1、不需要加file:///前缀，直接用路径就可以
//		2、key文件必须是左斜杠/ , ts文件用左斜杠 / 或者 右斜杠 \ 都可以

//		http://www.520dd.top/?m=vod-play-id-16086-src-1-num-1.html
//			https://video.lllwo2o.com:8091/20180615/DWT6HJU129/index.m3u8
//				#EXTM3U
//				#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=650000,RESOLUTION=720x480
//				650kb/hls/index.m3u8
//			https://video.lllwo2o.com:8091/20180615/DWT6HJU129/650kb/hls/index.m3u8
//				#EXTM3U
//				#EXT-X-VERSION:3
//				#EXT-X-TARGETDURATION:2
//				#EXT-X-MEDIA-SEQUENCE:0
//				#EXT-X-KEY:METHOD=AES-128,URI="key.key"
//				#EXTINF:1.668333,
//				opiMscP5470000.ts
//			https://video.lllwo2o.com:8091/20180615/DWT6HJU129/650kb/hls/key.key
//				782e53aad9e88999
		List<String> command = new ArrayList<>();
		// ffmpeg -f concat -safe 0 -i filelist.txt -c copy output.mp4
		command.add("ffmpeg");
		command.add("-allowed_extensions");
		command.add("ALL");
		command.add("-i");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\cindex.m3u8");
		command.add("-c");
		command.add("copy");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\out.mp4");
		process(command);
	}

	public static void change() {

		BufferedReader bufferedReader = null;
		PrintWriter out = null;
		try {
			bufferedReader = new BufferedReader(
					new FileReader(new File("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\index.m3u8")));
			out = new PrintWriter(
					new BufferedWriter(new FileWriter("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\cindex.m3u8")));
			String line;
			int i = 0;
			while ((line = bufferedReader.readLine()) != null) {
				if (line.endsWith(".ts")) {
//					String pre = line.substring(0, line.lastIndexOf("\\") + 1);
//					String substring = line.substring(line.lastIndexOf("\\") + 1);
					out.println(line.replace(line, "C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\" + i + "-" + line)); // 替换abc成def
					i++;
				} else {
					out.println(line);
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {

			try {
				bufferedReader.close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			out.close();
		}
	}

	public static void main(String[] args) {

//		merge("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted");
		merge("C:\\Users\\kyh\\Desktop\\m3u8\\zhentan\\02s\\08");

//		change();
//		decryptedMerge();
//		cut();
//		multiDownload("http://cdn-yong.bejingyongjiu.com/20200125/1913_7044abc9/index.m3u8",
//				"C:\\Users\\kyh\\Desktop\\m3u8\\sssszsh\\11");
	}

}