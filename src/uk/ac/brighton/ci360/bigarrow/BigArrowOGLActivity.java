package uk.ac.brighton.ci360.bigarrow;

import android.app.Activity;
import android.content.Context;
import android.graphics.PixelFormat;
import android.hardware.Camera;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.view.ViewGroup.LayoutParams;

public class BigArrowOGLActivity extends Activity {

    private GLSurfaceView mGLView;
	private MyCameraPreview myCameraPreview;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        mGLView = new MyGLSurfaceView(this);
        addContentView(mGLView, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
        Camera camera = Camera.open();
        myCameraPreview = new MyCameraPreview(this, camera); 
        addContentView(myCameraPreview, new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
    }
    
	@Override
	protected void onResume() {
		super.onResume();
		mGLView.onResume();
	}

	@Override
	protected void onPause() {
		super.onPause();
		mGLView.onPause();
	}
    
    class MyGLSurfaceView extends GLSurfaceView {

        public MyGLSurfaceView(Context context){
            super(context);

            setEGLContextClientVersion(2);
            // Set the Renderer for drawing on the GLSurfaceView
            setEGLConfigChooser(8,8,8,8,16,0);
            getHolder().setFormat(PixelFormat.TRANSLUCENT);
            
            
            setRenderer(new MyRenderer());
            setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
            
        }
    }
}
