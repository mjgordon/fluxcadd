#version 330 core
layout (location = 0) in vec4 aPos;

out vec3 fillColor;

uniform vec3 color;

uniform vec4 translation;
uniform mat4 transformation;
uniform int pointSize;

void main()
{
  gl_PointSize = pointSize;
  gl_Position = aPos + translation;
  gl_Position = transformation * gl_Position;
  fillColor = color;
}
