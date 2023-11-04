package application.utils;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

import application.component.task.MergeTask;
import application.dto.EXTINF;
import application.testexample.Multi;

public class FFMPEG {

	/**
	 * @param command
	 * @throws Exception
	 */
	private static void process(List<String> command) {
		try {
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable inputRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getInputStream()));
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

	/**
	 * @command ffmpeg -f concat -safe 0 -i ts.txt -c copy out.mp4
	 * @param tsfilepath
	 * @param out
	 * @return
	 */
	private static List<String> getTSCommand(String tsfilepath, String out) {
		List<String> command = new ArrayList<>();
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

	/**
	 * @command ffmpeg -allowed_extensions ALL -i cindex.m3u8 -c copy new.mp4
	 * @info
	 *       <ul>
	 *       <li>ffmpeg -allowed_extensions ALL -i cindex.m3u8 -c copy new.mp4</li>
	 *       <li>1.当使用本地的ts和key文件时，m3u8的路径格式是： 1、不需要加file:///前缀，直接用路径就可以</li>
	 *       <li>2.key文件必须是左斜杠/ , ts文件用左斜杠 / 或者 右斜杠 \ 都可以</li>
	 *       <li>http://www.520dd.top/?m=vod-play-id-16086-src-1-num-1.html</li>
	 *       <li>https://video.lllwo2o.com:8091/20180615/DWT6HJU129/index.m3u8</li>
	 *       <li>#EXTM3U</li>
	 *       <li>#EXT-X-STREAM-INF:PROGRAM-ID=1,BANDWIDTH=650000,RESOLUTION=720x480</li>
	 *       <li>650kb/hls/index.m3u8</li>
	 *       <li>https://video.lllwo2o.com:8091/20180615/DWT6HJU129/650kb/hls/index.m3u8</li>
	 *       <li>#EXTM3U #EXT-X-VERSION:3 #EXT-X-TARGETDURATION:2</li>
	 *       <li>#EXT-X-MEDIA-SEQUENCE:0</li>
	 *       <li>#EXT-X-KEY:METHOD=AES-128,URI="key.key"</li>
	 *       <li>#EXTINF:1.668333, opiMscP5470000.ts</li>
	 *       <li>https://video.lllwo2o.com:8091/20180615/DWT6HJU129/650kb/hls/key.key</li>
	 *       <li>782e53aad9e88999</li>
	 *       </ul>
	 * @return
	 */
	public static List<String> getCIndexCommand(String cindex, String out) {
		List<String> command = new ArrayList<>();
		command.add("ffmpeg");
		command.add("-allowed_extensions");
		command.add("ALL");
		command.add("-i");
//		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\cindex.m3u8");
		command.add(cindex);
		command.add("-c");
		command.add("copy");
//		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\out.mp4");
		command.add(out);
		return command;
	}

	public static boolean isEncrypted(String dir) {
		File file = new File(dir + File.separator + "cindex.m3u8");
		if (file.exists()) {
			return true;
		}
		return false;

	}

	public static void processMerge(List<String> command, MergeTask task) {

		try {
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable inputRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getInputStream()));
					try {
						// 将进程的输出流封装成缓冲读者对象

						// 对缓冲读者对象进行每行循环
						for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
							if (line.indexOf("size=") > -1) {
								String substring = line.substring(line.indexOf("size=") + 5, line.indexOf("time="));
								substring = substring.trim();
								substring = substring.substring(0, substring.length() - 2);
								task.updateFileSize(substring);
							}
						}
						// 通知完成
						task.done();
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

	public static void merge(String dir, MergeTask task) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		List<String> command;
		if (isEncrypted(dir)) {
			command = getCIndexCommand(dir + File.separator + "cindex.m3u8", dir + File.separator + date + ".mp4");
		} else {
			command = getTSCommand(dir + File.separator + Constants.TS_TXT, dir + File.separator + date + ".mp4");
		}
		processMerge(command, task);
	}

	public static void merge(String dir) {
		String date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
		List<String> command;
		if (isEncrypted(dir)) {
			command = getCIndexCommand(dir + File.separator + "cindex.m3u8", dir + File.separator + date + ".mp4");
		} else {
			command = getTSCommand(dir + File.separator + Constants.TS_TXT, dir + File.separator + date + ".mp4");
		}
		try {
			Process videoProcess = new ProcessBuilder(command).redirectErrorStream(true).start();
			Runnable inputRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getInputStream()));
					try {
						// 将进程的输出流封装成缓冲读者对象

						// 对缓冲读者对象进行每行循环
						for (String line = bufferedReader.readLine(); line != null; line = bufferedReader.readLine()) {
							if (line.indexOf("size=") > -1) {
								String substring = line.substring(line.indexOf("size=") + 5, line.indexOf("time="));
								substring = substring.trim();
								substring = substring.substring(0, substring.length() - 2);
								System.out.println(substring);
							}
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
			Runnable errorRunnable = new Runnable() {

				@Override
				public void run() {
					BufferedReader bufferedReader = new BufferedReader(
							new InputStreamReader(videoProcess.getErrorStream()));
					String line = "";
					try {
						while ((line = bufferedReader.readLine()) != null) {
							System.out.println(line);

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
		// ffmpeg -f concat -safe 0 -i cindex.m3u8 -c copy output.mp4
		command.add("ffmpeg");
		command.add("-allowed_extensions");
		command.add("ALL");
		command.add("-i");
		command.add("C:\\Users\\zhouyu\\Desktop\\lxzc\\20211031231351137\\cindex.m3u8");
		command.add("-c");
		command.add("copy");
		command.add("C:\\Users\\zhouyu\\Desktop\\lxzc\\20211031231351137\\out.mp4");
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
					out.println(line.replace(line, "C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\encrypted\\" + i + Constants.UNDERLINE + line)); // 替换abc成def
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

	public static void thumb() {
//	String ssString = "ffmpeg -i input.flv -vf fps=1 out%d.png";
		List<String> command = new ArrayList<>();
		command.add("ffmpeg");
		command.add("-i");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\aj\\test\\out.mp4");
		command.add("-s 350x240");
		command.add("-vf");
		command.add("fps=1");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\aj\\test\\" + "out%06d.png");
		process(command);
	}

	public static void framesthumb() {
//	ffmpeg -i input.flv -ss 00:00:14.435 -vframes 1 out.png
		List<String> command = new ArrayList<>();
		command.add("ffmpeg");
		command.add("-i");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\aj\\test\\out.mp4");
		command.add("-y");
		command.add("-s");
		command.add("350x240");
		command.add("-f");
		command.add("image2");
		command.add("-ss");
		command.add("30");
		command.add("-vframes");
		command.add("1");
		command.add("C:\\Users\\kyh\\Desktop\\m3u8\\aj\\test\\" + "out.png");
		process(command);

	}

	public static void main(String[] args) throws IOException {
//		framesthumb();

//		change();
//		decryptedMerge();
//		cut();
//		multiDownload("http://cdn-yong.bejingyongjiu.com/20200125/1913_7044abc9/index.m3u8",
//				"C:\\Users\\kyh\\Desktop\\m3u8\\sssszsh\\11");
		String m3u8 = "https://vip.ffzyread1.com/20230714/15271_e7c46fec/index.m3u8";
		m3u8 = "https://video.lllwo2o.com:8091/20180615/DWT6HJU129/index.m3u8";
		String dir = "C:\\Users\\sky\\Desktop\\testm3u8\\png";
		List<EXTINF> ts = M3U8.indexDownload(m3u8, dir);
		Multi.list(ts, dir);
//		merge(dir);
	}

}