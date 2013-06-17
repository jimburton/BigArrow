package uk.ac.brighton.ci360.bigarrow;

import android.app.ListActivity;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

/*
 * This app provides an example of connecting to a ContentProvider and binding it to a ListActivity.
 * Before installing, install the corresponding ContentProvider, uk.ac.brighton.ci360.notesclientprovider.
 */
public class MyListActivity extends ListActivity {

	private static final String TAG = "NotesClient";

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_mylist);
		
		/*helper = new NotesProviderHelper(this);
		Cursor notes = helper.getNotesCursor();

		String[] projection = { NotesProviderHelper.NOTES_ID, NotesProviderHelper.NOTES_TITLE };
		int[] to = new int[] { R.id.id_txt, R.id.title_text };
		// since v3 we should use LoaderManager and CursorLoader to avoid doing this in the UI
		// thread. See http://developer.android.com/guide/components/loaders.html
		@SuppressWarnings("deprecation")
		
		SimpleCursorAdapter sca = new SimpleCursorAdapter(this, R.layout.list_row,
				notes, projection, to);
		setListAdapter(sca);*/

	}

	@Override
	protected void onListItemClick(ListView l, View v, int position, long id) {
		super.onListItemClick(l, v, position, id);
		TextView tv = (TextView) v.findViewById(R.id.id_txt);
		long pubid = Long.parseLong(tv.getText().toString());
		Intent i = new Intent(this, MapActivity.class);
		i.putExtra(MapActivity.EXTRA_PUBID, pubid);
		startActivity(i);
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		getMenuInflater().inflate(R.menu.menu, menu);
		return true;
	}
}
