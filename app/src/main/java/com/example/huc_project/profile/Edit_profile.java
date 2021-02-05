package com.example.huc_project.profile;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.drawable.DrawableCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.huc_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;


import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Edit_profile extends AppCompatActivity {
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();
    private FirebaseAuth mAuth;
    private Uri profile_pic_uri;
    final int PICK_IMAGE_GALLERY = 100;
    String current_user;

    private boolean flag = false;
    private LocationManager locationMangaer = null;
    Location GPSlocation = null;
    Double lo = 0.0;
    Double la = 0.0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();


        locationMangaer = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        final Boolean guest_user;
        current_user = Profile_main_page.getCurrent_user();
        //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        if (current_user != null) guest_user = false;
        else guest_user = true;

        final DocumentReference docRef = db.collection("UTENTI").document(current_user);



        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        final String country = (String) document.get("Country");
                        String city = (String) document.get("City");
                        final String mail = (String) document.get("Email");
                        String description = (String) document.get("Description");
                        final Boolean hidden_mail = (Boolean) document.get("Hidemail");


                        final ImageButton getpositionButton = (ImageButton) findViewById(R.id.getPosition);
                        Drawable like = AppCompatResources.getDrawable(getApplicationContext(), R.drawable.mylocation);

                        final Drawable wrappedDrawable = DrawableCompat.wrap(like);
                        DrawableCompat.setTint(wrappedDrawable, getResources().getColor(R.color.color_button1));
                        getpositionButton.setImageDrawable(wrappedDrawable);
                        getpositionButton.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                if(displayGpsStatus()) {
                                    if (ActivityCompat.checkSelfPermission(Edit_profile.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                                            ActivityCompat.checkSelfPermission(Edit_profile.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {


                                        ActivityCompat.requestPermissions(Edit_profile.this,
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

                        final EditText user_name = findViewById(R.id.name);

                        final AutoCompleteTextView user_country = findViewById(R.id.country);
                        String[] countries_array = getResources().getStringArray(R.array.countries_array);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Edit_profile.this, android.R.layout.simple_list_item_1, countries_array);
                        user_country.setAdapter(adapter);

                        final AutoCompleteTextView user_city = findViewById(R.id.city);
                        String[] cities_array = getResources().getStringArray(R.array.cities_array);
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Edit_profile.this, android.R.layout.simple_list_item_1, cities_array);
                        user_city.setAdapter(adapter2);

                        final TextView user_mail = findViewById(R.id.mail);
                        ImageView profile_img = findViewById(R.id.profImage);
                        final CheckBox h_mail = findViewById(R.id.hidemail);
                        final EditText user_description = findViewById(R.id.description);

                        RequestOptions options = new RequestOptions().error(R.drawable.user); //if load gets error bc image does not exist, load default image
                        StorageReference ref = storage.getReference().child("users/" + current_user);
                        Glide.with(Edit_profile.this).load(ref).apply(options).skipMemoryCache(true).diskCacheStrategy(DiskCacheStrategy.NONE).into(profile_img);

                        user_name.setText(name);
                        user_description.setText(description);
                        h_mail.setChecked(hidden_mail);

                        if (hidden_mail) user_mail.setText("Hidden");
                        else user_mail.setText(mail);

                        h_mail.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                            @Override
                            public void onCheckedChanged(CompoundButton buttonView,boolean isChecked) {
                                if (h_mail.isChecked()) user_mail.setText("Hidden");
                                else user_mail.setText(mail);
                            }
                        });

                        if (country != null) user_country.setText(country);

                        if (city != null) user_city.setText(city);

                        Button done_button = findViewById(R.id.done_button);
                        done_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HashMap<String, String> upd = new HashMap<>();
                                HashMap<String, Boolean> upd2 = new HashMap<>();
                                upd.put("Name", user_name.getText().toString());
                                upd.put("Country", user_country.getText().toString());
                                upd.put("City", user_city.getText().toString());
                                upd.put("Description", user_description.getText().toString());
                                upd2.put("Hidemail", h_mail.isChecked());
                                db.collection("UTENTI").document(current_user).set(upd, SetOptions.merge());
                                db.collection("UTENTI").document(current_user).set(upd2, SetOptions.merge());
                                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                                intent.putExtra("user", current_user);
                                startActivity(intent);
                                finish();
                            }
                        });

                        Button reset_button = findViewById(R.id.reset_button);
                        reset_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user_name.setText((String) document.get("Name"));
                                if (document.get("Country") != null) user_country.setText((String) document.get("Country"));
                                if (document.get("City") != null) user_city.setText((String) document.get("City"));
                                user_description.setText((String) document.get("Description"));
                                h_mail.setChecked(hidden_mail);
                                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                                intent.putExtra("user", current_user);
                                startActivity(intent);
                                finish();
                            }
                        });

                        ImageButton editImage = findViewById(R.id.editImageButton);
                        editImage.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                SelectImage();
                            }
                        });
                    }
                }
            }
        });
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

            Geocoder geocoder = new Geocoder(Edit_profile.this, Locale.getDefault());
            List<Address> addresses = null;
            try {
                addresses = geocoder.getFromLocation(la, lo, 1);
            } catch (IOException e) {
                e.printStackTrace();
            }
            TextView user_country = (TextView) findViewById(R.id.country);
            TextView city = (TextView) findViewById(R.id.city);
            
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




    private  void SelectImage(){
        CropImage.startPickImageActivity(this);
    }

    private void uploadImage(Uri filePath){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference r2 = storage.getReference().child("users/"+ Profile_main_page.getCurrent_user());
        r2.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Edit_profile.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Edit_profile.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(UploadTask.TaskSnapshot taskSnapshot) {
                        double progress = (100.0*taskSnapshot.getBytesTransferred()/taskSnapshot
                                .getTotalByteCount());
                        progressDialog.setMessage("Uploaded "+(int)progress+"%");
                    }
                });
    }

    private void startCropImageActivity(Uri imageUri) {
        Intent intent = CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .getIntent(this.getBaseContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            profile_pic_uri = CropImage.getPickImageResultUri(this, data);
            startCropImageActivity(profile_pic_uri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && profile_pic_uri != null){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            Uri filePath = ar.getUri();
            final ImageView add_pic = findViewById(R.id.profImage);
            try {
                uploadImage(filePath);
                Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                add_pic.setImageBitmap(bitmap);
                profile_pic_uri = null;
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK && profile_pic_uri == null) {
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            final Uri filePath = ar.getUri();
            db.collection("UTENTI").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            ArrayList<String> list = new ArrayList<>();
                            if (document.get("images") != null)
                                list = (ArrayList<String>) document.get("images");
                            list.add(filePath.getLastPathSegment());
                            final HashMap<String, ArrayList<String>> imgs = new HashMap<>();
                            imgs.put("images", list);

                            StorageReference riversRef = storage.getReference().child("images/" + filePath.getLastPathSegment());
                            UploadTask uploadTask = riversRef.putFile(filePath);
                            uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                    db.collection("UTENTI").document(mAuth.getUid()).set(imgs, SetOptions.merge());
                                    Toast t = Toast.makeText(Edit_profile.this,"Your picture has been uploaded! Go see it in your gallery!", Toast.LENGTH_LONG);
                                    t.setGravity(Gravity.CENTER_HORIZONTAL,0,0);
                                    t.show();
                                }
                            });

                        }
                    }
                }
            });
        }
        if(requestCode == PICK_IMAGE_GALLERY && resultCode == Activity.RESULT_OK){
            Uri imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setRequestedSize(500,500, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                    .start(this);
        }
    }

    public void upload_images(View view) {
        Intent gallery = new Intent();//(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE_GALLERY);
    }

    public void manage_socials(View view) {
        Intent intent = new Intent(this, RemoveSocialAccount.class);
        startActivity(intent);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
        intent.putExtra("user",current_user);
        startActivity(intent);
        finish();
    }
}

