package com.example.huc_project.profile;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import androidx.viewpager.widget.ViewPager;

public class Profile_main_page extends AppCompatActivity {

    private static FirebaseUser current_user;
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        db = FirebaseFirestore.getInstance();

        Bundle bundle = getIntent().getExtras();
        current_user = (FirebaseUser) bundle. get("user");

        final Boolean guest_user;
        final ViewPager viewPager = findViewById(R.id.pager);

        if (current_user != null) guest_user = false;
        else guest_user = true;

        tabLayout = findViewById(R.id.tabLayout);
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



        final DocumentReference docRef = db.collection("UTENTI").document(current_user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        String country = (String) document.get("Country");
                        String city = (String) document.get("City");
                        Boolean hidden_mail = (Boolean) document.get("Hidemail");
                        Boolean slowLoad = (Boolean) document.get("SlowLoad");
                        TextView user_name = findViewById(R.id.user_name);
                        TextView user_country = findViewById(R.id.user_country);
                        TextView user_mail = findViewById(R.id.user_mail);
                        ImageView profile_img = findViewById(R.id.profile_image);

                        StorageReference ref = storage.getReference().child("users/" + current_user.getUid());
                        Glide.with(Profile_main_page.this).load(ref).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_img);

                        user_name.setText(name);

                        if (current_user.getUid().equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                            ImageButton edit_profile = findViewById(R.id.edit_profile);
                            edit_profile.setImageResource(R.drawable.ic_pencil);
                            edit_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    Intent intent = new Intent(getApplicationContext(), Edit_profile.class);
                                    startActivity(intent);
                                }
                            });
                        }
                        else {
                            ImageButton edit_profile = findViewById(R.id.edit_profile);
                            edit_profile.setImageResource(R.drawable.ic_star);
                            edit_profile.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {

                                }
                            });
                        }

                        if (country == null) user_country.setText("Unknown");
                        else {
                            if (city == null) user_country.setText(country);
                            else user_country.setText(country + ", " + city);
                        }

                        if (hidden_mail) user_mail.setText("Hidden");
                        else if (!guest_user) user_mail.setText(current_user.getEmail());

                        if(user_mail.getWidth() + profile_img.getWidth() > 480 ){
                            user_mail.setText( current_user.getEmail().substring(0 , current_user.getEmail().indexOf('@') ) + '\n' + current_user.getEmail().substring(current_user.getEmail().indexOf('@')) );
                        }
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
    }

    public static FirebaseUser getCurrent_user() {
        return current_user;
    }
}
