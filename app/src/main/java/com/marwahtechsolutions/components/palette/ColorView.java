package com.marwahtechsolutions.components.palette;


import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.graphics.drawable.ShapeDrawable;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.Checkable;
import android.widget.ImageButton;

/**
 * TODO: document your custom view class.
 */
public class ColorView extends ImageButton implements Checkable, View.OnClickListener {
    private boolean isSelected;
    private GradientDrawable unselected, selected;
    private int width, height, color;
    private Checkable parent;
    private int r, b, g, a;
    private final int outer_stroke = 15, inner_stroke = 5;

    public ColorView(Context context) {
        super(context);
        init();
    }

    public ColorView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public void init() {

        unselected = new GradientDrawable();
        selected = new GradientDrawable();

        unselected.setShape(GradientDrawable.OVAL);
        selected.setShape(GradientDrawable.OVAL);

        this.setChecked(false);
        this.setClickable(true);
        this.setOnClickListener(this);
    }

    public void setChecked(boolean checked) {
        isSelected = checked;
        if (isSelected) {
            this.setBackground(selected);
        } else {
            this.setBackground(unselected);
        }
    }

    @Override
    public boolean isChecked() {
        return isSelected;
    }

    @Override
    public void toggle() {
        setChecked(!isSelected);
    }

    public void setSelectionColor(int color) {
        selected.setStroke(inner_stroke, color);
        unselected.setStroke(inner_stroke, Color.TRANSPARENT);
    }

    public void setSize(int width, int height) {
        ViewGroup.LayoutParams params = this.getLayoutParams();
        params.height = height;
        params.width = width;
        this.setLayoutParams(params);
        selected.setSize(width, height);
        unselected.setSize(width, height);
    }

    public void setColor(int color) {
        this.r = Color.red(color);
        this.g = Color.green(color);
        this.b = Color.blue(color);

        this.color = color;
        selected.setColor(color);
        unselected.setColor(color);
    }

    public boolean isSame(int color){
        int r = Color.red(color),
            g = Color.green(color),
            b = Color.blue(color);

        return this.r == r && this.g == g && this.b == b;
    }

    public void setOpacity(int opacity) {
        this.a = opacity;
        color = Color.argb(a, r, g, b);
        selected.setColor(color);
        unselected.setColor(color);
    }

    public void setParent(Checkable parent) {
        this.parent = parent;
    }

    public int getColor() {
        return this.color;
    }

    @Override
    public void onClick(View v) {
        if (!this.isChecked()) {
            this.startAnimation(AnimationUtils.loadAnimation(getContext(), android.R.anim.fade_in));
            this.parent.toggle();
            this.toggle();
        }
    }
}
