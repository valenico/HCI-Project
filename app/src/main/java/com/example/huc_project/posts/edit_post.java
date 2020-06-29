package com.example.huc_project.posts;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.huc_project.CustomCheckbox;
import com.example.huc_project.R;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.homepage.PostCreatedSuccessfully;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class edit_post extends AppCompatActivity {

    Post post;
    ImageView post_image_view;
    final int PICK_IMAGE= 100;
    Uri imageUri;
    TextView title_view;
    TextView desc_view;
    String id;
    Dialog popChooseCategories ;
    TextView categories_selected;
    private HashMap<String,Object> interests_selected = new HashMap<>();
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };

    LinearLayout categoriesCardLayout;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);

        Intent intent = getIntent();
        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false), intent.getStringArrayListExtra("categories"),
                intent.getStringExtra("role"), intent.getStringExtra("country"), intent.getStringExtra("city"));
        id = intent.getStringExtra("id");
        storageref = intent.getStringExtra("storageref");
        Button post_button = findViewById(R.id.postBtn);
        post_button.setText("Save");
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, Object> post = new HashMap<>();
                EditText text = (EditText) findViewById(R.id.textDesc);
                EditText texttitle = (EditText) findViewById(R.id.textTitle);
                String postDescription = text.getText().toString();
                String postTitle = texttitle.getText().toString();
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


                ArrayList<String> categoriesChosen = new ArrayList<String>();


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
                            db.collection("posts").document(id).set(post);
                        }
                    });
                } else {
                    post.put("storageref", storageref);
                    db.collection("posts").document(id).set(post);
                }
                Intent intent = new Intent(getApplicationContext(), PostCreatedSuccessfully.class);
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

        ArrayList<String> categories = post.getCategories();
        for(int u = 0 ; u < categories.size(); u++){
            interests_selected.put(categories.get(u),true);
            Log.d("CAT", categories.get(u));
        }
        iniPopup();
        categories_selected = findViewById(R.id.categories_selected);
        TextView choose = findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popChooseCategories.show();
            }
        });
        setCategories();

    }

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


        popChooseCategories.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //doNothing
                }
                return true;
            }});

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
        sportCheck.setOnCheckedChangeListener(checker);
        fashionCheck = popChooseCategories.findViewById(R.id.fashion);
        fashionCheck.setOnCheckedChangeListener(checker);
        scienceCheck = popChooseCategories.findViewById(R.id.science);
        scienceCheck.setOnCheckedChangeListener(checker);
        musicCheck = popChooseCategories.findViewById(R.id.music);
        musicCheck.setOnCheckedChangeListener(checker);
        moviesCheck = popChooseCategories.findViewById(R.id.movies);
        moviesCheck.setOnCheckedChangeListener(checker);
        foodCheck = popChooseCategories.findViewById(R.id.food);
        foodCheck.setOnCheckedChangeListener(checker);
        natureCheck = popChooseCategories.findViewById(R.id.nature);
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
        String cat= new String();
        for (Map.Entry<String, Object> entry : interests_selected.entrySet()) {
            String k = entry.getKey();
            boolean value = (boolean) entry.getValue();
            if(value){

                cat+="#"+k;
                cat+=" ";
            }
        }

        if(cat.length()>0){
            cat =  cat.substring(0, cat.length() - 1);
            categories_selected.setText(cat);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) categories_selected.getLayoutParams();
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            categories_selected.setLayoutParams(params);
        }else {
            int margin = getResources().getDimensionPixelSize(R.dimen._1sdp);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) categories_selected.getLayoutParams();
            params.height = margin;
            categories_selected.setLayoutParams(params);
            categories_selected.setText("");
        }
        //Toast.makeText(CreateNewPostActivity.this, "Chosen " + cat, Toast.LENGTH_SHORT).show();
    }

    private void createCategoryCard(final String category){
        final LinearLayout parent = new LinearLayout(this);
        LinearLayout.LayoutParams paramsParent = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        paramsParent.setMargins(getResources().getDimensionPixelSize(R.dimen._5sdp) ,0, 0,0);
        parent.setLayoutParams(paramsParent);

        parent.setOrientation(LinearLayout.HORIZONTAL);
        parent.setBackgroundResource(R.drawable.custom_button);

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
        finish();
    }
}