package application.utils;

import java.io.IOException;

public class Registry {

	/***
	 *
	 * @description reg add [keyname] [/v | /ve] [/t] [/d] [/f]
	 * @param keyname HKEY_CURRENT_USER\Software\Google\Chrome\NativeMessagingHosts\chromem3u8
	 * @param /ve     所选项之下要添加或删除的值名为空值，默认项
	 * @param /v      所选项之下要添加或删除的值名，指定名称
	 * @param /t      RegKey 数据类型（reg_sz字符串）
	 * @param /d      要分配给添加的注册表v的数据(D:\xxx\chromem3u8.json)
	 * @param /f      不用提示就强行删除
	 */
	public static void regAddWindows() throws IOException {
		String regKey = "HKEY_CURRENT_USER\\Software\\Google\\Chrome\\NativeMessagingHosts\\testchromem3u8";
		String jsonPath = "\"D:\\m3u8\\chromem3u8.json\"";
		// ve默认项
		Runtime.getRuntime().exec("reg " + "add " + regKey + " /ve" + (" /t reg_sz /d " + jsonPath + " /f"));
//        Runtime.getRuntime().exec("reg "+"add "+regKey+" /v "+"NAME"+(" /t reg_sz /d "+jsonPath+" /f"));
	}

	public static void main(String[] args) throws IOException {
		regAddWindows();

	}
}
