package com.example.huc_project.ui.login;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huc_project.R;
import com.jh.circularlist.CircularAdapter;

import java.util.ArrayList;

// you should extends CircularAdapter to add your custom item
public class CircularItemAdapter extends CircularAdapter {

    private ArrayList<Integer> mItems;       // custom data, here we simply use string
    private LayoutInflater mInflater;
    private ArrayList<View> mItemViews;     // to store all list item

    public CircularItemAdapter(LayoutInflater inflater, ArrayList<Integer> items){
        this.mItemViews = new ArrayList<>();
        this.mItems = items;
        this.mInflater = inflater;

        for(final int s : mItems){
            View view = mInflater.inflate(R.layout.interests_item, null);
            ImageView itemView = view.findViewById(R.id.item_icon);
            itemView.setImageResource(s);
            mItemViews.add(view);
        }
    }

    @Override
    public ArrayList<View> getAllViews() {
        return mItemViews;
    }

    @Override
    public int getCount() {
        return mItemViews.size();
    }

    @Override
    public View getItemAt(int i) {
        return mItemViews.get(i);
    }

    @Override
    public void removeItemAt(int i) {
        if(mItemViews.size() > 0) {
            // remove from view list
            mItemViews.remove(i);
            // this is necessary to call to notify change
            notifyItemChange();
        }
    }

    @Override
    public void addItem(View view) {
        // add to view list
        mItemViews.add(view);
        // // this is necessary to call to notify change
        notifyItemChange();
    }
}
