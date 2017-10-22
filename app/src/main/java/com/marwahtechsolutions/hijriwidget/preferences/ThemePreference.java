package com.marwahtechsolutions.hijriwidget.preferences;

import android.content.Context;
import android.graphics.Color;
import android.preference.DialogPreference;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.marwahtechsolutions.components.palette.ColorPaletteLayout;
import com.marwahtechsolutions.hijriwidget.R;

public class ThemePreference extends DialogPreference implements
		OnSeekBarChangeListener {

	private final int mMaxValue = 100;
	private final int mMinValue = 0;
	private int mOpacity;
	private int mColor;

	private SeekBar mSeekBar;
	private ColorPaletteLayout colorPaletteLayout;

	public ThemePreference(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	@Override
	protected View onCreateDialogView() {
        int defaultColor = getContext().getResources().getColor(R.color.defaultThemeColor);
		mColor = getPersistedInt(defaultColor);
		mOpacity = (int) (Color.alpha(mColor) / 2.55);
		LayoutInflater inflater = (LayoutInflater) getContext()
				.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		View view = inflater.inflate(R.layout.pref_theme, null);

		colorPaletteLayout = (ColorPaletteLayout) view.findViewById(R.id.color_palette);
		colorPaletteLayout.setOpacity((int) (mOpacity * 2.55));
		colorPaletteLayout.setSelectedColor(mColor);

		mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
		mSeekBar.setMax(mMaxValue - mMinValue);
		mSeekBar.setProgress(mOpacity - mMinValue);
		mSeekBar.setOnSeekBarChangeListener(this);

		return view;
	}

	@Override
	protected void onDialogClosed(boolean positiveResult) {
		super.onDialogClosed(positiveResult);
		if (!positiveResult) {
			return;
		}
		if (shouldPersist()) {
			persistInt(colorPaletteLayout.getColor());
		}
		notifyChanged();
	}

	@Override
	public CharSequence getSummary() {
		String summary = super.getSummary().toString();
		int value = getPersistedInt(mColor);
		return String.format(summary, value);
	}

	public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
		mOpacity = value + mMinValue;
		colorPaletteLayout.setOpacity((int) (mOpacity * 2.55));
		mColor = colorPaletteLayout.getColor();
	}

	public void onStartTrackingTouch(SeekBar seek) {
		// Not used
	}

	public void onStopTrackingTouch(SeekBar seek) {
		// Not used
	}
}
