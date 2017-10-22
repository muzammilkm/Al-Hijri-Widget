package com.marwahtechsolutions.components.palette;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.Gravity;
import android.widget.Checkable;
import android.widget.GridLayout;
import com.marwahtechsolutions.hijriwidget.R;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Muzammil Khaja Mohamemd on 10/8/2017
 */

public class ColorPaletteLayout extends GridLayout implements Checkable {
    List<ColorView> colorViews;

    public ColorPaletteLayout(Context context) {
        super(context);
        init(context, null);
    }

    public ColorPaletteLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public void init(Context context, AttributeSet attrs) {

        int[] attrsArray = new int[]{
                R.attr.palette,
                R.attr.selectorHeight,
                R.attr.selectorWidth
        };

        TypedArray ta = context.obtainStyledAttributes(attrs, attrsArray);
        CharSequence[] colors = ta.getTextArray(0);
        int itemHeight = ta.getDimensionPixelSize(1, 0);
        int itemWidth = ta.getDimensionPixelSize(2, 0);
        ta.recycle();

        int rows = this.getRowCount();
        int columns = this.getColumnCount();


        colorViews = new ArrayList<>();

        for (int r = 0, i = 0; r < rows; r++) {
            for (int c = 0; c < columns && i < colors.length; c++) {
                ColorView colorView = new ColorView(context, attrs);

                GridLayout.LayoutParams param = new LayoutParams();
                param.setGravity(Gravity.CENTER);
                param.columnSpec = GridLayout.spec(c);
                param.rowSpec = GridLayout.spec(r);

                colorView.setLayoutParams(param);
                colorView.setColor(Color.parseColor(colors[i].toString()));
                colorView.setSize(itemWidth, itemHeight);
                colorView.setParent(this);
                colorView.setSelectionColor(Color.parseColor("#AAAAAA"));
                colorViews.add(colorView);

                this.addView(colorView);
                i++;
            }
        }
    }

    @Override
    public void setChecked(boolean checked) {
        for (ColorView colorView : colorViews) {
            colorView.setChecked(checked);
        }
    }

    @Override
    public boolean isChecked() {
        for (ColorView colorView : colorViews) {
            if (colorView.isChecked()) {
                return true;
            }
        }
        return false;
    }

    @Override
    public void toggle() {
        for (ColorView colorView : colorViews) {
            colorView.setChecked(false);
        }
    }

    public int getColor(){
        for (ColorView colorView : colorViews) {
            if (colorView.isChecked()) {
                return colorView.getColor();
            }
        }
        return 0;
    }

    public void setSelectedColor(int mColor) {
        for (ColorView colorView : colorViews) {
            colorView.setChecked(colorView.isSame(mColor));
        }
    }

    public void setOpacity(int opacity){
        for (ColorView colorView : colorViews) {
            colorView.setOpacity(opacity);
        }
        //super.setAlpha(alpha);
    }
}
