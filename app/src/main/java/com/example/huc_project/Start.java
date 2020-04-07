package com.example.huc_project;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.huc_project.ui.login.Login;

public class Start extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {


        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_start);
        final Button loginButton = findViewById(R.id.to_login);
        //Log.d("AIUTO", String.valueOf(loginButton));

        loginButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(Start.this , Login.class);
                startActivity(intent);
            }
        });

    }
}
