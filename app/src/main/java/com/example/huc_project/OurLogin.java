package com.example.huc_project;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.huc_project.homepage.Homepage;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.GoogleAuthProvider;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class OurLogin extends AppCompatActivity  {

    private GoogleSignInOptions gso;
    private String user_to_reset;
    private GoogleSignInClient mGoogleSignInClient;
    private static final int RC_SIGN_IN = 101;

    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken("1060787676521-r4035k726tfjddlhuiinlok7psp7gg0k.apps.googleusercontent.com")
                .requestEmail()
                .build();
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
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
        final Drawable error_indicator2 = this.getResources().getDrawable(R.drawable.error);

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

                if(!Patterns.EMAIL_ADDRESS.matcher(usr.getText().toString().trim()).matches()){
                    int left = usr.getLeft();
                    int top = usr.getTop();
                    int right = error_indicator.getIntrinsicHeight();
                    int bottom = error_indicator.getIntrinsicWidth();
                    error_indicator.setBounds(new Rect(left, top, right, bottom));
                    usr.setError("Ops! This e-mail is not valid.");
                    stop = true;
                }

                if( !stop){ // using email
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

                                    } else {

                                        int left = pwd.getLeft();
                                        int top = pwd.getTop();
                                        int right = error_indicator.getIntrinsicHeight();
                                        int bottom = error_indicator.getIntrinsicWidth();
                                        error_indicator.setBounds(new Rect(left, top, right, bottom));
                                        int left2 = usr.getLeft();
                                        int top2 = usr.getTop();
                                        int right2 = error_indicator2.getIntrinsicHeight();
                                        int bottom2 = error_indicator2.getIntrinsicWidth();
                                        error_indicator2.setBounds(new Rect(left2, top2, right2, bottom2));
                                        Toast.makeText(c,"Wrong e-mail/password.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });

                }
            }
        });

    }

    public void google_login(View v){
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Result code is RESULT_OK only if the user selects an Image

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
            try {
                Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
                handleSignInResult(task);
            } catch (Exception e){
                Log.d("TAG", "Errore " + e.toString());
            }

        }

    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            firebaseAuthWithGoogle(account.getIdToken());
        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("TAG", "signInResult:failed code=" + e.getStatusCode());

        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        final ProgressDialog progressDialog = new ProgressDialog(OurLogin.this);
        progressDialog.setMessage("Registering Please Wait...");
        progressDialog.show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("TAG", "signInWithCredential:success");
                            editor.putBoolean("logged",true);
                            editor.commit();
                            Intent gi = new Intent(OurLogin.this , Homepage.class);
                            startActivity(gi);

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.d("TAG", "signInWithCredential:failure", task.getException());
                        }
                        progressDialog.dismiss();
                    }
                });
    }

    public void showForgotDialog(View v) {
        final Context c = OurLogin.this;
        final EditText taskEditText = new EditText(c);
        AlertDialog dialog = new AlertDialog.Builder(c)
                .setTitle("Forgot Password")
                .setMessage("Enter your email address:")
                .setView(taskEditText)
                .setPositiveButton("Reset", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        user_to_reset = String.valueOf(taskEditText.getText());
                        FirebaseAuth.getInstance().sendPasswordResetEmail(user_to_reset)
                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> task) {
                                        if (task.isSuccessful()) {
                                            Log.d("TAG", "Email sent.");
                                        } else {
                                            Toast.makeText(c, "Our servers didn't recognize your email, maybe you are not registered.", Toast.LENGTH_LONG).show();
                                        }
                                    }
                                });
                    }
                })
                .setNegativeButton("Cancel", null)
                .create();
        dialog.show();
    }


}
