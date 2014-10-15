package com.opengl007.MovingRectangle;

import java.nio.FloatBuffer;
import java.nio.ShortBuffer;

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

public class MovingRectangle {

	private final int MAX_FPS = 100;
	private final ShaderProgram PROGRAM;
	private final int VAO, VBO, EBO;

	private final int OFFSET_LOCATION;

	public MovingRectangle() {

		try {
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setResizable(false);
			Display.setTitle("Moving Rectangle");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		PROGRAM = new ShaderProgram("../opengl007/MovingRectangle/movingRectangle.vert",
				"../opengl007/MovingRectangle/movingRectangle.frag");

		Display.setTitle("Moving Rectangle");

		// Vertices for our quad
		FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(24).put(
				new float[]	{
						-0.5f, +0.5f, // Top Left: ID 0
						+0.5f, +0.5f, // Top Right: ID 1
						-0.5f, -0.5f, // Bottom Left: ID 2
						+0.5f, -0.5f, // Bottom Right: ID 3

						1, 0, 0, 1,
						0, 1, 0, 1,
						0, 0, 1, 1,
						1, 1, 1, 1
				}).flip();

		// Elements for our quad
		ShortBuffer elements = (ShortBuffer)BufferUtils.createShortBuffer(4).put(new short[]{
				0, 1, 2, 3
		}).flip();

		// Create and bind the VAO
		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		// Create a VBO for vertices
		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Create an EBO for indexed drawing
		EBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elements, GL15.GL_STATIC_DRAW);

		// Unbind the VAO
		GL30.glBindVertexArray(0);
		OFFSET_LOCATION = GL20.glGetUniformLocation(PROGRAM.getId(), "offset");

		resize();
		loop();
	}

	private void resize() {
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}

	private void loop() {
		while (!Display.isCloseRequested()) {
			update();
			render();
			Display.update();
			Display.sync(MAX_FPS);
		}
	}
	
	private float xOff = 0.0f;
	private float yOff = 0.0f;
	private float xSpeed = 0.0153f;
	private float ySpeed = 0.01f;
	private int xDirection = 1;
	private int yDirection = 1;
	private void update(){
		xOff = xOff + (xSpeed * xDirection);
		if(xOff > 0.5f || xOff < -0.5f) {
			xDirection = -xDirection;
		}
		yOff = yOff + (ySpeed * yDirection);
		if(yOff > 0.5f || yOff < -0.5f){
			yDirection = -yDirection;
		}
	}

	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		PROGRAM.use();
		
		GL20.glUniform2f(OFFSET_LOCATION, xOff, yOff);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL30.glBindVertexArray(VAO);
		
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 32);

		// Draw the elements
		GL11.glDrawElements(GL11.GL_TRIANGLE_STRIP, 4, GL11.GL_UNSIGNED_SHORT, 0);

		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		PROGRAM.stopUsing();

	}

	public static void main(String[] args) {
		new MovingRectangle();
	}

}
