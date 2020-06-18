package com.example.huc_project.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
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
import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.posts.postView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.viewpager.widget.ViewPager;

import org.w3c.dom.Document;

import java.util.ArrayList;
import java.util.HashMap;

public class Profile_main_page extends AppCompatActivity {

    private static String current_user;
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        current_user = bundle. getString("user");

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

                        StorageReference ref = storage.getReference().child("users/" + current_user);
                        Glide.with(Profile_main_page.this).load(ref).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_img);

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
                                        else {
                                            DrawableCompat.setTint(wrappedDrawable, Color.rgb(3,98,86));
                                            edit_profile.setImageDrawable(wrappedDrawable);

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
                        }

                        if (country == null) user_country.setText("Unknown");
                        else {
                            if (city == null) user_country.setText(country);
                            else user_country.setText(country + ", " + city);
                        }

                        if (hidden_mail.equals(true)) user_mail.setText("Hidden");
                        else if(user_mail.getWidth() + profile_img.getWidth() > 480 && !guest_user){
                            user_mail.setText( mail.substring(0 , mail.indexOf('@') ) + '\n' + mail.substring(mail.indexOf('@')) );
                        } else if (!guest_user) user_mail.setText(mail);
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, Homepage.class);
        i.setFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT);
        startActivityIfNeeded(i, 0);
        startActivity(i);
        finish();
    }

    public static String getCurrent_user() {
        return current_user;
    }
}
