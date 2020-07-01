package com.example.huc_project.profile;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.ClipData;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.huc_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static androidx.core.content.ContextCompat.startActivity;

public class RecyclerViewAdapterSocial2 extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    public List<SocialRow> itemsList;
    public List<SocialRow> itemsListFull;
    public Context mContext;
    private FirebaseFirestore db;

    public RecyclerViewAdapterSocial2(List<SocialRow> itemList, Context mContext) {
        this.itemsList = itemList;
        this.mContext = mContext;
        itemsListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_row_social2, parent, false);
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
            tvItem = itemView.findViewById(R.id.relative_social2);
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
    private void populateItemRows(final ItemViewHolder viewHolder, final int position) {
        db = FirebaseFirestore.getInstance();
        final String current_user = Profile_main_page.getCurrent_user();
        //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        final SocialRow item = itemsList.get(position);
        String name = item.social.name;
        final String followers = item.social.followers;
        final String identity = item.social.identity;
        TextView tv1 = (TextView) viewHolder.tvItem.getChildAt(1);
        TextView tv2 = (TextView) viewHolder.tvItem.getChildAt(2);
        final ImageView view = (ImageView) viewHolder.tvItem.getChildAt(0);
        tv1.setText(name);
        tv2.setText(followers + " followers");
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
        ImageButton delete_account = (ImageButton) viewHolder.tvItem.getChildAt(3);
        delete_account.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete account")
                        .setMessage("Are you sure you want to delete this " + item.social.name + " account?")

                        // Specifying a listener allows you to take an action before dismissing the dialog.
                        // The dialog is automatically dismissed when a dialog button is clicked.
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                CollectionReference collection = db.collection("Social");

                                collection.whereEqualTo("user", current_user)
                                        .whereEqualTo("followers", followers)
                                        .whereEqualTo("identity", identity).get()
                                        .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : Objects.requireNonNull(task.getResult())) {
                                                        db.collection("Social").document(document.getId()).delete();
                                                        Intent intent = new Intent(mContext, RemoveSocialAccount.class);
                                                        intent.addFlags(intent.FLAG_ACTIVITY_NEW_TASK);
                                                        mContext.startActivity(intent);
                                                    }
                                                }
                                            }
                                        });                            }
                        })

                        // A null listener allows the button to dismiss the dialog and take no further action.
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
            }
        });
    }
}
