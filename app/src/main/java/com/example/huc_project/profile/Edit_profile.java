package com.example.huc_project.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.bumptech.glide.Glide;
import com.example.huc_project.R;
import com.example.huc_project.Signup;
import com.example.huc_project.ViewPageAdapter;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;

import org.w3c.dom.Text;

import java.util.HashMap;

public class Edit_profile extends AppCompatActivity {
    private FirebaseFirestore db;
    FirebaseStorage storage = FirebaseStorage.getInstance();

    TabLayout tabLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        db = FirebaseFirestore.getInstance();

        final Boolean guest_user;
        final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

        if (current_user != null) guest_user = false;
        else guest_user = true;

        final DocumentReference docRef = db.collection("UTENTI").document(current_user.getUid());

        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    final DocumentSnapshot document = task.getResult();
                    if (document != null) {
                        String name = (String) document.get("Name");
                        final String country = (String) document.get("Country");
                        String city = (String) document.get("City");
                        String mail = current_user.getEmail();
                        String description = (String) document.get("Description");
                        final Boolean hidden_mail = (Boolean) document.get("Hidemail");

                        final EditText user_name = findViewById(R.id.name);

                        final AutoCompleteTextView user_country = findViewById(R.id.country);
                        String[] countries_array = getResources().getStringArray(R.array.countries_array);
                        ArrayAdapter<String> adapter = new ArrayAdapter<>(Edit_profile.this, android.R.layout.simple_list_item_1, countries_array);
                        user_country.setAdapter(adapter);

                        final AutoCompleteTextView user_city = findViewById(R.id.city);
                        String[] cities_array = getResources().getStringArray(R.array.cities_array);
                        ArrayAdapter<String> adapter2 = new ArrayAdapter<>(Edit_profile.this, android.R.layout.simple_list_item_1, cities_array);
                        user_city.setAdapter(adapter2);

                        TextView user_mail = findViewById(R.id.mail);
                        ImageView profile_img = findViewById(R.id.profile_image);
                        CheckBox h_mail = findViewById(R.id.hidemail);
                        final EditText user_description = findViewById(R.id.description);

                        StorageReference ref = storage.getReference().child("users/" + current_user.getUid());
                        Glide.with(Edit_profile.this).load(ref).into(profile_img);

                        user_name.setText(name);
                        user_mail.setText(mail);
                        user_description.setText(description);
                        h_mail.setChecked(hidden_mail);

                        if (country == null) user_country.setText("Unknown");
                        else user_country.setText(country);

                        if (city == null) user_city.setText("Unknown");
                        else user_city.setText(city);

                        Button done_button = findViewById(R.id.done_button);
                        done_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                HashMap<String, String> upd = new HashMap<>();
                                HashMap<String, Boolean> upd2 = new HashMap<>();
                                upd.put("Name", user_name.getText().toString());
                                if (user_country.getText().toString() != "Unknown") upd.put("Country", user_country.getText().toString());
                                if (user_city.getText().toString() != "Unknown") upd.put("City", user_city.getText().toString());
                                upd.put("Description", user_description.getText().toString());
                                upd2.put("Hidemail", hidden_mail);
                                db.collection("UTENTI").document(current_user.getUid()).set(upd);
                                db.collection("UTENTI").document(current_user.getUid()).set(upd2, SetOptions.merge());
                                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                                startActivity(intent);
                            }
                        });

                        Button reset_button = findViewById(R.id.reset_button);
                        reset_button.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                user_name.setText((String) document.get("Name"));
                                if (document.get("Country") == null) user_country.setText("Unknown");
                                else user_country.setText((String) document.get("Country"));
                                if (document.get("City") == null) user_city.setText("Unknown");
                                else user_city.setText((String) document.get("City"));
                                user_description.setText((String) document.get("Description"));
                            }
                        });
                    }
                }
            }
        });
    }
}

