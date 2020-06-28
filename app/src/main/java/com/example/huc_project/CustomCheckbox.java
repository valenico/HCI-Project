package com.example.huc_project;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;

public class CustomCheckbox extends androidx.appcompat.widget.AppCompatCheckBox{
    public CustomCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked) this.setBackgroundResource(R.drawable.selected);
        else this.setBackgroundResource(R.drawable.deselected);
        super.setChecked(checked);
    }
}
