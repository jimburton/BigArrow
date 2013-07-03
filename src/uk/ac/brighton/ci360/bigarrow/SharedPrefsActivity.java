package uk.ac.brighton.ci360.bigarrow;
/**
 * This subclass of PreferenceActivity handles the behaviour of the settings menu.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 */
import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchEstab;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

@SuppressLint("DefaultLocale")
public class SharedPrefsActivity extends PreferenceActivity {
	
	public static final String OPT_SEARCH_TYPE = "pref_search_type";
	public static final String OPT_SEARCH_TYPE_DEF = SearchEstab.BAR.label().toLowerCase();

	
	@SuppressWarnings("deprecation")
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static SearchEstab getSearchType(Context con) {
		String estabStr = PreferenceManager.getDefaultSharedPreferences(con)
				.getString(OPT_SEARCH_TYPE,  OPT_SEARCH_TYPE_DEF);
		return SearchEstab.valueOf(estabStr.toUpperCase()); 
	}

}
