

// === SINGLE EXECUTION FOR TESTING
layout(local_size_x = 1, local_size_y = 1, local_size_z = 1) in;

uniform vec3 inputVector;

layout(std430, binding=0) buffer OutputBuffer {
	float resultValue;
};

void main(void)
{
	float dist = programDistance(inputVector);
	resultValue = dist;
}