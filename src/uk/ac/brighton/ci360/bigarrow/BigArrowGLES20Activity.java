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
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

public class BigArrowGLES20Activity extends PlaceSearchActivity {

	private SurfaceView cameraPreview;
	private SurfaceHolder previewHolder;
	// private ArrowView arrowView;
	private Camera camera;
	private boolean inPreview;
	private Location targetLocation;

	private MyGLSurfaceView mGLView;

	private final static String TAG = "BigArrow";
	private SensorManager sensorManager;

	private int orientationSensor;
	private float headingAngle;
	// private float pitchAngle;
	// private float rollAngle;

	private int accelerometerSensor;
	// private float xAxis;
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
		mGLView = new MyGLSurfaceView(this);
		addContentView(mGLView, new LayoutParams(LayoutParams.MATCH_PARENT,
				LayoutParams.MATCH_PARENT));
		previewHolder = cameraPreview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		nearestPubLabel = (TextView) findViewById(R.id.nearest_place_label);
		nearestPubLabel.setText(R.string.bigarrow_searching);

	}

	final SensorEventListener sensorEventListener = new SensorEventListener() {
		@SuppressWarnings("deprecation")
		public void onSensorChanged(SensorEvent sensorEvent) {

			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				headingAngle = sensorEvent.values[0];
				updateAngleToNearestPlace();
				Log.d(TAG, "heading angle: " + headingAngle);
				// pitchAngle = sensorEvent.values[1];
				// rollAngle = sensorEvent.values[2];

				// arrowView.updateData(headingAngle);

				// Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
				// Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
				// Log.d(TAG, "Roll: " + String.valueOf(rollAngle));

			}

			else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				// xAxis = sensorEvent.values[0];

				// Log.d(TAG, "X Axis: " + String.valueOf(xAxis));
				// Log.d(TAG, "Y Axis: " +
				// String.valueOf(sensorEvent.values[1]));
				// Log.d(TAG, "Z Axis: " +
				// String.valueOf(sensorEvent.values[2]));

				// nearestPubLabel.setText(String.valueOf(xAxis));
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

	public static void setCameraDisplayOrientation(Activity activity,
			int cameraId, android.hardware.Camera camera) {
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

			// arrowView.updateData(getAngle(location));
			updateAngleToNearestPlace();
		}
	}

	private void updateAngleToNearestPlace() {
		if (myLocation != null && targetLocation != null) {
			float myBearing = normalizeDegree(myLocation.bearingTo(targetLocation));
			Log.d(TAG, "myBearing: " + myBearing);
			GeomagneticField geoField = new GeomagneticField(Double.valueOf(
					myLocation.getLatitude()).floatValue(), Double.valueOf(
					myLocation.getLongitude()).floatValue(), Double.valueOf(
					myLocation.getAltitude()).floatValue(),
					System.currentTimeMillis());
			float realHeading = headingAngle - geoField.getDeclination();
			Log.d(TAG, "realHeading: " + realHeading);
			realHeading = normalizeDegree(myBearing - headingAngle);
			mGLView.getRenderer().setAngle(realHeading);
			Log.d(TAG, "angle to location: " + realHeading);
		}
	}

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

	private float getAngle(Location target) {
		float angle = (float) Math.toDegrees(Math.atan2(target.getLongitude()
				- myLocation.getLongitude(),
				target.getLatitude() - myLocation.getLatitude()));
		if (angle < 0) {
			angle += 360;
		}
		return angle;
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
