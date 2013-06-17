package uk.ac.brighton.ci360.bigarrow;

import android.content.Context;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;

public class Prefs extends PreferenceActivity {
	
	public static final String OPT_MUSIC = "music";
	public static final boolean OPT_MUSIC_DEF = true;
	public static final String OPT_HINTS = "hints";
	public static final boolean OPT_HINTS_DEF = true;
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		addPreferencesFromResource(R.xml.settings);
	}
	
	public static boolean getMusic(Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con)
				.getBoolean(OPT_MUSIC,  OPT_MUSIC_DEF);
	}
	
	public static boolean getHints(Context con) {
		return PreferenceManager.getDefaultSharedPreferences(con)
				.getBoolean(OPT_HINTS,  OPT_HINTS_DEF);
	}

}
