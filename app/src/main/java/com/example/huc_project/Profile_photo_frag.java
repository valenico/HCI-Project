package com.example.huc_project;

import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.profile.ImageAdapter;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile_photo_frag extends Fragment {

    public Profile_photo_frag() {
        // Required empty public constructor
    }

    GridView gridView;
    public ArrayList<Uri> imgArray = new ArrayList<>();
    View view = null;
    Boolean done = false;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final ImageView imageView = new ImageView(getContext());

        StorageReference ref = storage.getReference().child(current_user.getUid() + "/" + "ads.svg");

        ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                imgArray.add(uri);
                Log.d("lol", String.valueOf(uri));
                Log.d("lolaaa", String.valueOf(imgArray.size()));
                done = true;
            }
        });

        //imgArray.add(Uri.parse("https://firebasestorage.googleapis.com/v0/b/hci-project-311f8.appspot.com/o/K5YWRRgVTATx5XdzAOY7XNdu3Oh2%2Fads.svg?alt=media&token=73062d26-b0e9-4790-b018-af378841a0c7"));
        Log.d("lolo", String.valueOf(imgArray.size()));

        View view = inflater.inflate(R.layout.frag_photo, container, false);
        gridView = view.findViewById(R.id.gridView);
        gridView.setAdapter(new ImageAdapter(getContext(), imgArray));

        // Inflate the layout for this fragment
        return view;
    }
}
