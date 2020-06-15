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
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

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


        final FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                HashMap<String,Object> data = new HashMap<>();
                data.put("title",intent.getStringExtra("title"));
                data.put("user", intent.getStringExtra("user"));
                data.put("postdesc",intent.getStringExtra("desc") );
                data.put("isPackage",intent.getBooleanExtra("isPackage", false));
                data.put("storageref",intent.getStringExtra("storageref"));
                db.collection("Favorites").document(mAuth.getUid()).collection("post").add(data);
                Toast.makeText(postView.this, intent.getStringExtra("title").toString() + " added to your favorites posts!", Toast.LENGTH_LONG).show();
                Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.ic_baseline_favorite_24);
                Drawable wrappedDrawable = DrawableCompat.wrap(like);
                DrawableCompat.setTint(wrappedDrawable, Color.RED);
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