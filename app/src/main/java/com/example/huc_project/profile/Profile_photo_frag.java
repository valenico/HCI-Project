package com.example.huc_project.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import com.example.huc_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Profile_photo_frag extends Fragment {

    public Profile_photo_frag() {
        // Required empty public constructor
    }

    final String TAG = "TAG";
    GridView gridView;
    View view = null;
    private Uri profile_pic_uri;
    ArrayList<String> l = new ArrayList<String>();

    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    Uri imageUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view =inflater.inflate(R.layout.frag_photo, container, false);
        Button button = (Button) view.findViewById(R.id.add_photo);
        l.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200607-WA0004.jpg");

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();

            }
        });

        db = FirebaseFirestore.getInstance();
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        gridView = view.findViewById(R.id.gridView);


        db.collection("UTENTI").document(current_user.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    ArrayList<String> list = new ArrayList<String>();
                    if(document.get("images") != null) list = (ArrayList<String>) document.get("images");

                    gridView.setAdapter(new ImageAdapter(getContext(), list));



                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
        return view;
    }



    private void openGallery() {
        Intent gallery = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        startActivityForResult(gallery, 100);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == 100){
            imageUri = data.getData();
            final Uri file = imageUri;
            final StorageReference storageRef = storage.getReference();
            final String imgRef = file.getLastPathSegment();
            final String user = current_user.getUid();



            db.collection("UTENTI").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                            ArrayList<String> list = new ArrayList<String>();
                           if(document.get("images") != null) list = (ArrayList<String>) document.get("images");
                            list.add(imgRef);
                            HashMap<String, ArrayList<String>> imgs = new HashMap<>();
                            imgs.put("images",list);

                            StorageReference riversRef = storageRef.child("images/"+file.getLastPathSegment());
                            UploadTask uploadTask = riversRef.putFile(file);

                            db.collection("UTENTI").document(user).set(imgs , SetOptions.merge());

                        } else {
                            Log.d(TAG, "No such document");
                        }
                    } else {
                        Log.d(TAG, "get failed with ", task.getException());
                    }
                }
            });

        }

        FragmentTransaction ft = getFragmentManager().beginTransaction();
        ft.detach(Profile_photo_frag.this).attach(Profile_photo_frag.this).commit();
    }
}


