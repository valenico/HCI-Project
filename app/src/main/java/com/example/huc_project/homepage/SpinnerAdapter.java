package com.example.huc_project.homepage;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.core.widget.CompoundButtonCompat;

public class SpinnerAdapter extends ArrayAdapter  {
    private List<Boolean> checked;
    private List<String> array;
    private TextView titleView;
    RecyclerViewAdapter rva;

    SpinnerAdapter(@NonNull Context context, List<String> array, List<Boolean> checked, String title, RecyclerViewAdapter rva) {
        super(context, 0);
        this.array = array;
        this.rva = rva;
        titleView = new TextView(context);
        titleView.setText(title);
        titleView.setTextColor(Color.WHITE);
        this.checked = checked;
        titleView.setPadding(10, 3, 10, 3);
    }


    Integer getCheckedKeys(){
        Integer integer = 0;
        for(int index = 1; index < checked.size(); index++){
                if(checked.get(index)){
                    integer += 1;
                }
        }
        return integer;
    }

    List<String> getCheckedValues(){
        List<String> checked_values = new ArrayList<>();
        for(int index = 0; index < checked.size(); index++){
            if(checked.get(index)){
                checked_values.add(array.get(index));
            }
        }
        return checked_values;
    }

    class ViewHolder {
        CheckBox checkBox;
        TextView title;
    }

    private void handleDropDownHolder(final int position, ViewHolder holder){
        int states[][] = {{android.R.attr.state_checked}, {}};
        int colors[] = {Color.WHITE, Color.WHITE};
        holder.title.setText(array.get(position));
        holder.title.setTextColor(Color.WHITE);
        holder.checkBox.setChecked(checked.get(position));
        holder.checkBox.setTextColor(Color.WHITE);
        holder.checkBox.setOnCheckedChangeListener(null);
        CompoundButtonCompat.setButtonTintList(holder.checkBox, new ColorStateList(states, colors));
        holder.checkBox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View checkBox) {
                setChecked(checkBox, position);
                filtering(position);
            }
        });
    }

    void filtering(int position){
        this.rva.getFilter().filter(array.get(position));
    }

    void setChecked(View view, int position){
        Boolean n;
        if(checked.get(position)){
            ((CheckBox) view).setChecked(false);
            n = false;
        } else {
            ((CheckBox) view).setChecked(true);
            n = true;
        }
        checked.remove(position);
        checked.add(position,n );
    }

    @Override
    public int getCount() {
        return array.size();
    }

    @Override
    public String getItem(int position) {
        return array.get(position);
    }

    class Item extends LinearLayout {
        ViewHolder holder;
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        Item(Context context) {
            super(context);
            setLayoutParams(params);
            CheckBox checkBox = new CheckBox(context);
            TextView title = new TextView(context);
            title.setLayoutParams(params);
            holder = new ViewHolder();
            holder.title = title;
            holder.checkBox = checkBox;
            addView(checkBox);
            addView(title);
        }

        ViewHolder getHolder(){
            return holder;
        }
    }

    @Override
    public @NonNull View getDropDownView(int position, View view, @NonNull ViewGroup parent) {
        if(view == null){
            view = new Item(getContext());
        }
        handleDropDownHolder(position, ((Item) view).getHolder());
        return view;
    }

    @Override
    public @NonNull View getView(int position, View view, @NonNull ViewGroup parent) {
        return titleView;
    }


}