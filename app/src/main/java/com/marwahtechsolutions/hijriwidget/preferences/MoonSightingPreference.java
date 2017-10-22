package com.marwahtechsolutions.hijriwidget.preferences;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.preference.DialogPreference;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TimePicker;
import com.marwahtechsolutions.hijriwidget.R;
import com.marwahtechsolutions.hijriwidget.models.HijriAdjustor;

public class MoonSightingPreference extends DialogPreference implements
        OnSeekBarChangeListener {

    private HijriAdjustor hijriAdjustor;
    private final int mMaxValue =10;
    private final int mMinValue = -10;
    private TimePicker timePicker;
    private SeekBar mSeekBar;
    private TextView mValueText;
    private String defaultValue;

    public MoonSightingPreference(Context context, AttributeSet attrs) {
        super(context, attrs);
        defaultValue = getContext().getResources().getString(R.string.app_setting_default_moonSighting);

        setPositiveButtonText("Set");
        setNegativeButtonText("Cancel");
    }

    @Override
    protected View onCreateDialogView() {
        LayoutInflater inflater = (LayoutInflater) getContext()
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.pref_moon_sighting, null);

        hijriAdjustor = new HijriAdjustor(getPersistedString(defaultValue));

        ((TextView) view.findViewById(R.id.min_value)).setText(Integer
                .toString(mMinValue));
        ((TextView) view.findViewById(R.id.max_value)).setText(Integer
                .toString(mMaxValue));
        mSeekBar = (SeekBar) view.findViewById(R.id.seek_bar);
        timePicker = (TimePicker) view.findViewById(R.id.updateDateAt);
        mSeekBar.setMax(mMaxValue - mMinValue);
        mSeekBar.setProgress(hijriAdjustor.getDays() - mMinValue);
        mSeekBar.setOnSeekBarChangeListener(this);
        timePicker.setCurrentHour(hijriAdjustor.getHour());
        timePicker.setCurrentMinute(hijriAdjustor.getMinute());
        mValueText = (TextView) view.findViewById(R.id.current_value);
        mValueText.setText(Integer.toString(hijriAdjustor.getDays()));

        return view;
    }

    @Override
    protected void onDialogClosed(boolean positiveResult) {
        super.onDialogClosed(positiveResult);
        if (!positiveResult) {
            return;
        }
        if (shouldPersist()) {
            hijriAdjustor.setDays(mSeekBar.getProgress()+ mMinValue);
            hijriAdjustor.setHour(timePicker.getCurrentHour());
            hijriAdjustor.setMinute(timePicker.getCurrentMinute());
            persistString(hijriAdjustor.toString());
        }
        notifyChanged();
    }

    @Override
    public CharSequence getSummary() {
        String summary = super.getSummary().toString();
        HijriAdjustor ha = new HijriAdjustor(getPersistedString(defaultValue));
        return String.format(summary, ha.timeFormat(), ha.daysFormat());
    }

    public void onProgressChanged(SeekBar seek, int value, boolean fromTouch) {
        mValueText.setText(Integer.toString(value + mMinValue));
    }

    public void onStartTrackingTouch(SeekBar seek) {
        // Not used
    }

    public void onStopTrackingTouch(SeekBar seek) {
        // Not used
    }
}