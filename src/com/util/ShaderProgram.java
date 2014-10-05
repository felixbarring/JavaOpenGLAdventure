package com.util;

import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;

import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL20;

/**
 * 
 * @author Felix Bärring
 */

/**
 * A class that responsible for the interaction with a program.
 * Mainly creating and activating/deactivating.
 */
public class ShaderProgram {

	final int PROGRAM_ID;
	static boolean debugg = true;

	/**
	 * Creates a program consisting of two shader. 
	 * 
	 * @param vertexShaderID The id for a compiled vertex shader.
	 * @param fragmentShaderID The id for a compiled fragment shader.
	 */
	public ShaderProgram(String vertexShader, String fragmentShader) {

		final int vertexShaderID = createVertexShader(FileUtil.instance.readFromFile(vertexShader));
		final int fragmentShaderID = createFragmentShader(FileUtil.instance.readFromFile(fragmentShader));

		PROGRAM_ID = GL20.glCreateProgram();
		if (debugg && PROGRAM_ID == 0) {
			System.out.println("Error, could not create program!");
		}

		GL20.glAttachShader(PROGRAM_ID, vertexShaderID);
		GL20.glAttachShader(PROGRAM_ID, fragmentShaderID);
		GL20.glLinkProgram(PROGRAM_ID);

		int linkStatus = GL20.glGetProgrami(PROGRAM_ID, GL20.GL_LINK_STATUS);
		if (debugg && linkStatus == GL11.GL_FALSE) {
			System.out.println("Error, bad linkt status!");
			System.err.println("Failure in linking program. Error log:\n"
					+ glGetProgramInfoLog(PROGRAM_ID, glGetProgrami(PROGRAM_ID, GL_INFO_LOG_LENGTH)));
			GL20.glDeleteProgram(PROGRAM_ID);
		}

		GL20.glDeleteProgram(vertexShaderID);
		GL20.glDeleteProgram(fragmentShaderID);

	}

	/**
	 * Compiles and returns the ID of a VertexShader.
	 * 
	 * @param str - The path to the source file for the vertex shader.
	 * @return The id for the created shader, 0 means that an error occurred.
	 */
	private static int createVertexShader(String str) {
		return compileShader(GL20.GL_VERTEX_SHADER, str);
	}

	/**
	 * Compiles and returns the ID of a FragmentShader.
	 * 
	 * @param str - The path to the source file for the fragment shader.
	 * @return The id for the created shader, 0 means that an error occurred.
	 */
	private static int createFragmentShader(String str) {
		return compileShader(GL20.GL_FRAGMENT_SHADER, str);
	}

	/**
	 * Compiles the shader of the given type and returns its ID.
	 * 
	 * @param shaderType - The type of the shader: GL_VERTEX_SHADER or GL_FRAGMENT_SHADER
	 * @param str - The path to the source file for the shader
	 * @return The id for the created shader, 0 means that an error occurred.
	 */
	private static int compileShader(int shaderType, String str) {
		final int SHADER_ID = GL20.glCreateShader(shaderType);

		if (debugg && SHADER_ID == 0) {
			System.out.println("Error, could not create shader!");
			return 0;
		}

		GL20.glShaderSource(SHADER_ID, str);
		GL20.glCompileShader(SHADER_ID);

		int compileStatus = GL20.glGetShaderi(SHADER_ID, GL20.GL_COMPILE_STATUS);
		if (debugg && compileStatus == GL11.GL_FALSE) {
			System.out.println("Error, bad compile status!");
			System.err.println("Failure in compiling vertex shader. Error log:\n"
					+ glGetShaderInfoLog(SHADER_ID, glGetShaderi(SHADER_ID, GL_INFO_LOG_LENGTH)));
			return 0;
		}
		return SHADER_ID;
	}

	/**
	 * Use this program.
	 */
	public void use() {
		GL20.glUseProgram(PROGRAM_ID);
	}

	/**
	 * Stop using this program.
	 */
	public void stopUsing() {
		GL20.glUseProgram(0);
	}

	/**
	 * 
	 */
	// TODO
	public void dispose() {

	}

}
