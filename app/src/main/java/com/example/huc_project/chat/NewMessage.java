package com.example.huc_project.chat;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huc_project.R;
import com.example.huc_project.homepage.CreateNewPostActivity;
import com.example.huc_project.profile.Profile_main_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class NewMessage extends AppCompatActivity {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<String> nomi = new ArrayList<>();
    final ArrayList<String> uid = new ArrayList<>();

    private FirebaseUser usr = mAuth.getCurrentUser();
    private boolean unread_messages = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_message);

        setUp();
        final Intent i = getIntent();

        final AutoCompleteTextView av = findViewById(R.id.autoCompleteTextView);
        db.collection("UTENTI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if(task.isSuccessful()){
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            nomi.add(document.get("Name").toString());
                            uid.add(document.getId());
                            if(i.hasExtra("to") && document.getId().equals(i.getStringExtra("to"))){
                                av.setText(document.get("Name").toString());
                            }
                        }
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, nomi );
                        av.setAdapter(adapter);
                    }
                }
        });

        final ImageButton button = findViewById(R.id.send_new_text);
        button.setEnabled(false);
        final TextView tv = findViewById(R.id.new_text);
        tv.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) { }
            @Override
            public void afterTextChanged(Editable s) {}
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

                            setUpCircularMenu();

                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
    }

    public void send_text(View v){
        String who = ((EditText)findViewById(R.id.autoCompleteTextView)).getText().toString();
        String request_uid = uid.get(nomi.indexOf(who));
        String what = ((EditText)findViewById(R.id.new_text)).getText().toString();
        List<String> messages = new ArrayList<>();
        messages.add("0"+what);
        HashMap<String,Object> to_put = new HashMap<>();
        to_put.put("User1", mAuth.getCurrentUser().getUid());
        to_put.put("User2", request_uid );
        to_put.put("Messages", messages);
        to_put.put("read1", true);
        to_put.put("read2", false);
        db.collection("Chat").document(UUID.randomUUID().toString()).set(to_put);
        Intent i = new Intent(getBaseContext(), Chat.class);
        startActivity(i);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);
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

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setContentView(icon).build();

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

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
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
            }
        });

        profButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                startActivity(intent);
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), Chat.class);
                startActivity(i);
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            }
        });
    }

}
