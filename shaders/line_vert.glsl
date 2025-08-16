#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 fillColor;

uniform vec3 color;

uniform mat4 shape;
uniform mat4 projection;

void main()
{
  gl_Position = projection * shape * vec4(aPos, 1.0);
  fillColor = color;
}
