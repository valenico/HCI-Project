package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.MultiAutoCompleteTextView;
import android.widget.TextView;

import java.io.FileNotFoundException;

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

    public void complete_profile3(View v) {
        setContentView(R.layout.activity_signup3);
        String interests[] = {"Sport" , "Nature" , "Food" , "Technology" , "Fashion" };
        SCREEN = 4;

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
