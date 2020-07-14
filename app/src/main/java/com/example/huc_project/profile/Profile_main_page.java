package com.example.huc_project.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.huc_project.R;
import com.example.huc_project.chat.Chat;
import com.example.huc_project.chat.ChatMessage;
import com.example.huc_project.chat.ChatView;
import com.example.huc_project.chat.Conversation;
import com.example.huc_project.chat.NewMessage;
import com.example.huc_project.homepage.CreateNewPostActivity;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.example.huc_project.settings.Settings;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Profile_main_page extends AppCompatActivity {

    private static String current_user;
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    private FirebaseUser usr = mAuth.getCurrentUser();
    private boolean unread_messages = true;
    private boolean old_message_user = false;
    private ChatMessage myconvo;
    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    private SharedPreferences pref;
    private int rtl;
    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        // Sets the Toolbar to act as the ActionBar for this Activity window.
        // Make sure the toolbar exists in the activity and is not null
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);

        View.OnClickListener back_to_hp = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_main_page.this, Homepage.class);
                startActivity(i);
                finish();
            }
        };

        findViewById(R.id.yourlogo).setOnClickListener(back_to_hp);
        findViewById(R.id.appName).setOnClickListener(back_to_hp);


        Intent intent = getIntent();
        current_user = intent.getStringExtra("user");
        Log.d("BELL", current_user+"bel");

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        rtl = pref.getInt("rtl", 4);
        final Boolean guest_user;
        tabLayout = findViewById(R.id.tabLayout);
        final ViewPager viewPager = findViewById(R.id.pager);

        if (current_user != null) guest_user = false;
        else guest_user = true;

        ViewPageAdapter adapter = new ViewPageAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        final DocumentReference docRef = db.collection("UTENTI").document(current_user);

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @SuppressLint("SetTextI18n")
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        String country = (String) document.get("Country");
                        String city = (String) document.get("City");
                        String mail = (String) document.get("Email");
                        Boolean hidden_mail = (Boolean) document.get("Hidemail");
                        Boolean slowLoad = (Boolean) document.get("SlowLoad");
                        TextView user_name = findViewById(R.id.user_name);
                        final TextView user_country = findViewById(R.id.user_country);
                        TextView user_mail = findViewById(R.id.user_mail);
                        ImageView profile_img = findViewById(R.id.profile_image);

                        RequestOptions options = new RequestOptions().error(R.drawable.user); //if load gets error bc image does not exist, load default image
                        StorageReference ref = storage.getReference().child("users/" + current_user);
                        Glide.with(Profile_main_page.this).load(ref).apply(options).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_img);

                        user_name.setText(name);

                        final ImageButton edit_profile = findViewById(R.id.edit_profile);
                        ImageButton favorite = findViewById(R.id.favorites);


                        if (current_user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            edit_profile.setImageResource(R.drawable.ic_pencil);
                            edit_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Edit_profile.class);
                                    startActivity(intent);
                                    finish();
                                }
                            });

                            favorite.setVisibility(View.VISIBLE);
                            favorite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Favorite.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_heart);
                            final Drawable wrappedDrawable = DrawableCompat.wrap(like);
                            DrawableCompat.setTint(wrappedDrawable, Color.rgb(3,98,86));
                            edit_profile.setImageDrawable(wrappedDrawable);

                            final ArrayList<String> fav_user = new ArrayList<>();
                            db.collection("Favorites").document(mAuth.getCurrentUser().getUid()).collection("user")
                                    .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            fav_user.add((String) document.get("id"));
                                        }
                                    } else {
                                        Log.w("lola", "Error getting documents.", task.getException());
                                    }
                                    for (int i=0; i<fav_user.size(); i++) {
                                        if (current_user.equals(fav_user.get(i))) {
                                            DrawableCompat.setTint(wrappedDrawable, Color.RED);
                                            edit_profile.setImageDrawable(wrappedDrawable);
                                            break;
                                        }
                                    }
                                }
                            });


                            edit_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                    Boolean add_fav = true;
                                    for (int i=0; i<fav_user.size(); i++) {
                                        if (current_user.equals(fav_user.get(i))) {
                                            add_fav = false;
                                            break;
                                        }
                                        else {
                                            add_fav = true;
                                        }
                                    }
                                    if (add_fav) {
                                        final String c_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                                        final HashMap<String,Object> data = new HashMap<>();
                                        db.collection("UTENTI").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if(task.isSuccessful()) {
                                                    data.put("Name",  task.getResult().get("Name") );
                                                    data.put("id", current_user);
                                                    db.collection("Favorites").document(c_user).collection("user").add(data);
                                                    fav_user.add(current_user);
                                                    HashMap<String, ArrayList<String>> user_favorites = new HashMap<>();
                                                    user_favorites.put("Fav_user", fav_user);
                                                    db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(user_favorites, SetOptions.merge());
                                                    Toast.makeText(Profile_main_page.this, "User added to favorites." ,Toast.LENGTH_SHORT ).show();
                                                    DrawableCompat.setTint(wrappedDrawable, Color.RED);
                                                }
                                            }
                                        });
                                    }
                                    else {
                                        fav_user.remove(current_user);
                                        HashMap<String, ArrayList<String>> user_favorites = new HashMap<>();
                                        user_favorites.put("Fav_user", fav_user);
                                        db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(user_favorites, SetOptions.merge());
                                        Toast.makeText(Profile_main_page.this, "User removed from favorites." ,Toast.LENGTH_SHORT ).show();
                                        DrawableCompat.setTint(wrappedDrawable, Color.rgb(3,98,86));
                                        db.collection("Favorites").document(mAuth.getCurrentUser().getUid()).collection("user")
                                                .whereEqualTo("id", current_user).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    for (QueryDocumentSnapshot document : task.getResult()) {
                                                        document.getReference().delete();
                                                    }
                                                } else {
                                                    Log.w("lola", "Error getting documents.", task.getException());
                                                }
                                            }
                                        });
                                    }
                                    edit_profile.setImageDrawable(wrappedDrawable);

                                }
                            });

                            favorite.setVisibility(View.VISIBLE);
                            Drawable send = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_send_text);
                            final Drawable wrappedSend = DrawableCompat.wrap(send);
                            DrawableCompat.setTint(wrappedSend, getResources().getColor(R.color.textColor));
                            favorite.setImageDrawable(wrappedSend);
                            favorite.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                   if(old_message_user){
                                       Intent intent = new Intent(getBaseContext(), ChatView.class);
                                       intent.putExtra("user", current_user);
                                       intent.putStringArrayListExtra("messages", (ArrayList<String>) myconvo.getMessages() );
                                       intent.putExtra("who", myconvo.getI_am_0());
                                       intent.putExtra("document", myconvo.getDocument());
                                       startActivity(intent);
                                       finish();
                                   } else {
                                       Intent i = new Intent(getBaseContext(), NewMessage.class);
                                       i.putExtra("to", current_user);
                                       startActivity(i);
                                       finish();
                                   }
                                }
                            });

                        }

                        if (country!= null && country.length() != 0 ) {
                            if (city.length() == 0) user_country.setText(country);
                            else user_country.setText(country + ", " + city);
                        }

                        if (hidden_mail != null && hidden_mail.equals(true)) user_mail.setText("");
                        else if(mail!= null && mail.length() > 18  && !guest_user){
                            user_mail.setText( mail.substring(0 , mail.indexOf('@') ) + '\n' + mail.substring(mail.indexOf('@')) );
                        } else if (!guest_user) user_mail.setText(mail);
                    }
                    setUp();
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        finish();
    }

    public static String getCurrent_user() {
        return current_user;
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
                                    if(convo.getUser2().equals(current_user)){
                                        old_message_user = true;
                                        myconvo = new ChatMessage( Glide.with(getBaseContext()) , last_message, current_user,  all_messages, true, document.getId() , unread_messages );
                                    }
                                } else if (convo.getUser2().equals(usr.getUid())) {
                                    final String last_message = convo.getLastMessage();
                                    final List<String> all_messages = convo.getMessages();
                                    if(unread_messages) unread_messages = convo.isRead2();
                                    if(convo.getUser1().equals(current_user)){
                                        old_message_user = true;
                                        myconvo = new ChatMessage( Glide.with(getBaseContext()) , last_message, current_user,  all_messages, false, document.getId() , unread_messages );
                                    }
                                }
                            }

                            setUpCircularMenu();

                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
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

        FloatingActionButton actionButton = new FloatingActionButton.Builder(this).setContentView(icon).setPosition(rtl).build();

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
                finish();
            }
        });

        profButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                if (current_user.equals(mAuth.getCurrentUser().getUid())) {
                    //Toast.makeText(Profile_main_page.this, "Stai già sul tuo profilo fattone di merda!", Toast.LENGTH_LONG).show();
                    intent.putExtra("user" , mAuth.getCurrentUser().getUid());

                }
                else {
                    final String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
                    intent.putExtra("user", current_user);
                }
                startActivity(intent);
                finish();
            }
        });

        chatButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_main_page.this, Chat.class);
                startActivity(i);
                finish();
            }
        });

        settingsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Profile_main_page.this, Settings.class);
                startActivity(i);
            }
        });
    }
}
