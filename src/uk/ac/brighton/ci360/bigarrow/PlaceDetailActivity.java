package uk.ac.brighton.ci360.bigarrow;

/**
 * This activity shows the full details of a selected place.
 * 
 * Copyright (c) 2013 University of Brighton.
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import java.util.ArrayList;
import java.util.LinkedHashMap;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import uk.ac.brighton.ci360.bigarrow.ui.ExpandListAdapter;
import uk.ac.brighton.ci360.bigarrow.ui.ExpandListChild;
import uk.ac.brighton.ci360.bigarrow.ui.ExpandListGroup;
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

	private TextView nameTV, openingHoursTV, addressTV;
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
		openingHoursTV = (TextView) findViewById(R.id.opening_hours_txt);
		addressTV = (TextView) findViewById(R.id.address_txt);
		expList = (ExpandableListView) findViewById(R.id.exp_list);
		
		ArrayList<ExpandListGroup> grps = new ArrayList<ExpandListGroup>();
		contactGroup = new ExpandListGroup();
		contactGroup.setName("Contact details");
		grps.add(contactGroup);
		
		photoGroup = new ExpandListGroup();
		contactGroup.setName("Photos");
		grps.add(photoGroup);
		
		reviewGroup = new ExpandListGroup();
		contactGroup.setName("Reviews");
		grps.add(reviewGroup);
		
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
					Place place = placeDetails.result;
					if (status.equals("OK") && place != null) {
						nameTV.setText(place.name);
						if (place.opening_hours != null) {
							String open_txt = (place.opening_hours.open_now ? OPEN
									: "");
							open_txt += place.opening_hours.toString();
							openingHoursTV.setText(open_txt);
						}
						addressTV.setText(place.formatted_address);
						if (place.photos != null && place.photos.length > 0) {
							pSearch.getPhotos(place.photos);
						}

						/*
						 * ExpandList = (ExpandableListView)
						 * findViewById(R.id.ExpList); ExpAdapter = new
						 * ExpandListAdapter( PlaceDetailActivity.this,
						 * getExpListItems(place));
						 * ExpandList.setAdapter(ExpAdapter);
						 */
					}
				}
			}

			/**
			 * @param place
			 *            - the place, details of which are asked
			 * @return - list of groups used for showing the expandable view to
			 *         the user
			 */
			private ArrayList<ExpandListGroup> getExpListItems(Place place) {
				ArrayList<ExpandListGroup> list = new ArrayList<ExpandListGroup>();
				ArrayList<ExpandListChild> list2 = new ArrayList<ExpandListChild>();

				final LinkedHashMap<String, String> details = place
						.getDetails();

				// loop through all details available in the hashmap
				for (String key : details.keySet()) {
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

	@Override
	public void updatePhotos(ArrayList<Bitmap> results) {
		for (Bitmap bmp : results) {
			ImageView i = new ImageView(this);
			i.setImageBitmap(bmp);
			ExpandListChild c = new ExpandListChild();
			c.setBmp(bmp);
			adapter.addItem(c, photoGroup);
		}

	}
}
