package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.OvershootInterpolator;
import android.view.animation.ScaleAnimation;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.example.huc_project.homepage.Homepage;
import com.example.huc_project.ui.login.CircularItemAdapter;
import com.example.huc_project.ui.login.PaintText;
import com.jh.circularlist.CircularListView;
import com.jh.circularlist.CircularTouchListener;


import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;

public class Signup extends AppCompatActivity {

    private static final int GET_FROM_GALLERY = 1;
    public static int SCREEN = 1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SCREEN = 1;
        setContentView(R.layout.activity_signup);

        final Context now = getBaseContext();
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

    }

    public void complete_profile1(View v) {
        setContentView(R.layout.activity_signup1);
        SCREEN = 2;
        AutoCompleteTextView countries = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
        String[] countries_array = getResources().getStringArray(R.array.countries_array);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, countries_array);
        countries.setAdapter(adapter);

        AutoCompleteTextView cities = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
        String[] cities_array = getResources().getStringArray(R.array.cities_array);
        ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, cities_array);
        cities.setAdapter(adapter2);

    }

    public void complete_profile2(View v) {

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
            private float pre_x = 0;
            private float pre_y = 0;
            private float cur_x = 0;
            private float cur_y = 0;
            private float move_x = 0;
            private float move_y = 0;
            private float minClickDistance = 30.0f;
            private float minMoveDistance = 30.0f;
            private float mMovingSpeed = 2000.0f;  // default is 2000, larger > faster
            private boolean can_rotate = true;
            private boolean isCircularMoving = false; // ensure that item click only triggered when it's not moving
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                 int max = getMaxValue(children);
                 if(max == 6){
                     can_rotate = true;
                 }
                 switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cur_x = event.getX();
                        cur_y = event.getY();
                        init_x = event.getX();
                        init_y = event.getY();

                    case MotionEvent.ACTION_MOVE:
                        pre_x = cur_x;
                        pre_y = cur_y;
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
                                        circularListView.addView(new PaintText( c, i,
                                                view.getLeft()-60, view.getTop()-60,view.getRight()+60, view.getBottom()+60,
                                                -145,135) );
                                        children[i] = max + 1;
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
            switch (requestCode) {
                case GET_FROM_GALLERY:
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
                   break;
            }


    }

    public static Bitmap decodeUri(Context c, Uri uri, final int requiredSize)  throws FileNotFoundException {
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;
        BitmapFactory.decodeStream(c.getContentResolver().openInputStream(uri), null, o);

        int width_tmp = o.outWidth , height_tmp = o.outHeight;
        int scale = 1;

        while(true) {
            if(width_tmp / 2 < requiredSize || height_tmp / 2 < requiredSize)
                break;
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
        Intent i = null;
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
        Intent i = new Intent(this , Homepage.class);
        Toast.makeText(getApplicationContext(),"Registered successfully.",Toast.LENGTH_SHORT).show();
        startActivity(i);
    }

}

