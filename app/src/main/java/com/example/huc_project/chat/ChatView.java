package com.example.huc_project.chat;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.huc_project.R;
import com.example.huc_project.homepage.CreateNewPostActivity;
import com.example.huc_project.profile.Profile_main_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

public class ChatView extends AppCompatActivity {

    private List<String> messages = new ArrayList<>();
    private String usr_uid = "";
    private Boolean i_am_0 = false;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseStorage storage = FirebaseStorage.getInstance();
    private RecyclerView recyclerView;
    private ImageView img_view;
    private RVA rcv_adapter;
    private String document;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_view);

        Intent this_intent = getIntent();
        Bundle b = this_intent.getExtras();
        if(b!=null){
            usr_uid = (String) b.get("user");
            messages = (List<String>) b.get("messages");
            i_am_0 = (Boolean) b.get("who");
            document = (String) b.get("document");
        }

        final DocumentReference docRef = db.collection("UTENTI").document(usr_uid);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = document.get("Name").toString();
                        StorageReference ref = storage.getReference().child("users/" + usr_uid);
                        Glide.with(ChatView.this).load(ref).into(img_view);
                        getSupportActionBar().setTitle(name);
                    }
                }
            }
        });

        setUpRecyclerView();
        initScrollListener();
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.list_messages);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        rcv_adapter = new RVA(messages,i_am_0);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(rcv_adapter);
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
                linearLayoutManager.scrollToPosition(messages.size() - 1);
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.chat_menu, menu);

        MenuItem avatar_item = menu.findItem(R.id.avatar);
        img_view = (ImageView) avatar_item.getActionView();
        img_view.setLayoutParams(new ImageSwitcher.LayoutParams( ImageSwitcher.LayoutParams.WRAP_CONTENT , ImageSwitcher.LayoutParams.MATCH_PARENT ));
        img_view.setScaleType(ImageView.ScaleType.FIT_CENTER);

        /*MenuItem searchItem = menu.findItem(R.id.chat_search);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                rcv_adapter.getFilter().filter(newText);
                return false;
            }
        });*/

        return true;
    }

    public void send_chat_text(View v){
        String what = ((EditText)findViewById(R.id.text_in_chat)).getText().toString();
        if (i_am_0){
            messages.add("0"+what);
        } else {
            messages.add("1"+what);
        }
        HashMap<String,Object> to_put = new HashMap<>();
        to_put.put("Messages", messages);
        db.collection("Chat").document(document).set(to_put, SetOptions.merge());
        ((EditText)findViewById(R.id.text_in_chat)).setText("");

        InputMethodManager mgr = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        mgr.hideSoftInputFromWindow(((EditText)findViewById(R.id.text_in_chat)).getWindowToken(), 0);
    }


}

class RVA extends RecyclerView.Adapter<RecyclerView.ViewHolder> implements Filterable {
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    public List<String> itemsList;
    public List<String> itemsListFull;
    public Boolean who_text;

    public RVA() {}

    public RVA(List<String> itemList, Boolean who_text) {
        this.itemsList = itemList;
        this.itemsListFull = new ArrayList<>(itemList);
        this.who_text = who_text;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false);
            return new RVA.ItemViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.hp_item_loading, parent, false);
            return new RVA.LoadingViewHolder(view);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int position) {

        if (viewHolder instanceof RVA.ItemViewHolder) {
            populateItemRows((RVA.ItemViewHolder) viewHolder, position);
        } else if (viewHolder instanceof RVA.LoadingViewHolder) {
            showLoadingView((RVA.LoadingViewHolder) viewHolder, position);
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

        LinearLayout message;

        public ItemViewHolder(@NonNull View itemView) {
            super(itemView);
            message = itemView.findViewById(R.id.card_message);
        }

    }

    private class LoadingViewHolder extends RecyclerView.ViewHolder {

        ProgressBar progressBar;

        public LoadingViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = itemView.findViewById(R.id.itemRow_progressBar);
        }

    }

    private void showLoadingView(RVA.LoadingViewHolder viewHolder, int position) {
        //ProgressBar would be displayed
    }

    private void populateItemRows(final RVA.ItemViewHolder viewHolder, int position) {
        final String item = itemsList.get(position);
        ((TextView)viewHolder.message.getChildAt(0)).setText(item.substring(1));
       // Log.d("CHAT", "code " + item.charAt(0) + " from user who texted? " + this.who_text.toString());
        if((item.charAt(0) == '0' && this.who_text) || ( (item.charAt(0) == '1' && !this.who_text) )) {
            viewHolder.message.setGravity(Gravity.END);
        } else if ( (item.charAt(0) == '1' && this.who_text) || (item.charAt(0) == '0' && !this.who_text) ) {
            viewHolder.message.setGravity(Gravity.START);
        }

    }


    @Override
    public Filter getFilter() {
        return filtered_results;
    }

    private Filter filtered_results = new Filter() {
        @Override
        protected FilterResults performFiltering(CharSequence constraint) {
            List<String> filteredList = new ArrayList<>();

            if(constraint.toString().isEmpty()){
                filteredList.addAll(itemsListFull);
            } else {
                String filterPattern = constraint.toString().toLowerCase().trim();
                for (String s : itemsListFull){
                    if(s.toLowerCase().contains(filterPattern)){
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
            itemsList.addAll((Collection<? extends String>) results.values);
            notifyDataSetChanged();
        }
    };


}