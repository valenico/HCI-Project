package com.example.huc_project.posts;

import android.content.Intent;
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
import androidx.appcompat.widget.Toolbar;

import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.huc_project.R;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

public class postView extends AppCompatActivity {

    Post post;
    ImageView post_image_view;
    TextView title_view;
    TextView desc_view;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post_view);

        Intent intent = getIntent();
        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false));

        this.post_image_view = findViewById(R.id.imageViewplaces);
        this.title_view = findViewById(R.id.postTitle);
        this.desc_view = findViewById(R.id.postDesc);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
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