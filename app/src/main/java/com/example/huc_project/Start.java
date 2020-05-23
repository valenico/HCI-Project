package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.huc_project.dialog.GuestModeDialog;
import com.example.huc_project.homepage.Homepage;

public class Start extends AppCompatActivity {

    SharedPreferences pref;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        boolean logged = pref.getBoolean("logged",false);
        if(logged){
            Intent i = new Intent(this, Homepage.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_start);
        final Button loginButton = findViewById(R.id.to_login);
        final Button signupButton = findViewById(R.id.to_signup);
        final Button guestModeButton = findViewById(R.id.guest_mode);

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Start.this , OurLogin.class);
                startActivity(intent);
            }
        });

        signupButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Start.this , Signup.class);
                startActivity(intent);
            }
        });

        guestModeButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                DialogFragment dialog = new GuestModeDialog();
                dialog.show(getSupportFragmentManager(), "guestmode");
            }
        });


    }

}
