package scheme;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.ArrayList;

import java.time.LocalDateTime;

import console.Console;

/**
 * Contains the original plaintext scheme source of geometry, and manages the auto-updating after external changes
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
	
	public String filepath;
	
	private long timeLastLoad;

	public SourceFile(String filePath) {
		this.filepath = filePath;
		reload();
	}

	public void reload() {
		Console.log("Loading source file : " + filepath);
		StringBuilder sb = new StringBuilder();
		lines = new ArrayList<String>();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(filepath));
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
		
		timeLastLoad = System.currentTimeMillis();
	}
	
	public boolean updateable() {
		File file = new File(filepath);
		long timeModified = file.lastModified();
		
		return timeModified > timeLastLoad;
	}

}
