package uk.ac.brighton.ci360.bigarrow.opengles;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.Matrix;
import android.os.SystemClock;
import android.util.Log;


public class MyRenderer implements GLSurfaceView.Renderer {

	private static final String TAG = "MyRenderer";


	private Triangle triangle;
	//private Square square;

	
	private final float[] mMVPMatrix = new float[16];
    private final float[] mProjMatrix = new float[16];
    private final float[] mVMatrix = new float[16];
    private final float[] mRotationMatrix = new float[16];
 // Declare as volatile because we are updating it from another thread
    private volatile float mAngle;

	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		// Set the background frame color
		gl.glClearColor(0, 0, 0, 0);

		//square   = new Square();
		triangle = new Triangle();
	}

	public void onDrawFrame(GL10 unused) {
        // Draw background color
        GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);

        // Set the camera position (View matrix)
        Matrix.setLookAtM(mVMatrix, 0, 0, 0, -3, 0f, 0f, 0f, 0f, 1.0f, 0.0f);

        // Calculate the projection and view transformation
        Matrix.multiplyMM(mMVPMatrix, 0, mProjMatrix, 0, mVMatrix, 0);

        // Draw square
        //square.draw(mMVPMatrix);

        // Create a rotation for the triangle
        //long time = SystemClock.uptimeMillis() % 4000L;
        //setAngle(0.090f * ((int) time));
        Log.d(TAG,  "arrow angle: "+getAngle());
        Matrix.setRotateM(mRotationMatrix, 0, getAngle(), 0, 0, -1.0f);

        // Combine the rotation matrix with the projection and camera view
        Matrix.multiplyMM(mMVPMatrix, 0, mRotationMatrix, 0, mMVPMatrix, 0);

        // Draw triangle
        triangle.draw(mMVPMatrix);
        
	}

	public void onSurfaceChanged(GL10 unused, int width, int height) {
		GLES20.glViewport(0, 0, width, height);

	    float ratio = (float) width / height;

	    // this projection matrix is applied to object coordinates
	    // in the onDrawFrame() method
	    Matrix.frustumM(mProjMatrix, 0, -ratio, ratio, -1, 1, 3, 7);
	}

	public static int loadShader(int type, String shaderCode) {

		// create a vertex shader type (GLES20.GL_VERTEX_SHADER)
		// or a fragment shader type (GLES20.GL_FRAGMENT_SHADER)
		int shader = GLES20.glCreateShader(type);

		// add the source code to the shader and compile it
		GLES20.glShaderSource(shader, shaderCode);
		GLES20.glCompileShader(shader);

		return shader;
	}
	
    /**
     * Utility method for debugging OpenGL calls. Provide the name of the call
     * just after making it:
     *
     * <pre>
     * mColorHandle = GLES20.glGetUniformLocation(mProgram, "vColor");
     * MyGLRenderer.checkGlError("glGetUniformLocation");</pre>
     *
     * If the operation is not successful, the check throws an error.
     *
     * @param glOperation - Name of the OpenGL call to check.
     */
    public static void checkGlError(String glOperation) {
        int error;
        while ((error = GLES20.glGetError()) != GLES20.GL_NO_ERROR) {
            Log.e(TAG, glOperation + ": glError " + error);
            throw new RuntimeException(glOperation + ": glError " + error);
        }
    }

	public float getAngle() {
		return mAngle;
	}

	public void setAngle(float mAngle) {
		this.mAngle = mAngle;
	}

}
