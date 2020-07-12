package com.example.huc_project.chat;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.Drawable;
import android.os.Bundle;


import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.huc_project.homepage.CreateNewPostActivity;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.profile.Profile_main_page;
import com.example.huc_project.settings.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;

import com.example.huc_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.List;

public class Chat extends AppCompatActivity implements com.example.huc_project.chat.RecyclerViewAdapter.OnItemListener {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();;
    private FirebaseFirestore db;
    private FirebaseUser usr = mAuth.getCurrentUser();
    private RecyclerView recyclerView;
    ArrayList<ChatMessage> rowsChatList = new ArrayList<>();
    private boolean unread_messages = true;
    static com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton actionButton;
    private int rtl;
    SharedPreferences pref;
    private RecyclerViewAdapter recyclerViewAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rtl = pref.getInt("rtl", 4);

        FloatingActionButton fab = findViewById(R.id.fab);
        if(rtl==6){
            fab.setTranslationX(getResources().getDimension(R.dimen._225sdp));
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Chat.this, NewMessage.class);
                startActivity(i);
            }
        });

        setUp();
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
                                    ChatMessage cm = new ChatMessage( Glide.with(Chat.this) , last_message, convo.getUser2(), all_messages, true, document.getId(), convo.isRead1());
                                    rowsChatList.add(cm);
                                } else if (convo.getUser2().equals(usr.getUid())) {
                                    final String last_message = convo.getLastMessage();
                                    final List<String> all_messages = convo.getMessages();
                                    if(unread_messages) unread_messages = convo.isRead2();
                                    ChatMessage cm = new ChatMessage( Glide.with(Chat.this), last_message, convo.getUser1() , all_messages ,false , document.getId(),convo.isRead2());
                                    rowsChatList.add(cm);
                                }
                            }
                            setUpRecyclerView();
                            setUpCircularMenu();
                            initScrollListener();
                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recyclerViewChat);
        if(rowsChatList.size() > 0){
            recyclerView.setVisibility(View.VISIBLE);
            findViewById(R.id.messtext).setVisibility(View.VISIBLE);
            findViewById(R.id.textView1).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView2).setVisibility(View.INVISIBLE);
            findViewById(R.id.arrowLeft).setVisibility(View.INVISIBLE);
            findViewById(R.id.arrowRight).setVisibility(View.INVISIBLE);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
            recyclerViewAdapter = new RecyclerViewAdapter(rowsChatList, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
        }
        else{
            recyclerView.setVisibility(View.INVISIBLE);
            findViewById(R.id.messtext).setVisibility(View.INVISIBLE);
            findViewById(R.id.textView1).setVisibility(View.VISIBLE);
            findViewById(R.id.textView2).setVisibility(View.VISIBLE);
            if(rtl==6) findViewById(R.id.arrowRight).setVisibility(View.VISIBLE);
            else findViewById(R.id.arrowLeft).setVisibility(View.VISIBLE);
        }

    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        RequestOptions options = new RequestOptions().error(R.drawable.user);
        glide.load(ref).apply(options).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(view);
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
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        SearchView searchView = (SearchView) searchItem.getActionView();
        searchView.setQueryHint("Search...");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                if(recyclerViewAdapter==null) return false;
                recyclerViewAdapter.getFilter().filter(newText);
                return false;
            }
        });

        MenuItem filteritem = menu.findItem(R.id.menu_slide);
        filteritem.setVisible(false);
        return true;
    }

    private void setUpCircularMenu(){
        final ImageView icon = new ImageView(this);
        final Drawable menu_ic_id;
        if(unread_messages){
            menu_ic_id = getResources().getDrawable(R.drawable.ic_menu);
        } else {
            menu_ic_id = getResources().getDrawable(R.drawable.ic_menu_notification);
        }
        final Drawable add_ic_id = getResources().getDrawable(R.drawable.ic_add);
        icon.setImageDrawable(menu_ic_id);

        actionButton = new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.Builder(this).setContentView(icon).setPosition(rtl).build();

        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams params=new com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton.LayoutParams(220,220);
        itemBuilder.setLayoutParams(params);

        //settings
        ImageView settingsItem = new ImageView(this);
        settingsItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_settings));
        SubActionButton settingsButton = itemBuilder.setContentView(settingsItem).build();
        //chat
        ImageView chatItem = new ImageView(this);
        chatItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));
        if(unread_messages){
            chatItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat));
        } else {
            chatItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_chat_notification));
        }
        SubActionButton chatButton = itemBuilder.setContentView(chatItem).build();
        //profile
        ImageView profItem = new ImageView(this);
        profItem.setImageDrawable(getResources().getDrawable(R.drawable.ic_profile));
        SubActionButton profButton = itemBuilder.setContentView(profItem).build();
        //new post
        ImageView addItem = new ImageView(this);
        addItem.setImageDrawable(add_ic_id);
        SubActionButton addButton = itemBuilder.setContentView(addItem).build();
        int end = 360;
        if(rtl == 4){
            end = 270;
        }
        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(settingsButton)
                .addSubActionView(chatButton)
                .addSubActionView(profButton)
                .addSubActionView(addButton)
                .setStartAngle(rtl*45)
                .setEndAngle(end)
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
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Settings.class);
                startActivity(i);
            }
        });
    }

    @Override
    public void onItemClick(int position) {
        //Toast.makeText(Homepage.this, "Clicked item" + position, Toast.LENGTH_SHORT).show();
        ChatMessage chat_clicked = rowsChatList.get(position);
        Intent intent = new Intent(Chat.this, ChatView.class);
        intent.putExtra("user", chat_clicked.getMessageUid());
        intent.putStringArrayListExtra("messages", (ArrayList<String>) chat_clicked.getMessages());
        intent.putExtra("who", chat_clicked.getI_am_0());
        intent.putExtra("document",chat_clicked.getDocument());
        startActivity(intent);
        finish();
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(Chat.this, Homepage.class);
        startActivity(back);
        finish();
    }

}