#version 330 core
layout (location = 0) in vec4 aPos;
layout (location = 1) in vec3 colorIn;

out vec3 fillColor;

uniform mat4 transformation;

void main()
{
  gl_Position = transformation * aPos;
  fillColor = colorIn;
}
