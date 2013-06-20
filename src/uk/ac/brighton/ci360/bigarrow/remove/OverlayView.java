package uk.ac.brighton.ci360.bigarrow.remove;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.hardware.Camera;
import android.hardware.Camera.PreviewCallback;
import android.location.Location;
import android.util.AttributeSet;
import android.util.Log;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

public class OverlayView extends SurfaceView {
	protected static final String TAG = "OverlayView";
	/*
	private SurfaceHolder surfaceHolder;
	private Camera camera;
	private Camera.Size frameSize;
	private Place nearestPub;
	private Location npLocation;
	private float distance;

	public OverlayView(Context ctx) {
        super(ctx);
        setEmptyPub();
        surfaceHolder= getHolder();
    }
	
	public OverlayView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		setEmptyPub();
		surfaceHolder = getHolder();
	}
	
	private void setEmptyPub() {
		nearestPub = new Place();
        nearestPub.name = "Searching";
	}

	// Called by Sobel.surfaceChanged, to set dimensions
	public void setPreviewSize(Camera.Size s) {
		frameSize = s;
	}

	// Called by initCamera, to set callback
	public void setCamera(Camera c) {
		camera = c;
		camera.setPreviewCallback(new PreviewCallback() {
			// Called by camera hardware, with preview frame
			public void onPreviewFrame(byte[] frame, Camera c) {
				//Log.d(TAG, "surfaceHolder is null?"+(surfaceHolder == null));
				Canvas canvas = surfaceHolder.lockCanvas(null);
				canvas.drawColor( 0, PorterDuff.Mode.CLEAR );
				try {
					// Perform overlay rendering here
					Paint pt = new Paint();
					pt.setColor(Color.BLACK);
					pt.setTextSize(70);
					
					if(nearestPub == null) Log.d(TAG, "Help! nearestPub is null");
					String pubTxt = nearestPub.name;
					if(distance > 0) pubTxt += ": "+(int)distance+"m";
					
					canvas.drawText(pubTxt, 10, frameSize.height-10, pt);
					
					Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

				    paint.setStrokeWidth(2);
				    paint.setColor(android.graphics.Color.RED);     
				    paint.setStyle(Paint.Style.FILL_AND_STROKE);
				    paint.setAntiAlias(true);

				    Path path = new Path();
				    path.setFillType(Path.FillType.EVEN_ODD);
				    path.moveTo(10, frameSize.height-10);
				    int w = 700;
					path.lineTo(w, frameSize.height-10);
					path.moveTo(w, frameSize.height-10);
					path.lineTo(((w-10)/2)+10, frameSize.height-200);
					path.moveTo(((w-10)/2)+10, frameSize.height-200);
					path.lineTo(10, frameSize.height-10);
					path.close();

				    canvas.drawPath(path, paint);

				} catch (Exception e) {
					Log.d(TAG, e.toString()+". "+e.getMessage());
				} finally {
					surfaceHolder.unlockCanvasAndPost(canvas);
				}
			}
		});
	}

	public Place getNearestPub() {
		return nearestPub;
	}

	public void setNearestPub(Place nearestPub) {
		this.nearestPub = nearestPub;
		Log.d(TAG, "nearestPub:"+nearestPub.name);
	}

	public Location getNpLocation() {
		return npLocation;
	}

	public void setNpLocation(Location npLocation) {
		this.npLocation = npLocation;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}*/
	
	private SurfaceHolder mOverSH;
	private Camera mCam;
	private Camera.Size frameSize;
	private Place nearestPub;
	private Location npLocation;
	private float distance;

	public OverlayView(Context ctx, AttributeSet attr) {
		super(ctx, attr);
		mOverSH = getHolder();
	}

	public void setPreviewSize(Camera.Size s) {
		frameSize = s;
	}

	// Called by initCamera, to set callback
	public void setCamera(Camera c) {
		mCam = c;
		mCam.setPreviewCallback(new PreviewCallback() {
			// Called by camera hardware, with preview frame
			public void onPreviewFrame(byte[] frame, Camera c) {
				Canvas canvas = mOverSH.lockCanvas(null);
				canvas.drawColor( 0, PorterDuff.Mode.CLEAR );
				try {
					Paint pt = new Paint();
					pt.setColor(Color.BLACK);
					pt.setTextSize(70);
					
					//canvas.drawText(TAG, 100, 100, pt);
					
					if(nearestPub != null && distance > 0) {
						canvas.drawText(nearestPub.name+": "+(int)distance+"m", 10, frameSize.height-10, pt);
					}
					
					Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);

				    paint.setStrokeWidth(2);
				    paint.setColor(android.graphics.Color.RED);     
				    paint.setStyle(Paint.Style.FILL_AND_STROKE);
				    paint.setAntiAlias(true);

				    Path path = new Path();
				    path.setFillType(Path.FillType.EVEN_ODD);
				    path.moveTo(10, frameSize.height-10);
				    int w = 700;
					path.lineTo(w, frameSize.height-10);
					path.moveTo(w, frameSize.height-10);
					path.lineTo(((w-10)/2)+10, frameSize.height-200);
					path.moveTo(((w-10)/2)+10, frameSize.height-200);
					path.lineTo(10, frameSize.height-10);
					path.close();

				    canvas.drawPath(path, paint);

				} catch (Exception e) {
					Log.d(TAG, e.toString()+". "+e.getMessage());
				} finally {
					mOverSH.unlockCanvasAndPost(canvas);
				}
			}
		});
	}

	public Place getNearestPub() {
		return nearestPub;
	}

	public void setNearestPub(Place nearestPub) {
		this.nearestPub = nearestPub;
	}

	public Location getNpLocation() {
		return npLocation;
	}

	public void setNpLocation(Location npLocation) {
		this.npLocation = npLocation;
	}

	public float getDistance() {
		return distance;
	}

	public void setDistance(float distance) {
		this.distance = distance;
	}
	 
}
