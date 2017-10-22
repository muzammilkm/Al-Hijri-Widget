package com.marwahtechsolutions.hijriwidget.preferences;

import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

import com.marwahtechsolutions.hijriwidget.R;

/**
 * Created by Muzammil Khaja Mohammed on 10/10/2017.
 */

public class LanguagePreference extends ListPreference {
    public LanguagePreference (Context context, AttributeSet attrs) {
        super(context, attrs);
        super.setDefaultValue(context.getResources().getString(R.string.app_setting_default_language));
    }

    @Override
    public CharSequence getSummary() {
        // Format summary string with current value
        String summary = super.getSummary().toString();
        String defaultLanguage = getContext().getResources().getString(R.string.app_setting_default_language);
        return String.format(summary, super.getPersistedString(defaultLanguage));
    }
}
