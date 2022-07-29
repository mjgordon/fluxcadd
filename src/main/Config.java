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
	
	

}
