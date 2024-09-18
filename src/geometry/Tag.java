package geometry;

import java.util.HashMap;

/**
 * An alternative to a normal layer system, and a broadening of the Revit style category system.
 * Meant to behave something akin to Lisp symbols, being created and referenced by strings but with primitive equality
 */
public class Tag {
	public static int tagCounter = 0;
	public static HashMap<String,Integer> tagLibrary;
	
	public static int TAG_DEFAULT;
	
	public static void initTags() {
		tagLibrary = new HashMap<String,Integer>();
		TAG_DEFAULT = getTag("default");
	}
	
	public static int getTag(String name) {
		Integer out = tagLibrary.get(name);
		if (out == null) {
			out = tagCounter;
			tagLibrary.put(name, out);
			
			tagCounter ++;	
		}
		return out;
	}
}
