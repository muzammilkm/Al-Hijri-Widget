package com.marwahtechsolutions.hijriwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.OnSharedPreferenceChangeListener;
import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceManager;
import android.view.KeyEvent;

public class HijriConfig extends PreferenceActivity {
	private static final String TAG = "Al Hijri Widget";
	private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setResult(RESULT_CANCELED);

		Intent intent = getIntent();
		Bundle localBundle = intent.getExtras();
		if (localBundle != null) {
			appWidgetId = localBundle.getInt(
					AppWidgetManager.EXTRA_APPWIDGET_ID,
					AppWidgetManager.INVALID_APPWIDGET_ID);

		}
		addPreferencesFromResource(R.xml.settings);
	}

	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		if (keyCode == KeyEvent.KEYCODE_BACK) {
			Intent result = new Intent(this, HijriWidgetProvider.class);
			result.setAction(AppWidgetManager.ACTION_APPWIDGET_UPDATE);
			result.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetId);
			setResult(RESULT_OK, result);
			sendBroadcast(result);
			finish();
		}
		return (super.onKeyDown(keyCode, event));
	}
}
