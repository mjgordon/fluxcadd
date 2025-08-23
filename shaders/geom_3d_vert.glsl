#version 330 core
layout (location = 0) in vec4 aPos;

out vec3 fillColor;

uniform vec3 color;

uniform mat4 model;
uniform mat4 view;
uniform mat4 projection;

void main()
{
  gl_Position = projection * view * model * aPos;
  fillColor = color;
}
