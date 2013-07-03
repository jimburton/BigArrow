package uk.ac.brighton.ci360.bigarrow;

/**
 * This activity shows the nearest n places in a ListView.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import java.util.ArrayList;
import java.util.HashMap;

import uk.ac.brighton.ci360.bigarrow.places.Place;
import uk.ac.brighton.ci360.bigarrow.places.PlaceDetails;
import uk.ac.brighton.ci360.bigarrow.places.PlacesList;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;


public class MyListActivity extends PlaceSearchActivity {

	private static final String TAG = "MyListActivity";
	ArrayList<HashMap<String, String>> placesListItems = new ArrayList<HashMap<String,String>>();
    ListView lv;
	private ProgressDialog pDialog;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		firstSearchType = SearchType.MANY;
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylist);
		
		pDialog = new ProgressDialog(MyListActivity.this);
		pDialog.setMessage("Loading details ...");
		pDialog.setIndeterminate(false);
		pDialog.setCancelable(false);
		pDialog.show();

		lv = (ListView)findViewById(R.id.list_view);
		//lv.setAdapter(adapter);
		lv.setOnItemClickListener(new OnItemClickListener()
		{
			@Override
            public void onItemClick(AdapterView<?> parent, View view,
                    int position, long id) {
                String reference = ((TextView) view.findViewById(R.id.id_reference)).getText().toString();
                Log.d(TAG, reference);
                Intent in = new Intent(getApplicationContext(), PlaceDetailActivity.class);
                in.putExtra(PlaceDetails.KEY_REFERENCE, reference);
                startActivity(in);
            }
		});
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}

	@Override
	public void updateNearestPlace(Place place, Location location, float distance) {
		// TODO Auto-generated method stub
		Log.d(TAG, "updateNearestPlaces");
	}

	@Override
	public void updateNearestPlaces(PlacesList places) {
		pDialog.dismiss();
		Log.d(TAG, "Got "+places.results.size()+" places to show in list");
		if (places.results != null) {
            // loop through each place
            for (Place p : places.results) {
                HashMap<String, String> map = new HashMap<String, String>();
                map.put(PlaceDetails.KEY_REFERENCE, p.reference);
                map.put(PlaceDetails.KEY_NAME, p.name);
                placesListItems.add(map);
            }
            // list adapter
            ListAdapter adapter = new SimpleAdapter(MyListActivity.this, placesListItems,
                    R.layout.list_row,
                    new String[] { PlaceDetails.KEY_REFERENCE, PlaceDetails.KEY_NAME}, new int[] {
                            R.id.id_reference, R.id.id_name });
             
            // Adding data into listview
            lv.setAdapter(adapter);
        }
		
	}

	@Override
	public void onLocationChanged(Location arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderDisabled(String provider) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onProviderEnabled(String provider) {
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
