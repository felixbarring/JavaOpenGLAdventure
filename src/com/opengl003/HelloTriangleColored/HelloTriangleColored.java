package com.opengl003.HelloTriangleColored;

import static org.lwjgl.opengl.GL15.GL_STATIC_DRAW;

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

import com.util.ShaderProgram;

public class HelloTriangleColored {

	private final int MAX_FPS = 100;
	private final ShaderProgram program;
	private final int vbo;

	public HelloTriangleColored() {
		try {
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setResizable(false);
			Display.setTitle("Hello Triangle Colored");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		GL11.glClearColor(0.2f, 0.2f, 0.2f, 0);

		program = new ShaderProgram("../opengl003/HelloTriangleColored/helloTriangle.vert",
				"../opengl003/HelloTriangleColored/helloTriangle.frag");

		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, (FloatBuffer) BufferUtils.createFloatBuffer(24).put(
				new float[] { 
						.0f, 0.6f, 0.0f, 1.0f,
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

		program.use();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(0);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

		GL20.glDisableVertexAttribArray(0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		program.stopUsing();
	}

	private void destroy() {
		Display.destroy();
		System.exit(0);
	}

	private void resized() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	public static void main(String[] args) {
		new HelloTriangleColored();
	}


}
