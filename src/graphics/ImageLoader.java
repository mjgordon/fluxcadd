package graphics;

import static org.lwjgl.system.MemoryUtil.memSlice;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.IntBuffer;
import java.nio.channels.SeekableByteChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import org.lwjgl.BufferUtils;
import org.lwjgl.stb.STBImage;
import org.lwjgl.system.MemoryStack;


public class ImageLoader {
	
	public ByteBuffer buffer;
	
	public int width;
	public int height;
	
	public ImageLoader(String filepath) {
		ByteBuffer imageBuffer;
		
		try {
			imageBuffer = readByteBuffer(filepath);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		int icomp;

		try (MemoryStack stack = MemoryStack.stackPush()) {
            IntBuffer w    = stack.mallocInt(1);
            IntBuffer h    = stack.mallocInt(1);
            IntBuffer comp = stack.mallocInt(1);

            // Use info to read image metadata without decoding the entire image.
            // We don't need this for this demo, just testing the API.
            if (!STBImage.stbi_info_from_memory(imageBuffer, w, h, comp)) {
         
                throw new RuntimeException("Failed to read image information: " + STBImage.stbi_failure_reason());
            }

            System.out.println("Image width: " + w.get(0));
            System.out.println("Image height: " + h.get(0));
            System.out.println("Image components: " + comp.get(0));
            System.out.println("Image HDR: " + STBImage.stbi_is_hdr_from_memory(imageBuffer));

            // Decode the image
            buffer = STBImage.stbi_load_from_memory(imageBuffer, w, h, comp, 0);
            if (buffer == null) {
                throw new RuntimeException("Failed to load image: " + STBImage.stbi_failure_reason());
            }

            width = w.get(0);
            height = h.get(0);
            icomp = comp.get(0);
        }
	}
	
	
	public static ByteBuffer readByteBuffer(String resource) throws IOException {
		ByteBuffer buffer;

		Path path = Paths.get(resource);
		try (SeekableByteChannel fc = Files.newByteChannel(path)) {
			buffer = BufferUtils.createByteBuffer((int) fc.size() + 1);
			while (fc.read(buffer) != -1) {}
		}

		buffer.flip();
		return memSlice(buffer);
	}
}
