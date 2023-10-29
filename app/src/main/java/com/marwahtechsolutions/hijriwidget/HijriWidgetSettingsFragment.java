package com.marwahtechsolutions.hijriwidget;

import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;

import com.marwahtechsolutions.hijriwidget.preference.TimePreference;
import com.marwahtechsolutions.hijriwidget.preference.TimePreferenceDialogFragmentCompat;

public class HijriWidgetSettingsFragment extends PreferenceFragmentCompat {
    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.hijri_widget_settings, rootKey);
    }

    private static final String DIALOG_FRAGMENT_TAG = "TimePreference";

    @Override
    public void onDisplayPreferenceDialog(Preference preference) {
        if (getParentFragmentManager().findFragmentByTag(DIALOG_FRAGMENT_TAG) != null) {
            return;
        }

        if (preference instanceof TimePreference) {
            final DialogFragment f = new TimePreferenceDialogFragmentCompat((TimePreference) preference);

            f.setTargetFragment(this, 0);
            f.show(getParentFragmentManager(), DIALOG_FRAGMENT_TAG);
        } else {
            super.onDisplayPreferenceDialog(preference);
        }
    }
}
