#version 330 core
// 3D vert program that takes a single pre-multiplied model-view-projection matrix
layout (location = 0) in vec4 aPos;

out vec3 fillColor;

uniform vec3 color;

uniform mat4 transformation;

void main()
{
  gl_Position = transformation * aPos;
  fillColor = color;
}
