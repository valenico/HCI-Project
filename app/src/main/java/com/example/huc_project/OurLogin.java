package com.example.huc_project;

import android.content.Context;
import android.content.Intent;
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
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        Button btn = findViewById(R.id.login);
        final EditText pwd = findViewById(R.id.password);
        final EditText usr = findViewById(R.id.username);

        if(mAuth.getCurrentUser() != null){
            // update UI to logged user
        }

        final Context c = this.getBaseContext();

        pwd.setOnEditorActionListener(new EmptyTextListener(pwd , this.getResources()));
        //pwd.addTextChangedListener(new InputValidator(pwd, this.getResources()));

        usr.setOnEditorActionListener(new EmptyTextListener(usr , this.getResources()));
        //usr.addTextChangedListener(new InputValidator(usr, this.getResources()));

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if( Patterns.EMAIL_ADDRESS.matcher(usr.getText().toString().trim()).matches() ){ // using email
                    mAuth.signInWithEmailAndPassword(usr.getText().toString(),pwd.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(c, "Welcome!", Toast.LENGTH_SHORT);
                                        Intent i = new Intent(c, Homepage.class);
                                        startActivity(i);
                                        // update UI logged user
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(c, "Something went wrong...", Toast.LENGTH_SHORT);
                                    }
                                }
                            });

                }
            }
        });

    }

}
