package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.MultiAutoCompleteTextView;

public class Signup extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        final Context now = getBaseContext();
        final CheckBox age_check  = findViewById(R.id.age_check);
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
        } );

        sign_up_button.setOnClickListener( new Button.OnClickListener() {
            @Override
            public void onClick(View v) {
                setContentView(R.layout.activity_signup1);

                AutoCompleteTextView countries = (AutoCompleteTextView) findViewById(R.id.autocomplete_country);
                String[] countries_array = getResources().getStringArray(R.array.countries_array);
                ArrayAdapter<String> adapter = new ArrayAdapter<String>(now, android.R.layout.simple_list_item_1, countries_array);
                countries.setAdapter(adapter);

                AutoCompleteTextView cities = (AutoCompleteTextView) findViewById(R.id.autocomplete_city);
                String[] cities_array = getResources().getStringArray(R.array.cities_array);
                ArrayAdapter<String> adapter2 = new ArrayAdapter<String>(now, android.R.layout.simple_list_item_1, cities_array);
                cities.setAdapter(adapter2);

            }
        });

    }




}

