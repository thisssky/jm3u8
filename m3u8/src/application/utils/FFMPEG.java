package application.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

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
							int _ch;
							_ch = videoProcess.getInputStream().read();
							if (_ch == -1) {
								break;
							} else {
//								System.out.print((char) _ch);
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

	public static void main(String[] args) {
		merge("C:\\Users\\kyh\\Desktop\\m3u8\\xxx\\43");
	}
}