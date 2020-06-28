package com.example.huc_project.profile;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.huc_project.R;
import com.example.huc_project.homepage.GlideRequest;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.example.huc_project.posts.postView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;

public class Favorite extends AppCompatActivity implements RecyclerViewAdapter.OnItemListener, RecyclerViewAdapterUser.OnItemListener{
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private Uri profile_pic_uri;

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    RecyclerView recyclerViewUser;
    RecyclerViewAdapterUser recyclerViewAdapterUser;
    ArrayList<PostRow> rowsArrayList = new ArrayList<>();
    ArrayList<PostRow> rowsPostList = new ArrayList<>();
    ArrayList<UserRow> rowsArrayList_user = new ArrayList<>();
    ArrayList<UserRow> rowsUserList = new ArrayList<>();

    boolean isLoading = false;

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_favorites);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final Boolean guest_user;
        final String current_user = Profile_main_page.getCurrent_user();

        if (current_user != null) guest_user = false;
        else guest_user = true;

        rowsArrayList.clear();
        rowsPostList.clear();

        assert current_user != null;
        db.collection("Favorites").document(current_user).collection("post")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        Post post = document.toObject(Post.class);
                        StorageReference storageRef = storage.getReference();


                        StorageReference islandRef = null;
                        if(post.getStorageref() != null){
                            islandRef = storageRef.child("images/" + post.getStorageref());
                        }

                        PostRow post_row = new PostRow(post, islandRef, Glide.with(Favorite.this));
                        rowsPostList.add(post_row);
                    }
                    populateData();
                    setUpRecyclerView();
                    initScrollListener();

                } else {
                    Log.w("Tag", "Error getting documents.", task.getException());
                }
            }
        });

        db.collection("Favorites").document(current_user).collection("user")
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        String name = (String) document.get("Name");
                        String uid = (String) document.get("id");

                        StorageReference storageRef = storage.getReference();
                        StorageReference islandRef = null;
                        if(uid != null){
                            islandRef = storageRef.child("users/" + uid);

                            UserRow user_row = new UserRow(name, islandRef, Glide.with(Favorite.this));
                            rowsUserList.add(user_row);
                        }

                    }
                    populateData_user();
                    setUpRecyclerView_user();
                    initScrollListener_user();

                } else {
                    Log.w("Tag", "Error getting documents.", task.getException());
                }
            }
        });


        Button done_button = findViewById(R.id.save_and_close);
        done_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                intent.putExtra("user", current_user);
                startActivity(intent);
            }
        });

    }

    private void populateData() {
        int i = 0;
        while (i < rowsPostList.size()) {
            rowsArrayList.add(rowsPostList.get(i));
            i++;
        }
    }

    private void populateData_user() {
        int i = 0;
        while (i < rowsUserList.size()) {
            rowsArrayList_user.add(rowsUserList.get(i));
            i++;
        }
    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        glide.load(ref).into(view);
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.rev_post);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);

        recyclerView.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
    }

    private void setUpRecyclerView_user() {
        recyclerViewUser = findViewById(R.id.rec_user);
        recyclerViewUser.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapterUser = new RecyclerViewAdapterUser(rowsArrayList_user, this);
        recyclerViewUser.setLayoutManager(layoutManager);
        recyclerViewUser.setAdapter(recyclerViewAdapterUser);

        recyclerViewUser.addOnItemTouchListener(new RecyclerView.OnItemTouchListener() {
            @Override
            public boolean onInterceptTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {
                return false;
            }

            @Override
            public void onTouchEvent(@NonNull RecyclerView rv, @NonNull MotionEvent e) {

            }

            @Override
            public void onRequestDisallowInterceptTouchEvent(boolean disallowIntercept) {

            }
        });
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

    private void initScrollListener_user() {
        recyclerViewUser.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

                if (!isLoading) {
                    if (linearLayoutManager != null && linearLayoutManager.findLastCompletelyVisibleItemPosition() == rowsArrayList_user.size() - 1) {
                        //bottom of list!
                        //loadMore();
                        //isLoading = true;
                    }
                }
            }
        });


    }

    @Override
    public void onItemClick(int position) {
        PostRow post_clicked = rowsArrayList.get(position);
        Intent intent = new Intent(Favorite.this, postView.class);
        intent.putExtra("title", post_clicked.getTitle());
        intent.putExtra("desc", post_clicked.getDesc());
        intent.putExtra("storageref", post_clicked.getPost().getStorageref());
        intent.putExtra("user", post_clicked.getPost().getUser());
        intent.putExtra("isPackage", post_clicked.getPost().getIsPackage());
        startActivity(intent);
    }

    @Override
    public void onItemClickUser(int position) {
        UserRow user_clicked = rowsArrayList_user.get(position);
        int start = user_clicked.getUid().indexOf("users/");
        String suffix = user_clicked.getUid().substring(start + 1);
        start = suffix.indexOf("/");
        suffix = suffix.substring(start + 1);
        Intent intent_user = new Intent(Favorite.this, Profile_main_page.class);
        intent_user.putExtra("user", suffix);
        startActivity(intent_user);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
        final String current_user = FirebaseAuth.getInstance().getCurrentUser().getUid();
        intent.putExtra("user", current_user);
        startActivity(intent);
        finish();
    }
}

