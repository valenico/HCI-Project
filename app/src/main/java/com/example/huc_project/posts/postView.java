package com.example.huc_project.posts;

import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import com.bumptech.glide.Glide;
import com.example.huc_project.homepage.DataGettingActivity;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.profile.Favorite;
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
import android.widget.TextView;
import android.widget.Toast;

import com.example.huc_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.protobuf.StringValue;
import com.google.protobuf.StringValueOrBuilder;

import java.util.ArrayList;
import java.util.HashMap;

public class postView extends AppCompatActivity {

    Post post;
    ImageView post_image_view;
    TextView title_view;
    TextView desc_view;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        final Intent intent = getIntent();
        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false));

        this.post_image_view = findViewById(R.id.imageViewplaces);
        this.title_view = findViewById(R.id.postTitle);
        this.desc_view = findViewById(R.id.postDesc);

        final ArrayList<String> fav_post = new ArrayList<>();
        db.collection("UTENTI").document(mAuth.getCurrentUser().getUid())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.get("Fav_post") != null) {
                        ArrayList<String> l = (ArrayList<String>) document.get("Fav_post");
                        for (int i=0; i<l.size(); i++) { fav_post.add(l.get(i)); }
                    }
                } else {
                    Log.w("lola", "Error getting documents.", task.getException());
                }
            }
        });

        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24);
        final Drawable wrappedDrawable = DrawableCompat.wrap(like);
        for (int i=0; i<fav_post.size(); i++) {
            if (intent.getStringExtra("storageref").equals(fav_post.get(i))) {
                DrawableCompat.setTint(wrappedDrawable, Color.RED); // TODO cercare di farlo diventare rosso se già esistente nei preferiti
                fab.setImageDrawable(wrappedDrawable);
                break;
            }
        }
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Boolean add_post = true;
                for (int i=0; i<fav_post.size(); i++) {
                    if (intent.getStringExtra("storageref").equals(fav_post.get(i))) {
                        add_post = false;
                        break;
                    }
                    else {
                        add_post = true;
                    }
                }
                if (add_post) {
                    HashMap<String,Object> data = new HashMap<>();
                    data.put("title",intent.getStringExtra("title"));
                    data.put("user", intent.getStringExtra("user"));
                    data.put("postdesc",intent.getStringExtra("desc") );
                    data.put("isPackage",intent.getBooleanExtra("isPackage", false));
                    data.put("storageref",intent.getStringExtra("storageref"));
                    db.collection("Favorites").document(mAuth.getUid()).collection("post").add(data);
                    fav_post.add(intent.getStringExtra("storageref"));
                    HashMap<String, ArrayList<String>> post_favorites = new HashMap<>();
                    post_favorites.put("Fav_post", fav_post);
                    db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(post_favorites, SetOptions.merge());
                    Toast.makeText(postView.this, intent.getStringExtra("title").toString() + " added to your favorites posts!", Toast.LENGTH_LONG).show();
                    DrawableCompat.setTint(wrappedDrawable, Color.RED);
                }
                else {
                    fav_post.remove(intent.getStringExtra("storageref"));
                    HashMap<String, ArrayList<String>> post_favorites = new HashMap<>();
                    post_favorites.put("Fav_post", fav_post);
                    db.collection("UTENTI").document(mAuth.getCurrentUser().getUid()).set(post_favorites, SetOptions.merge());
                    DrawableCompat.setTint(wrappedDrawable, Color.GREEN); //TODO Cercare di rimettere il colore originale
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

        setUpPost();
    }

    public void setUpPost(){
        title_view.setText(post.getTitle());
        desc_view.setText(post.getPostdesc());

        if(post.getStorageref() != null){
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("images/" + post.getStorageref());

            Glide.with(this).load(islandRef).into(post_image_view);

        }
    }

    @Override
    public void onBackPressed(){
        finish();
    }
}