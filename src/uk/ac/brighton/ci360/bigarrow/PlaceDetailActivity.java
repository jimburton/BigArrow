package uk.ac.brighton.ci360.bigarrow;

import java.util.ArrayList;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import uk.ac.brighton.ci360.bigarrow.adapter.*;
import uk.ac.brighton.ci360.bigarrow.classes.*;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.widget.ExpandableListView;

public class PlaceDetailActivity extends PlaceSearchActivity {

	private static final String TAG = "PlaceDetailActivity";
	PlaceDetails placeDetails;
	ProgressDialog pDialog;
	
	private ExpandListAdapter ExpAdapter;
	private ExpandableListView ExpandList;
	

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
		Log.d(TAG, "displaying details for  " + details.result.name);
		pDialog.dismiss();
		placeDetails = details;
		// updating UI from Background Thread
		runOnUiThread(new Runnable() {
			public void run() {
				if (placeDetails != null) 
				{
					String status = placeDetails.status;
					Place place = placeDetails.result;
					if (status.equals("OK") && place != null) {
						ExpandList = (ExpandableListView) findViewById(R.id.ExpList);
				        ExpAdapter = new ExpandListAdapter(PlaceDetailActivity.this, getExpListItems(place));
				        ExpandList.setAdapter(ExpAdapter);
					}
				}
			}

			/**
			 * @param place - the place, details of which are asked
			 * @return - list of groups used for showing the expandable view to the user
			 */
			private ArrayList<ExpandListGroup> getExpListItems(Place place)
			{
				ArrayList<ExpandListGroup> list = new ArrayList<ExpandListGroup>();
		    	ArrayList<ExpandListChild> list2 = new ArrayList<ExpandListChild>();
				
				String[] groupNames = {"Name", "Address", "Phone", "Location", "Rating", "Opening Hours"};
				String[] childNames = getFormattedPlaceDetails(place);
				
				for (int i = 0; i < groupNames.length; i++)
				{
					ExpandListGroup group = new ExpandListGroup();
			        group.setName(groupNames[i]);
			        ExpandListChild child = new ExpandListChild();
			        child.setName(childNames[i]);
			        child.setTag(null);
			        list2.add(child);
	
			        group.setItems(list2);      
			        list2 = new ArrayList<ExpandListChild>();
			        
			        list.add(group);
				}
				
				return list;
			}
			
			/**
			 * @param place - the place, details of which are asked
			 * @return - a String array containing correctly formatted (no nulls)
			 * details of the place in order
			 */
			private String[] getFormattedPlaceDetails(Place place)
			{
				String[] details = new String[6];
				
				//I have no choice at the moment
				//However I plan to replace it with enums and dynamic code
				details[0] = format(place.name);
				details[1] = format(place.formatted_address);
				details[2] = format(place.formatted_phone_number);
				details[3] = "Latitude: " + format(Double.toString(place.getLatLng().latitude))
						+ " Longitude: " + format(Double.toString(place.getLatLng().longitude));
				details[4] = format(Double.toString(place.rating));
				details[5] = place.opening_hours == null ? "Not present" : (place.opening_hours.open_now ? "open now" : "closed");
				
				return details;
			}
			
			private String format(String detail) {
				return detail == null ? "Not present" : detail;
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
