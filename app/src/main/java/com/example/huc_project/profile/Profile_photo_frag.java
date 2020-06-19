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

    final int PICK_IMAGE = 100;
    final String TAG = "TAG";
    GridView gridView;
    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> l = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    ImageAdapter gridAdapter;

    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    Uri imageUri;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
    final String current_user = Profile_main_page.getCurrent_user();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.frag_photo, container, false);

        gridView = view.findViewById(R.id.gridView);
        swipeRefresh = view.findViewById(R.id.swipeContainerPhotos);

        if (current_user.equals(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
            Button button = view.findViewById(R.id.add_photo);
            button.setVisibility(View.VISIBLE);
            l.add("/storage/emulated/0/WhatsApp/Media/WhatsApp Images/IMG-20200607-WA0004.jpg");

            button.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    openGallery();
                }
            });
        }


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


    private void openGallery() {
        Intent gallery = new Intent();//(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == PICK_IMAGE && data!=null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setRequestedSize(500,500, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                    .start(getContext(),this);
        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == -1){
                imageUri = result.getUri();
                uploadImage(imageUri);
            }
        }
    }


    private void uploadImage(Uri imageUri){
        final Uri file = imageUri;
        final StorageReference storageRef = storage.getReference();
        final String imgRef = file.getLastPathSegment();
        final String user = current_user;

        db.collection("UTENTI").document(user).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                        ArrayList<String> list = new ArrayList<>();
                        if (document.get("images") != null)
                            list = (ArrayList<String>) document.get("images");
                        list.add(imgRef);
                        final HashMap<String, ArrayList<String>> imgs = new HashMap<>();
                        imgs.put("images", list);

                        StorageReference riversRef = storageRef.child("images/" + file.getLastPathSegment());
                        UploadTask uploadTask = riversRef.putFile(file);
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                                db.collection("UTENTI").document(user).set(imgs, SetOptions.merge());
                            }
                        });

                    } else {
                        Log.d(TAG, "No such document");
                    }


                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}

