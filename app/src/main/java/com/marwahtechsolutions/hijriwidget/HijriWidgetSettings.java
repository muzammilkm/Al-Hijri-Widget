package com.marwahtechsolutions.hijriwidget;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;


public class HijriWidgetSettings extends AppCompatActivity {

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


        AppWidgetManager manager = AppWidgetManager.getInstance(this);
        ComponentName largeComponentName = new ComponentName(this, HijriWidgetProviderLarge.class);
        int[] appLargeWidgetIds = manager.getAppWidgetIds(largeComponentName);

        ComponentName smallComponentName = new ComponentName(this, HijriWidgetProviderSmall.class);
        int[] appSmallWidgetIds = manager.getAppWidgetIds(smallComponentName);

        Intent largeIntent = new Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this,
                HijriWidgetProviderLarge.class
        );
        largeIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appLargeWidgetIds);
        sendBroadcast(largeIntent);


        Intent smallIntent = new Intent(
                AppWidgetManager.ACTION_APPWIDGET_UPDATE, null, this,
                HijriWidgetProviderSmall.class
        );
        smallIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_IDS, appSmallWidgetIds);
        sendBroadcast(smallIntent);

        setResult(RESULT_OK, largeIntent);
        setResult(RESULT_OK, smallIntent);
        finish();
    }
}


