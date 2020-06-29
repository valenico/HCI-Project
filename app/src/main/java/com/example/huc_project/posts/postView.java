package com.example.huc_project.posts;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.huc_project.chat.ChatView;
import com.example.huc_project.homepage.DataGettingActivity;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.profile.Favorite;
import com.example.huc_project.profile.Profile_main_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.drawable.DrawableCompat;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huc_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageException;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.google.protobuf.StringValueOrBuilder;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class postView extends AppCompatActivity {

    Post post;
    ImageView post_image_view;
    TextView title_view;
    TextView desc_view;
    TextView owner_name;
    TextView place_view;
    TextView is_sponsor_view;
    TextView is_package_view;
    ImageView owner_img;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
    String current_user;
    String id;
    boolean guest_mode;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        final Intent intent = getIntent();
        this.guest_mode = intent.getBooleanExtra("guest",false);
        if(!guest_mode) this.current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false), intent.getStringArrayListExtra("categories"),
                intent.getStringExtra("role"), intent.getStringExtra("country"), intent.getStringExtra("city"));
        this.id = intent.getStringExtra("id");
        this.post_image_view = findViewById(R.id.imageViewplaces);
        this.title_view = findViewById(R.id.postTitle);
        this.desc_view = findViewById(R.id.postDesc);
        owner_name = findViewById(R.id.owner_name);
        owner_img = findViewById(R.id.owner_img);
        place_view = findViewById(R.id.postLocation);
        is_sponsor_view = findViewById(R.id.viewsponsor);
        is_package_view = findViewById(R.id.viewpackage);

        if(!guest_mode){
            owner_img.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go_profile = new Intent(postView.this, Profile_main_page.class);
                    go_profile.putExtra("user",post.getUser());
                    startActivity(go_profile);
                }
            });

            owner_name.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent go_profile = new Intent(postView.this, Profile_main_page.class);
                    go_profile.putExtra("user",post.getUser());
                    startActivity(go_profile);
                }
            });
        }

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);

        if(guest_mode){
            fab.setVisibility(View.INVISIBLE);
        } else if (current_user.equals(post.getUser())) {
            Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_pencil);
            final Drawable wrappedDrawable = DrawableCompat.wrap(like);
            fab.setImageDrawable(wrappedDrawable);
            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent(postView.this, edit_post.class);
                    intent.putExtra("title", post.getTitle());
                    intent.putExtra("desc", post.getPostdesc());
                    intent.putExtra("storageref", post.getStorageref());
                    intent.putExtra("user", post.getUser());
                    intent.putExtra("id", id );
                    intent.putExtra("country", post.getCountry());
                    intent.putExtra("city",post.getCity());
                    intent.putExtra("categories", post.getCategories());
                    intent.putExtra("role", post.getRole());
                    intent.putExtra("isPackage", post.getIsPackage());
                    startActivity(intent);
                }
            });
        }
        else {

            Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24);
            final Drawable wrappedDrawable = DrawableCompat.wrap(like);

            final ArrayList<String> fav_post = new ArrayList<>();
            db.collection("UTENTI").document(mAuth.getCurrentUser().getUid())
                    .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.get("Fav_post") != null) {
                            ArrayList<String> l = (ArrayList<String>) document.get("Fav_post");
                            for (int i = 0; i < l.size(); i++) {
                                fav_post.add(l.get(i));
                            }
                        }
                    } else {
                        Log.w("lola", "Error getting documents.", task.getException());
                    }
                    for (int i = 0; i < fav_post.size(); i++) {
                        if (post.getStorageref().equals(fav_post.get(i))) {
                            DrawableCompat.setTint(wrappedDrawable, Color.RED);
                            fab.setImageDrawable(wrappedDrawable);
                            break;
                        }
                    }
                }
            });

            fab.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {

                    Boolean add_post = true;
                    for (int i = 0; i < fav_post.size(); i++) {
                        if (intent.getStringExtra("storageref").equals(fav_post.get(i))) {
                            add_post = false;
                            break;
                        } else {
                            add_post = true;
                        }
                    }
                    if (add_post) {
                        HashMap<String, Object> data = new HashMap<>();
                        data.put("title", intent.getStringExtra("title"));
                        data.put("user", intent.getStringExtra("user"));
                        data.put("postdesc", intent.getStringExtra("desc"));
                        data.put("isPackage", intent.getBooleanExtra("isPackage", false));
                        data.put("storageref", intent.getStringExtra("storageref"));
                        db.collection("Favorites").document(mAuth.getUid()).collection("post").add(data);
                        fav_post.add(intent.getStringExtra("storageref"));
                        HashMap<String, ArrayList<String>> post_favorites = new HashMap<>();
                        post_favorites.put("Fav_post", fav_post);
                        db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(post_favorites, SetOptions.merge());
                        Toast.makeText(postView.this, intent.getStringExtra("title").toString() + " added to your favorites posts!", Toast.LENGTH_LONG).show();
                        DrawableCompat.setTint(wrappedDrawable, Color.RED);
                    } else {
                        fav_post.remove(intent.getStringExtra("storageref"));
                        HashMap<String, ArrayList<String>> post_favorites = new HashMap<>();
                        post_favorites.put("Fav_post", fav_post);
                        db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(post_favorites, SetOptions.merge());
                        DrawableCompat.setTint(wrappedDrawable, Color.rgb(3, 98, 86));
                        Toast.makeText(postView.this, intent.getStringExtra("title").toString() + " removed from your favorites posts!", Toast.LENGTH_LONG).show();
                        db.collection("Favorites").document(mAuth.getCurrentUser().getUid()).collection("post")
                                .whereEqualTo("storageref", intent.getStringExtra("storageref")).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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
                    fab.setImageDrawable(wrappedDrawable);
                }
            });
        }
        setUpPost();
    }

    public void setUpPost(){
        title_view.setText(post.getTitle());
        desc_view.setText(post.getPostdesc());

        if(post.getRole().equals("sponsor")) is_sponsor_view.setText("\u2713 Sponsor");
        else is_sponsor_view.setText("\u2713 Sponsorship");

        if(post.getIsPackage()) is_package_view.setText("\u2713 Package");

        if(post.getCountry().trim().length() > 0 && post.getCity().trim().length() > 0) place_view.setText(post.getCountry() + ", " + post.getCity());
        else if(post.getCountry().trim().length() > 0) place_view.setText(post.getCountry());
        else if(post.getCity().trim().length() > 0) place_view.setText(post.getCity());
        else {
            ((ImageView) findViewById(R.id.postlocationimage)).setImageResource(R.drawable.ic_nolocation);
            place_view.setText("There is no location provided for this advertisement.");
        }

        int i;
        LinearLayout ll = findViewById(R.id.tags);
        ArrayList<String> mytags = post.getCategories();
        for(i=0; i < mytags.size(); i++) ((TextView) ll.getChildAt(i)).setText(switchText(mytags.get(i)));
        for(; i < 3; i++) ((TextView) ll.getChildAt(i)).setVisibility(View.INVISIBLE);

        if(post.getStorageref() != null){
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("images/" + post.getStorageref());

            Glide.with(this).load(islandRef).into(post_image_view);
        }

        final DocumentReference docRef = db.collection("UTENTI").document(post.getUser());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        owner_name.setText((String) document.get("Name"));
                        RequestOptions options = new RequestOptions().error(R.drawable.add_img);
                        StorageReference ref = storage.getReference().child("users/" + post.getUser());
                        Glide.with(postView.this).load(ref).apply(options).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(owner_img);
                    }
                }
            }
        });
    }

    private String switchText(String tag){

        String res = "#";

        switch (tag) {
            case "nature": return res+"Nature";
            case "science": return res+"Science&IT";
            case "food": return res+"Food";
            case "fashion": return res+"Fashion";
            case "sport": return res+"Sport";
            case "movies": return res+"Movies";
            default: return res+"Music";
        }

    }

    @Override
    public void onBackPressed(){
        finish();
    }
}