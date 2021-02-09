package com.example.huc_project.posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.bumptech.glide.Glide;
import com.example.huc_project.CustomCheckbox;
import com.example.huc_project.R;
import com.example.huc_project.homepage.CreateNewPostActivity;
import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.homepage.Post;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class edit_post extends AppCompatActivity {

    Post post;
    String idPostEdit;
    ImageView post_image_view;
    final int PICK_IMAGE= 100;
    Uri imageUri;
    TextView title_view;
    TextView desc_view;
    String id;
    String titleForUpdate;
    String descForUpdate;
    Dialog popChooseCategories ;
    TextView categories_selected;
    private HashMap<String,Object> interests_selected = new HashMap<>();
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };

    LinearLayout categoriesCardLayout;
    TextView choose;
    final int maxChecked = 3;
    int countChecked = 0;
    boolean isTheImageUp = false;

    CustomCheckbox sportCheck;
    CustomCheckbox fashionCheck;
    CustomCheckbox scienceCheck;
    CustomCheckbox musicCheck;
    CustomCheckbox moviesCheck;
    CustomCheckbox foodCheck;
    CustomCheckbox natureCheck;
    String storageref;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    TextView textDesc;
    TextView textTitle;

    private LocationManager locationMangaer = null;
    Location GPSlocation = null;
    Double lo = 0.0;
    Double la = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);

        Intent intent = getIntent();

        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false), intent.getStringArrayListExtra("categories"),
                intent.getStringExtra("role"), intent.getStringExtra("country"), intent.getStringExtra("city"), intent.getStringExtra("idpost"));
                Log.e("POSTEDIT", intent.getStringExtra("title"));
        id = intent.getStringExtra("id");
        titleForUpdate=intent.getStringExtra("title");
        descForUpdate=intent.getStringExtra("desc");
        Log.e("POSTEDIT", id);
        storageref = intent.getStringExtra("storageref");
        final Button post_button = findViewById(R.id.postBtn);
        post_button.setText("Save");
        Intent intento1 = getIntent();

        //post.put("id", intento.getStringExtra("idpost"));
        idPostEdit =intento1.getStringExtra("idpost");
        textDesc = findViewById(R.id.textDesc);
        textTitle = findViewById(R.id.textTitle);

        textDesc.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    post_button.setActivated(false);
                    post_button.setBackgroundResource(R.drawable.custom_button_disabled);
                    post_button.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if( ((EditText) findViewById(R.id.textTitle)).getText().toString().length() > 0) {
                    post_button.setActivated(true);
                    post_button.setBackgroundResource(R.drawable.custom_button);
                    post_button.setTextColor(Color.WHITE);
                }
            }
        });

        final ImageButton getpositionButton = (ImageButton) findViewById(R.id.getPosition);
        Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mylocation);

        final Drawable wrappedDrawable = DrawableCompat.wrap(like);
        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.color_button1));
        getpositionButton.setImageDrawable(wrappedDrawable);
        getpositionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(displayGpsStatus()) {
                    if (ActivityCompat.checkSelfPermission(edit_post.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                            ActivityCompat.checkSelfPermission(edit_post.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                        ActivityCompat.requestPermissions(edit_post.this,
                                new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                                99);

                    } else {
                        Criteria criteria = new Criteria();
                        criteria.setAccuracy(Criteria.ACCURACY_FINE);
                        locationMangaer = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                        locationMangaer.requestSingleUpdate(criteria, locationListener, null);

                    }
                }else{
                    Toast.makeText(getBaseContext(),
                            "You need to enable gps", Toast.LENGTH_SHORT).show();
                }

            }
        });

        textTitle.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                if(s.length() == 0){
                    post_button.setActivated(false);
                    post_button.setBackgroundResource(R.drawable.custom_button_disabled);
                    post_button.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if( ((EditText) findViewById(R.id.textTitle)).getText().toString().length() > 0) {
                    post_button.setActivated(true);
                    post_button.setBackgroundResource(R.drawable.custom_button);
                    post_button.setTextColor(Color.WHITE);
                }
            }
        });

        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(!post_button.isActivated()){
                    if(textTitle.getText().toString().trim().length() <= 0) textTitle.setError("Title cannot be empty");
                    if(textDesc.getText().toString().trim().length() <= 0) textDesc.setError("Description cannot be empty.");
                    //Toast.makeText(CreateNewPostActivity.this, "Title and description are mandatory!",Toast.LENGTH_LONG).show();
                    return;
                }

                final Map<String, Object> post = new HashMap<>();
                EditText text = (EditText) findViewById(R.id.textDesc);
                EditText texttitle = (EditText) findViewById(R.id.textTitle);
                String postDescription = text.getText().toString();
                final String postTitle = texttitle.getText().toString();
                String country= ((AutoCompleteTextView) findViewById(R.id.countries_list)).getText().toString();
                String city = ((AutoCompleteTextView) findViewById(R.id.cities_list)).getText().toString();
                Bitmap bitmap = ((BitmapDrawable)post_image_view.getDrawable()).getBitmap();

                Boolean isPackage = true;

                StorageReference storageRef = storage.getReference();
                StorageReference riversRef;
                UploadTask uploadTask;
                String role="";
                CheckBox checkispackage=(CheckBox) findViewById(R.id.checkpackage);
                if (((CheckBox)findViewById(R.id.sponsor)).isChecked()) {
                    role="sponsor";
                }
                else if (((CheckBox)findViewById(R.id.sponsorship)).isChecked()) {
                    role="sponsorship";
                }
                if ( checkispackage.isChecked()) {
                    isPackage = true;
                }
                else {
                    isPackage = false;
                }


                final ArrayList<String> categoriesChosen = new ArrayList<String>();


                if (interests_selected.containsKey("science") && (boolean)interests_selected.get("science") ) categoriesChosen.add("science");
                if (interests_selected.containsKey("nature") && (boolean)interests_selected.get("nature")) categoriesChosen.add("nature");
                if (interests_selected.containsKey("sport") && (boolean)interests_selected.get("sport")) categoriesChosen.add("sport");
                if (interests_selected.containsKey("fashion") && (boolean)interests_selected.get("fashion")) categoriesChosen.add("fashion");
                if (interests_selected.containsKey("food") && (boolean)interests_selected.get("food")) categoriesChosen.add("food");
                if (interests_selected.containsKey("movies") && (boolean)interests_selected.get("movies")) categoriesChosen.add("movies");
                if (interests_selected.containsKey("music") && (boolean)interests_selected.get("music")) categoriesChosen.add("music");


                post.put("title", postTitle);
                post.put("postdesc", postDescription);
                post.put("user", mAuth.getUid());
                post.put("isPackage", isPackage);
                post.put("categories", categoriesChosen);
                post.put("role", role);
                post.put("city", city);
                post.put("country", country);
                Intent intento = getIntent();

                post.put("id", intento.getStringExtra("idpost"));
                //idPostEdit =intento.getStringExtra("idpost");
                Log.i("UPDATEid",  intento.getStringExtra("idpost"));
                if (isTheImageUp) {
                    riversRef = storageRef.child("images/" + imageUri.getLastPathSegment());
                    post.put("storageref", imageUri.getLastPathSegment());
                    uploadTask = riversRef.putFile(imageUri);
                    uploadTask.addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception exception) {
                            // Handle unsuccessful uploads
                        }
                    }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                            // Add a new document with a generated ID
                            //TODO CHIAMARE QUI UPDATE POSTS

                            try {
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    String URL = "https://shielded-peak-80677.herokuapp.com/posts/"+ post.get("id");
                                    JSONObject jsonBody = new JSONObject();
                                    //jsonBody.put("id", "id_"+System.currentTimeMillis());
                                    jsonBody.put("title", post.get("title"));
                                    jsonBody.put("storageref", post.get("storageref"));
                                    jsonBody.put("postdesc", post.get("postdesc"));
                                    jsonBody.put("user", post.get("user"));
                                    jsonBody.put("isPackage", post.get("isPackage"));
                                    //ArrayList<String> carente = post.get("categories");
                                    if (categoriesChosen.isEmpty()) jsonBody.put("carente", "");
                                    else jsonBody.put("carente", categoriesChosen.get(0));

                                    jsonBody.put("role", post.get("role"));
                                    jsonBody.put("country", post.get("country"));
                                    jsonBody.put("city", post.get("city"));
                                    Log.i("VOLLEY", jsonBody.toString());
                                    final String requestBody = jsonBody.toString();

                                    StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                                        @Override
                                        public void onResponse(String response) {
                                            Log.i("VOLLEY", response);
                                        }
                                    }, new Response.ErrorListener() {
                                        @Override
                                        public void onErrorResponse(VolleyError error) {
                                            Log.e("VOLLEY", error.toString());
                                        }
                                    }) {
                                        @Override
                                        public String getBodyContentType() {
                                            return "application/json; charset=utf-8";
                                        }

                                        @Override
                                        public byte[] getBody() throws AuthFailureError {
                                            try {
                                                return requestBody == null ? null : requestBody.getBytes("utf-8");
                                            } catch (UnsupportedEncodingException uee) {
                                                VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                                return null;
                                            }
                                        }

                                        @Override
                                        protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                            String responseString = "";
                                            if (response != null) {
                                                responseString = String.valueOf(response.statusCode);
                                                // can get more details such as response.headers
                                            }
                                            return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                                        }
                                    };

                                    requestQueue.add(stringRequest);
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }


                            /*db.collection("posts").whereEqualTo("title", titleForUpdate).whereEqualTo("postdesc", descForUpdate).whereEqualTo("user", mAuth.getUid())
                            .get()
                                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                        @Override
                                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                            if (task.isSuccessful()) {
                                                for (QueryDocumentSnapshot document : task.getResult()) {
                                                    Log.e("IL VERO ID DEL POST", document.getId() + " => " + document.getData());
                                                    id=document.getId();
                                                    db.collection("posts").document(id).set(post);
                                                }
                                            } else {
                                                Log.d("taggy", "Error getting documents: ", task.getException());
                                            }
                                        }
                                    });*/
                        }
                    });
                } else {
                    post.put("storageref", storageref);
                    //TODO CHIAMARE ANCHE QUI UPDATE POSTS
                    try {
                        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                        String URL = "https://shielded-peak-80677.herokuapp.com/posts/"+ post.get("id");
                        JSONObject jsonBody = new JSONObject();
                        //jsonBody.put("id", "id_"+System.currentTimeMillis());
                        jsonBody.put("title", post.get("title"));
                        jsonBody.put("storageref", post.get("storageref"));
                        jsonBody.put("postdesc", post.get("postdesc"));
                        jsonBody.put("user", post.get("user"));
                        jsonBody.put("isPackage", post.get("isPackage"));
                        //ArrayList<String> carente = post.get("categories");
                        if (categoriesChosen.isEmpty()) jsonBody.put("carente", "");
                        else jsonBody.put("carente", categoriesChosen.get(0));
                        jsonBody.put("role", post.get("role"));
                        jsonBody.put("country", post.get("country"));
                        jsonBody.put("city", post.get("city"));
                        Log.i("VOLLEY", jsonBody.toString());
                        final String requestBody = jsonBody.toString();

                        StringRequest stringRequest = new StringRequest(Request.Method.PUT, URL, new Response.Listener<String>() {
                            @Override
                            public void onResponse(String response) {
                                Log.i("VOLLEY", response);
                            }
                        }, new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("VOLLEY", error.toString());
                            }
                        }) {
                            @Override
                            public String getBodyContentType() {
                                return "application/json; charset=utf-8";
                            }

                            @Override
                            public byte[] getBody() throws AuthFailureError {
                                try {
                                    return requestBody == null ? null : requestBody.getBytes("utf-8");
                                } catch (UnsupportedEncodingException uee) {
                                    VolleyLog.wtf("Unsupported Encoding while trying to get the bytes of %s using %s", requestBody, "utf-8");
                                    return null;
                                }
                            }

                            @Override
                            protected Response<String> parseNetworkResponse(NetworkResponse response) {
                                String responseString = "";
                                if (response != null) {
                                    responseString = String.valueOf(response.statusCode);
                                    // can get more details such as response.headers
                                }
                                return Response.success(responseString, HttpHeaderParser.parseCacheHeaders(response));
                            }
                        };

                        requestQueue.add(stringRequest);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                    /*db.collection("posts").whereEqualTo("title", titleForUpdate).whereEqualTo("postdesc", descForUpdate).whereEqualTo("user", mAuth.getUid())
                            .get()
                            .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                                    if (task.isSuccessful()) {
                                        for (QueryDocumentSnapshot document : task.getResult()) {
                                            Log.e("IL VERO ID DEL POST", document.getId() + " => " + document.getData());
                                            id=document.getId();
                                            db.collection("posts").document(id).set(post);
                                        }
                                    } else {
                                        Log.d("taggy", "Error getting documents: ", task.getException());
                                    }
                                }
                            });*/
                }
                //Intent intent = new Intent(getApplicationContext(), PostCreatedSuccessfully.class);
                Toast.makeText(getApplicationContext(), "Post modified successfully.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                startActivity(intent);
                finish();

            }
        });

        this.post_image_view = findViewById(R.id.imageBtn);
        if(post.getStorageref() != null){
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("images/" + post.getStorageref());
            Glide.with(this).load(islandRef).into(post_image_view);
        }
        post_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        this.title_view = findViewById(R.id.textTitle);
        title_view.setText(post.getTitle());
        this.desc_view = findViewById(R.id.textDesc);
        desc_view.setText(post.getPostdesc());
        ( (AutoCompleteTextView) findViewById(R.id.countries_list) ).setText(post.getCountry());
        ( (AutoCompleteTextView) findViewById(R.id.cities_list) ).setText(post.getCity());
        ( (CheckBox) findViewById(R.id.checkpackage)).setChecked(post.getIsPackage());
        if(post.getRole().equals("sponsor")) {
            ( (CheckBox) findViewById(R.id.sponsor)).setChecked(true);
            ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(false);
        } else {
            ( (CheckBox) findViewById(R.id.sponsor)).setChecked(false);
            ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(true);
        }

        CompoundButton.OnCheckedChangeListener sponsorChecker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(buttonView.equals(( (CheckBox) findViewById(R.id.sponsor))) && isChecked){
                    if(( (CheckBox) findViewById(R.id.sponsorship)).isChecked()){
                        ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(false);
                    }
                    ( (CheckBox) findViewById(R.id.sponsor)).setChecked(true);
                }
                else if(isChecked){
                    if(( (CheckBox) findViewById(R.id.sponsor)).isChecked()){
                        ( (CheckBox) findViewById(R.id.sponsor)).setChecked(false);
                    }
                    ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(true);
                }
            }
        };

        ( (CheckBox) findViewById(R.id.sponsor)).setOnCheckedChangeListener(sponsorChecker);
        ( (CheckBox) findViewById(R.id.sponsorship)).setOnCheckedChangeListener(sponsorChecker);

        ArrayList<String> categories = post.getCategories();
        if (post.getCategories()!=null) {
            for (int u = 0; u < categories.size(); u++) {
                interests_selected.put(categories.get(u), true);
                Log.d("CAT", categories.get(u));
            }
        }
        categoriesCardLayout = (LinearLayout) findViewById(R.id.categories_wrapper);
        iniPopup();
        categories_selected = findViewById(R.id.categories_selected);
        choose = findViewById(R.id.choose);
        categories_selected = findViewById(R.id.categories_selected);
        categoriesCardLayout = (LinearLayout) findViewById(R.id.categories_wrapper);

        View.OnClickListener openCategories = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkBoxes();
                popChooseCategories.show();
            }
        };
        choose.setOnClickListener(openCategories);
        findViewById(R.id.add_category1).setOnClickListener(openCategories);
        findViewById(R.id.add_category2).setOnClickListener(openCategories);

        setCategories();

        LinearLayout lay = findViewById(R.id.layout_post);
        final Button cancel = new Button(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        int left = (int) getResources().getDimension(R.dimen._5sdp);
        int top = - (int) getResources().getDimension(R.dimen._10sdp);
        params.setMargins(left,top,left,-top*2);
        cancel.setLayoutParams(params);
        cancel.setMaxHeight( post_button.getHeight() );
        cancel.setBackgroundResource(R.drawable.custom_button_selector);
        cancel.setGravity(Gravity.CENTER);
        cancel.setTypeface(Typeface.DEFAULT_BOLD);
        cancel.setTextColor(Color.WHITE);
        cancel.setText("Delete Post");
        cancel.setId(lay.getChildCount() + 1);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Drawable alert = AppCompatResources.getDrawable(getApplicationContext(), android.R.drawable.ic_dialog_alert);
                final Drawable wrappedDrawable = DrawableCompat.wrap(alert);
                DrawableCompat.setTint(wrappedDrawable, Color.RED);
                new AlertDialog.Builder(edit_post.this)
                        .setTitle("Delete post")
                        .setMessage("Do you really want to delete this post?")
                        .setIcon(wrappedDrawable)
                        .setPositiveButton("Yes", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {
                                cancel.setEnabled(false);
                                //TODO qua mettere la delete request al backend
                                RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());

                                String url = "https://shielded-peak-80677.herokuapp.com/posts/"+ idPostEdit;
                                StringRequest dr = new StringRequest(Request.Method.DELETE, url,
                                        new Response.Listener<String>()
                                        {
                                            @Override
                                            public void onResponse(String response) {
                                                // response
                                                Toast.makeText(edit_post.this, "Post deleted succesfully.", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(edit_post.this, Homepage.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        },
                                        new Response.ErrorListener()
                                        {
                                            @Override
                                            public void onErrorResponse(VolleyError error) {
                                                // error.
                                                Toast.makeText(edit_post.this, "Post couldn't be deleted, try later.", Toast.LENGTH_LONG).show();
                                                cancel.setEnabled(true);

                                            }
                                        }
                                );
                                requestQueue.add(dr);
                                //queue.add(dr);
                                /*db.collection("posts").document(id).delete()
                                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) {
                                                Toast.makeText(edit_post.this, "Post deleted succesfully.", Toast.LENGTH_LONG).show();
                                                Intent i = new Intent(edit_post.this, Homepage.class);
                                                startActivity(i);
                                                finish();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(edit_post.this, "Post couldn't be deleted, try later.", Toast.LENGTH_LONG).show();
                                        cancel.setEnabled(true);
                                    }
                                });*/
                            }})
                        .setNegativeButton("No", null).show();

            }
        });
        lay.addView(cancel);

    }



    private Boolean displayGpsStatus() {
        ContentResolver contentResolver = getBaseContext()
                .getContentResolver();
        boolean gpsStatus = Settings.Secure
                .isLocationProviderEnabled(contentResolver,
                        LocationManager.GPS_PROVIDER);
        if (gpsStatus) {
            return true;

        } else {
            return false;
        }
    }

    @SuppressLint("MissingPermission")
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 99: {
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Criteria criteria = new Criteria();
                    criteria.setAccuracy(Criteria.ACCURACY_FINE);
                    locationMangaer = (LocationManager) getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
                    locationMangaer.requestSingleUpdate(criteria, locationListener, null);

                } else {

                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }

            // other 'switch' lines to check for other
            // permissions this app might request
        }

    }

    final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            GPSlocation = location;
            la = location.getLatitude();
            lo = location.getLongitude();

            Geocoder geocoder = new Geocoder(edit_post.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(la, lo, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextView user_country = (TextView) findViewById(R.id.countries_list);
            TextView city = (TextView) findViewById(R.id.cities_list);

            user_country.setText(addresses.get(0).getAdminArea()+ ", " + addresses.get(0).getCountryCode());
            city.setText(addresses.get(0).getLocality());
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            Log.d("Status Changed", String.valueOf(status));
        }

        @Override
        public void onProviderEnabled(String provider) {
            Log.d("Provider Enabled", provider);
        }

        @Override
        public void onProviderDisabled(String provider) {
            Log.d("Provider Disabled", provider);
        }
    };



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
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == -1){
                isTheImageUp = true;
                imageUri = result.getUri();
                this.post_image_view.setImageURI(imageUri);
            }
        }
    }

    @SuppressLint("ClickableViewAccessibility")
    private void iniPopup() {

        popChooseCategories = new Dialog(this);
        popChooseCategories.setContentView(R.layout.new_post_choose_categories);
        popChooseCategories.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.backgroundColor)));
        popChooseCategories.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        popChooseCategories.getWindow().getAttributes().gravity = Gravity.TOP;

        final Button ok_categories = popChooseCategories.findViewById(R.id.ok_categories);

        CompoundButton.OnCheckedChangeListener checker = new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(countChecked == maxChecked && isChecked){
                    buttonView.setChecked(false);
                    Toast.makeText(getBaseContext(),
                            "You can't select more than 3 categories!", Toast.LENGTH_SHORT).show();
                }else if(isChecked){
                    countChecked++;
                }else{
                    countChecked--;
                }
            }
        };
        sportCheck = popChooseCategories.findViewById(R.id.sport);
        sportCheck.setTextColor(getResources().getColor(R.color.textColor));
        sportCheck.setGravity(Gravity.CENTER);
        sportCheck.setOnCheckedChangeListener(checker);
        fashionCheck = popChooseCategories.findViewById(R.id.fashion);
        fashionCheck.setTextColor(getResources().getColor(R.color.textColor));
        fashionCheck.setGravity(Gravity.CENTER);
        fashionCheck.setOnCheckedChangeListener(checker);
        scienceCheck = popChooseCategories.findViewById(R.id.science);
        scienceCheck.setTextColor(getResources().getColor(R.color.textColor));
        scienceCheck.setGravity(Gravity.CENTER);
        scienceCheck.setOnCheckedChangeListener(checker);
        musicCheck = popChooseCategories.findViewById(R.id.music);
        musicCheck.setTextColor(getResources().getColor(R.color.textColor));
        musicCheck.setGravity(Gravity.CENTER);
        musicCheck.setOnCheckedChangeListener(checker);
        moviesCheck = popChooseCategories.findViewById(R.id.movies);
        moviesCheck.setTextColor(getResources().getColor(R.color.textColor));
        moviesCheck.setGravity(Gravity.CENTER);
        moviesCheck.setOnCheckedChangeListener(checker);
        foodCheck = popChooseCategories.findViewById(R.id.food);
        foodCheck.setTextColor(getResources().getColor(R.color.textColor));
        foodCheck.setGravity(Gravity.CENTER);
        foodCheck.setOnCheckedChangeListener(checker);
        natureCheck = popChooseCategories.findViewById(R.id.nature);
        natureCheck.setTextColor(getResources().getColor(R.color.textColor));
        natureCheck.setGravity(Gravity.CENTER);
        natureCheck.setOnCheckedChangeListener(checker);

        checkBoxes();

        ok_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(sportCheck.isChecked()) interests_selected.put((String) sportCheck.getText().toString().toLowerCase() , true);
                else interests_selected.put((String) sportCheck.getText().toString().toLowerCase(), false);
                if(fashionCheck.isChecked()) interests_selected.put((String) fashionCheck.getText().toString().toLowerCase(), true);
                else interests_selected.put((String) fashionCheck.getText().toString().toLowerCase(), false);
                if(scienceCheck.isChecked()) interests_selected.put("science", true);
                else interests_selected.put((String) "science", false);
                if(musicCheck.isChecked()) interests_selected.put((String) musicCheck.getText().toString().toLowerCase(), true);
                else interests_selected.put((String) musicCheck.getText().toString().toLowerCase(), false);
                if(moviesCheck.isChecked()) interests_selected.put((String) moviesCheck.getText().toString().toLowerCase(), true);
                else interests_selected.put((String) moviesCheck.getText().toString().toLowerCase(), false);
                if(foodCheck.isChecked()) interests_selected.put((String) foodCheck.getText().toString().toLowerCase(), true);
                else interests_selected.put((String) foodCheck.getText().toString().toLowerCase(), false);
                if(natureCheck.isChecked()) interests_selected.put((String) natureCheck.getText().toString().toLowerCase(), true);
                else interests_selected.put((String) natureCheck.getText().toString().toLowerCase(), false);

                popChooseCategories.cancel();
                setCategories();

            }
        });
    }

    private void checkBoxes(){
        if (interests_selected.containsKey("science") && (boolean)interests_selected.get("science")) scienceCheck.setChecked(true);
        else scienceCheck.setChecked(false);
        if (interests_selected.containsKey("nature") && (boolean)interests_selected.get("nature")) natureCheck.setChecked(true);
        else natureCheck.setChecked(false);
        if (interests_selected.containsKey("sport") && (boolean)interests_selected.get("sport")) sportCheck.setChecked(true);
        else sportCheck.setChecked(false);
        if (interests_selected.containsKey("fashion") && (boolean)interests_selected.get("fashion")) fashionCheck.setChecked(true);
        else fashionCheck.setChecked(false);
        if (interests_selected.containsKey("food") && (boolean)interests_selected.get("food")) foodCheck.setChecked(true);
        else foodCheck.setChecked(false);
        if (interests_selected.containsKey("movies") && (boolean)interests_selected.get("movies")) moviesCheck.setChecked(true);
        else moviesCheck.setChecked(false);
        if (interests_selected.containsKey("music") && (boolean)interests_selected.get("music")) musicCheck.setChecked(true);
        else musicCheck.setChecked(false);
    }

    private void setCategories(){
        categoriesCardLayout.removeAllViews(); // clear cards

        for (Map.Entry<String, Object> entry : interests_selected.entrySet()) {
            String k = entry.getKey();
            boolean value = (boolean) entry.getValue();
            if(value) createCategoryCard(k);
        }
    }

    private void createCategoryCard(final String category){
        final LinearLayout parent = new LinearLayout(this);
        LinearLayout.LayoutParams paramsParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsParent.setMargins(getResources().getDimensionPixelSize(R.dimen._5sdp) ,0, 0,0);
        parent.setLayoutParams(paramsParent);

        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setBackgroundResource(R.drawable.custom_button_selector);

        ImageView minus = new ImageView(this);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params.setMargins(0, 0, 0, 0);
        minus.setLayoutParams(params);
        minus.setImageResource(R.drawable.remove_category);
        minus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                categoriesCardLayout.removeView(parent);
                interests_selected.put(category, false);
            }
        });
        TextView categ = new TextView(this);
        int marginRight = getResources().getDimensionPixelSize(R.dimen._10sdp);
        int marginLeft = getResources().getDimensionPixelSize(R.dimen._4sdp);
        int marginTop = getResources().getDimensionPixelSize(R.dimen._4sdp);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        params1.setMargins(marginLeft, marginTop, marginRight, 0);
        categ.setLayoutParams(params1);
        categ.setText(category);

        parent.addView(minus);
        parent.addView(categ);

        categoriesCardLayout.addView(parent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(edit_post.this, postView.class);
        intent.putExtra("title", post.getTitle());
        intent.putExtra("id",id);
        intent.putExtra("desc", post.getPostdesc());
        intent.putExtra("storageref", post.getStorageref());
        intent.putExtra("user", post.getUser());
        intent.putExtra("country", post.getCountry());
        intent.putExtra("city", post.getCity());
        intent.putExtra("categories", post.getCategories());
        intent.putExtra("role", post.getRole());
        intent.putExtra("isPackage", post.getIsPackage());
        startActivity(intent);
        finish();
    }
}