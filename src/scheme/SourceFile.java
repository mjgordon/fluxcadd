package scheme;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;

import console.Console;

/**
 * Contains the original plaintext scheme source of geometry. This class
 * interfaces with the Content_Editor class for in-program editing. In addition
 * to holding the String-array representation of the file, this class also
 * handles syntax-highlighting, basic parsing, and keeping up with external
 * editing. ... this may get deleted soon
 */
public class SourceFile {

	/**
	 * An list of strings, each string containing a single line of the source file.
	 */
	public ArrayList<String> lines;
	
	/**
	 * A single string containing the full text of the file. Contains no linebreak
	 * characters?
	 */
	public String fullFile;

	/**
	 * An array of the character ids within the fullfile string wherein each new
	 * line starts.
	 */
	private ArrayList<Integer> lineStarts;
	
	private String filePath;

	public SourceFile(String filePath) {
		this.filePath = filePath;
		reload();
	}

	public void reload() {
		Console.log("Loading source file : " + filePath);
		StringBuilder sb = new StringBuilder();
		lines = new ArrayList<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filePath));
			String line = br.readLine();

			while (line != null) {
				lines.add(line);
				sb.append(line);
				line = br.readLine();
			}
			fullFile = sb.toString();
			br.close();
		} catch (Exception e) {
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
