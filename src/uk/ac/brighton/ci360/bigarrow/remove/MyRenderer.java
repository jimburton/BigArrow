package uk.ac.brighton.ci360.bigarrow.remove;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLES20;
import android.opengl.GLSurfaceView;
import android.opengl.GLU;

public class MyRenderer implements GLSurfaceView.Renderer {

	private Triangle triangle;
	private Square square;
	
	/**
	 * Instance the Triangle and Square objects
	 */
	public MyRenderer() {
		triangle = new Triangle();
		square = new Square();
	}

    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        // Set the background frame color
        //GLES20.glClearColor(0.5f, 0.5f, 0.5f, 1.0f);
        
        gl.glShadeModel(GL10.GL_SMOOTH); 			//Enable Smooth Shading
		gl.glClearColor(0.0f, 0.0f, 0.0f, 0.5f); 	//Black Background
		gl.glClearDepthf(1.0f); 					//Depth Buffer Setup
		gl.glEnable(GL10.GL_DEPTH_TEST); 			//Enables Depth Testing
		gl.glDepthFunc(GL10.GL_LEQUAL); 			//The Type Of Depth Testing To Do
		
		//Really Nice Perspective Calculations
		gl.glHint(GL10.GL_PERSPECTIVE_CORRECTION_HINT, GL10.GL_NICEST); 
        
    }

    public void onDrawFrame(GL10 gl) {
        // Redraw background color
        //GLES20.glClear(GLES20.GL_COLOR_BUFFER_BIT);
        
        gl.glClear(GL10.GL_COLOR_BUFFER_BIT | GL10.GL_DEPTH_BUFFER_BIT);	
		gl.glLoadIdentity();					//Reset The Current Modelview Matrix
		
		/*
		 * Minor changes to the original tutorial
		 * 
		 * Instead of drawing our objects here,
		 * we fire their own drawing methods on
		 * the current instance
		 */
		gl.glTranslatef(0.0f, -1.2f, -6.0f);	//Move down 1.2 Unit And Into The Screen 6.0
		square.draw(gl);						//Draw the square
		
		gl.glTranslatef(0.0f, 2.5f, 0.0f);		//Move up 2.5 Units
		triangle.draw(gl);
    }

    public void onSurfaceChanged(GL10 gl, int width, int height) {
    	if(height == 0) { 						//Prevent A Divide By Zero By
			height = 1; 						//Making Height Equal One
		}

		gl.glViewport(0, 0, width, height); 	//Reset The Current Viewport
		gl.glMatrixMode(GL10.GL_PROJECTION); 	//Select The Projection Matrix
		gl.glLoadIdentity(); 					//Reset The Projection Matrix

		//Calculate The Aspect Ratio Of The Window
		GLU.gluPerspective(gl, 45.0f, (float)width / (float)height, 0.1f, 100.0f);

		gl.glMatrixMode(GL10.GL_MODELVIEW); 	//Select The Modelview Matrix
		gl.glLoadIdentity();
    }

}