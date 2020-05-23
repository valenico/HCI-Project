package com.example.huc_project;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huc_project.homepage.Homepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OurLogin extends AppCompatActivity  {

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        pref =  getSharedPreferences("Preferences", Context.MODE_PRIVATE); // 0 - for private mode
        editor = pref.edit();
        boolean logged = pref.getBoolean("logged",false);
        if(logged){
            Intent i = new Intent(this, Homepage.class);
            startActivity(i);
        }

        setContentView(R.layout.activity_login);
        Button btn = findViewById(R.id.login);
        final EditText pwd = findViewById(R.id.password);
        final EditText usr = findViewById(R.id.username);
        final Drawable error_indicator = this.getResources().getDrawable(R.drawable.error);

        final Context c = this.getBaseContext();

        pwd.setOnEditorActionListener(new EmptyTextListener(pwd , this.getResources()));
        usr.setOnEditorActionListener(new EmptyTextListener(usr , this.getResources()));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                boolean stop = false;

                if( usr.getText().toString().trim().length() < 1 ){
                    int left = usr.getLeft();
                    int top = usr.getTop();
                    int right = error_indicator.getIntrinsicHeight();
                    int bottom = error_indicator.getIntrinsicWidth();
                    error_indicator.setBounds(new Rect(left, top, right, bottom));
                    usr.setError("Ops! You forgot your e-mail");
                    stop = true;
                }

                if( pwd.getText().toString().trim().length() < 1 ){
                    int left = pwd.getLeft();
                    int top = pwd.getTop();
                    int right = error_indicator.getIntrinsicHeight();
                    int bottom = error_indicator.getIntrinsicWidth();
                    error_indicator.setBounds(new Rect(left, top, right, bottom));
                    pwd.setError("Ops! You forgot your password");
                    stop = true;
                }

                if( Patterns.EMAIL_ADDRESS.matcher(usr.getText().toString().trim()).matches() && !stop){ // using email
                    mAuth.signInWithEmailAndPassword(usr.getText().toString(),pwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(c, "Welcome!", Toast.LENGTH_SHORT);
                                        editor.putBoolean("logged", true);
                                        editor.commit();
                                        Intent i = new Intent(c, Homepage.class);
                                        startActivity(i);
                                        // update UI logged user
                                    } else {
                                        int left = pwd.getLeft();
                                        int top = pwd.getTop();
                                        int right = error_indicator.getIntrinsicHeight();
                                        int bottom = error_indicator.getIntrinsicWidth();
                                        error_indicator.setBounds(new Rect(left, top, right, bottom));
                                        left = usr.getLeft();
                                        top = usr.getTop();
                                        right = error_indicator.getIntrinsicHeight();
                                        bottom = error_indicator.getIntrinsicWidth();
                                        error_indicator.setBounds(new Rect(left, top, right, bottom));
                                        Toast.makeText(c,"Wrong e-mail/passowrd.", Toast.LENGTH_LONG);
                                    }
                                }
                            });

                }
            }
        });

    }

}
