#version 430 core
layout (local_size_x = 8, local_size_y = 8, local_size_z = 1) in;
layout(binding = 0, rgba32f) uniform image2D framebuffer;

// The camera specification
uniform vec3 eye;
uniform float focalLength;

uniform mat4 cameraMatrix;


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


float sdfBooleanUnion(float distA, float distB)
{
  return min(distA, distB);
}


float sdfPrimitiveGroundPlane(vec3 position, mat4 frame, float height)
{
  // localize
  return position.z;
}


float sdfPrimitiveCube(vec3 position, mat4 frame, vec3 dimensions)
{
  // localize
  vec3 q = abs(position) - dimensions;
  float maxQ = max(q.x, max(q.y, q.z));
  float distValue = length(max(q, 0.0)) + min(maxQ, 0);
  return distValue;
}


mat4 globalYo = mat4(1.0);


Material programMaterial(vec3 position)
{
  Material matA = Material(vec3(1, 0, 0), 0);
  Material matB = Material(vec3(0, 0, 1), 0);

  float distA = sdfPrimitiveCube(position, mat4(1.0), vec3(2, 2, 2));
  float distB = sdfPrimitiveGroundPlane(position, mat4(1.0), 0);

  Material matOut = (distA < distB) ? matA : matB;

  return matOut;
}


float programDistance(vec3 position)
{
  float distA = sdfPrimitiveCube(position, mat4(1.0), vec3(2, 2, 2));
  float distB = sdfPrimitiveGroundPlane(position, mat4(1.0), 0);
  float dist = sdfBooleanUnion(distA, distB);

  return dist;
}


// SCENE PROGRAM


vec3 march(in vec3 position, in vec3 direction, in vec3 goalPoint, in bool useGoalPoint, out bool success)
{
  float distanceDelta = 0;
  const float farClip = 5000;
  const float epsilon = 0.000001;
  success = true;
  
  while(distanceDelta < farClip) {
    float dist = programDistance(position);

    if (dist <= epsilon) {
      return position;
    }
    
    direction = normalize(direction) * dist;
    position += direction;
    distanceDelta += dist;

    if (useGoalPoint) {
      if (dot(goalPoint - position, direction) < 0) {
	//break;
      }
    }
  }
  
  success = false;
  return vec3(0,0,0);
}


void main(void)
{
  ivec2 pix = ivec2(gl_GlobalInvocationID.xy);
  ivec2 size = imageSize(framebuffer);
  if (pix.x >= size.x || pix.y >= size.y) {
    return;
  }

  vec2 framePos = vec2(pix) / vec2(size.x, size.y);
  vec3 dir = vec3(pix.x - (size.x / 2), pix.y - (size.y / 2), -focalLength);
  dir = normalize(dir);

  vec4 dir4 = vec4(dir, 0.0);

  dir4 = dir4 * cameraMatrix;
  dir = dir4.xyz;

  bool success = true;
  
  vec3 marchResult = march(eye, dir, vec3(0,0,0), true, success);

  vec4 color = vec4(0.3, 0.3, 1, 1.0);

  if (success) {
    Material mat = programMaterial(marchResult);
    color = vec4(mat.diffuse, 1.0);
  }

  imageStore(framebuffer, pix, color);
}
