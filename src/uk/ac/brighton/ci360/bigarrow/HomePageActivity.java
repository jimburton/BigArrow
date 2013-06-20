package uk.ac.brighton.ci360.bigarrow;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;

public class HomePageActivity extends Activity {
	
	private static final String TAG = "HomePageActivity";

	public void onCreate(Bundle inst) {
		super.onCreate(inst);
		setContentView(R.layout.activity_home);
	}
	
	@Override
    public boolean onCreateOptionsMenu(Menu menu) {
    	super.onCreateOptionsMenu(menu);
    	MenuInflater mi = getMenuInflater();
    	mi.inflate(R.menu.menu, menu);
    	return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
    	switch(item.getItemId()) {
    	case R.id.settings:
    		startActivity(new Intent(this, Prefs.class));
    		return true;
    	}
    	return false;
    }

	public void onClick(View v) {
		Intent i;
		switch(v.getId()) {
		case R.id.bigarrow_button:
			i = new Intent(this, BigArrowActivity.class);
			startActivity(i);
			break;
		case R.id.list_button:
			i = new Intent(this, MyListActivity.class);
			startActivity(i);
			break;
		case R.id.map_button:
			i = new Intent(this, MyMapActivity.class);
			startActivity(i);
			break;
		}
	}
	
	@Override
	protected void onPause() {
		super.onPause();
	}

}
