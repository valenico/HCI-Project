package com.example.huc_project.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.PostRow;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.opencensus.resource.Resource;

public class RecyclerViewAdapterSocial extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<SocialRow> itemsList;
    public List<SocialRow> itemsListFull;

    public RecyclerViewAdapterSocial(List<SocialRow> itemList) {
        this.itemsList = itemList;
        itemsListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_row_social, parent, false);
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
            tvItem = itemView.findViewById(R.id.relative_social);
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
        SocialRow item = itemsList.get(position);
        String name = item.social.name;
        String followers = item.social.followers;
        String identity = item.social.identity;
        String lw = item.social.lw;
        String lm = item.social.lm;
        TextView tv1 = (TextView) viewHolder.tvItem.getChildAt(1);
        TextView tv2 = (TextView) viewHolder.tvItem.getChildAt(2);
        TextView tv3 = (TextView) viewHolder.tvItem.getChildAt(4);
        TextView tv4 = (TextView) viewHolder.tvItem.getChildAt(5);
        ImageView view = (ImageView) viewHolder.tvItem.getChildAt(0);
        tv1.setText(name);
        tv2.setText(followers + " followers");
        tv3.setText("last month: " + lm + "%");
        tv4.setText("last week: " + lw + "%");
        switch (name) {
            case "instagram":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_instagram, view);
                break;
            case "facebook":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_facebook, view);
                break;
            case "spotify":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_spotify, view);
                break;
            case "snapchat":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_snapchat, view);
                break;
            case "github":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_github, view);
                break;
            case "linkedin":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_linkedin, view);
                break;
            case "tumblr":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_tumblr, view);
                break;
            case "twitch":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_twitch, view);
                break;
            case "twitter":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_twitter, view);
                break;
            case "vimeo":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_vimeo, view);
                break;
            case "youtube":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_youtube, view);
                break;
            case "reddit":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_reddit, view);
                break;
            case "website":
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_internet, view);
                break;
            default:
                Profile_prof_frag.glideTask(item.glide, R.drawable.ic_vk, view);
                break;
        }

    }
}
