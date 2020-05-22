package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.huc_project.dialog.GuestModeDialog;

public class Start extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
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
