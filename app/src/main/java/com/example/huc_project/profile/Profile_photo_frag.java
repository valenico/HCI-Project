package com.example.huc_project.profile;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.RelativeLayout;
import android.widget.Toast;
import android.widget.Toolbar;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.bumptech.glide.Glide;
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
import java.util.zip.Inflater;

import static android.content.Context.LAYOUT_INFLATER_SERVICE;

public class Profile_photo_frag extends Fragment {

    public Profile_photo_frag() {
        // Required empty public constructor
    }

    final String TAG = "TAG";
    GridView gridView;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    SwipeRefreshLayout swipeRefresh;
    ArrayList<String> l = new ArrayList<>();
    ArrayList<String> list = new ArrayList<>();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();
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
                    final Context c = getActivity().getApplicationContext();
                    gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                            LayoutInflater inflater = getLayoutInflater();
                            ConstraintLayout popupView = (ConstraintLayout) inflater.inflate(R.layout.popup_image, null);

                            // create the popup window
                            int width = LinearLayout.LayoutParams.MATCH_PARENT;
                            int height = LinearLayout.LayoutParams.MATCH_PARENT;
                            boolean focusable = true; // lets taps outside the popup also dismiss it
                            final PopupWindow popupWindow = new PopupWindow(popupView, width, height, focusable);

                            final String name_pic = (String) gridAdapter.getItem(position);
                            StorageReference storageRef = storage.getReference();
                            StorageReference islandRef = (StorageReference) storageRef.child("images/" + name_pic);
                            ImageView imageView = popupView.findViewById(R.id.big_image);
                            Glide.with(Profile_photo_frag.this)
                                    .load(islandRef)
                                    .into(imageView);
                            popupWindow.showAtLocation(view, Gravity.CENTER, 0, 0);
                            popupView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View v) {
                                    popupWindow.dismiss();
                                }
                            });
                            Button b = popupView.findViewById(R.id.delete_pic);
                            if(!current_user.equals(mAuth.getUid())){
                                b.setVisibility(View.INVISIBLE);
                            } else {
                                b.setOnClickListener(new View.OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        db.collection("UTENTI").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                            @Override
                                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                                if (task.isSuccessful()) {
                                                    DocumentSnapshot document = task.getResult();
                                                    if (document.exists()) {
                                                        ArrayList<String> list = (ArrayList<String>) document.get("images");
                                                        for(int u = 0; u < list.size(); u++){
                                                            if(list.get(u).equals(name_pic)){
                                                                list.remove(u);
                                                            }
                                                        }
                                                        final HashMap<String, ArrayList<String>> imgs = new HashMap<>();
                                                        imgs.put("images", list);
                                                        db.collection("UTENTI").document(mAuth.getUid()).set(imgs, SetOptions.merge());
                                                        Toast.makeText(c,"The image has been cancelled.", Toast.LENGTH_LONG).show();
                                                        popupWindow.dismiss();
                                                        setUpGridView();
                                                    }
                                                }
                                            }
                                        });
                                    }
                                });
                            }
                        }
                    });


                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }


}

