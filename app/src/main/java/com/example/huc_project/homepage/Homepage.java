package com.example.huc_project.homepage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.SearchView;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Switch;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huc_project.OurLogin;
import com.example.huc_project.R;
import com.example.huc_project.Signup;
import com.example.huc_project.Start;
import com.example.huc_project.chat.Chat;
import com.example.huc_project.chat.ChatMessage;
import com.example.huc_project.chat.Conversation;
import com.example.huc_project.posts.postView;
import com.example.huc_project.profile.Profile_main_page;
import com.example.huc_project.settings.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.gson.internal.bind.ArrayTypeAdapter;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.json.JSONArray;

import java.util.ArrayList;
import java.util.List;

public class Homepage extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener{

    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<PostRow> rowsArrayList = new ArrayList<>();

    private boolean unread_messages = true;
    private FirebaseUser usr = mAuth.getCurrentUser();

    final int numItems = 10;

    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    ArrayList<PostRow> rowsPostList = new ArrayList<>();
    Spinner spinner_filter;
    List<String> mylist;
    List<Boolean> list_check;
    final private String pattern = Integer.toString(R.string.pattern);

    boolean guest_mode = false;
    boolean isLoading = false;

    private SwipeRefreshLayout swipeContainer;
    private FloatingActionMenu actionMenu;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);
        Intent i = getIntent();

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = pref.edit();
        guest_mode = i.getBooleanExtra("guest",false);

        recyclerView=null;
        setUpHomepage();

    }

    private void populateData() {
        int i = 0;
        while (i < rowsPostList.size()) {
            rowsArrayList.add(rowsPostList.get(i));
            i++;
        }
    }

    private void setUpHomepage(){
        db = FirebaseFirestore.getInstance();
        CollectionReference collezione = db.collection("posts");

        collezione.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                StorageReference storageRef = storage.getReference();
                                StorageReference islandRef = null;
                                if(post.storageref != null){
                                    islandRef = storageRef.child("images/" + post.storageref);
                                }

                                PostRow post_row = new PostRow(post, islandRef, Glide.with(Homepage.this));
                                rowsPostList.add(post_row);
                            }
                            populateData();
                            setUp();


                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
    }
    private void setUp(){
        db = FirebaseFirestore.getInstance();
        CollectionReference mess = db.collection("Chat");
        mess.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {

                                final Conversation convo = document.toObject(Conversation.class);
                                if(convo.getUser1().equals(usr.getUid())){
                                    final String last_message = convo.getLastMessage();
                                    final List<String> all_messages = convo.getMessages();
                                    if(unread_messages) unread_messages = convo.isRead1(); // isread is false when you haven't read,
                                } else if (convo.getUser2().equals(usr.getUid())) {
                                    final String last_message = convo.getLastMessage();
                                    final List<String> all_messages = convo.getMessages();
                                    if(unread_messages) unread_messages = convo.isRead2();
                                }
                            }
                            setUpRecyclerView();
                            setUpCircularMenu();
                            initScrollListener();
                            swipeContainer.setRefreshing(false);
                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        glide.load(ref).into(view);
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerView);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);
        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList, this);
        recyclerView.setAdapter(recyclerViewAdapter);
        swipeContainer = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);

        swipeContainer.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                rowsArrayList.clear();
                rowsPostList.clear();
                recyclerViewAdapter.notifyDataSetChanged();
                setUpHomepage();
            }
        });
        SpinnerAdapter sa = new SpinnerAdapter(getApplicationContext(), mylist, list_check ,"" , recyclerViewAdapter);
        spinner_filter.setAdapter(sa);
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

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList.size() - 1) {
                        //bottom of list!
                        //loadMore();
                        //isLoading = true;
                    }
                }
            }
        });


    }

    private void loadMore() {
        rowsArrayList.add(null);
        recyclerViewAdapter.notifyItemInserted(rowsArrayList.size() - 1);


        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                rowsArrayList.remove(rowsArrayList.size() - 1);
                int scrollPosition = rowsArrayList.size();
                recyclerViewAdapter.notifyItemRemoved(scrollPosition);
                int currentSize = scrollPosition;
                int nextLimit = currentSize + 10;

                while (currentSize - 1 < nextLimit) {
                    //rowsArrayList.add("Item " + currentSize);
                    currentSize++;
                }

                recyclerViewAdapter.notifyDataSetChanged();
                isLoading = false;
            }
        }, 2000);


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem filter = menu.findItem(R.id.menu_filter);
        spinner_filter = (Spinner) filter.getActionView();
        mylist = new ArrayList<>();
        mylist.add("Nature");
        mylist.add("Sport"); // "Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature"
        mylist.add("Fashion");
        mylist.add("Food");
        mylist.add("Music");
        mylist.add("Science & IT");

        list_check = new ArrayList<>();
        list_check.add(false);
        list_check.add(false);
        list_check.add(false);
        list_check.add(false);
        list_check.add(false);
        list_check.add(false);

        MenuItem search_package = menu.findItem(R.id.menu_switch);
        Switch switch_package = (Switch) search_package.getActionView();
        switch_package.setText("Packages");
        switch_package.setTextSize(10);
        switch_package.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    recyclerViewAdapter.getFilter().filter("is_package");
                } else {
                    recyclerViewAdapter.getFilter().filter("");
                }
            }
        });

        return true;
    }



    private void setUpCircularMenu(){
        final ImageView icon = new ImageView(this);
        FloatingActionButton actionButton;
        if(guest_mode){
            Drawable exit = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_exit);
            final Drawable wrappedDrawable = DrawableCompat.wrap(exit);
            DrawableCompat.setTint(wrappedDrawable, Color.BLACK);
            icon.setImageDrawable(wrappedDrawable);
            actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();
            actionButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        } else{
            final Drawable menu_ic_id;
            if(unread_messages){
                menu_ic_id = getResources().getDrawable(R.drawable.ic_menu);
            } else {
                menu_ic_id = getResources().getDrawable(R.drawable.ic_menu_notification);
            }
            final Drawable add_ic_id = getResources().getDrawable(R.drawable.ic_add);
            icon.setImageDrawable(menu_ic_id);

            actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();

            SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
            FloatingActionButton.LayoutParams params=new FloatingActionButton.LayoutParams(220,220);
            itemBuilder.setLayoutParams(params);

            //settings
            ImageView settingsItem = new ImageView(this);
            settingsItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
            SubActionButton settingsButton = itemBuilder.setContentView(settingsItem).build();
            //chat
            ImageView chatItem = new ImageView(this);
            chatItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));
            SubActionButton chatButton = itemBuilder.setContentView(chatItem).build();
            //profile
            ImageView profItem = new ImageView(this);
            profItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
            SubActionButton profButton = itemBuilder.setContentView(profItem).build();
            //new post
            ImageView addItem = new ImageView(this);
            addItem.setImageDrawable(add_ic_id);
            SubActionButton addButton = itemBuilder.setContentView(addItem).build();

            actionMenu = new FloatingActionMenu.Builder(this)
                    .addSubActionView(settingsButton)
                    .addSubActionView(chatButton)
                    .addSubActionView(profButton)
                    .addSubActionView(addButton)
                    .setRadius(470)
                    .attachTo(actionButton)
                    .build();
            actionMenu.setStateChangeListener(new FloatingActionMenu.MenuStateChangeListener() {
                @Override
                public void onMenuOpened(FloatingActionMenu menu) {
                    icon.setImageDrawable(add_ic_id);
                    icon.setRotation(45);
                }

                @Override
                public void onMenuClosed(FloatingActionMenu menu) {
                    icon.setImageDrawable(menu_ic_id);
                    icon.setRotation(0);
                }
            });


            addButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), CreateNewPostActivity.class);
                    startActivity(intent);
                    finish();
                }
            });
            profButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                    final String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    intent.putExtra("user", current_user);
                    startActivity(intent);
                }
            });
            chatButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Homepage.this, Chat.class);
                    startActivity(i);
                }
            });
            settingsButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent i = new Intent(Homepage.this, Settings.class);
                    startActivity(i);
                }
            });
        }
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(Homepage.this, "Clicked item" + position, Toast.LENGTH_SHORT).show();
        PostRow post_clicked = rowsArrayList.get(position);
        Intent intent = new Intent(Homepage.this, postView.class);
        intent.putExtra("title", post_clicked.getTitle());
        intent.putExtra("desc", post_clicked.getDesc());
        intent.putExtra("storageref", post_clicked.getPost().getStorageref());
        intent.putExtra("user", post_clicked.getPost().getUser());
        intent.putExtra("isPackage", post_clicked.getPost().getIsPackage());
        if(guest_mode) intent.putExtra("guest", true);
        startActivity(intent);
    }
}
