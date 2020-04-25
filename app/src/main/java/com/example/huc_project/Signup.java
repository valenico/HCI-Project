package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Paint;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.ImageButton;

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

        circularListView.setOnItemClickListener(new CircularTouchListener.CircularItemClickListener() {
            @Override
            public void onItemClick(View view, int index){
                float curr_size = view.getScaleX();
                if(curr_size == (float) 1) {
                    view.setScaleX((float) 1.5);
                    view.setScaleY((float) 1.5);
                    circularListView.addView(new PaintText( c, index,
                            view.getLeft()-60, view.getTop()-60,view.getRight()+50, view.getBottom()+50,
                            -180,200) );
                    int max = getMaxValue(children);
                    children[index] = max + 1;
                } else {
                    view.setScaleX((float) 1);
                    view.setScaleY((float) 1);
                    circularListView.removeViewAt(children[index]);
                    for(int elem = 0; elem < children.length; elem++){
                        if (children[elem] > children[index]){
                            children[elem] -= 1;
                        }
                    }
                    children[index] = 6;

                }

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
}

