package uk.ac.brighton.ci360.bigarrow.opengles;

import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;

public class MyGLSurfaceView extends GLSurfaceView {

	public MyGLSurfaceView(Context context) {
		super(context);

		setEGLContextClientVersion(2);
		// Set the Renderer for drawing on the GLSurfaceView
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// setRenderer(new CubeRenderer(true));
		setRenderer(new MyRenderer());
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

	}
}