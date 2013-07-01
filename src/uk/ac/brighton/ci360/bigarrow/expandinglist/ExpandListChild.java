package uk.ac.brighton.ci360.bigarrow.expandinglist;

import android.graphics.Bitmap;

public class ExpandListChild {

	private String Name;
	private String Tag;
	private Bitmap bmp;
	
	public String getName() {
		return Name;
	}
	public void setName(String Name) {
		this.Name = Name;
	}
	public String getTag() {
		return Tag;
	}
	public void setTag(String Tag) {
		this.Tag = Tag;
	}
	public Bitmap getBmp() {
		return bmp;
	}
	public void setBmp(Bitmap bmp) {
		this.bmp = bmp;
	}
}
