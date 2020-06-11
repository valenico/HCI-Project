package com.example.huc_project.chat;

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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List<ChatMessage> itemsList;
    public List<ChatMessage> itemsListFull;

    public RecyclerViewAdapter() {}

    public RecyclerViewAdapter(List<ChatMessage> itemList) {
        this.itemsList = itemList;
        itemsListFull = new ArrayList<>(itemList);
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_row, parent, false);
            return new com.example.huc_project.chat.RecyclerViewAdapter.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_loading, parent, false);
            return new com.example.huc_project.chat.RecyclerViewAdapter.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof com.example.huc_project.chat.RecyclerViewAdapter.ItemViewHolder) {
            populateItemRows((com.example.huc_project.chat.RecyclerViewAdapter.ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof com.example.huc_project.chat.RecyclerViewAdapter.LoadingViewHolder) {
            showLoadingView((com.example.huc_project.chat.RecyclerViewAdapter.LoadingViewHolder) viewHolder, position);
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


    private class ItemViewHolder extends RecyclerView.ViewHolder {

        RelativeLayout tvItem;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);

            tvItem = itemView.findViewById(R.id.relative);
        }
    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.itemRow_progressBar);
        }
    }

    private void showLoadingView(com.example.huc_project.chat.RecyclerViewAdapter.LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed

    }

    private void populateItemRows(final com.example.huc_project.chat.RecyclerViewAdapter.ItemViewHolder viewHolder, int position) {
        final ChatMessage item = itemsList.get(position);
        final String text = item.getMessageText();
        final String uid = item.getMessageUid();

        final DocumentReference docRef = db.collection("UTENTI").document(uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        TextView tv1 = (TextView) viewHolder.tvItem.getChildAt(1);
                        TextView tv2 = (TextView) viewHolder.tvItem.getChildAt(2);
                        ImageView view = (ImageView) viewHolder.tvItem.getChildAt(0);
                        StorageReference ref = storage.getReference().child("users/" + uid);
                        Chat.glideTask(item.glide, ref, view);
                        tv1.setText(name);
                        tv2.setText(text);
                    }
                }
            }
        });

    }



    @Override
    public Filter getFilter() {
        return filtered_results;
    }

    private Filter filtered_results = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<ChatMessage> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(itemsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (ChatMessage s : itemsListFull){
                    if(s.getMessageText().toLowerCase().contains(filterPattern)){
                        filteredList.add(s);
                    }
                }
            }
            FilterResults res = new FilterResults();
            res.values = filteredList;
            return res;
        }

        @Override
        protected void publishResults(CharSequence constraint, FilterResults results) {
            itemsList.clear();
            itemsList.addAll((Collection<? extends ChatMessage>) results.values);
            notifyDataSetChanged();
        }
    };


}
