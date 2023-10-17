package application.dto;

import java.io.IOException;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

public class History {

	public static Properties history = new Properties();
	static {
		try {
			history.load(History.class.getClassLoader().getResourceAsStream("history.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static void setProperty(String key, String value) {
		history.setProperty(key, value);
	}

	public static void delete(String key) {
		history.remove(key);
	}

	public static void main(String[] args) {
		history.setProperty("key", "value");
		history.setProperty("key", "value1");
		System.out.println(history.size());
		System.out.println(history.getProperty("key"));
		Set<Entry<Object, Object>> entrySet = history.entrySet();
		for (Entry<Object, Object> entry : entrySet) {
			System.out.println(entry.getKey() + ":" + entry.getValue());

		}
	}
}
