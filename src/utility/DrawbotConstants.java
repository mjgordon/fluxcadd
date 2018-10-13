package utility;

public final class DrawbotConstants {
	public static final byte DB_PROGRAM_START = 0x00;
	public static final byte DB_PROGRAM_END = 0x01;
	public static final byte DB_PROGRAM_PAUSE = 0x02;
	public static final byte DB_PROGRAM_WAIT = 0x03;
	
	public static final byte DB_GOTO_HOME = 0x10;
	public static final byte DB_GOTO_POSITION = 0x11;
	public static final byte DB_GOTO_ROTATION = 0x12;
	
	public static final byte DB_PEN_DOWN = 0x20;
	public static final byte DB_PEN_UP = 0x21;
	
	public static final byte DB_RESPONSE_READY = (byte)0xA0;
	public static final byte DB_RESPONSE_BAD_COMMAND = (byte)0xA1;
	public static final byte DB_RESPONSE_BAD_POSITION = (byte)0xA2;
	
	public static final byte[] EMPTY_DATA = new byte[0]; 
}
