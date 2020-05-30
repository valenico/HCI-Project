package com.example.huc_project.profile;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huc_project.R;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.example.huc_project.profile.Profile_main_page;
import com.facebook.FacebookSdk;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class Profile_prof_frag extends Fragment {

    private FirebaseFirestore db;

    public Profile_prof_frag() {
        // Required empty public constructor
    }

    RecyclerView recyclerView;
    RecyclerViewAdapterSocial recyclerViewAdapter;
    ArrayList<SocialRow> rowsArrayList = new ArrayList<>();
    ArrayList<SocialRow> rowsSocialList = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    boolean guest_mode = false;
    boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rowsArrayList.clear();
        rowsSocialList.clear();

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

                        /*try {
                            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("fb://profile/1830688126" ));
                            startActivity(intent);
                        } catch(Exception e) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.facebook.com/appetizerandroid")));
                        }*/

                    }
                }
                ImageButton addSocial = getView().findViewById(R.id.addSocial);
                addSocial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), AddNewSocialAccount.class);
                        startActivity(intent);
                    }
                });

                ImageButton remSocial = getView().findViewById(R.id.remove_account);
                remSocial.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), RemoveSocialAccount.class);
                        startActivity(intent);
                    }
                });
            }
        });

        CollectionReference collection = db.collection("Social");

        collection.whereEqualTo("user", current_user.getUid()).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                String name = (String) document.get("name");
                                String identity = (String) document.get("identity");
                                String user = (String) document.get("user");
                                String followers = (String) document.get("followers");
                                String lm = (String) document.get("last_month");
                                String lw = (String) document.get("last_week");
                                Social social = new Social(name, identity, user, followers, lm, lw);
                                SocialRow social_row = new SocialRow(social, Glide.with(Profile_prof_frag.this));
                                rowsSocialList.add(social_row);
                                }
                            }
                        populateData();
                        setUpRecyclerView();
                        initScrollListener();
                    }
                }
        );
                // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_profile, container, false);
    }

    private void populateData() {
        int i = 0;
        while (i < rowsSocialList.size()) {
            rowsArrayList.add(rowsSocialList.get(i));
            i++;
        }
    }

    private void setUpRecyclerView() {
        recyclerView = getView().findViewById(R.id.ricicla);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerViewAdapter = new RecyclerViewAdapterSocial(rowsArrayList);
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

    public static void glideTask(RequestManager glide, int ref, ImageView view){
        glide.load(ref).into(view);
    }
}
