package com.example.huc_project.profile;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.example.huc_project.R;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostRow;
import com.example.huc_project.homepage.RecyclerViewAdapter;
import com.example.huc_project.posts.postView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class Profile_post_frag extends Fragment implements RecyclerViewAdapter.OnItemListener {

    RecyclerView recyclerView;
    RecyclerViewAdapter recyclerViewAdapter;
    ArrayList<PostRow> rowsArrayList = new ArrayList<>();
    ArrayList<PostRow> rowsPostList = new ArrayList<>();
    ArrayList<String> idDocs = new ArrayList<>();
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    JSONObject obj;
    JSONArray objarray;

    boolean guest_mode = false;
    boolean isLoading = false;

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container,
                             Bundle savedInstanceState) {

        rowsArrayList.clear();
        rowsPostList.clear();

        final String current_user = Profile_main_page.getCurrent_user();
        //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        RequestQueue ExampleRequestQueue = Volley.newRequestQueue(this.getContext());

        String url = "https://shielded-peak-80677.herokuapp.com/posts";
        StringRequest ExampleStringRequest = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                //This code is executed if the server responds, whether or not the response contains data.
                Log.i("profile", response);

                Log.i("profile", "TRASFORMAZIONE JSON");
                //String tmpresp= "{\"response\":"+response+"}";
                try {
                    objarray= new JSONArray((response));
                    //JSONObject obj = new JSONObject(tmpresp);


                    Log.d("profile", objarray.toString());
                    //setUpCircularMenu();
                    //obj = objarray.getJSONObject(0);
                    //Log.d("VOLLEY1", obj.toString());
                    for (int i = 0; i < objarray.length(); i++) {

                        JSONObject rec = null;
                        try {
                            rec = objarray.getJSONObject(i);

                            if (rec.getString("user").equals(current_user)) {
                                Log.i("profile", "sono dentro IF!");

                                Post post = new Post();
                                post.setId(rec.getString("id"));
                                post.setTitle(rec.getString("title"));
                                post.setStorageref(rec.getString("storageref"));
                                post.setPostdesc(rec.getString("postdesc"));
                                post.setUser(rec.getString("user"));
                                post.setPackage(rec.getBoolean("isPackage"));
                                ArrayList<String> carentearray=new ArrayList<>();
                                carentearray.add(rec.getString("carente"));
                                post.setCategories(carentearray);
                                post.setRole(rec.getString("role"));
                                post.setCountry(rec.getString("country"));
                                post.setCity(rec.getString("city"));
                                StorageReference storageRef = storage.getReference();
                                StorageReference islandRef = null;
                                if (post.getStorageref() != null) {
                                    islandRef = storageRef.child("images/" + post.getStorageref());
                                }
                                PostRow post_row = new PostRow(post, islandRef, Glide.with(Profile_post_frag.this));
                                if(!post_row.getPost().getIsPackage()) rowsPostList.add(post_row);
                                idDocs.add(post.getId());
                            }


                            //setUp();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                        // ...
                    }
                    if(getView()!=null){
                        populateData();
                        setUpRecyclerView();
                        initScrollListener();
                    }
                } catch (Throwable t) {
                    Log.e("profile", "Could not parse malformed JSON:  ");
                }

            }
        }, new Response.ErrorListener() { //Create an error listener to handle errors appropriately.
            @Override
            public void onErrorResponse(VolleyError error) {
                //This code is executed if there is an error.
            }
        });

        ExampleRequestQueue.add(ExampleStringRequest);

        /*db = FirebaseFirestore.getInstance();

        CollectionReference collezione = db.collection("posts");

        collezione.whereEqualTo("user", current_user).get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                Post post = document.toObject(Post.class);
                                StorageReference storageRef = storage.getReference();

                                StorageReference islandRef = null;
                                if(post.getStorageref() != null){
                                    islandRef = storageRef.child("images/" + post.getStorageref());
                                }

                                PostRow post_row = new PostRow(post, islandRef, Glide.with(Profile_post_frag.this));
                                if(!post_row.getPost().getIsPackage()) rowsPostList.add(post_row);
                                idDocs.add(document.getId());
                            }
                            if(getView()!=null){
                                populateData();
                                setUpRecyclerView();
                                initScrollListener();
                            }

                        } else {
                            Log.w("Tag", "Error getting documents.", task.getException());
                        }
                    }
                });*/
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_post, container, false);
    }

    private void populateData() {
        int i = 0;
        while (i < rowsPostList.size()) {
            rowsArrayList.add(rowsPostList.get(i));
            i++;
        }
    }

    public static void glideTask(RequestManager glide, StorageReference ref, ImageView view){
        glide.load(ref).into(view);
    }

    private void setUpRecyclerView() {
        recyclerView = getView().findViewById(R.id.recView);
        if(rowsArrayList.size()>0) {
            getView().findViewById(R.id.no_posts).setVisibility(View.INVISIBLE);
            recyclerView.setVisibility(View.VISIBLE);
            recyclerView.setHasFixedSize(true);
            RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
            recyclerViewAdapter = new RecyclerViewAdapter(rowsArrayList, this);
            recyclerView.setLayoutManager(layoutManager);
            recyclerView.setAdapter(recyclerViewAdapter);
        }else{
            getView().findViewById(R.id.no_posts).setVisibility(View.VISIBLE);
            recyclerView.setVisibility(View.INVISIBLE);

        }
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

    @Override
    public void onItemClick(int position) {
        PostRow post_clicked = rowsArrayList.get(position);
        Intent intent = new Intent(getContext(), postView.class);
        intent.putExtra("title", post_clicked.getTitle());
        intent.putExtra("desc", post_clicked.getDesc());
        intent.putExtra("role", post_clicked.getPost().getRole());
        intent.putExtra("city", post_clicked.getPost().getCity());
        intent.putExtra("country", post_clicked.getPost().getCountry());
        intent.putExtra("categories", post_clicked.getPost().getCategories());
        intent.putExtra("id", idDocs.get(position));
        intent.putExtra("storageref", post_clicked.getPost().getStorageref());
        intent.putExtra("user", post_clicked.getPost().getUser());
        intent.putExtra("isPackage", post_clicked.getPost().getIsPackage());
        startActivity(intent);
    }
}
