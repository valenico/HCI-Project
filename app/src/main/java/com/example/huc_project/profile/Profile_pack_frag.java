package com.example.huc_project.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.example.huc_project.posts.postView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile_pack_frag extends Fragment implements RecyclerViewAdapter.OnItemListener {

    public Profile_pack_frag() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<PostRow> rowsArrayList = new ArrayList<>();
    ArrayList<PostRow> rowPostList = new ArrayList<>();
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    boolean guest_mode = false;
    boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        rowsArrayList.clear();
        rowPostList.clear();

        final FirebaseUser current_user = Profile_main_page.getCurrent_user();
        //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        db = FirebaseFirestore.getInstance();

        CollectionReference collezione = db.collection("posts");

        collezione.whereEqualTo("user", current_user.getUid()).whereEqualTo("isPackage", true).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
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

                                PostRow post_row = new PostRow(post, islandRef, Glide.with(Profile_pack_frag.this));
                                rowPostList.add(post_row);
                            }
                            populateData();
                            setUpRecyclerView();
                            initScrollListener();

                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_pack, container, false);
    }

    private void populateData() {
        int i = 0;
        while (i < rowPostList.size()) {
            rowsArrayList.add(rowPostList.get(i));
            i++;
        }
    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        glide.load(ref).into(view);
    }

    private void setUpRecyclerView() {
        recyclerView = getView().findViewById(R.id.recycleView);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList, this);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recyclerViewAdapter);
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

    @Override
    public void onItemClick(int position) {
        PostRow post_clicked = rowsArrayList.get(position);
        Intent intent = new Intent(getContext(), postView.class);
        intent.putExtra("title", post_clicked.getTitle());
        intent.putExtra("desc", post_clicked.getDesc());
        intent.putExtra("storageref", post_clicked.getPost().getStorageref());
        intent.putExtra("user", post_clicked.getPost().getUser());
        intent.putExtra("isPackage", post_clicked.getPost().getIsPackage());
        startActivity(intent);
    }
}