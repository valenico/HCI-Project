package com.example.huc_project.homepage;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import com.example.huc_project.R;

import androidx.annotation.NonNull;

import android.content.Intent;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.example.huc_project.ui.login.CircularItemAdapter;
import com.example.huc_project.ui.login.PaintText;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.jh.circularlist.CircularListView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;


public class CreateNewPostActivity extends AppCompatActivity {
    private static final String TAG = "taggy";
    private FirebaseDatabase mdatabase = FirebaseDatabase.getInstance();
    private DatabaseReference mdatabaseReference = mdatabase.getReference();
    private FirebaseFirestore db;
    ImageButton imageView;
    private static final int PICK_IMAGE = 100;
    private HashMap<String,Object>  interests_selected = new HashMap<>();
    boolean isTheImageUp=false;
    Uri imageUri= Uri.parse("android.resource://com.example.project/"+R.drawable.error);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Dialog popChooseCategories ;
    TextView show_cat;
    TextView choose;
    TextView categories_selected;
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        db = FirebaseFirestore.getInstance();
        Button buttonCreateP=(Button)findViewById(R.id.postBtn);
        iniPopup();
        imageView = (ImageButton) findViewById(R.id.imageBtn);
        //button = (Button)findViewById(R.id.buttonLoadPicture);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });

            show_cat = findViewById(R.id.categories_selected);

        choose = findViewById(R.id.choose);
        categories_selected = findViewById(R.id.categories_selected);

