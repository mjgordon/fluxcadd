package graphics;

import java.util.ArrayDeque;

import org.joml.Matrix4f;

public class MatrixStack {
	private ArrayDeque<Matrix4f> stackInternal;
	
	public MatrixStack() {
		stackInternal = new ArrayDeque<Matrix4f>();
		stackInternal.push(new Matrix4f().identity());
	}
	
	public Matrix4f get() {
		return stackInternal.peek();
	}
	
	
	public Matrix4f push() {
		stackInternal.push(new Matrix4f(stackInternal.peek()));
		return stackInternal.peek();
	}
	
	
	public void pop() {
		if (stackInternal.size() > 0) {
			stackInternal.pop();	
		}
		
		if (stackInternal.size() == 0) {
			stackInternal.push(new Matrix4f().identity());
		}
	}
}
