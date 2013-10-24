package data;

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
	LispList lispData;
	
	public Parser(String sourcePath) {
		source = new SourceFile(sourcePath);
		parseSource();
		//parseData();
	}
	
	public void parseSource() {
		String s = source.fullFile;
		LispData currentData = new LispList(null);
		for (char c : s.toCharArray()) {
			currentData.receiveChar(c);
		}
	}
}
