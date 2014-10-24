package com.opengl008.TexturedRectangle;

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
import com.util.Texture;

public class TexturedRectangle {

	private final int MAX_FPS = 100;
	private final ShaderProgram PROGRAM;
	private final int OFFSET_LOCATION;
	int VAO, VBO, VBO_T, EBO;
	Texture texture;

	public TexturedRectangle() {

		try {
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setResizable(false);
			Display.setTitle("Moving Rectangle");
		} catch (LWJGLException e) {
			e.printStackTrace();
		}

		PROGRAM = new ShaderProgram("../opengl008/TexturedRectangle/texturedRectangle.vert",
				"../opengl008/TexturedRectangle/texturedRectangle.frag");

		Display.setTitle("Textured Rectangle");

		FloatBuffer vertices = (FloatBuffer) BufferUtils.createFloatBuffer(16).put(new float[] {
				-0.5f, +0.5f, // ID 0: Top Left
				+0.5f, +0.5f, // ID 1: Top Right
				-0.5f, -0.5f, // ID 2: Bottom Left
				+0.5f, -0.5f, // ID 3: Bottom Right
//				-0.27f, +1f, // ID 0: Top Left
//				+0.27f, +1f, // ID 1: Top Right
//				-0.27f, -1f, // ID 2: Bottom Left
//				+0.27f, -1f, // ID 3: Bottom Right
				// The texture coordinates
//				0, 0, // Top Left
//				1, 0, // Top Right
//				0, 1, // Bottom Left
//				1, 1 	// Bottom Right
				1, 1, // Top Left
				0, 1, // Top Right
				1, 0, // Bottom Left
				0, 0 	// Bottom Right
		}).flip();

		// Elements for the EBO
		ShortBuffer elements = (ShortBuffer) BufferUtils.createShortBuffer(6).put(new short[] {
				0, 1, 2, 2, 3, 1
		}).flip();

		VAO = GL30.glGenVertexArrays();
		GL30.glBindVertexArray(VAO);

		// Create the VBO for vertices
		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(0, 2, GL11.GL_FLOAT, false, 0, 0);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Create the VBO for texture coordinates
		VBO_T = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO_T);
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, vertices, GL15.GL_STATIC_DRAW);
		GL20.glVertexAttribPointer(1, 2, GL11.GL_FLOAT, false, 0, 32);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);

		// Create the EBO
		EBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ELEMENT_ARRAY_BUFFER, EBO);
		GL15.glBufferData(GL15.GL_ELEMENT_ARRAY_BUFFER, elements, GL15.GL_STATIC_DRAW);

		GL30.glBindVertexArray(0);

		// Load the texture and bind to our sampler
		texture = Texture.loadTexture("com/opengl008/TexturedRectangle/ScoutSmall.png");
		texture.setActiveTextureUnit(0);
		texture.bind();

		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
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

	private void update() {
		xOff = xOff + (xSpeed * xDirection);
		if (xOff > 0.5f || xOff < -0.5f) {
			xDirection = -xDirection;
		}
		
		yOff = yOff + (ySpeed * yDirection);
		if (yOff > 0.5f || yOff < -0.5f) {
			yDirection = -yDirection;
		}
	}

	private void render() {
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);

		PROGRAM.use();
		GL20.glUniform2f(OFFSET_LOCATION, xOff, yOff);
		
		// Bind the VAO and enable locations
		GL30.glBindVertexArray(VAO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);

		// Draw the elements
		GL11.glDrawElements(GL11.GL_TRIANGLES, 6, GL11.GL_UNSIGNED_SHORT, 0);

		// Disable the locations and unbind the VAO
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL30.glBindVertexArray(0);
		PROGRAM.stopUsing();
	}

	public static void main(String[] args) {
		new TexturedRectangle();
	}

}