            choose.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    popChooseCategories.show();
                }
            });

        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();


        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        final AutoCompleteTextView countryView = (AutoCompleteTextView)
                findViewById(R.id.countries_list);
        countryView.setAdapter(adapter);

        final AutoCompleteTextView cityView = (AutoCompleteTextView)
                findViewById(R.id.cities_list);



        buttonCreateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Map<String, Object> post = new HashMap<>();
                EditText text = (EditText) findViewById(R.id.textDesc);
                EditText texttitle = (EditText) findViewById(R.id.textTitle);
                String postDescription = text.getText().toString();
                String postTitle=texttitle.getText().toString();
                String country=countryView.getText().toString();
                String city = cityView.getText().toString();
                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                Boolean isPackage = true;

                StorageReference storageRef = storage.getReference();
                StorageReference riversRef;
                UploadTask uploadTask;
                String role="";
                CheckBox sponsor=(CheckBox) findViewById(R.id.sponsor);
                CheckBox sponsorship=(CheckBox) findViewById(R.id.sponsorship);
                CheckBox checkispackage=(CheckBox) findViewById(R.id.checkpackage);
                if (sponsor.isChecked()) {
                    role="sponsor";
                }
                else if (sponsorship.isChecked()) {
                    role="sponsorship";
                }
                if (checkispackage.isChecked()) {
                    isPackage = true;
                }
                else {
                    isPackage = false;
                }


                ArrayList<String> categoriesChosen = new ArrayList<String>();


                if (interests_selected.containsKey("Science & IT")) categoriesChosen.add("science");
                if (interests_selected.containsKey("Nature")) categoriesChosen.add("nature");
                if (interests_selected.containsKey("Sport")) categoriesChosen.add("sport");
                if (interests_selected.containsKey("Fashion")) categoriesChosen.add("fashion");
                if (interests_selected.containsKey("Food")) categoriesChosen.add("food");
                if (interests_selected.containsKey("Movies")) categoriesChosen.add("movies");
                if (interests_selected.containsKey("Music")) categoriesChosen.add("music");

                post.put("title", postTitle);
                post.put("postdesc", postDescription);
                post.put("user", current_user.getUid());
                post.put("isPackage", isPackage);
                post.put("categories", categoriesChosen);
                post.put("role", role);
                post.put("city", city);
                post.put("country", country);


                // Handle unsuccessful uploads
                // taskSnapshot.getMetadata() contains file metadata such as size, content-type, etc.
                // ...
                if (isTheImageUp==false) {
                    String stringRef = imageUri.getLastPathSegment()+"_"+ System.currentTimeMillis();
                    riversRef = storageRef.child("images/" + stringRef);
                    Log.e("PROVOLA",  stringRef);
                    post.put("storageref", stringRef);
                    Uri imageUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.drawable.add_img);
                    uploadTask = riversRef.putFile(imageUri);
                }
                else {
                    riversRef = storageRef.child("images/" + imageUri.getLastPathSegment());
                    Log.e("PROVOLA", "HAI MESSO LA FOTO, BRAVO");
                    post.put("storageref", imageUri.getLastPathSegment());
                    uploadTask = riversRef.putFile(imageUri);
                }

                uploadTask.addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception exception) {
                        // Handle unsuccessful uploads
                    }
                }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
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
                                });                    }
                });
                Intent intent = new Intent(getApplicationContext(), PostCreatedSuccessfully.class);
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
                isTheImageUp=true;
                imageUri = result.getUri();
                imageView.setImageURI(imageUri);
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

        final Context c = this.getBaseContext();
        final int[] children = new int[]{ 6,6,6,6,6,6,6 };
        final ArrayList<Integer> interests = new ArrayList<>(Arrays.asList(R.drawable.ic_sport , R.drawable.ic_fashion, R.drawable.ic_food, R.drawable.ic_movie, R.drawable.ic_music, R.drawable.ic_technology, R.drawable.ic_nature));
        final CircularListView circularListView = popChooseCategories.findViewById(R.id.circle_interests_new_post);
        final Button ok_categories = popChooseCategories.findViewById(R.id.ok_categories);
        circularListView.setRadius(80);
        CircularItemAdapter adapter = new CircularItemAdapter(getLayoutInflater(), interests);
        circularListView.setAdapter(adapter);

        circularListView.setOnTouchListener(new CircularListView.OnTouchListener() {
            private float init_x = 0;
            private float init_y = 0;
            private float cur_x = 0;
            private float cur_y = 0;
            private float move_x = 0;
            private float move_y = 0;
            private boolean can_rotate = true;
            private boolean isCircularMoving = false; // ensure that item click only triggered when it's not moving
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int max = getMaxValue(children);
                if(max == 6){
                    can_rotate = true;
                }
                float minClickDistance = 30.0f;
                float minMoveDistance = 30.0f;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cur_x = event.getX();
                        cur_y = event.getY();
                        init_x = event.getX();
                        init_y = event.getY();

                    case MotionEvent.ACTION_MOVE:
                        float pre_x = cur_x;
                        float pre_y = cur_y;
                        cur_x = event.getX();
                        cur_y = event.getY();

                        float diff_x = cur_x - pre_x;
                        float diff_y = cur_y - pre_y;
                        move_x = init_x - cur_x;
                        move_y = init_y - cur_y;
                        float moveDistance = (float) Math.sqrt(move_x * move_x + move_y * move_y);


                        if (cur_y >= ((CircularListView) v).layoutCenter_y) diff_x = -diff_x;
                        if (cur_x <= ((CircularListView) v).layoutCenter_x) diff_y = -diff_y;

                        // should rotate the layout
                        if (moveDistance > minMoveDistance && can_rotate) {
                            isCircularMoving = true;
                            // default is 2000, larger > faster
                            float mMovingSpeed = 2000.0f;
                            CircularListView.MoveAccumulator += (diff_x + diff_y) / mMovingSpeed;

                            // calculate new position around circle
                            for (int i = 0; i < ((CircularListView) v).itemViewList.size(); i++) {
                                final int idx = i;
                                final View itemView = ((CircularListView) v).itemViewList.get(i);
                                itemView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                                                itemView.getLayoutParams();
                                        params.setMargins(
                                                (int) (((CircularListView) v).layoutCenter_x - (((CircularListView) v).itemWith / 2) +
                                                        (((CircularListView) v).radius * Math.cos(idx * ((CircularListView) v).getIntervalAngle() +
                                                                CircularListView.MoveAccumulator * Math.PI * 2))),
                                                (int) (((CircularListView) v).layoutCenter_y - (((CircularListView) v).itemHeight / 2) +
                                                        (((CircularListView) v).radius * Math.sin(idx * ((CircularListView) v).getIntervalAngle() +
                                                                CircularListView.MoveAccumulator * Math.PI * 2))),
                                                0,
                                                0);
                                        itemView.setLayoutParams(params);
                                        itemView.requestLayout();
                                    }
                                });
                            }
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        // it is an click action if move distance < min distance
                        moveDistance = (float) Math.sqrt(move_x * move_x + move_y * move_y);
                        if (moveDistance < minClickDistance && !isCircularMoving) {
                            for (int i = 0; i < ((CircularListView) v).itemViewList.size(); i++) {
                                View view = ((CircularListView) v).itemViewList.get(i);
                                if (isTouchInsideView(cur_x, cur_y, view)) {
                                    can_rotate = false;
                                    float curr_size = view.getScaleX();
                                    max = getMaxValue(children);
                                    if(curr_size == (float) 1) {
                                        view.setScaleX((float) 1.5);
                                        view.setScaleY((float) 1.5);
                                        circularListView.addView(new PaintText( c , i,
                                                view.getLeft()-60, view.getTop()-60,view.getRight()+60, view.getBottom()+60,
                                                -145,135) );
                                        children[i] = max + 1;
                                        interests_selected.put(Text[i], true);
                                    } else {
                                        view.setScaleX((float) 1);
                                        view.setScaleY((float) 1);
                                        circularListView.removeViewAt(children[i]);
                                        for (int elem = 0; elem < children.length; elem++) {
                                            if (children[elem] > children[i]) {
                                                children[elem] -= 1;
                                            }
                                        }
                                        children[i] = 6;
                                        interests_selected.remove(Text[i]);
                                        interests_selected.put(Text[i], false);
                                    }
                                    break;
                                }
                            }
                        }
                        isCircularMoving = false; // reset moving state when event ACTION_UP
                        return true;
                }
                return false;
            }

            private boolean isTouchInsideView(float x, float y, View view){
                float left = view.getX();
                float top  = view.getY();
                float wid = view.getWidth();
                float h = view.getHeight();
                return (x > left && x < left + wid && y > top && y < top+h);
            }

        });

        ok_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toast.makeText(CreateNewPostActivity.this, "Chosen " + interests_selected, Toast.LENGTH_SHORT).show();
                //popChooseCategories.dismiss();
                popChooseCategories.cancel();
                setCategories();

            }
        });
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
            Log.d("TAAC", String.valueOf(interests_selected.containsKey("Fashion")));
            Log.d("TAAC", String.valueOf(interests_selected));
        }else {
            int margin = getResources().getDimensionPixelSize(R.dimen._1sdp);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) categories_selected.getLayoutParams();
            params.height = margin;
            categories_selected.setLayoutParams(params);
            categories_selected.setText("");
        }
        //Toast.makeText(CreateNewPostActivity.this, "Chosen " + cat, Toast.LENGTH_SHORT).show();
    }

    public static int getMaxValue(int[] numbers){
        int maxValue = numbers[0];
        for(int i=1;i < numbers.length;i++){
            if(numbers[i] > maxValue){
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }

    @Override
    public void onBackPressed() {
        Intent i = new Intent(this, Homepage.class);
        startActivity(i);
        finish();

    }

    private static final String[] COUNTRIES = new String[] {
            "Afghanistan", "Albania", "Algeria", "American Samoa", "Andorra", "Angola", "Anguilla",

            "Antarctica", "Antigua and Barbuda", "Argentina", "Armenia", "Aruba", "Australia", "Austria",

            "Azerbaijan", "Bahamas", "Bahrain", "Bangladesh", "Barbados", "Belarus", "Belgium",

            "Belize", "Benin", "Bermuda", "Bhutan", "Bolivia", "Bosnia and Herzegovina", "Botswana",

            "Brazil", "British Indian Ocean Territory", "British Virgin Islands", "Brunei", "Bulgaria",

            "Burkina Faso", "Burma (Myanmar)", "Burundi", "Cambodia", "Cameroon", "Canada", "Cape Verde",

            "Cayman Islands", "Central African Republic", "Chad", "Chile", "China", "Christmas Island",

            "Cocos (Keeling) Islands", "Colombia", "Comoros", "Cook Islands", "Costa Rica",

            "Croatia", "Cuba", "Cyprus", "Czech Republic", "Democratic Republic of the Congo",

            "Denmark", "Djibouti", "Dominica", "Dominican Republic",

            "Ecuador", "Egypt", "El Salvador", "Equatorial Guinea", "Eritrea", "Estonia",

            "Ethiopia", "Falkland Islands", "Faroe Islands", "Fiji", "Finland", "France", "French Polynesia",

            "Gabon", "Gambia", "Gaza Strip", "Georgia", "Germany", "Ghana", "Gibraltar", "Greece",

            "Greenland", "Grenada", "Guam", "Guatemala", "Guinea", "Guinea-Bissau", "Guyana",

            "Haiti", "Holy See (Vatican City)", "Honduras", "Hong Kong", "Hungary", "Iceland", "India",

            "Indonesia", "Iran", "Iraq", "Ireland", "Isle of Man", "Israel", "Italy", "Ivory Coast", "Jamaica",

            "Japan", "Jersey", "Jordan", "Kazakhstan", "Kenya", "Kiribati", "Kosovo", "Kuwait",

            "Kyrgyzstan", "Laos", "Latvia", "Lebanon", "Lesotho", "Liberia", "Libya", "Liechtenstein",

            "Lithuania", "Luxembourg", "Macau", "Macedonia", "Madagascar", "Malawi", "Malaysia",

            "Maldives", "Mali", "Malta", "Marshall Islands", "Mauritania", "Mauritius", "Mayotte", "Mexico",

            "Micronesia", "Moldova", "Monaco", "Mongolia", "Montenegro", "Montserrat", "Morocco",

            "Mozambique", "Namibia", "Nauru", "Nepal", "Netherlands", "Netherlands Antilles", "New Caledonia",

            "New Zealand", "Nicaragua", "Niger", "Nigeria", "Niue", "Norfolk Island", "North Korea",

            "Northern Mariana Islands", "Norway", "Oman", "Pakistan", "Palau", "Panama",

            "Papua New Guinea", "Paraguay", "Peru", "Philippines", "Pitcairn Islands", "Poland",

            "Portugal", "Puerto Rico", "Qatar", "Republic of the Congo", "Romania", "Russia", "Rwanda",

            "Saint Barthelemy", "Saint Helena", "Saint Kitts and Nevis", "Saint Lucia", "Saint Martin",

            "Saint Pierre and Miquelon", "Saint Vincent and the Grenadines", "Samoa", "San Marino",

            "Sao Tome and Principe", "Saudi Arabia", "Senegal", "Serbia", "Seychelles", "Sierra Leone",

            "Singapore", "Slovakia", "Slovenia", "Solomon Islands", "Somalia", "South Africa", "South Korea",

            "Spain", "Sri Lanka", "Sudan", "Suriname", "Swaziland", "Sweden", "Switzerland",

            "Syria", "Taiwan", "Tajikistan", "Tanzania", "Thailand", "Timor-Leste", "Togo", "Tokelau",

            "Tonga", "Trinidad and Tobago", "Tunisia", "Turkey", "Turkmenistan", "Turks and Caicos Islands",

            "Tuvalu", "Uganda", "Ukraine", "United Arab Emirates", "United Kingdom", "United States", "Uruguay", "US Virgin Islands", "Uzbekistan", "Vanuatu", "Venezuela", "Vietnam",

            "Wallis and Futuna", "West Bank", "Yemen", "Zambia", "Zimbabwe"
    };

}