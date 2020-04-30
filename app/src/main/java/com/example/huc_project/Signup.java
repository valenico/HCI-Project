package com.example.huc_project;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jh.circularlist.CircularListView;

import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class Signup extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 1;
    public static int SCREEN = 1;
    private FirebaseAuth mAuth;
    private FirebaseUser mUser;
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };
    private List<String> interests_selected;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SCREEN = 1;
        setContentView(R.layout.activity_signup);

        db = FirebaseFirestore.getInstance();

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

        mail_view.addTextChangedListener(new InputValidator(mail_view , this.getResources()));
        pass.addTextChangedListener(new InputValidator(pass, this.getResources()));
        pass_confirm.addTextChangedListener(new InputValidator(pass_confirm, this.getResources()));

        // Called when an action is performed on the EditText
        mail_view.setOnEditorActionListener(new EmptyTextListener(mail_view , this.getResources()));
        pass.setOnEditorActionListener(new EmptyTextListener(pass, this.getResources()));
        pass_confirm.setOnEditorActionListener(new EmptyTextListener(pass_confirm, this.getResources()));

        mAuth = FirebaseAuth.getInstance();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        mUser = mAuth.getCurrentUser();
        // updateUI(currentUser); TODO
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
            username.setError("Username is requried.");
            stop = true;
        }
        if(mail_view.getText().length() < 1) {
            int left = mail_view.getLeft();
            int top = mail_view.getTop();
            int right = error_indicator.getIntrinsicHeight();
            int bottom = error_indicator.getIntrinsicWidth();
            error_indicator.setBounds(new Rect(left, top, right, bottom));
            mail_view.setError("E-mail is required.");
            stop = true;
        }

        if(!stop){
            SCREEN = 2;
            mAuth.createUserWithEmailAndPassword(mail_view.getText().toString().trim() , pass.getText().toString().trim())
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        public void onComplete(@NonNull Task<AuthResult> task) {

                            if (!task.isSuccessful()) {
                                Toast.makeText(Signup.this, "ERROR",Toast.LENGTH_LONG).show();
                            } else {
                                // update name TODO
                                // a quanto pareil displayname de firebase lo sanno solo loro e non si può vedere se lo setti o no
                                // BELLA MERDA
                                mUser = mAuth.getCurrentUser();
                                HashMap<String, String> upd = new HashMap<>();
                                upd.put("Name", username.getText().toString());
                                db.collection("UTENTI").document(mUser.getUid()).set(upd);

                                 setContentView(R.layout.activity_signup1);
                                 AutoCompleteTextView countries = findViewById(R.id.autocomplete_country);
                                 String[] countries_array = getResources().getStringArray(R.array.countries_array);
                                 ArrayAdapter<String> adapter = new ArrayAdapter<>(Signup.this, android.R.layout.simple_list_item_1, countries_array);
                                 countries.setAdapter(adapter);
                                 AutoCompleteTextView cities = findViewById(R.id.autocomplete_city);
                                 String[] cities_array = getResources().getStringArray(R.array.cities_array);
                                 ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Signup.this, android.R.layout.simple_list_item_1, cities_array);
                                 cities.setAdapter(adapter2);
                            }
                        }
                    });
        }
    }

    public void complete_profile2(View v) {

        EditText country =  findViewById(R.id.autocomplete_country);
        EditText city = findViewById(R.id.autocomplete_city);
        EditText phone = findViewById(R.id.phonenumber);

        HashMap<String, String> upd = new HashMap<>();
        if(country.getText().toString().trim().length() > 0 ){
            upd.put("Country" , country.getText().toString());
        }
        if(city.getText().toString().trim().length() > 0 ){
            upd.put("City" , city.getText().toString());
        }
        db.collection("UTENTI").document(mUser.getUid()).set(upd);

        if(phone.getText().toString().trim().length() > 0){
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Integer>().put("Phone" , Integer.parseInt(phone.getText().toString().trim())));
        }

        if(  ((CheckBox) findViewById(R.id.hidemail)).isChecked() ){
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("HideMail" , true));
        } else {
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("HideMail" , false));
        }

        setContentView(R.layout.activity_signup2);
        SCREEN = 3;
        final ImageButton add_pic = findViewById(R.id.profile_pic);
        add_pic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivityForResult(new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.INTERNAL_CONTENT_URI), GET_FROM_GALLERY);
            }
        });


    }

    @SuppressLint("ClickableViewAccessibility")
    public void complete_profile3(View vi) {

        EditText desc = findViewById(R.id.description);
        if(desc.getText().toString().trim().length() > 0){
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , String>().put("Description" , desc.getText().toString().trim() ));
        }

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
                                        interests_selected.add(Text[i]);
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK)
            if( requestCode == GET_FROM_GALLERY) {
                    //data.getData returns the content URI for the selected Image
                    Uri selectedImage = data.getData();
                    final ImageButton add_pic = findViewById(R.id.profile_pic);
                    Bitmap image = null;
                    try {
                        image = decodeUri(this,selectedImage,125);
                    } catch (FileNotFoundException e) {
                        e.printStackTrace();
                    }

                    add_pic.setImageBitmap(image);
            }

    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)  throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth , height_tmp = o.outHeight;
        int scale = 1;

        while(width_tmp / 2 > requiredSize && height_tmp / 2 > requiredSize) {
            width_tmp /= 2;
            height_tmp /= 2;
            scale *= 2;
        }

        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        return BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o2);
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

        db.collection("UTENTI").document(mUser.getUid()).set( new HashMap<String, List<String>>().put("Ineterests" , interests_selected) );
        if(  ((CheckBox) findViewById(R.id.is_sponsor)).isChecked() ){
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("Sponsor" , true));
        } else {
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("Sponsor" , false));
        }
        if(  ((CheckBox) findViewById(R.id.look_sponsors)).isChecked() ){
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("LookSponsor" , true));
        } else {
            db.collection("UTENTI").document(mUser.getUid()).set(new HashMap<String , Boolean>().put("LookSponsor" , false));
        }

        Intent i = new Intent(this , Homepage.class);
        Toast.makeText(getApplicationContext(),"Registered successfully.",Toast.LENGTH_SHORT).show();
        startActivity(i);
    }
}
