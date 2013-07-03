package uk.ac.brighton.ci360.bigarrow.ui;
/**
 * A group within the expandable list.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author Almas Baimagambetov
 */
import java.util.ArrayList;

public class ExpandListGroup {
 
	private String Name;
	private ArrayList<ExpandListChild> Items;
	
	public String getName() {
		return Name;
	}
	public void setName(String name) {
		this.Name = name;
	}
	public ArrayList<ExpandListChild> getItems() {
		return Items;
	}
	public void setItems(ArrayList<ExpandListChild> Items) {
		this.Items = Items;
	}
	
	
}
