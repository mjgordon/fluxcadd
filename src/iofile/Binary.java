package iofile;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;

public class Binary {

	/**
	 * Originally implemented for now-unused NEAT integration, pay attention to
	 * format if using again
	 * 
	 * @param fileName
	 * @param size
	 * @return
	 */
	public static float[] loadBinaryFloats(String fileName, int size) {
		float raw[] = new float[100 * 100 * 100];

		try {
			FileInputStream fstream = new FileInputStream(fileName);
			BufferedInputStream bstream = new BufferedInputStream(fstream);
			DataInputStream dstream = new DataInputStream(bstream);

			float max = 0;
			float min = 0;

			for (int i = 0; i < raw.length; i++) {
				float val = dstream.readFloat();

				// Ugly order switching, there's a better method of loading no doubt
				int bits = Float.floatToRawIntBits(val);
				byte[] bytes = new byte[4];
				bytes[3] = (byte) (bits & 0xff);
				bytes[2] = (byte) ((bits >> 8) & 0xff);
				bytes[1] = (byte) ((bits >> 16) & 0xff);
				bytes[0] = (byte) ((bits >> 24) & 0xff);
				val = ByteBuffer.wrap(bytes).order(ByteOrder.LITTLE_ENDIAN).getFloat();

				raw[i] = val;
				if (val < min) {
					min = val;
				}
				if (val > max) {
					max = val;
				}
			}

			// Implementation specific
			// System.out.println(min);
			// System.out.println(max);

			// SDFNEAT.max = max;
			// SDFNEAT.min = min;

			dstream.close();
		} catch (FileNotFoundException e) {
			System.out.println("File not found exception: " + fileName);
		} catch (IOException e) {
			System.out.println("IOException");
		}

		return raw;
	}

}
