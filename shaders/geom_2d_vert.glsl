#version 330 core
layout (location = 0) in vec3 aPos;

out vec3 fillColor;

uniform vec3 color;

uniform mat3 shape;
uniform mat4 view;
uniform mat4 projection;

void main()
{
  vec3 temp = aPos;
  temp[2] = 1; // Apparently the last position does not automatically get set to 1 for vec3
  temp = shape * temp;
 
  gl_Position = projection * view * vec4(temp, 1);
  fillColor = color;
}
