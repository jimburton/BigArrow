package uk.ac.brighton.ci360.bigarrow;

import java.util.List;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import android.app.Activity;
import android.hardware.Camera;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Surface;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.widget.TextView;

public class BigArrowPAARActivity extends Activity implements LocationListener,
		PubSearchRequester {
	SurfaceView cameraPreview;
	SurfaceHolder previewHolder;
	Camera camera;
	boolean inPreview;

	final static String TAG = "BigArrow";
	SensorManager sensorManager;

	int orientationSensor;
	float headingAngle;
	float pitchAngle;
	float rollAngle;

	int accelerometerSensor;
	float xAxis;

	LocationManager locationManager;
	double latitude;
	double longitude;
	double altitude;

	TextView nearestPubLabel;

	private PubSearch pSearch;
	private final boolean PLACES_SEARCH_ON = true;

	@Override
	public void onCreate(Bundle savedInstanceState) {
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
		previewHolder = cameraPreview.getHolder();
		previewHolder.addCallback(surfaceCallback);
		previewHolder.setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

		nearestPubLabel = (TextView) findViewById(R.id.xAxisValue);
		nearestPubLabel.setText(R.string.bigarrow_searching); 
		
		if (PLACES_SEARCH_ON) {
			pSearch = new PubSearch(this);
			locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
			getNearestPub(locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER));
			locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,
					10000, 50, this);
		}
		
	}

	private void getNearestPub(Location l) {
		pSearch.search(l, "bar");
	}

	final SensorEventListener sensorEventListener = new SensorEventListener() {
		public void onSensorChanged(SensorEvent sensorEvent) {
			if (sensorEvent.sensor.getType() == Sensor.TYPE_ORIENTATION) {
				headingAngle = sensorEvent.values[0];
				pitchAngle = sensorEvent.values[1];
				rollAngle = sensorEvent.values[2];

				// Log.d(TAG, "Heading: " + String.valueOf(headingAngle));
				// Log.d(TAG, "Pitch: " + String.valueOf(pitchAngle));
				// Log.d(TAG, "Roll: " + String.valueOf(rollAngle));

			}

			else if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
				xAxis = sensorEvent.values[0];

				// Log.d(TAG, "X Axis: " + String.valueOf(xAxis));
				// Log.d(TAG, "Y Axis: " +
				// String.valueOf(sensorEvent.values[1]));
				// Log.d(TAG, "Z Axis: " +
				// String.valueOf(sensorEvent.values[2]));

				// nearestPubLabel.setText(String.valueOf(xAxis));
			}
		}

		public void onAccuracyChanged(Sensor senor, int accuracy) {
			// Not used
		}
	};

	@Override
	public void onResume() {
		super.onResume();
		locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER,// or
																			// NETWORK_PROVIDER
				10000, 50, this);
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
		locationManager.removeUpdates(this);
		sensorManager.unregisterListener(sensorEventListener);
		camera.release();
		camera = null;
		inPreview = false;

		super.onPause();
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
	public void updateNearestPub(Place place, Location location, float distance) {
		nearestPubLabel.setText(place.name + ": " + distance + "m");

	}

	@Override
	public void updateNearestPubs(List<Place> places) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onLocationChanged(Location location) {
		if (PLACES_SEARCH_ON) {
			getNearestPub(location);
		}
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub

	}

	@Override
	public void onProviderEnabled(String provider) {
		getNearestPub(locationManager.getLastKnownLocation(provider));

	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
	}
}
