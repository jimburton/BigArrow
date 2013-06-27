package uk.ac.brighton.ci360.bigarrow;

import uk.ac.brighton.ci360.bigarrow.PlaceSearchRequester.SearchEstab;
import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	
	public static final String OPT_SEARCH_TYPE = "pref_search_type";
	public static final String OPT_SEARCH_TYPE_DEF = SearchEstab.BAR.label().toLowerCase();

	
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
