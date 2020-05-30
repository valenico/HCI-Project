package com.example.huc_project.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huc_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Random;

public class RemoveSocialAccount extends AppCompatActivity {
    private FirebaseFirestore db;
    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

    RecyclerView recyclerView;
    RecyclerViewAdapterSocial2 recyclerViewAdapter;
    ArrayList<SocialRow> rowsArrayList = new ArrayList<>();
    ArrayList<SocialRow> rowsSocialList = new ArrayList<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();

    boolean guest_mode = false;
    boolean isLoading = false;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_remove_social);


        rowsArrayList.clear();
        rowsSocialList.clear();

        db = FirebaseFirestore.getInstance();
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

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
                                                       SocialRow social_row = new SocialRow(social, Glide.with(RemoveSocialAccount.this));
                                                       rowsSocialList.add(social_row);
                                                   }
                                               }
                                               populateData();
                                               setUpRecyclerView();
                                               initScrollListener();
                                           }
                                       }
                );


    }

    private void populateData() {
        int i = 0;
        while (i < rowsSocialList.size()) {
            rowsArrayList.add(rowsSocialList.get(i));
            i++;
        }
    }

    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.riciclamino);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerViewAdapter = new RecyclerViewAdapterSocial2(rowsArrayList, getApplicationContext());
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
                    }
                }
            }
        });
        Button deleteAllAccounts = findViewById(R.id.delete_all_accounts);
        deleteAllAccounts.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                startActivity(intent);
            }
        });
    }
}
