

// === RAYMARCHING ===  
layout (local_size_x = 16, local_size_y = 16, local_size_z = 1) in;
layout(binding = 0, rgba32f) uniform image2D framebuffer;

// The scene specficiations
uniform vec3 cameraPosition;
uniform float focalLength;

uniform mat4 cameraMatrix;

uniform vec3 sunPosition;

uniform float ambientLight;


/**
Computes the transformation matrix to aim one vector at another,
rotating around their cross axis
*/
mat4 getTransformVecVec(vec3 a, vec3 b)
{
	vec3 v = cross(a, b);
	float c = dot(a,b);
	
	mat4 vx = mat4(1.0);
	
	vx[0][0] = 0;
	vx[0][1] = v.z;
	vx[0][2] = -v.y;
	
	vx[1][0] = -v.z;
	vx[1][1] = 0;
	vx[1][2] = v.x;
	
	vx[2][0] = v.y;
	vx[2][1] = -v.x;
	vx[2][2] = 0;
	
	mat4 vx2 = vx * vx;
	
	vx2 *= (1 / (1 + c));
	
	mat4 mat_out = mat4(1.0);
	mat_out += vx;
	mat_out += vx2;
	
	return mat_out;
}


/**
Main raymarching logic
*/
vec3 march(in vec3 position, in vec3 direction, in vec3 goalPoint, in bool useGoalPoint, out bool success)
{
  float distanceDelta = 0;
  const float farClip = 5000;
  const float epsilon = 0.00001; // This is also adjusted from the internal version, or else some things become extremely slow
  success = true;
  
  while(distanceDelta < farClip) {
    float dist = programDistance(position);

    if (dist <= epsilon) {
      return position;
    }
    
    vec3 step = direction * dist;
    position += step;
    distanceDelta += dist;

    if (useGoalPoint) {
      if (dot(goalPoint - position, direction) < 0) {
			  break;
      }
    }
  }
  
  success = false;
  return vec3(0,0,0);
}

const int shadowK = 32;

float marchShadow(vec3 position, vec3 direction, float nearClip, float farClip)
{
		// This epsilon has also been chosen to avoid weird dithering in shadows, however this one is less visually interesting than the normal one
		const float epsilon = 0.00001;
		float distanceDelta = nearClip;
		float result = 1.0;
		for (int i = 0; i < 256 && distanceDelta < farClip; i++)
		{
			float dist = programDistance((direction * distanceDelta) + position);
			if (dist < epsilon)
			{
				return 0.0;
			}
			distanceDelta += dist;
			
			result = min(result, shadowK * dist / distanceDelta);
		}
		return result;
}


vec3 getNormal(vec3 position)
{
	// Note, this was originally the same hit epsilon from the Java double code (1e-6)
	// This created some odd dithering effects, which on the one hand are cool so we can explore later
	// how to bring them back in a controlled manner
	const float epsilon = 1e-4;
	float d = programDistance(position);
	float a = programDistance(vec3(position.x + epsilon, position.y, position.z)) - d;
	float b = programDistance(vec3(position.x, position.y + epsilon, position.z)) - d;
	float c = programDistance(vec3(position.x, position.y, position.z + epsilon)) - d;
	
	vec3 vec_out = normalize(vec3(a,b,c));
	
	return vec_out;
}


/**
Calculates the reflection angle away from a surface given the incoming vector and the surfaces normal 
*/
vec3 getReflection(vec3 vec, vec3 normal)
{
	return vec - (2 * dot(vec, normal) * normal);
}

// Maximum number of reflections
const int maxDepth = 10;


/**
Contains the logic for handling reflection, normals, shadows etc built on the raymarch result
Reflection is handled by recording material information at each hit point, then stepping through them backwards and lerping based on each materials reflectivity
*/
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
	
	dir = (cameraMatrix * vec4(dir,0.0)).xyz;

	Material[maxDepth] materials;

	// The ray start point and vector for this bounce loop
	vec3 positionLoop = vec3(cameraPosition);
	vec3 dirLoop = vec3(dir);

	int finalDepth = 0;
	for (int i = 0; i < maxDepth; i++)
	{
		// Perform a raymarch from this starting position
		bool success = true;
		vec3 hitPosition = march(positionLoop, dirLoop, vec3(0,0,0), false, success);
		finalDepth = i;
		
		// If unhit, the ray returns the sky color, otherwise we use the material and lighting data
		if (!success) {
			materials[i] = Material(vec3(0.15, 0.39, 1), 0);
			break;
		}
		
		// Retrieve the material data for this bounce
		materials[i] = programMaterial(hitPosition);
		
		// Calculate the relationship to the sun
		vec3 hitNormal = getNormal(hitPosition);
		vec3 shadowVector = sunPosition - hitPosition;
		float shadowLength = length(shadowVector);
		shadowVector = normalize(shadowVector);
		
		float sunNormalAngle = clamp(dot(hitNormal, shadowVector),0,1);
	
		// --- Shadows ---
		float shadowFactor = marchShadow(hitPosition, shadowVector, 0.01, shadowLength);	

		float multFactor = mix(ambientLight, 1, shadowFactor * sunNormalAngle);
		
		materials[i].diffuse *= multFactor;
		
		//materials[i] = Material(vec3(shadowFactor, shadowFactor, shadowFactor), 0.0);
		//materials[i] = Material(vec3(sunNormalAngle, sunNormalAngle, sunNormalAngle), 0.0);
		//materials[i] = Material(vec3(sunNormalAngle * shadowFactor, sunNormalAngle * shadowFactor, sunNormalAngle * shadowFactor), 0.0);
		//materials[i] = Material(vec3(multFactor, multFactor, multFactor), 0.0);
		
		// If the most recent hit service is not reflective, stop here
		if (materials[i].reflectivity == 0.0) break;
		
		// On the next loop, start slightly offset from the last hit position, pointing along the incoming vectors reflection angle
		positionLoop = (hitNormal * 0.1) + hitPosition;
		dirLoop = getReflection(dirLoop, hitNormal);
	}
	
	Material matFinal = materials[finalDepth];
	for (int i = maxDepth - 1; i >= 0; i--)
	{
		matFinal = Material(mix(materials[i].diffuse, matFinal.diffuse, materials[i].reflectivity), 0);
	}
	
	//matFinal = materials[0];

  imageStore(framebuffer, pix, vec4(matFinal.diffuse, 1.0));
}