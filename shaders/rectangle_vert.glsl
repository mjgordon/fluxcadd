#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 fillColor;

uniform vec2 position;
uniform vec2 size;
uniform vec3 color;

uniform mat4 projection;

void main()
{
  gl_Position = projection * vec4(aPos.x * size.x + position.x, aPos.y * size.y + position.y, aPos.z, 1.0);
  fillColor = color;
}
