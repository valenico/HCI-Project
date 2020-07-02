package com.example.huc_project.settings;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SearchView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Scroller;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.huc_project.OurLogin;
import com.example.huc_project.R;
import com.example.huc_project.chat.Chat;
import com.example.huc_project.homepage.Homepage;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;


public class Settings extends AppCompatActivity implements CustomAdapter.OnItemListener {

    private final List<String> listItems = new ArrayList<>();
    private RecyclerView recyclerView;
    private CustomAdapter adapter;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private FirebaseAuth mAuth = FirebaseAuth.getInstance();
    SharedPreferences pref;
    SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        pref = getSharedPreferences("Preferences", Context.MODE_PRIVATE);
        editor = pref.edit();

        listItems.add("Invite Friends");
        listItems.add("General");
        listItems.add("Privacy & Security");
        listItems.add("Help & About");

        setUpRecyclerView();
        initScrollListener();

    }

    private void setUpRecyclerView(){
        recyclerView = findViewById(R.id.list_of_settings);
        recyclerView.setHasFixedSize(true);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        adapter = new CustomAdapter(listItems, this);

        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onItemClick(int position) {

        ArrayList<String> mylist = new ArrayList<>(listItems);
        String clicked = listItems.get(position);

        if(clicked.equals("General") && !mylist.contains("Change Email")){
            listItems.add(position+1,"Change Email"); // done
            listItems.add(position+2,"Log Out"); // done
            listItems.add(position+3, "Left-Handed Layout");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("General")){
            listItems.remove("Change Email"); // done
            listItems.remove("Log Out"); // done
            listItems.remove("Left-Handed Layout");
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Help & About") && !mylist.contains("Report a Problem")){
            listItems.add(position+1,"Report a Problem"); // done
            listItems.add(position+2,"Send a Feedback"); // done
            listItems.add(position+3,"Terms of Use & Data Policy"); // done
            listItems.add(position+4,"About the App"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Help & About")){
            listItems.remove("Report a Problem"); // done
            listItems.remove("Send a Feedback"); // done
            listItems.remove("Terms of Use & Data Policy");  // done
            listItems.remove("About the App"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Privacy & Security") && !mylist.contains("Change Password")){
            listItems.add(position+1,"Change Password"); // done
            listItems.add(position+2,"Account Privacy");
            listItems.add(position+3,"Blocked Accounts"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Privacy & Security")) {
            listItems.remove("Change Password"); // done
            listItems.remove("Account Privacy");
            listItems.remove("Blocked Accounts"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Invite Friends") && !mylist.contains("Invite Friends by Email")){
            listItems.add(position+1,"Invite Friends by Email"); // done
            listItems.add(position+2,"Invite Friends by SMS"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Invite Friends")){
            listItems.remove("Invite Friends by Email"); // done
            listItems.remove("Invite Friends by SMS"); // done
            adapter.notifyDataSetChanged();
        } else if(clicked.equals("Invite Friends by Email")){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
            emailIntent.setType("text/plain");
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "DOWNLOAD SPONSOR APP");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "Download Sponsor App from here! I'm already there!");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));
        } else if(clicked.equals("Invite Friends by SMS")){
            Intent sendIntent = new Intent(Intent.ACTION_VIEW);
            sendIntent.setData(Uri.parse("sms:"));
            sendIntent.putExtra("sms_body", "Download Sponsor App from here! I'm already there!" );
            startActivity(sendIntent);
        } else if(clicked.equals("Report a Problem")){
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL  , new String[] { "progettohumanci@gmail.com" });
            intent.putExtra(Intent.EXTRA_SUBJECT, "REPORT A BUG");
            startActivity(Intent.createChooser(intent, "Email via..."));
        }  else if(clicked.equals("Change Email")){
            final Context c = Settings.this;
            final EditText taskEditText = new EditText(c);
            AlertDialog dialog = new AlertDialog.Builder(c)
                    .setTitle("Change Email")
                    .setView(taskEditText)
                    .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            HashMap<String,String> data = new HashMap<>();
                            data.put("Email",taskEditText.getText().toString());
                            db.collection("UTENTI").document(mAuth.getUid()).set(data, SetOptions.merge());
                            mAuth.getCurrentUser().updateEmail(taskEditText.getText().toString())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Toast.makeText(Settings.this, "Email changed successfully." , Toast.LENGTH_SHORT).show();
                                            } else {
                                                Toast.makeText(Settings.this, "Error: our servers are not able to change your email now." , Toast.LENGTH_SHORT).show();
                                            }
                                        }
                                    });
                        }
                    })
                    .setNegativeButton("Cancel", null)
                    .create();
            dialog.show();
        } else if(clicked.equals("Change Password")){
            FirebaseAuth.getInstance().sendPasswordResetEmail(mAuth.getCurrentUser().getEmail())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(Settings.this, "An email to change your password has been sent to your email.", Toast.LENGTH_LONG).show();
                            } else {
                                Toast.makeText(Settings.this, "Error: our servers are busy and cannot handle your request now.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
        } else if(clicked.equals("Send a Feedback")){
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://")));
            } catch (android.content.ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/")));
            }
        } else if(clicked.equals("Terms of Use & Data Policy")){
            final Context c = Settings.this;
            final TextView taskEditText = new TextView(c);
            taskEditText.setText(R.string.termsofuse);
            taskEditText.setScroller(new Scroller(this));
            taskEditText.setVerticalScrollBarEnabled(true);
            taskEditText.setMovementMethod(new ScrollingMovementMethod());
            AlertDialog dialog = new AlertDialog.Builder(c)
                    .setTitle("Terms of Use & Data Policy")
                    .setView(taskEditText)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        } else if(clicked.equals("Log Out")){
            mAuth.signOut();
            editor.clear();
            editor.commit();
            Intent to_start = new Intent(getApplicationContext() , OurLogin.class);
            startActivity(to_start);
            finish();
        } else if(clicked.equals("About the App")){
            final Context c = Settings.this;
            final TextView taskEditText = new TextView(c);
            taskEditText.setText(R.string.about_app);
            taskEditText.setScroller(new Scroller(this));
            taskEditText.setVerticalScrollBarEnabled(true);
            taskEditText.setMovementMethod(new ScrollingMovementMethod());
            AlertDialog dialog = new AlertDialog.Builder(c)
                    .setTitle("About the App")
                    .setView(taskEditText)
                    .setPositiveButton("Ok", null)
                    .create();
            dialog.show();
        } else if(clicked.equals("Blocked Accounts")){
            Intent i = new Intent(getApplicationContext(), BlockedAccounts.class);
            startActivity(i);
        } else if(clicked.equals("Account Privacy")){
            final Context c = Settings.this;
            final Switch sw = new Switch(c);
            sw.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    HashMap<String, Boolean> data = new HashMap<>();
                    data.put("guest_can_see", isChecked);
                    db.collection("UTENTI").document(mAuth.getUid()).set(data, SetOptions.merge());
                }
            });
            db.collection("UTENTI").document(mAuth.getUid()).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if(task.isSuccessful()) {
                        DocumentSnapshot d = task.getResult();
                        Boolean on = false;
                        if(d.contains("guest_can_see")){
                            on = (Boolean) d.get("guest_can_see");
                        }
                        sw.setChecked(on);
                        AlertDialog dialog = new AlertDialog.Builder(c)
                                .setTitle("Can Guests see your profile?")
                                .setView(sw)
                                .create();
                        dialog.show();
                    }
                }
            });

        } else if(clicked.equals("Left-Handed Layout")){
            int rtl = pref.getInt("rtl", 4);
            if(rtl == 4){
                editor.putInt("rtl", 6);
                Toast.makeText( Settings.this, "Left-Handed Mode activated.", Toast.LENGTH_SHORT).show();
            } else {
                editor.putInt("rtl", 4);
                Toast.makeText( Settings.this, "Left-Handed Mode de-activated.", Toast.LENGTH_SHORT).show();
            }
            Log.d("RTLLL", String.valueOf(rtl));
            editor.commit();

        }
    }

    private void initScrollListener() {
        recyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                LinearLayoutManager linearLayoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        menu.clear();
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.main_menu, menu);

        MenuItem searchItem = menu.findItem(R.id.search_icon);
        searchItem.setVisible(false);

        MenuItem filter = menu.findItem(R.id.menu_slide);
        filter.setVisible(false);

        return true;
    }

    @Override
    public void onBackPressed() {
        Intent back = new Intent(this, Homepage.class);
        startActivity(back);
        finish();
    }


}