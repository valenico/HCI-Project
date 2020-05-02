package com.example.huc_project.homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.example.huc_project.R;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

public class DataGettingActivity extends AppCompatActivity {
    private static final String TAG = "taggy";
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data_getting);

        db = FirebaseFirestore.getInstance();
        CollectionReference collezione=db.collection("posts");

        collezione.get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Log.d(TAG, document.getId() + " => " + document.getData());
                                TextView dataFromDatabase = (TextView) findViewById(R.id.datafromdatabase);
                                //String post=(document.getData().values().toString());
                                DocumentSnapshot documentSnapshot = document;
                                Post post=new Post();
                                post = documentSnapshot.toObject(Post.class);
                                Log.d(TAG, "le velineeeeeeeeeeeeeeeeeeeeee"+post);
                                dataFromDatabase.setText(dataFromDatabase.getText()+"\n"+post.postdesc);
                                //dataFromDatabase.setText((CharSequence) document.getData());

                            }
                        } else {
                            Log.w(TAG, "Error getting documents.", task.getException());
                        }
                    }
                });


    }

}

