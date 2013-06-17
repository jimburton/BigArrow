package uk.ac.brighton.ci360.bigarrow;

import android.app.Activity;
import android.os.Bundle;

public class MapActivity extends Activity {
	
	public static final String EXTRA_PUBID = "PUBID";

	@Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);
    }

} 