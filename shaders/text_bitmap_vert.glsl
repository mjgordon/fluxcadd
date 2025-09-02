#version 330 core
layout (location = 0) in vec2 aPos;
layout (location = 1) in vec2 aTexCoord;
layout (location = 2) in float charId; 

// Note: charId should really just be a byte or int, but there were issues with bad (very large) values being received, maybe due to 2's complement? 

out vec2 TexCoord;

uniform mat4 projection;
uniform vec2 start;
uniform vec2 cellOffset;

void main()
{
	float offset = 1 / 16.0;
 	gl_Position = projection * vec4(aPos.x + start.x + (gl_InstanceID * cellOffset.x), aPos.y + start.y, 0, 1.0);
 	float atlasX = mod(charId,16);
	float atlasY = floor(charId / 16);
 	TexCoord = aTexCoord;
 	TexCoord.x += atlasX * offset;
 	TexCoord.y += atlasY * offset;
}
