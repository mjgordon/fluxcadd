package graphics;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import org.joml.Matrix4f;
import org.lwjgl.opengl.GL33;

/**
 * Helper class for loading and compiling vert+frag linked programs, as well as activation and uniform setting
 */
public class Shader {
	public int id;
	
	@SuppressWarnings("static-access")
	public Shader(String vertexPath, String fragmentPath) {
		int[] shaderCompileStatus = new int[1];

		int glidVertexShader = GL33.glCreateShader(GL33.GL_VERTEX_SHADER);
		String sourceString = loadPlaintextAsString(vertexPath);
		GL33.glShaderSource(glidVertexShader, sourceString);
		GL33.glCompileShader(glidVertexShader);

		GL33.glGetShaderiv(glidVertexShader, GL33.GL_COMPILE_STATUS, shaderCompileStatus);
		if (shaderCompileStatus[0] == GL33.GL_FALSE) {
			System.out.println(GL33.glGetShaderInfoLog(glidVertexShader));
		}

		int glidFragmentShader = GL33.glCreateShader(GL33.GL_FRAGMENT_SHADER);
		String sourceStringFragment = loadPlaintextAsString(fragmentPath);
		GL33.glShaderSource(glidFragmentShader, sourceStringFragment);
		GL33.glCompileShader(glidFragmentShader);

		GL33.glGetShaderiv(glidFragmentShader, GL33.GL_COMPILE_STATUS, shaderCompileStatus);
		if (shaderCompileStatus[0] == GL33.GL_FALSE) {
			System.out.println(GL33.glGetShaderInfoLog(glidFragmentShader));
		}

		id = GL33.glCreateProgram();
		GL33.glAttachShader(id, glidVertexShader);
		GL33.glAttachShader(id, glidFragmentShader);
		GL33.glLinkProgram(id);

		GL33.glGetShaderiv(id, GL33.GL_LINK_STATUS, shaderCompileStatus);
		if (shaderCompileStatus[0] == GL33.GL_FALSE) {
			System.out.println(GL33.glGetShaderInfoLog(id));
		}

		// GL33.glUseProgram(glidShaderProgram);
		GL33.glDeleteShader(glidVertexShader);
		GL33.glDeleteShader(glidFragmentShader);
	}
	
	
	@SuppressWarnings("static-access")
	public void use() {
		GL33.glUseProgram(id);
	}
	
	
	@SuppressWarnings("static-access")
	public void setFloat(String name, float value) {
		int location= GL33.glGetUniformLocation(id,  name);
		GL33.glUniform1f(location, value);
	}
	
	
	@SuppressWarnings("static-access")
	public void setVec2(String name, int x, int y) {
		int location = GL33.glGetUniformLocation(id, name);
		GL33.glUniform2f(location, x, y);
	}
	
	
	@SuppressWarnings("static-access")
	public void setVec3(String name, float x, float y, float z) {
		int location = GL33.glGetUniformLocation(id, name);
		GL33.glUniform3f(location, x, y, z);
	}
	
	
	@SuppressWarnings("static-access")
	public void setMatrix4(String name, Matrix4f m) {
		int location = GL33.glGetUniformLocation(id, name);
		float[] values = new float[16];
		m.get(values);
		GL33.glUniformMatrix4fv(location, false, values);
	}
	
	
	private static String loadPlaintextAsString(String path) {
		StringBuilder output = new StringBuilder();

		BufferedReader br;
		try {
			br = new BufferedReader(new FileReader(path));
			String line = br.readLine();
			while (line != null) {
				output.append(line);
				output.append("\n");
				line = br.readLine();
			}
			br.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return output.toString();
	}
}
