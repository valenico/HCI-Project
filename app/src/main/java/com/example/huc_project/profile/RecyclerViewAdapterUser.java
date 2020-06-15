package com.example.huc_project.profile;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huc_project.R;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;

public class RecyclerViewAdapterUser extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private RecyclerViewAdapter.OnItemListener onItemListener;

    public List<UserRow> itemsList;
    public List<UserRow> itemsListFull;

    public RecyclerViewAdapterUser(List<UserRow> itemList, RecyclerViewAdapter.OnItemListener onItemListener) {
        this.itemsList = itemList;
        this.onItemListener = onItemListener;
        itemsListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_row_user, parent, false);
            return new ItemViewHolder(view);
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

    @Override
    public Filter getFilter() {
        return null;
    }


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tvItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            tvItem = itemView.findViewById(R.id.relative_user);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.itemRow_progressBar);
        }
    }

    private void showLoadingView(LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    @SuppressLint("SetTextI18n")
    private void populateItemRows(ItemViewHolder viewHolder, int position) {
        UserRow item = itemsList.get(position);
        String name = item.user;
        StorageReference uid = item.uid;
        TextView tv1 = (TextView) viewHolder.tvItem.getChildAt(1);
        ImageView view = (ImageView) viewHolder.tvItem.getChildAt(0);
        tv1.setText(name);
        Favorite.glideTask(item.glide, uid, view);
    }
}
