package uk.ac.brighton.ci360.bigarrow;

/**
 * This activity shows the full details of a selected place.
 * 
 * @author jb259
 */
import java.util.ArrayList;

import uk.ac.brighton.ci360.bigarrow.expandinglist.ExpandListAdapter;
import uk.ac.brighton.ci360.bigarrow.expandinglist.ExpandListChild;
import uk.ac.brighton.ci360.bigarrow.expandinglist.ExpandListGroup;
import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.widget.ExpandableListView;
import android.widget.ImageView;
import android.widget.TextView;

public class PlaceDetailActivity extends PlaceSearchActivity {

	private static final String TAG = "PlaceDetailActivity";
	public static final String OPEN = "open now";

	PlaceDetails placeDetails;
	ProgressDialog pDialog;

	private TextView nameTV, openingHoursTV, addressTV, distanceTV;
	private ExpandListAdapter adapter;
	private ExpandableListView expList;
	private ExpandListGroup contactGroup, photoGroup, reviewGroup;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		firstSearchType = SearchType.DETAIL;
		Intent i = getIntent();
		placeReference = i.getStringExtra(PlaceDetails.KEY_REFERENCE);

		super.onCreate(savedInstanceState);
		setContentView(R.layout.place_detail);
		nameTV = (TextView) findViewById(R.id.name_txt);
		addressTV = (TextView) findViewById(R.id.address_txt);
		distanceTV = (TextView) findViewById(R.id.distance_txt);
		openingHoursTV = (TextView) findViewById(R.id.opening_hours_txt);
		
		expList = (ExpandableListView) findViewById(R.id.exp_list);
		
		ArrayList<ExpandListGroup> grps = new ArrayList<ExpandListGroup>();
		contactGroup = new ExpandListGroup();
		contactGroup.setName("Contact details");
		grps.add(contactGroup);
		
		photoGroup = new ExpandListGroup();
		photoGroup.setName("Photos");
		grps.add(photoGroup);
		
		reviewGroup = new ExpandListGroup();
		reviewGroup.setName("Reviews");
		//grps.add(reviewGroup);  no reviews yet
		
		adapter = new ExpandListAdapter(this, grps);
        expList.setAdapter(adapter);

		pDialog = new ProgressDialog(PlaceDetailActivity.this);
		pDialog.setMessage("Loading details ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();
	}


	@Override
	public void updatePlaceDetails(PlaceDetails details) {
		// Log.d(TAG, "displaying details for  " + details.result.name);
		pDialog.dismiss();
		placeDetails = details;
		// updating UI from Background Thread
		runOnUiThread(new Runnable() {
			public void run() {
				if (placeDetails != null) {
					String status = placeDetails.status;
					final Place place = placeDetails.result;
					if (status.equals("OK") && place != null) {
						nameTV.setText(place.name);
						addressTV.setText(place.formatted_address);
						distanceTV.setText("Distance: " + distanceBetweenFormatted(place, myLocation));
						
						if (place.opening_hours != null) {
							String open_txt = (place.opening_hours.open_now ? OPEN
									: "");

        					if (place.opening_hours.periods != null && place.opening_hours.periods.length > 0
        					        && place.opening_hours.periods[0].open != null)
        					    open_txt += " " + place.opening_hours.periods[0].open.toString();
        					
        					if (place.opening_hours.periods != null && place.opening_hours.periods.length > 0
                                    && place.opening_hours.periods[0].close != null)
                                open_txt += "-" + place.opening_hours.periods[0].close.toString();
        					    
        					openingHoursTV.setText(open_txt);
        				}
						
						if (place.photos != null && place.photos.length > 0) {
                            pSearch.getPhotos(place.photos);
                        }
						
						ExpandListChild c = new ExpandListChild();
						String phone = place.formatted_phone_number == null ?
						        "Not present" : place.formatted_phone_number;
			            c.setName(phone);
			            adapter.addItem(c, contactGroup);
					}
				}
			}
		});
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle extras) {
		// TODO Auto-generated method stub
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

	@Override
	public void updatePhotos(final ArrayList<Bitmap> results) {
	    runOnUiThread(new Runnable() {
	       public void run() {
	           for (Bitmap bmp : results) {
	               ImageView i = (ImageView) findViewById(R.id.imageView1);
	               i.setImageBitmap(bmp);
	               ExpandListChild c = new ExpandListChild();
	               c.setBmp(bmp);
	               adapter.addItem(c, photoGroup);
	           }
	       }
	    });
	}

}
