package com.example.huc_project.profile;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.huc_project.R;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.profile.Profile_main_page;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile_prof_frag extends Fragment {

    private FirebaseFirestore db;

    public Profile_prof_frag() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        db = FirebaseFirestore.getInstance();
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
        final Boolean guest_user;

        if (current_user != null) guest_user = false;
        else guest_user = true;

        final DocumentReference docRef = db.collection("UTENTI").document(current_user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {

            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        String description = (String) document.get("Description");
                        TextView descrizione = getView().findViewById(R.id.description);
                        if (description == null) descrizione.setText("Hi I am " + name + " and I am using this App!");
                        else descrizione.setText(description);
                    }
                }
            }
        });

            // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_profile, container, false);
    }
}
