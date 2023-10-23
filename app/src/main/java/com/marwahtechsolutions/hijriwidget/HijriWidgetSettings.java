package com.marwahtechsolutions.hijriwidget;

import android.appwidget.AppWidgetManager;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

public class HijriWidgetSettings extends AppCompatActivity {

    private int appWidgetId = AppWidgetManager.INVALID_APPWIDGET_ID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.hijri_widget_settings);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new HijriWidgetSettingsFragment())
                    .commit();
        }

        Intent intent = getIntent();
        Bundle localBundle = intent.getExtras();
        if (localBundle != null) {
            appWidgetId = localBundle.getInt(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        UpdateWidget();
        return true;
    }

    @Override
    public void onBackPressed() {
        UpdateWidget();
    }

    private void UpdateWidget() {
        Intent intent = new Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this,
                HijriWidgetProvider.class
        );
        intent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, new int[]{appWidgetId});
        sendBroadcast(intent);
        setResult(RESULT_OK, intent);
        finish();
    }
}


