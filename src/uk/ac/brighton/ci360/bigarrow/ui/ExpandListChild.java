package uk.ac.brighton.ci360.bigarrow.ui;
/**
 * A child of the expandable list.
 * 
 * Copyright (c) 2013 University of Brighton.
 * See the file LICENSE for copying permission.
 * 
 * @author Almas Baimagambetov
 */
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
