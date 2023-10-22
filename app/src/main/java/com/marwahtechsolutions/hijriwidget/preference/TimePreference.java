package com.marwahtechsolutions.hijriwidget.preference;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.preference.DialogPreference;

import com.marwahtechsolutions.hijriwidget.models.Time;

public class TimePreference extends DialogPreference {
    public int hour = 0;
    public int minute = 0;

    public TimePreference(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected Object onGetDefaultValue(TypedArray a, int index) {
        return a.getString(index);
    }

    @Override
    protected void onSetInitialValue(boolean restoreValue, Object defaultValue) {
        String value;
        if (restoreValue) {
            if (defaultValue == null)
                value = getPersistedString("00:00");
            else
                value = getPersistedString(defaultValue.toString());
        } else
            value = defaultValue.toString();

        hour = Time.parseHour(value);
        minute = Time.parseMinute(value);
    }

    public void persistStringValue(String value) {
        persistString(value);
    }

    @Override
    public CharSequence getSummary() {
        return Time.to12HourFormat(hour, minute);
    }
}
