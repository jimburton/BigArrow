package uk.ac.brighton.ci360.bigarrow;

import java.util.ArrayList;
import java.util.LinkedHashMap;

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
		    	
		    	final LinkedHashMap<String, String> details = place.getDetails();
		    	
		    	//loop through all details available in the hashmap
		    	for (String key : details.keySet())
		    	{
		    		ExpandListGroup group = new ExpandListGroup();
			        group.setName(key);
			        ExpandListChild child = new ExpandListChild();
			        child.setName(details.get(key));
			        child.setTag(null);
			        list2.add(child);
	
			        group.setItems(list2);      
			        list2 = new ArrayList<ExpandListChild>();
			        
			        list.add(group);
		    	}
				
				return list;
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
}
