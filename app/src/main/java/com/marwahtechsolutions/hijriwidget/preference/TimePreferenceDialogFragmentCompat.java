package com.marwahtechsolutions.hijriwidget.preference;

import androidx.preference.DialogPreference;
import androidx.preference.Preference;
import androidx.preference.PreferenceDialogFragmentCompat;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;

import com.marwahtechsolutions.hijriwidget.models.Time;

public class TimePreferenceDialogFragmentCompat extends PreferenceDialogFragmentCompat
        implements DialogPreference.TargetFragment {
    private final TimePreference preference;
    TimePicker timePicker = null;


    public TimePreferenceDialogFragmentCompat(TimePreference preference) {
        this.preference = preference;

        final Bundle b = new Bundle();
        b.putString(ARG_KEY, preference.getKey());
        setArguments(b);
    }

    @Override
    protected View onCreateDialogView(Context context) {
        timePicker = new TimePicker(context);
        return (timePicker);
    }

    @Override
    protected void onBindDialogView(View v) {
        super.onBindDialogView(v);
        timePicker.setIs24HourView(false);
        TimePreference pref = (TimePreference) getPreference();
        timePicker.setHour(pref.hour);
        timePicker.setMinute(pref.minute);
    }

    @Override
    public void onDialogClosed(boolean positiveResult) {
        if (positiveResult) {
            TimePreference pref = (TimePreference) getPreference();
            pref.hour = timePicker.getHour();
            pref.minute = timePicker.getMinute();

            String value = Time.toString(pref.hour, pref.minute);
            pref.setSummary(Time.to12HourFormat(pref.hour, pref.minute));
            if (pref.callChangeListener(value))
                pref.persistStringValue(value);
        }
    }

    @Override
    public Preference findPreference(CharSequence charSequence) {
        return getPreference();
    }
}