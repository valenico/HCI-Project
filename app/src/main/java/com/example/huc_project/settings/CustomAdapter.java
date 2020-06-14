package com.example.huc_project.settings;

import android.graphics.Color;
import android.graphics.Typeface;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.example.huc_project.R;
import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

public class CustomAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder>  {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<String> itemsList;
    public List<String> itemsListFull;
    private OnItemListener onItemListener;

    public CustomAdapter() {}

    public CustomAdapter(List<String> itemList,  OnItemListener onItemListener) {
        this.itemsList = itemList;
        this.itemsListFull = new ArrayList<>(itemList);
        this.onItemListener = onItemListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.setting_main_item, parent, false);
            return new ItemViewHolder(view, onItemListener);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_loading, parent, false);
            return new LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof ItemViewHolder) {
            populateItemRows((ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof LoadingViewHolder) {
            showLoadingView((LoadingViewHolder) viewHolder, position);
        }

    }

    @Override
    public int getItemCount() {

        return itemsList == null ? 0 : itemsList.size();
    }

    @Override
    public int getItemViewType(int position) {
        return itemsList.get(position) == null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        RelativeLayout tvItem;
        OnItemListener onItemListener;

        public ItemViewHolder(@NonNull View itemView, OnItemListener onItemListener) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.text_settings);
            this.onItemListener = onItemListener;
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            onItemListener.onItemClick(getAdapterPosition());
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.itemRow_progressBar);
        }

    }


    public interface OnItemListener{
        void onItemClick(int position);
    }


    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(final ItemViewHolder viewHolder, int position) {
        final String item = itemsList.get(position);
        ((TextView)viewHolder.tvItem.getChildAt(0)).setText(item);

        if(item.equals("General") || item.equals("Help & About") || item.equals("Privacy & Security") || item.equals("Invite Friends")) {
            ((TextView)viewHolder.tvItem.getChildAt(0)).setTextSize(25);
            ((TextView)viewHolder.tvItem.getChildAt(0)).setTypeface( null, Typeface.BOLD);
            ((TextView)viewHolder.tvItem.getChildAt(0)).setGravity(Gravity.CENTER_HORIZONTAL);
            ((CardView)viewHolder.tvItem.getParent()).setCardBackgroundColor(0);
        } else {
            ((TextView)viewHolder.tvItem.getChildAt(0)).setTextSize(15);
            ((TextView)viewHolder.tvItem.getChildAt(0)).setGravity(Gravity.START);
            ((TextView)viewHolder.tvItem.getChildAt(0)).setTypeface( null, Typeface.NORMAL);
            ((CardView)viewHolder.tvItem.getParent()).setCardBackgroundColor(Color.parseColor("#e6e6e6"));
        }

    }





}