package com.example.huc_project.homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.huc_project.R;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class CreateNewPostActivity extends AppCompatActivity {
    private static final int GET_FROM_GALLERY = 1;
    private static final String TAG = "taggy";
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    ImageButton imageView;
    Button button;
    private static final int PICK_IMAGE = 100;
    boolean isTheImageUp=false;
    Uri imageUri= Uri.parse("android.resource://com.example.project/"+R.drawable.error);
    FirebaseStorage storage = FirebaseStorage.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        db = FirebaseFirestore.getInstance();
        Button buttonCreateP=(Button)findViewById(R.id.postBtn);
        imageView = (ImageButton) findViewById(R.id.imageBtn);
        //button = (Button)findViewById(R.id.buttonLoadPicture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        //TODO aggiungere un modo per distinguere i post normali dai pacchetti
        // andrà a modificare la variabile --- isPackage ---

        final Boolean isPackage = true;


        buttonCreateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Map<String, Object> post = new HashMap<>();
                EditText text = (EditText) findViewById(R.id.textDesc);
                EditText texttitle = (EditText) findViewById(R.id.textTitle);
                String postDescription = text.getText().toString();
                String postTitle=texttitle.getText().toString();
                //imageView = (ImageView)findViewById(R.id.imageView);
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();
                //DAVIDE I THINK YOU HAVE TO USE CLOUD STORAGE INSTEAD OF FIRESTORE e in particolare firebaseui

                //Post postcomplete=new Post(postDescription, "le velineeee");

                Uri file = imageUri;
                StorageReference storageRef = storage.getReference();
                StorageReference riversRef;
                UploadTask uploadTask;
                String role="";
                CheckBox sponsor=(CheckBox) findViewById(R.id.sponsor);
                CheckBox sponsorship=(CheckBox) findViewById(R.id.sponsorship);
                if (sponsor.isChecked()) {
                    role="sponsor";
                }
                else if (sponsorship.isChecked()) {
                    role="sponsorship";
                }

                ArrayList<String> categoriesChosen = new ArrayList<String>();

                CheckBox science=(CheckBox) findViewById(R.id.checkBoxScience);
                CheckBox nature=(CheckBox) findViewById(R.id.checkBoxNature);
                CheckBox sport=(CheckBox) findViewById(R.id.checkBoxSport);
                CheckBox fashion=(CheckBox) findViewById(R.id.checkBoxFashion);
                CheckBox food=(CheckBox) findViewById(R.id.checkBoxFood);
                CheckBox movies=(CheckBox) findViewById(R.id.checkBoxMovies);
                CheckBox music=(CheckBox) findViewById(R.id.checkBoxMusic);

                if (science.isChecked()) {
                    categoriesChosen.add("science");

                }
                if (nature.isChecked()) {
                    categoriesChosen.add("nature");

                }
                if (sport.isChecked()) {
                    categoriesChosen.add("sport");

                }
                if (fashion.isChecked()) {
                    categoriesChosen.add("fashion");

                }
                if (food.isChecked()) {
                    categoriesChosen.add("food");

                }
                if (movies.isChecked()) {
                    categoriesChosen.add("movies");

                }
                if (music.isChecked()) {
                    categoriesChosen.add("music");

                }

                post.put("title", postTitle);
                post.put("postdesc", postDescription);
                post.put("user", current_user.getUid());
                post.put("isPackage", isPackage);
                post.put("categories", categoriesChosen);
                post.put("role", role);

                if(file != null){
                    if (isTheImageUp==false) {
                        String stringRef=file.getLastPathSegment()+"_"+ System.currentTimeMillis();
                        riversRef = storageRef.child("images/" + stringRef);
                        Log.e("PROVOLA",  stringRef);
                        post.put("storageref", stringRef);
                        Uri imageUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.drawable.add_img);
                        uploadTask = riversRef.putFile(imageUri);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                            }
                        });
                    }
                    else {
                        riversRef = storageRef.child("images/" + file.getLastPathSegment());
                        Log.e("PROVOLA", "HAI MESSO LA FOTO, BRAVO");
                        post.put("storageref", file.getLastPathSegment());
                        uploadTask = riversRef.putFile(file);

                        // Register observers to listen for when the download is done or if it fails
                        uploadTask.addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception exception) {
                                // Handle unsuccessful uploads
                            }
                        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                            @Override
                            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                                // ...
                            }
                        });

                    }
                }

                // Add a new document with a generated ID
                db.collection("posts")
                        .add(post)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });

                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                startActivity(intent);
                finish();
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
        isTheImageUp=true;
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
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
            }
        }
    }

    @Override
    public void onBackPressed(){
        Intent i = new Intent(this, Homepage.class);
        startActivity(i);
        finish();
    }
}