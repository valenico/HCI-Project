package com.example.huc_project;

import android.content.Context;
import android.util.AttributeSet;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

public class CustomCheckbox extends androidx.appcompat.widget.AppCompatCheckBox{
    private int maxLimit = 3;
    public CustomCheckbox(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public void setChecked(boolean checked) {
        if(checked) this.setBackgroundResource(R.drawable.selected);
        else this.setBackgroundResource(R.drawable.deselected);
        super.setChecked(checked);
    }

    int count=0;
    OnCheckedChangeListener checker = new OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if(count == maxLimit && isChecked){
                buttonView.setChecked(false);
                Toast.makeText(getContext(),
                        "Limit reached!!!", Toast.LENGTH_SHORT).show();
            }else if(isChecked){

                count++;
            }else if(!isChecked){
                count--;
            }
        }
    };




}
