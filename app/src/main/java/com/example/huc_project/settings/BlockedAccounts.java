package com.example.huc_project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.huc_project.R;
import com.example.huc_project.chat.Chat;
import com.example.huc_project.chat.ChatMessage;
import com.example.huc_project.chat.Conversation;
import com.example.huc_project.chat.RecyclerViewAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Document;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

public class BlockedAccounts extends AppCompatActivity {

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    final ArrayList<String> nomi = new ArrayList<>();
    AutoCompleteTextView new_blocked;
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    private BlockedRecycler recycler;
    List<String> blocked_list;
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_blocked_accounts);

        new_blocked = findViewById(R.id.new_blocked_user);
        final Button button = findViewById(R.id.button_block);
        button.setEnabled(false);
        recyclerView = findViewById(R.id.recycler_blocked);
        db.collection("UTENTI").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if(task.isSuccessful()){
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        nomi.add(document.get("Name").toString());
                    }
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(getBaseContext(), android.R.layout.simple_list_item_1, nomi );
                    new_blocked.setAdapter(adapter);
                }
            }
        });

        new_blocked.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(s.toString().trim().length()==0){
                    button.setEnabled(false);
                } else {
                    button.setEnabled(true);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) { }
            @Override
            public void afterTextChanged(Editable s) {}
        });
        setUp();
    }

    private void setUp(){

        db.collection("Blocked").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if(task.isSuccessful()){
                     DocumentSnapshot d = task.getResult();
                     if (d.contains("blocked")) {
                         blocked_list = (List<String>) d.get("blocked");
                     } else {
                         blocked_list = new ArrayList<>();
                     }
                     setUpRecyclerView();
                     initScrollListener();
                }
            }
        });

    }


    private void setUpRecyclerView() {
        recyclerView = findViewById(R.id.recycler_blocked);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recycler = new BlockedRecycler(blocked_list);
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(recycler);
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
            }
        });
    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        glide.load(ref).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(view);
    }

    public void block_this_user(View v){
        String mytext = new_blocked.getText().toString() + " is not able to text you and see your profile now.";
        Toast.makeText(this, mytext, Toast.LENGTH_LONG).show();
        blocked_list.add( new_blocked.getText().toString() );
        new_blocked.setText("");
        HashMap<String, Object> data = new HashMap<>();
        data.put( "blocked" , blocked_list );
        db.collection("Blocked").document(mAuth.getUid()).set(data, SetOptions.merge());
        this.setUp();
    }
}