package scheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

/**
 * Contains the original plaintext scheme source of geometry. This class
 * interfaces with the Content_Editor class for in-program editing. In addition
 * to holding the String-array representation of the file, this class also
 * handles syntax-highlighting, basic parsing, and keeping up with external editing. 
 * ...
 * this may get deleted soon
 */
public class SourceFile {
	
	/**
	 * An list of strings, each string containing a single line of the source file. 
	 */
	ArrayList<String> lines;
	
	/**
	 * A single string containing the full text of the file. Contains no
	 * linebreak characters?
	 */
	String fullFile;
	
	/**
	 * An array of the character ids within the fullfile string wherein each
	 * new line starts.
	 */
	ArrayList<Integer> lineStarts;
	
	public SourceFile(String path) {
		StringBuilder sb = new StringBuilder();
		lines = new ArrayList<String>();
		
		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
	        String line = br.readLine();
	        
	        while (line != null) {
	        	lines.add(line);
	        	sb.append(line);
	            line = br.readLine();
	        }
	        fullFile = sb.toString();
	        br.close();
		} catch(Exception e) {
			System.out.println(e);
		}
		
		lineStarts = new ArrayList<Integer>();
		int count = 0;
		lineStarts.add(count);
		for (String line : lines) {
			count += line.length();
			lineStarts.add(count);
		}
	}
	

}
