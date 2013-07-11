package uk.ac.brighton.ci360.bigarrow.opengles;
/**
 * This class extends GLSurfaceView and sets up the renderer.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import android.content.Context;
import android.graphics.PixelFormat;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;

public class MyGLSurfaceView extends GLSurfaceView {

	private MyRenderer renderer;

	public MyGLSurfaceView(Context context, AttributeSet attrs) {
		super(context, attrs);

		setEGLContextClientVersion(2);
		// Set the Renderer for drawing on the GLSurfaceView
		setEGLConfigChooser(8, 8, 8, 8, 16, 0);
		getHolder().setFormat(PixelFormat.TRANSLUCENT);

		// setRenderer(new CubeRenderer(true));
		renderer = new MyRenderer();
		setRenderer(renderer);
		// setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);

	}
	
	public MyRenderer getRenderer() {
		return renderer;
	}
}