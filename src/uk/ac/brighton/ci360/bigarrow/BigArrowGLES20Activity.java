package uk.ac.brighton.ci360.bigarrow;

import java.util.ArrayList;

import uk.ac.brighton.ci360.bigarrow.opengles.MyGLSurfaceView;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.Activity;
import android.graphics.Bitmap;
import android.hardware.Camera;
import android.hardware.GeomagneticField;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

public class BigArrowGLES20Activity extends PlaceSearchActivity {

	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	private Camera camera;
	private boolean inPreview;
	private Location targetLocation;

	private MyGLSurfaceView mGLView;

	private final static String TAG = "BigArrow";
	private SensorManager sensorManager;

	private int orientationSensor;
	private float headingAngle;
	private float pitchAngle;

	private int accelerometerSensor;
	private TextView nearestPubLabel;

	protected SearchType firstSearchType = SearchType.SINGLE;

	@SuppressWarnings("deprecation")
	@Override
	public void onCreate(Bundle savedInstanceState) {
		firstSearchType = SearchType.SINGLE;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_bigarrow);

		sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
		orientationSensor = Sensor.TYPE_ORIENTATION;
		accelerometerSensor = Sensor.TYPE_ACCELEROMETER;
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(orientationSensor),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(accelerometerSensor),
				SensorManager.SENSOR_DELAY_NORMAL);

		inPreview = false;

		cameraPreview = (SurfaceView) findViewById(R.id.cameraPreview);
		mGLView = (MyGLSurfaceView) findViewById(R.id.arrowView);
		previewHolder = cameraPreview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		nearestPubLabel = (TextView) findViewById(R.id.nearest_place_label);
		nearestPubLabel.setText(R.string.bigarrow_searching);
		nearestPubLabel.setVisibility(View.VISIBLE);

	}

	final SensorEventListener sensorEventListener = new SensorEventListener() {
		@SuppressWarnings("deprecation")
		public void onSensorChanged(SensorEvent sensorEvent) {

			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				headingAngle = sensorEvent.values[0];
				pitchAngle = Math.abs(sensorEvent.values[1]);
				updateArrowView();
				//float rollAngle = sensorEvent.values[2];
			}
		}

		public void onAccuracyChanged(Sensor sensor, int accuracy) {
			// Not used
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		if (mGLView != null)
			mGLView.onResume();
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(orientationSensor),
				SensorManager.SENSOR_DELAY_NORMAL);
		sensorManager.registerListener(sensorEventListener,
				sensorManager.getDefaultSensor(accelerometerSensor),
				SensorManager.SENSOR_DELAY_NORMAL);
		camera = Camera.open();
		setCameraDisplayOrientation(this, 0, camera);
	}

	@Override
	public void onPause() {
		if (inPreview) {
			camera.stopPreview();
		}
		sensorManager.unregisterListener(sensorEventListener);
		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
		if (mGLView != null)
			mGLView.onPause();
	}

	/**
	 * Get the best preview size this device is capable of.
	 * @param width
	 * @param height
	 * @param parameters
	 * @return
	 */
	private Camera.Size getBestPreviewSize(int width, int height,
			Camera.Parameters parameters) {
		Camera.Size result = null;
		for (Camera.Size size : parameters.getSupportedPreviewSizes()) {
			if (size.width <= width && size.height <= height) {
				if (result == null) {
					result = size;
				} else {
					int resultArea = result.width * result.height;
					int newArea = size.width * size.height;

					if (newArea > resultArea) {
						result = size;
					}
				}
			}
		}

		return (result);
	}

	/**
	 * Change the camera preview orientation when the device orientation changes.
	 * @param activity
	 * @param cameraId
	 * @param camera
	 */
	public static void setCameraDisplayOrientation(Activity activity, int cameraId, Camera camera) {
		android.hardware.Camera.CameraInfo info = new android.hardware.Camera.CameraInfo();
		android.hardware.Camera.getCameraInfo(cameraId, info);
		int rotation = activity.getWindowManager().getDefaultDisplay()
				.getRotation();
		int degrees = 0;
		switch (rotation) {
		case Surface.ROTATION_0:
			degrees = 0;
			break;
		case Surface.ROTATION_90:
			degrees = 90;
			break;
		case Surface.ROTATION_180:
			degrees = 180;
			break;
		case Surface.ROTATION_270:
			degrees = 270;
			break;
		}

		int result;
		if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
			result = (info.orientation + degrees) % 360;
			result = (360 - result) % 360; // compensate the mirror
		} else { // back-facing
			result = (info.orientation - degrees + 360) % 360;
		}
		camera.setDisplayOrientation(result);
	}

	SurfaceHolder.Callback surfaceCallback = new SurfaceHolder.Callback() {
		public void surfaceCreated(SurfaceHolder holder) {
			try {
				camera.setPreviewDisplay(previewHolder);
			} catch (Throwable t) {
				Log.e(TAG, "Exception in setPreviewDisplay()", t);
			}
		}

		public void surfaceChanged(SurfaceHolder holder, int format, int width,
				int height) {
			Camera.Parameters parameters = camera.getParameters();
			Camera.Size size = getBestPreviewSize(width, height, parameters);

			if (size != null) {
				parameters.setPreviewSize(size.width, size.height);
				camera.setParameters(parameters);
				camera.startPreview();
				inPreview = true;
			}
		}

		public void surfaceDestroyed(SurfaceHolder holder) {
			// not used
		}

	};

	@Override
	public void updateNearestPlace(Place place, Location location,
			float distance) {
		targetLocation = location;
		nearestPubLabel.setText(place.name);
		if (!place.id.equals(Place.NO_RESULT)) {
			nearestPubLabel.append(": " + (int) distance + "m");
			updateArrowView();
		}
	}

	/**
	 * Calculate the angles of rotation and pitch to pass on the the MyGLSurfaceView
	 */
	private void updateArrowView() {
		if (myLocation != null && targetLocation != null) {
			float myBearing = normalizeDegree(myLocation.bearingTo(targetLocation));
			GeomagneticField geoField = new GeomagneticField(Double.valueOf(
					myLocation.getLatitude()).floatValue(), Double.valueOf(
					myLocation.getLongitude()).floatValue(), Double.valueOf(
					myLocation.getAltitude()).floatValue(),
					System.currentTimeMillis());
			float realHeading = headingAngle - geoField.getDeclination();
			realHeading = normalizeDegree(myBearing - headingAngle);
			mGLView.getRenderer().setHeading(realHeading);
			mGLView.getRenderer().setPitch(pitchAngle);
		}
	}

	/**
	 * Convert heading angles and bearings from -180 to 180 scale to 0 to 360 scale.
	 * @param value
	 * @return
	 */
	private float normalizeDegree(float value) {
		return (value < 0) ? -value + 180 : value;
	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}

	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePhotos(ArrayList<Bitmap> results) {
		// TODO Auto-generated method stub

	}
}
