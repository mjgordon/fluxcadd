package main;

import java.util.HashMap;

import iofile.Plaintext;

public class Config {

	private static HashMap<String,String> config;
	
	public static void loadTextFile(String path) {
		Config.config = Plaintext.loadKVSimple(path);
	}
	
	public static int getInt(String key) {
		return Integer.valueOf(config.get(key));
	}
	
	public static int getInt(String key, int radix) {
		return Integer.valueOf(config.get(key),radix);
	}
	
	public static boolean getFlag(String key) {
		return config.get(key) != null;
	}
	
	public static void setFlag(String key, boolean value) {
		config.put(key, value ? "true" : null);
	}
	
	public static boolean toggleFlag(String key) {
		boolean flag = config.get(key) != null;
		flag = !flag;
		setFlag(key,flag);
		return flag;
	}
	
	
	
	

}
