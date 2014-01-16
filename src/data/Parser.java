package data;

import java.util.ArrayList;

public class Parser {
	/**
	 * The linked sourcefile for this set of geometry.
	 * Will eventually need to add support for multiple and linked together
	 * source files.
	 */
	SourceFile source;
	
	/**
	 * Geometry file updated alongside the current source file. 
	 */
	public GeometryFile geometry;
	
	/**
	 * A list of each top-level LispData object. Generated from the first 
	 * parsing of the source. It is then run through to create the geometry.
	 */
	public ArrayList<LispData> root;
	
	public Parser(String sourcePath) {
		source = new SourceFile(sourcePath);
		root = new ArrayList<LispData>();
		parseSource();
		//parseData();
	}
	
	public void parseSource() {
		String s = source.fullFile;

		boolean endFlag = true;
		for (char c : s.toCharArray()) {
			if (endFlag) {
				root.add(new LispData());
				endFlag = false;
			}
			if (root.get(root.size()-1).receiveChar(c)) {
				endFlag = true;
			}
		}
		System.out.println(root.size());
	}
}
