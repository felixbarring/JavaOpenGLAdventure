
package com.opengl006.MovingTriangle;

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
	private final int VBO;
	private final ShaderProgram PROGRAM;
	private final FloatBuffer VERTEX_DATA;
	private final int OFFSET_LOCATION;
	
	public MovingTriangle(){
		
		try{
			Display.create(new PixelFormat(), new ContextAttribs(3, 2).withProfileCore(true));
			Display.setDisplayMode(new DisplayMode(800, 600));
			Display.setResizable(false);
			Display.setTitle("Moving Triangle Again");
		} catch (LWJGLException e){
			e.printStackTrace();
		}
		
		PROGRAM = new ShaderProgram("../opengl006/MovingTriangle/movingTriangle.vert", "../opengl006/MovingTriangle/movingTriangle.frag");
		
		VBO = GL15.glGenBuffers();
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		
		VERTEX_DATA = (FloatBuffer) BufferUtils.createFloatBuffer(24).put(new float[]{
				0f, 0.6f, 0.0f, 1.0f,
				0.6f, -0.6f, 0.0f, 1.0f,
				-0.6f, -0.6f, 0.0f, 1.0f,
				1.0f, 0.0f, 0.0f, 1.0f,
				0.0f, 1.0f, 0.0f, 1.0f,
				0.0f, 0.0f, 1.0f, 1.0f
		}).flip();
		
		GL15.glBufferData(GL15.GL_ARRAY_BUFFER, VERTEX_DATA, GL15.GL_STATIC_DRAW);
		GL30.glBindVertexArray(GL30.glGenVertexArrays());
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, 0);
		GL11.glClearColor(0.2f, 0.2f, 0.2f, 1.0f);
		OFFSET_LOCATION = GL20.glGetUniformLocation(PROGRAM.getId(), "offset");
		
		resize();
		loop();
		
	}
	
	private void resize(){
		GL11.glViewport(0, 0, Display.getWidth(), Display.getHeight());
	}
	
	
	private void loop(){
		while(!Display.isCloseRequested()){
			update();
			render();
			Display.update();
			Display.sync(MAX_FPS);
		}
	}
	
	private float xOff = 0.0f;
	private float yOff = 0.0f;
	private int xDirection = 1;
	private int yDirection = 1;
	private final float xSpeed = 0.015f;
	private final float ySpeed = 0.005f;
	private void update(){
		xOff = xOff + xDirection * xSpeed;
		if(xOff < -0.4f || xOff > 0.4f){
			xDirection = -xDirection;
		}
		yOff = yOff + yDirection * ySpeed;
		if(yOff < -0.4f || yOff > 0.4){
			yDirection = -yDirection;  
		}
	}
	
	private void render(){
		GL11.glClear(GL11.GL_COLOR_BUFFER_BIT);
		
		PROGRAM.use();
		
		GL20.glUniform2f(OFFSET_LOCATION, xOff, yOff);
		
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		GL20.glEnableVertexAttribArray(0);
		GL20.glEnableVertexAttribArray(1);
		GL20.glVertexAttribPointer(0, 4, GL11.GL_FLOAT, false, 0, 0);
		GL20.glVertexAttribPointer(1, 4, GL11.GL_FLOAT, false, 0, 48);
		
		GL11.glDrawArrays(GL11.GL_TRIANGLES, 0, 3);
		
		GL20.glDisableVertexAttribArray(0);
		GL20.glDisableVertexAttribArray(1);
		GL15.glBindBuffer(GL15.GL_ARRAY_BUFFER, VBO);
		PROGRAM.stopUsing();
	}
	
	public static void main(String[] args){
		new MovingTriangle();
	}

}
