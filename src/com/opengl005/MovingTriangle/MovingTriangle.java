package com.opengl005.MovingTriangle;

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

public class MovingTriangle {

	private final int MAX_FPS = 100;
	private final int vbo;
	private FloatBuffer vertexData, newData;
	private final ShaderProgram program;

	public MovingTriangle() {

		try {
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setResizable(false);
			Display.setTitle("Moving Triangle");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		program = new ShaderProgram("../opengl005/MovingTriangle/movingTriangle.vert",
				"../opengl005/MovingTriangle/movingTriangle.frag");

		vbo = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);

		vertexData = (FloatBuffer) BufferUtils.createFloatBuffer(24).put(
				new float[] {
						0f, 0.6f, 0.0f, 1.0f,
						0.6f, -0.6f, 0.0f, 1.0f,
						-0.6f, -0.6f, 0.0f, 1.0f,
						1.0f, 0.0f, 0.0f, 1.0f,
						0.0f, 1.0f, 0.0f, 1.0f,
						0.0f, 0.0f, 1.0f, 1.0f
				}).flip();
		
		newData = BufferUtils.createFloatBuffer(12);

		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertexData, GL15.GL_STREAM_DRAW);

		GL30.glBindVertexArray(GL30.glGenVertexArrays());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);

		resize();
		loop();
	}

	private void resize() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	int xDirection = 1;
	int yDirection = 1;
	float xOffset = 0.1f;
	float yOffset = 0.1f;
	
	// Don't fear the magic numbers
	private void update() {
		
		if((xOffset + vertexData.get(0) + 0.6) > 1 || (xOffset + vertexData.get(0) - 0.6) < -1){
			xDirection = -xDirection; 
		}
		
		if((yOffset + vertexData.get(1)) > 1 || (yOffset + vertexData.get(1) - 1.2) < -1){
			yDirection = -yDirection; 
		}

		xOffset = xOffset + (0.001f * xDirection);
		yOffset =yOffset + (0.002f * yDirection);

		newData.clear();
		for (int a = 0; a < 12; a += 4) {
			newData.put(vertexData.get(a)+xOffset);
			newData.put(vertexData.get(a+1)+yOffset);
			newData.put(vertexData.get(a+2));
			newData.put(vertexData.get(a+3));
		}

		newData.flip();

		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL15.glBufferSubData(GL15.GL_ARRAY_BUFFER, 0, newData);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
	}

	private void loop() {
		while (!Display.isCloseRequested()) {
			update();
			render();
			Display.update();
			Display.sync(MAX_FPS);
		}
	}

	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		program.use();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 48);

		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, vbo);
		program.stopUsing();
	}

	public static void main(String[] args) {
		new MovingTriangle();
	}

}
