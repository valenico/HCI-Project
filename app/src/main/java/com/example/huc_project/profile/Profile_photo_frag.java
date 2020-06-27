package com.example.huc_project.profile;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.huc_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

public class Profile_photo_frag extends Fragment {

    public Profile_photo_frag() {
        // Required empty public constructor
    }

    final String TAG = "TAG";
    GridView gridView;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> l = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ImageAdapter gridAdapter;

    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private FirebaseFirestore db;
    final String current_user = Profile_main_page.getCurrent_user();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_photo, container, false);

        gridView = view.findViewById(R.id.gridView);
        swipeRefresh = view.findViewById(R.id.swipeContainerPhotos);
        db = FirebaseFirestore.getInstance();

        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                list.clear();
                gridAdapter.notifyDataSetChanged();
                setUpGridView();
                swipeRefresh.setRefreshing(false);
            }
        });

        setUpGridView();

        return view;
    }

    public void setUpGridView(){
        db.collection("UTENTI").document(current_user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    list = new ArrayList<>();
                    if (document.get("images") != null){
                        list = (ArrayList<String>) document.get("images");
                        Collections.reverse(list);
                    }


                    gridAdapter = new ImageAdapter(getContext(), list);
                    gridView.setAdapter(gridAdapter);
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            Toast.makeText(getContext(),"Clicked Image " + position, Toast.LENGTH_LONG).show();
                        }
                    });


                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


}

