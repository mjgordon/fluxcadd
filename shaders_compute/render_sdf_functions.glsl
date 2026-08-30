#version 430 core

// === GENERIC DEFINITIONS === 

struct Material
{
  vec3 diffuse;
  float reflectivity;
};


Material materialBoolean(Material matA, Material matB, float distA, float distB) {
  if (distA < distB) {
    return matA;
  }
  return matB;
}


vec3 localPosition(vec3 position, mat4 matrixInvert)
{
	return (matrixInvert * vec4(position, 1)).xyz;
}


float sdfBooleanDifference(float distA, float distB)
{
  return max(distA, -distB);
}


float sdfBooleanIntersection(float distA, float distB)
{
  return max(distA, distB);
}


float sdfBooleanUnion(float distA, float distB)
{
  return min(distA, distB);
}


float sdfOpAdd(float distA, float distB, float mult)
{
  return distA + (distB * mult);
}


float sdfOpAddConstant(float distA, float constant)
{
	return distA + constant;
}


float sdfOpAverage(float distA, float distB)
{
  return (distA + distB) * 0.5;
}


float sdfOpChamfer(float distA, float distB, float size)
{
		float h = max(size - abs(distA - distB), 0);
		return min(distA, distB) - h * 0.5;
}


float sdfOpLerp(float distA, float distB, float f)
{
  float diff = distB - distA;
  return distA + (f * diff);
}


vec3[8] sdfOpModuloGetPositions(vec3 position, vec3 stride)
{
	float idX = 0;
	float idY = 0;
	float idZ = 0;
	float dirX = 0;
	float dirY = 0; 
	float dirZ = 0;
	
	if (stride.x > 0)
	{
		idX = round(position.x / stride.x);
		dirX = sign(position.x - (stride.x * idX));
	}
	
	if (stride.y > 0)
	{
		idY = round(position.y / stride.y);
		dirY = sign(position.y - (stride.y * idY));
	}
	
	if (stride.z > 0)
	{
		idZ = round(position.z / stride.z);
		dirZ = sign(position.z - (stride.z * idZ));
	}
	
	vec3 items[8];
	int counter = 0;
	for (int x = 0; x < 2; x++)
  {
    for (int y = 0; y < 2; y++)
    {
      for (int z = 0; z < 2; z++)
      {
				items[counter] = vec3(x,y,z);
				items[counter] *= vec3(dirX, dirY, dirZ);
				items[counter] += vec3(idX, idY, idZ);
				items[counter] *= stride;
				items[counter] = position - items[counter];
				
				counter += 1;
      } 
    }
  }
	
	return items;
}


float sdfOpSmooth(float distA, float distB, float size, float sizeReciprocal)
{
	float h = max(size - abs(distA - distB), 0) * sizeReciprocal;
	return min(distA, distB) - h * h * size * 0.25;
}


float sdfOpSubtract(float distA, float distB, float factor)
{
  return distA - (distB * factor);
}


float sdfOpSubtractConstant(float distA, float constant)
{
		return distA - constant;
}


float sdfPrimitiveCross(vec3 position, mat4 matrixInvert, mat3x2 matrixInvert2d, float hypotSize)
{
	vec3 local = localPosition(position, matrixInvert);
	vec2 pos;
	
	if (local.x <= local.z && local.y <= local.z) {
		pos = vec2(local.x, local.y);
	}
	else if (local.x <= local.y && local.z <= local.y) {
		pos = vec2(local.x, local.z);
	}
	else {
		pos = vec2(local.y, local.z);
	}
	
	pos = (matrixInvert2d * vec3(pos, 1)).xy;
	
	float compX = min(hypotSize, max(0, pos.x));
	
	float dist = distance(pos, vec2(compX, 0));
	
	dist *= sign(pos.y);
	
	return dist;
}

float sdfPrimitiveCube(vec3 position, mat4 matrixInvert, vec3 dimensions)
{
  vec3 local = localPosition(position, matrixInvert);
  vec3 q = abs(local) - dimensions;
  float maxQ = max(q.x, max(q.y, q.z));
  float distValue = length(max(q, 0.0)) + min(maxQ, 0);
  return distValue;
}


float sdfPrimitiveCylinder(vec3 position, mat4 matrixInvert, float radius, float halfHeight)
{
  vec3 local = localPosition(position, matrixInvert);
  
	vec2 l2d = vec2(sqrt(pow(local.x, 2) + pow(local.y, 2)), local.z);
	
	l2d = abs(l2d) - vec2(radius, halfHeight);
	
	float max2d = max(l2d.x, l2d.y);
	
	return length(max(l2d, vec2(0,0))) + min(0, max2d);
}


float sdfPrimitiveDiamond(vec3 position, mat4 matrixInvert, float axisSize)
{
	vec3 local = localPosition(position, matrixInvert);
	
	local = abs(local);
	
	float m = local.x + local.y + local.z - axisSize;
	
	vec3 q;
	if (3 * local.x < m) q = local.xyz;
	else if (3 * local.y < m) q = local.yzx;
	else if (3 * local.z < m) q = local.zxy;
	else return m * 0.57735027;
	
	float k = clamp(0.5 * (q.z- q.y + axisSize), 0.0, axisSize);
	
	return length(vec3(q.x, q.y - axisSize + k, q.z - k));
}


float sdfPrimitiveGroundPlane(vec3 position, mat4 matrixInvert)
{
  vec3 local = localPosition(position, matrixInvert);
  return local.z;
}


float sdfPrimitiveSphere(vec3 position, mat4 matrixInvert, float radius)
{
	vec3 local = localPosition(position, matrixInvert);
	
	return length(local) - radius;
}


float sdfPrimitiveStar(vec3 position, mat4 matrixInvert, float halfSize, float sphereSize)
{
	vec3 local = localPosition(position, matrixInvert);
	
	float distance;
	
	if (local.x <= halfSize && local.y <= halfSize && local.z <= halfSize)
	{
		distance = sphereSize - sqrt(pow(local.x - halfSize, 2) + pow(local.y - halfSize, 2) + pow(local.z - halfSize, 2));
	}
	else if (local.x >= local.y && local.x >= local.z)
	{
		distance = sqrt(pow(local.x - halfSize, 2) + pow(local.y, 2) + pow(local.z, 2));
	}
	else if (local.y >= local.x && local.y >= local.z)
	{
		distance = sqrt(pow(local.y - halfSize, 2) + pow(local.x, 2) + pow(local.z, 2));
	}
	else
	{
		distance = sqrt(pow(local.z - halfSize, 2) + pow(local.x, 2) + pow(local.y, 2));
	}
	
	return distance;
}


float sdfPrimitiveStarError0(vec3 position, mat4 matrixInvert, float size)
{
	vec3 local = localPosition(position, matrixInvert);
	return (local.x * local.y * local.z) + (local.x + local.y + local.z) - size;
}


float sdfPrimitiveStarError1(vec3 position, mat4 matrixInvert, float size)
{
	vec3 local = localPosition(position, matrixInvert);
	return (local.x * local.y * local.z) - size;
}


float sdfPrimitiveTorus(vec3 position, mat4 matrixInvert, float ringRadius, float profileRadius)
{
	vec3 local = localPosition(position, matrixInvert);
	vec2 local2d = vec2(distance(local, vec3(0,0,local.z)), local.z);
	
	float distOut = distance(local2d, vec2(ringRadius, 0));
	
	distOut -= profileRadius;
	
	return distOut;
}


// === PROGRAM BEGINS ===