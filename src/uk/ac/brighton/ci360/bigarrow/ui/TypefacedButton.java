package uk.ac.brighton.ci360.bigarrow.ui;
/**
 * A custom button that uses a typeface specified in the layout file.
 * 
 * Copyright (c) 2013 The BigArrow authors (see the file AUTHORS).
 * See the file LICENSE for copying permission.
 * 
 * @author jb259
 **/
import uk.ac.brighton.ci360.bigarrow.R;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.Button;

public class TypefacedButton extends Button {

	public TypefacedButton(Context context) {
		super(context);
		// TODO Auto-generated constructor stub
	}

	public TypefacedButton(Context context, AttributeSet attrs) {
		super(context, attrs);

		// Typeface.createFromAsset doesn't work in the layout editor.
		// Skipping...
		if (isInEditMode()) {
			return;
		}

		TypedArray styledAttrs = context.obtainStyledAttributes(attrs,
				R.styleable.TypefacedTextView); 
		String fontName = styledAttrs
				.getString(R.styleable.TypefacedTextView_typeface);
		styledAttrs.recycle();

		if (fontName != null) {
			Typeface typeface = Typeface.createFromAsset(context.getAssets(),
					"fonts/"+fontName); 
			setTypeface(typeface);
		}
	}

}
