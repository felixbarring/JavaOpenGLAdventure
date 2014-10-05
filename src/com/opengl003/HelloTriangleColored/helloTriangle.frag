#version 330

out vec4 finalColor;

void main()
{
	float lerpValue = gl_FragCoord.x / 500.0f;
  finalColor = mix(vec4(0.0f, 1.0f, 0.0f, 1.0f), vec4(0.2f, 0.0f, 0.0f, 1.0f), lerpValue);
  
}