package iofile;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

public class Plaintext {
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
}
