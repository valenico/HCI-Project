package com.example.huc_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.ui.login.CircularItemAdapter;
import com.example.huc_project.ui.login.PaintText;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.google.protobuf.Empty;
import com.jh.circularlist.CircularListView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.InvalidMarkException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Signup extends AppCompatActivity {

    SharedPreferences pref;
    SharedPreferences.Editor editor;

    private static final int GET_FROM_GALLERY = 1;
    public static int SCREEN = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private Uri profile_pic_uri;
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };
    private HashMap<String,Object>  interests_selected = new HashMap<>();
    FirebaseStorage storage = FirebaseStorage.getInstance();
    StorageReference ref = storage.getReference();
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SCREEN = 1;
        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = pref.edit();
        setContentView(R.layout.activity_signup);
        db = FirebaseFirestore.getInstance();
        mAuth = FirebaseAuth.getInstance();

        final CheckBox age_check = findViewById(R.id.age_check);
        final Button sign_up_button = findViewById(R.id.signup);

        age_check.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    sign_up_button.setEnabled(true);
                } else {
                    sign_up_button.setEnabled(false);
                }
            }
        });

        EditText mail_view = findViewById(R.id.email);
        EditText pass = findViewById(R.id.password_signup);
        EditText pass_confirm = findViewById(R.id.confirm_password);
        EditText username_view = findViewById(R.id.username_signup);

        if (pref.contains("username")) {
            username_view.setText(pref.getString("username", ""));
            mail_view.setText(pref.getString("mail", ""));
            mAuth.getCurrentUser().delete();
        }

        mail_view.addTextChangedListener(new InputValidator(mail_view , this.getResources()));
        pass.addTextChangedListener(new InputValidator(pass, this.getResources()));
        pass_confirm.addTextChangedListener(new InputValidator(pass_confirm, this.getResources()));
        username_view.addTextChangedListener(new InputValidator(username_view, this.getResources()));

        // Called when an action is performed on the EditText
        mail_view.setOnEditorActionListener(new EmptyTextListener(mail_view , this.getResources()));
        pass.setOnEditorActionListener(new EmptyTextListener(pass, this.getResources()));
        pass_confirm.setOnEditorActionListener(new EmptyTextListener(pass_confirm, this.getResources()));
        username_view.setOnEditorActionListener(new EmptyTextListener(username_view, this.getResources()));

    }

    @Override
    public void onStart() {
        super.onStart();
        // no check for logged in, se sei qua
    }

    public static boolean isNumeric(String str)
    {
        return str.matches("-?\\d+(.\\d+)?");
    }

    public void complete_profile1(View v) {

        final EditText mail_view = findViewById(R.id.email);
        final EditText username = findViewById(R.id.username_signup);
        EditText pass = findViewById(R.id.password_signup);
        EditText pass_confirm = findViewById(R.id.confirm_password);
        Drawable error_indicator = this.getResources().getDrawable(R.drawable.error);
        boolean same_pass = pass.getText().toString().matches(pass_confirm.getText().toString());
        boolean stop = false;

        if(!same_pass) {
            pass_confirm.setText("");
            int left = pass_confirm.getLeft();
            int top = pass_confirm.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            pass_confirm.setError("Passwords do not match.");
            stop = true;
        }
        if( pass.getText().length() == 0){
            pass.setText("");
            int left = pass.getLeft();
            int top = pass.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            pass.setError("Password is required.");
            stop = true;
        }
        if(username.getText().length() < 1) {
            int left = username.getLeft();
            int top = username.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            username.setError("Username is required.");
            stop = true;
        }
        if( isNumeric(username.getText().toString()) ) {
            int left = username.getLeft();
            int top = username.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            username.setError("Username can't be only numeric.");
            stop = true;
        }
        editor.putString("username",username.getText().toString());
        if(mail_view.getText().length() < 1) {
            int left = mail_view.getLeft();
            int top = mail_view.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            mail_view.setError("E-mail is required.");
            stop = true;
        }
        editor.putString("mail",mail_view.getText().toString());
        editor.commit();
        if(!stop){
            SCREEN = 2;
            mAuth.createUserWithEmailAndPassword(mail_view.getText().toString().trim() , pass.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this, "ERROR",Toast.LENGTH_LONG).show();
                            } else {
                                mUser = mAuth.getCurrentUser();
                                HashMap<String, String> upd = new HashMap<>();
                                upd.put("Name", username.getText().toString());
                                db.collection("UTENTI").document(mUser.getUid()).set(upd);
                                 setContentView(R.layout.activity_signup1);

                                 AutoCompleteTextView countries = findViewById(R.id.autocomplete_country);
                                 String[] countries_array = getResources().getStringArray(R.array.countries_array);
                                 ArrayAdapter<String> adapter = new ArrayAdapter<>(Signup.this, android.R.layout.simple_list_item_1, countries_array);
                                 countries.setAdapter(adapter);

                                if (pref.contains("country")) {
                                    countries.setText(pref.getString("country", ""));
                                }

                                 AutoCompleteTextView cities = findViewById(R.id.autocomplete_city);
                                 String[] cities_array = getResources().getStringArray(R.array.cities_array);
                                 ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Signup.this, android.R.layout.simple_list_item_1, cities_array);
                                 cities.setAdapter(adapter2);

                                if (pref.contains("city")) {
                                    cities.setText(pref.getString("city", ""));
                                }
                            }
                        }
                    });
        }
    }

    private  void SelectImage(){
        CropImage.startPickImageActivity(this);
    }

    public void complete_profile2(View v) {

        EditText country =  findViewById(R.id.autocomplete_country);
        EditText city = findViewById(R.id.autocomplete_city);
        EditText phone = findViewById(R.id.phonenumber);

        HashMap<String, Object> upd = new HashMap<>();

        if (country.getText().toString().trim().length() > 0) {
            upd.put("Country", country.getText().toString());
            editor.putString("country",country.getText().toString());
        }

        if (city.getText().toString().trim().length() > 0) {
            upd.put("City", city.getText().toString());
            editor.putString("city", city.getText().toString());
        }
        if (phone.getText().toString().trim().length() > 0) {
           upd.put("Phone", Integer.parseInt(phone.getText().toString()));
            editor.putString("phone", phone.getText().toString());
        }
        upd.put("Hidemail" , ((CheckBox) findViewById(R.id.hidemail)).isChecked() );
        editor.putBoolean("hidemail", ((CheckBox) findViewById(R.id.hidemail)).isChecked() );
        editor.commit();
        db.collection("UTENTI").document(mUser.getUid()).set(upd , SetOptions.merge());
        setContentView(R.layout.activity_signup2);
        SCREEN = 3;
        final ImageView add_pic = findViewById(R.id.profile_pic);
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               // startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
                SelectImage();
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    public void complete_profile3(View vi) {

        EditText desc = findViewById(R.id.description);
        if(desc.getText().toString().trim().length() > 0){
            HashMap<String,Object> upd = new HashMap<>();
            upd.put("Description" , desc.getText().toString().trim());
            db.collection("UTENTI").document(mUser.getUid()).set( upd , SetOptions.merge());
        }

        editor.putString("description",desc.getText().toString());
        editor.commit();

        setContentView(R.layout.activity_signup3);
        final Context c = this.getBaseContext();
        SCREEN = 4;
        final int[] children = new int[]{ 6,6,6,6,6,6,6 };
        ArrayList<Integer> interests = new ArrayList<>(Arrays.asList(R.drawable.ic_sport , R.drawable.ic_fashion, R.drawable.ic_food, R.drawable.ic_movie, R.drawable.ic_music, R.drawable.ic_technology, R.drawable.ic_nature));
        final CircularListView circularListView = findViewById(R.id.circle_interests);
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

    private void startCropImageActivity(Uri imageUri) {
        Intent intent = CropImage.activity(imageUri)
                .setAspectRatio(1,1)
                .getIntent(this.getBaseContext());
        startActivityForResult(intent, CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE);
    }

    private void uploadImage(Uri filePath){
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setTitle("Uploading...");
        progressDialog.show();

        StorageReference r2 = ref.child("users/"+ mAuth.getCurrentUser().getUid());
        r2.putFile(filePath)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Uploaded", Toast.LENGTH_SHORT).show();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        progressDialog.dismiss();
                        Toast.makeText(Signup.this, "Failed "+e.getMessage(), Toast.LENGTH_SHORT).show();
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CropImage.PICK_IMAGE_CHOOSER_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            profile_pic_uri = CropImage.getPickImageResultUri(this, data);
            startCropImageActivity(profile_pic_uri);
        }
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode == Activity.RESULT_OK){
            CropImage.ActivityResult ar = CropImage.getActivityResult(data);
            Uri filePath = ar.getUri();
            final ImageView add_pic = findViewById(R.id.profile_pic);
            try {
                 uploadImage(filePath);
                 Bitmap bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), filePath);
                 add_pic.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }

        }

    }


    @Override
    public void onBackPressed() {
        Intent i;
        switch(SCREEN){
            case 1:
                i = new Intent(this , Start.class);
                startActivity(i);
                break;
            case 2:
                i = new Intent(this , Signup.class);
                startActivity(i);
                break;
            case 3:
                complete_profile1(null);
                break;
            case 4:
                complete_profile2(null);
                break;
            default:
                break;
        }
    }

    public void end_signup(View v){
        HashMap<String, Object> upd = new HashMap<>();
        upd.put("Interests", interests_selected);
        upd.put("Sponsors" ,  ((CheckBox) findViewById(R.id.is_sponsor)).isChecked() );
        upd.put("LookSponsors", ((CheckBox) findViewById(R.id.look_sponsors)).isChecked() );
        db.collection("UTENTI").document(mUser.getUid()).set(upd, SetOptions.merge());

        editor.clear();
        editor.putBoolean("logged",true);
        editor.commit();

        Intent i = new Intent(this , Homepage.class);
        Toast.makeText(getApplicationContext(),"Registered successfully.",Toast.LENGTH_SHORT).show();
        startActivity(i);
    }
}
