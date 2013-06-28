package uk.ac.brighton.ci360.bigarrow.classes;

import android.annotation.SuppressLint;
import uk.ac.brighton.ci360.bigarrow.places.Place.Detail;

public class Utils
{
	public static final String NO_DATA = "Not present";
	
	public static String format(String detail) {
		return detail == null ? NO_DATA : detail;
	}
	
	public static String format(double detail) {
		return detail == 0.0 ? NO_DATA : Double.toString(detail);
	}
	
	@SuppressLint("DefaultLocale")
	public static String format(Detail detail) {
		return detail.name().toLowerCase().replace('_', ' ');
	}
}
