package iofile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Loads various file formats based on plain text
 *
 */
public class Plaintext {
	/**
	 * Returns an array of Strings corresponding to each line of a text file
	 * @param path the path to the file
	 * @return
	 */
	public static String[] loadPlaintext(String path) {
		ArrayList<String> output = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				output.add(line);
				line = br.readLine();
			}
			br.close();
		} catch (Exception e) {
			System.out.println(e);
			return(null);
		}
		
		
		return(output.toArray(new String[output.size()]));
	}
	
	
	/**
	 * Returns a dictionary of Strings from a text file, wherein each line is one entry and the first space character splitting the key and value
	 * @param path the path to the file
	 * @return
	 */
	public static HashMap<String,String> loadKVSimple(String path) {
		String[] input = loadPlaintext(path);
		
		HashMap<String,String> output = new HashMap<String,String>();
		
		for (String s : input) {
			if (s.equals("")) {
				continue;
			}
			int firstSpace = s.indexOf(' ');
			String key = s.substring(0,firstSpace);
			String value = s.substring(firstSpace + 1);
			
			output.put(key, value);
		}
		
		return(output);
	}
}
