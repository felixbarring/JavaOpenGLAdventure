#version 330 core

layout(location = 0) in vec2 pos;
layout(location = 1) in vec2 tex;

uniform vec2 offset;
uniform vec2 texOffset;

out vec2 texCoords;

void main()
{
    texCoords = tex+texOffset;
    vec4 offsetComplete = vec4(offset, 0.0f, 0.0f);
    gl_Position = vec4(pos, 0.0, 1.0)+offsetComplete;
}