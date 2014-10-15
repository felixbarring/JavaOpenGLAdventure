#version 330

layout(location = 0) in vec4 position;
layout(location = 1) in vec4 color;

uniform vec2 offset;

smooth out vec4 finalColor;

void main()
{
	vec4 offsetComplete = vec4(offset, 0.0f, 0.0f);
	gl_Position = position + offsetComplete;
	finalColor = color;
} 