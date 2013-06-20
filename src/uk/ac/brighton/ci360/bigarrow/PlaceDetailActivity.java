package uk.ac.brighton.ci360.bigarrow;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.widget.TextView;

public class PlaceDetailActivity extends PlaceSearchActivity {

	private static final String TAG = "PlaceDetailActivity";
	PlaceDetails placeDetails;
	ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		firstSearchType = SearchType.DETAIL;
		Intent i = getIntent();
		placeReference = i.getStringExtra(PlaceDetails.KEY_REFERENCE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail);
		pDialog = new ProgressDialog(PlaceDetailActivity.this);
		pDialog.setMessage("Loading details ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) { 
		// TODO Auto-generated method stub

	}

	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		Log.d(TAG, "displaying details for  "+details.result.name);
		pDialog.dismiss();
		placeDetails = details;
		// updating UI from Background Thread
		runOnUiThread(new Runnable() {
			public void run() {
				if (placeDetails != null) {
					String status = placeDetails.status;
					if (status.equals("OK") && placeDetails.result != null) {
						String name = placeDetails.result.name;
						String address = placeDetails.result.formatted_address;
						String phone = placeDetails.result.formatted_phone_number;
						String latitude = Double
								.toString(placeDetails.result.geometry.location.lat);
						String longitude = Double
								.toString(placeDetails.result.geometry.location.lng);

						Log.d("Place ", name + address + phone + latitude
								+ longitude);

						TextView lbl_name = (TextView) findViewById(R.id.name);
						TextView lbl_address = (TextView) findViewById(R.id.address);
						TextView lbl_phone = (TextView) findViewById(R.id.phone);
						TextView lbl_location = (TextView) findViewById(R.id.location);

						name = name == null ? "Not present" : name;
						address = address == null ? "Not present" : address;
						phone = phone == null ? "Not present" : phone;
						latitude = latitude == null ? "Not present" : latitude;
						longitude = longitude == null ? "Not present"
								: longitude;

						lbl_name.setText(name);
						lbl_address.setText(address);
						lbl_phone.setText(Html.fromHtml("<b>Phone:</b> "
								+ phone));
						lbl_location.setText(Html
								.fromHtml("<b>Latitude:</b> " + latitude
										+ ", <b>Longitude:</b> " + longitude));
					}
				}

			}
		});
	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		// TODO Auto-generated method stub

	}

	@Override
	public void updateNearestPlace(Place place, Location location,
			float distance) {
		// TODO Auto-generated method stub

	}

}
