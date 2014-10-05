package com.opengl001.HelloTriangle;

import static org.lwjgl.opengl.GL11.GL_FALSE;
import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;
import static org.lwjgl.opengl.GL20.GL_COMPILE_STATUS;
import static org.lwjgl.opengl.GL20.GL_INFO_LOG_LENGTH;
import static org.lwjgl.opengl.GL20.GL_LINK_STATUS;
import static org.lwjgl.opengl.GL20.glGetProgramInfoLog;
import static org.lwjgl.opengl.GL20.glGetProgrami;
import static org.lwjgl.opengl.GL20.glGetShaderInfoLog;
import static org.lwjgl.opengl.GL20.glGetShaderi;
import static org.lwjgl.opengl.GL20.glLinkProgram;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.FloatBuffer;

import org.lwjgl.BufferUtils;
import org.lwjgl.LWJGLException;
import org.lwjgl.opengl.ContextAttribs;
import org.lwjgl.opengl.Display;
import org.lwjgl.opengl.DisplayMode;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL15;
import org.lwjgl.opengl.GL20;
import org.lwjgl.opengl.GL30;
import org.lwjgl.opengl.PixelFormat;

public class HelloTriangle {

	final int MAX_FPS = 100;
	final int vbo;
	final int program;

	public HelloTriangle() {
		// Setup the window.
		try {
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setVSyncEnabled(true);
			Display.setTitle("Hello Triangle");
			Display.setResizable(false);
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		// Initialize
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 0f);

		int vs = GL20.glCreateShader(GL20.GL_VERTEX_SHADER);
		GL20.glShaderSource(vs, readFromFile("helloTriangle.vert"));
		GL20.glCompileShader(vs);

		int fs = GL20.glCreateShader(GL20.GL_FRAGMENT_SHADER);
		GL20.glShaderSource(fs, readFromFile("helloTriangle.frag"));
		GL20.glCompileShader(fs);

		if (glGetShaderi(vs, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failure in compiling vertex shader. Error log:\n"
					+ glGetShaderInfoLog(vs, glGetShaderi(vs, GL_INFO_LOG_LENGTH)));
			System.exit(0);
		}

		if (glGetShaderi(fs, GL_COMPILE_STATUS) == GL_FALSE) {
			System.err.println("Failure in compiling fragment shader. Error log:\n"
					+ glGetShaderInfoLog(fs, glGetShaderi(fs, GL_INFO_LOG_LENGTH)));
			destroy();
		}

		program = GL20.glCreateProgram();
		GL20.glAttachShader(program, vs);
		GL20.glAttachShader(program, fs);

		glLinkProgram(program);

		if (glGetProgrami(program, GL_LINK_STATUS) == GL_FALSE) {
			System.err.println("Failure in linking program. Error log:\n"
					+ glGetProgramInfoLog(program, glGetProgrami(program, GL_INFO_LOG_LENGTH)));
			destroy();
		}

		GL20.glAttachShader(program, vs);
		GL20.glAttachShader(program, fs);
		GL20.glLinkProgram(program);
		GL20.glDetachShader(program, vs);
		GL20.glDetachShader(program, fs);

		// What does dis d0?
		// GL20.glBindAttribLocation(program, 0, "position");

		GL20.glDeleteShader(vs);
		GL20.glDeleteShader(fs);

		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(24).put(
				new float[] {
						0.0f, 0.6f, 0.0f, 1.0f,
						0.6f, -0.6f, 0.0f, 1.0f,
						-0.6f, -0.6f, 0.0f, 1.0f }).flip(), GL_STATIC_DRAW);

		GL30.glBindVertexArray(GL30.glGenVertexArrays());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		resized();
		loop();
	}

	private void loop() {
		try {
			while (!Display.isCloseRequested()) {
				render();
				Display.update();
				Display.sync(MAX_FPS);
			}
		} catch (Throwable exc) {
			exc.printStackTrace();
		} finally {
			destroy();
		}
	}

	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		GL20.glUseProgram(program);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL20.glUseProgram(0);
	}

	private void destroy() {
		Display.destroy();
		System.exit(0);
	}

	private void resized() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static void main(String[] args) {
		new HelloTriangle();
	}

	private String readFromFile(String str) {
		try {
			InputStream input = getClass().getResourceAsStream(str);
			try (BufferedReader reader = new BufferedReader(new InputStreamReader(input, "UTF-8"))) {
				StringBuilder s = new StringBuilder();
				String l;
				while ((l = reader.readLine()) != null)
					s.append(l).append('\n');
				return s.toString();
			} catch (Exception exc) {
				throw new RuntimeException("Failure reading input stream", exc);
			}
		} catch (Exception exc) {
			throw new RuntimeException("Failure reading file " + str, exc);
		}
	}

}
