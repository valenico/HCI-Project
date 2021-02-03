package com.example.huc_project.homepage;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

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
import com.example.huc_project.CustomCheckbox;
import com.example.huc_project.R;

import androidx.annotation.NonNull;
import android.content.Intent;
import android.text.Editable;
import android.text.TextWatcher;
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
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.huc_project.Signup;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.AuthResult;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
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
    Uri imageUri= Uri.parse("android.resource://com.example.project/"+R.drawable.no_imag);
    FirebaseStorage storage = FirebaseStorage.getInstance();
    Dialog popChooseCategories ;
    TextView choose;
    TextView categories_selected;
    LinearLayout categoriesCardLayout;
    final int maxChecked = 1;
    int countChecked = 0;

    CustomCheckbox sportCheck;
    CustomCheckbox fashionCheck;
    CustomCheckbox scienceCheck;
    CustomCheckbox musicCheck;
    CustomCheckbox moviesCheck;
    CustomCheckbox foodCheck;
    CustomCheckbox natureCheck;
    TextView textDesc;
    TextView textTitle;


        @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);
        db = FirebaseFirestore.getInstance();
        final Button buttonCreateP=(Button)findViewById(R.id.postBtn);
        buttonCreateP.setActivated(false);
        buttonCreateP.setBackgroundResource(R.drawable.custom_button_disabled);
        buttonCreateP.setTextColor(getResources().getColor(R.color.colorAccent));
        iniPopup();
        imageView = (ImageButton) findViewById(R.id.imageBtn);
        FloatingActionButton fab_add = findViewById(R.id.fab_add);
        View.OnClickListener add_image = new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        };
        imageView.setOnClickListener(add_image);
        fab_add.setOnClickListener(add_image);

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

        final CheckBox sponsor=(CheckBox) findViewById(R.id.sponsor);
        final CheckBox sponsorship=(CheckBox) findViewById(R.id.sponsorship);

        CompoundButton.OnCheckedChangeListener sponsorChecker = new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if(buttonView.equals(sponsor) && isChecked){
                        if(sponsorship.isChecked()){
                            sponsorship.setChecked(false);
                        }
                        sponsor.setChecked(true);
                    }
                    else if(isChecked){
                        if(sponsor.isChecked()){
                            sponsor.setChecked(false);
                        }
                        sponsorship.setChecked(true);
                    }
                }
            };

            sponsor.setOnCheckedChangeListener(sponsorChecker);
            sponsorship.setOnCheckedChangeListener(sponsorChecker);


        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                android.R.layout.simple_dropdown_item_1line, COUNTRIES);
        final AutoCompleteTextView countryView = (AutoCompleteTextView)
                findViewById(R.id.countries_list);
        countryView.setAdapter(adapter);

        final AutoCompleteTextView cityView = (AutoCompleteTextView)
                findViewById(R.id.cities_list);

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
                    buttonCreateP.setActivated(false);
                    buttonCreateP.setBackgroundResource(R.drawable.custom_button_disabled);
                    buttonCreateP.setTextColor(getResources().getColor(R.color.colorAccent));
                } else if( ((EditText) findViewById(R.id.textTitle)).getText().toString().length() > 0) {
                    buttonCreateP.setActivated(true);
                    buttonCreateP.setBackgroundResource(R.drawable.custom_button);
                    buttonCreateP.setTextColor(Color.WHITE);
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
                        buttonCreateP.setActivated(false);
                        buttonCreateP.setBackgroundResource(R.drawable.custom_button_disabled);
                        buttonCreateP.setTextColor(getResources().getColor(R.color.colorAccent));
                    } else if( ((EditText) findViewById(R.id.textDesc)).getText().toString().length() > 0) {
                        buttonCreateP.setActivated(true);
                        buttonCreateP.setBackgroundResource(R.drawable.custom_button);
                        buttonCreateP.setTextColor(Color.WHITE);
                    }
                }
            });

        buttonCreateP.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!buttonCreateP.isActivated()){
                    if(textTitle.getText().toString().trim().length() <= 0) textTitle.setError("Title cannot be empty");
                    if(textDesc.getText().toString().trim().length() <= 0) textDesc.setError("Description cannot be empty.");
                    //Toast.makeText(CreateNewPostActivity.this, "Title and description are mandatory!",Toast.LENGTH_LONG).show();
                    return;
                }
                new AlertDialog.Builder(CreateNewPostActivity.this)
                        .setTitle("Confirm creation")
                        .setMessage("Do you want to create a new post?")
                        .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {

                            public void onClick(DialogInterface dialog, int whichButton) {



                                final Map<String, Object> post = new HashMap<>();
                                EditText text = (EditText) findViewById(R.id.textDesc);
                                EditText texttitle = (EditText) findViewById(R.id.textTitle);
                                String postDescription = text.getText().toString();
                                String postTitle=texttitle.getText().toString();
                                String country=countryView.getText().toString();
                                String city = cityView.getText().toString();
                                Bitmap bitmap = ((BitmapDrawable)imageView.getDrawable()).getBitmap();

                                if(postTitle.trim().equals("") || postDescription.trim().equals("")){
                                    Toast.makeText(CreateNewPostActivity.this, "Title and description are mandatory!",Toast.LENGTH_LONG).show();
                                    return;
                                }

                                Boolean isPackage = true;

                                StorageReference storageRef = storage.getReference();
                                StorageReference riversRef;
                                UploadTask uploadTask;
                                String role="";
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


                                if (interests_selected.containsKey("Science & IT") && (boolean)interests_selected.get("Science & IT") ) categoriesChosen.add("science");
                                if (interests_selected.containsKey("Nature") && (boolean)interests_selected.get("Nature")) categoriesChosen.add("nature");
                                if (interests_selected.containsKey("Sport") && (boolean)interests_selected.get("Sport")) categoriesChosen.add("sport");
                                if (interests_selected.containsKey("Fashion") && (boolean)interests_selected.get("Fashion")) categoriesChosen.add("fashion");
                                if (interests_selected.containsKey("Food") && (boolean)interests_selected.get("Food")) categoriesChosen.add("food");
                                if (interests_selected.containsKey("Movies") && (boolean)interests_selected.get("Movies")) categoriesChosen.add("movies");
                                if (interests_selected.containsKey("Music") && (boolean)interests_selected.get("Music")) categoriesChosen.add("music");

                                post.put("title", postTitle);
                                post.put("postdesc", postDescription);
                                post.put("user", current_user.getUid());
                                post.put("isPackage", isPackage);
                                post.put("categories", categoriesChosen);
                                post.put("role", role);
                                post.put("city", city);
                                post.put("country", country);

                                if (isTheImageUp==false) {
                                    String stringRef = imageUri.getLastPathSegment()+"_"+ System.currentTimeMillis();
                                    riversRef = storageRef.child("images/" + stringRef);
                                    Log.e("PROVOLA",  stringRef);
                                    post.put("storageref", stringRef);
                                    Uri imageUri = Uri.parse("android.resource://" + R.class.getPackage().getName() + "/" + R.drawable.no_imag);
                                    uploadTask = riversRef.putFile(imageUri);
                                }
                                else {
                                    riversRef = storageRef.child("images/" + imageUri.getLastPathSegment());
                                    Log.e("PROVOLA", "HAI MESSO LA FOTO, BRAVO");
                                    post.put("storageref", imageUri.getLastPathSegment());
                                    uploadTask = riversRef.putFile(imageUri);
                                }


                                //QUI BISOGNA SOSTITUIRE CON UNA REQUEST AL BACKEND
                                //TODO DA PROVARE!
                                try {
                                    RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
                                    String URL = "http://10.0.2.2:8080/posts";
                                    JSONObject jsonBody = new JSONObject();
                                    jsonBody.put("id", "id_"+System.currentTimeMillis());
                                    jsonBody.put("title", post.get("title"));
                                    jsonBody.put("storageref", post.get("storageref"));
                                    jsonBody.put("postdesc", post.get("postdesc"));
                                    jsonBody.put("user", post.get("user"));
                                    jsonBody.put("isPackage", post.get("isPackage"));
                                    jsonBody.put("carente", categoriesChosen.get(0));
                                    jsonBody.put("role", post.get("role"));
                                    jsonBody.put("country", post.get("country"));
                                    jsonBody.put("city", post.get("city"));
                                    Log.i("VOLLEY", jsonBody.toString());
                                    final String requestBody = jsonBody.toString();

                                    StringRequest stringRequest = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
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






                                //
                                /*
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
                                });*/
                                // Intent intent = new Intent(getApplicationContext(), PostCreatedSuccessfully.class);
                                Toast.makeText(getApplicationContext(), "Post created successfully.", Toast.LENGTH_LONG).show();
                                Intent intent = new Intent(getApplicationContext(), Homepage.class);
                                startActivity(intent);
                                finish();

                            }})
                        .setNegativeButton("Cancel", null).show();


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
                            "You can't select more than " +  maxChecked +  " category!", Toast.LENGTH_SHORT).show();
                }else if(isChecked){
                    countChecked++;
                }else if(!isChecked){
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
                //Toast.makeText(CreateNewPostActivity.this, "Chosen " + interests_selected, Toast.LENGTH_SHORT).show();
                //popChooseCategories.dismiss();
                if(sportCheck.isChecked()) interests_selected.put((String) sportCheck.getText(), true);
                else interests_selected.put((String) sportCheck.getText(), false);
                if(fashionCheck.isChecked()) interests_selected.put((String) fashionCheck.getText(), true);
                else interests_selected.put((String) fashionCheck.getText(), false);
                if(scienceCheck.isChecked()) interests_selected.put((String) scienceCheck.getText(), true);
                else interests_selected.put((String) scienceCheck.getText(), false);
                if(musicCheck.isChecked()) interests_selected.put((String) musicCheck.getText(), true);
                else interests_selected.put((String) musicCheck.getText(), false);
                if(moviesCheck.isChecked()) interests_selected.put((String) moviesCheck.getText(), true);
                else interests_selected.put((String) moviesCheck.getText(), false);
                if(foodCheck.isChecked()) interests_selected.put((String) foodCheck.getText(), true);
                else interests_selected.put((String) foodCheck.getText(), false);
                if(natureCheck.isChecked()) interests_selected.put((String) natureCheck.getText(), true);
                else interests_selected.put((String) natureCheck.getText(), false);

                popChooseCategories.cancel();
                setCategories();

            }
        });
    }

    private void checkBoxes(){
        if (interests_selected.containsKey("Science & IT") && (boolean)interests_selected.get("Science & IT")) scienceCheck.setChecked(true);
        else scienceCheck.setChecked(false);
        if (interests_selected.containsKey("Nature") && (boolean)interests_selected.get("Nature")) natureCheck.setChecked(true);
        else natureCheck.setChecked(false);
        if (interests_selected.containsKey("Sport") && (boolean)interests_selected.get("Sport")) sportCheck.setChecked(true);
        else sportCheck.setChecked(false);
        if (interests_selected.containsKey("Fashion") && (boolean)interests_selected.get("Fashion")) fashionCheck.setChecked(true);
        else fashionCheck.setChecked(false);
        if (interests_selected.containsKey("Food") && (boolean)interests_selected.get("Food")) foodCheck.setChecked(true);
        else foodCheck.setChecked(false);
        if (interests_selected.containsKey("Movies") && (boolean)interests_selected.get("Movies")) moviesCheck.setChecked(true);
        else moviesCheck.setChecked(false);
        if (interests_selected.containsKey("Music") && (boolean)interests_selected.get("Music")) musicCheck.setChecked(true);
        else musicCheck.setChecked(false);
    }


    private void setCategories(){
        categoriesCardLayout.removeAllViews(); // clear cards

        for (Map.Entry<String, Object> entry : interests_selected.entrySet()) {
            String k = entry.getKey();
            boolean value = (boolean) entry.getValue();
            if(value) createCategoryCard(k);
        }
        //Log.d("TAAC", String.valueOf(interests_selected));
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
        Intent i = new Intent(this, Homepage.class);
        startActivity(i);
        finish();

    }

    public static final String[] COUNTRIES = new String[] {
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