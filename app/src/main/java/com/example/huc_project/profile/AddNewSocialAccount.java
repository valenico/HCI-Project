package com.example.huc_project.profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.huc_project.R;
import com.example.huc_project.ui.login.CircularItemAdapter;
import com.example.huc_project.ui.login.PaintText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.jh.circularlist.CircularListView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Random;

public class AddNewSocialAccount extends AppCompatActivity {
    private FirebaseFirestore db;
    final String current_user = Profile_main_page.getCurrent_user();
    //final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();
    private String[] Text = {"facebook", "instagram", "twitter", "youtube",
                            "github", "linkedin", "reddit", "snapchat",
                            "twitch", "vimeo", "spotify", "tumblr", "vk", "website"};
    String social_selected;
    Integer icon_selected;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_social_account);
        db = FirebaseFirestore.getInstance();

        Button done_Button = findViewById(R.id.button_done);
        done_Button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (social_selected != null) {
                    HashMap<String, String> social = new HashMap<>();
                    social.put("name", social_selected);
                    social.put("user", current_user);
                    Random rand = new Random();
                    int n = rand.nextInt(100000);
                    int lm = rand.nextInt(40);
                    int lw = rand.nextInt(60);
                    int isPositive = rand.nextInt(2);
                    String LW = String.valueOf(lw);
                    String LM = String.valueOf(lm);
                    if (isPositive==1) {
                        LW = "+" + LW;
                        LM = "+" + LM; }
                    else {
                        LW = "-" + LW;
                        LM = "-" + LM; }
                    social.put("followers", String.valueOf(n));
                    social.put("identity", String.valueOf(icon_selected));
                    social.put("last_month", LM);
                    social.put("last_week", LW);
                    db.collection("Social").add(social);
                }
                Intent intent = new Intent(getApplicationContext(), Profile_main_page.class);
                intent.putExtra("user", current_user);
                startActivity(intent);
            }
        });

        final Context c = this.getBaseContext();
        final int[] children = new int[]{ 6,6,6,6,6,6,6,6,6,6,6,6,6 };
        final ArrayList<Integer> socials = new ArrayList<>(Arrays.asList(R.drawable.ic_facebook, R.drawable.ic_instagram, R.drawable.ic_twitter, R.drawable.ic_youtube,
                                                                        R.drawable.ic_github, R.drawable.ic_linkedin, R.drawable.ic_reddit, R.drawable.ic_snapchat,
                                                                        R.drawable.ic_twitch, R.drawable.ic_vimeo, R.drawable.ic_spotify, R.drawable.ic_tumblr,
                                                                        R.drawable.ic_vk, R.drawable.ic_internet));
        final CircularListView circularListView = findViewById(R.id.circle_socials);
        circularListView.setRadius(100);
        CircularItemAdapter adapter = new CircularItemAdapter(getLayoutInflater(), socials);
        circularListView.setAdapter(adapter);
        circularListView.setOnTouchListener(new CircularListView.OnTouchListener() {
            private float init_x = 0;
            private float init_y = 0;
            private float cur_x = 0;
            private float cur_y = 0;
            private float move_x = 0;
            private float move_y = 0;
            private boolean can_rotate = true;
            private boolean isCircularMoving = false; // ensure that item click only triggered when it's not moving
            @Override
            public boolean onTouch(final View v, MotionEvent event) {
                int max = getMaxValue(children);
                if(max == 6){
                    can_rotate = true;
                }
                float minClickDistance = 30.0f;
                float minMoveDistance = 30.0f;
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        cur_x = event.getX();
                        cur_y = event.getY();
                        init_x = event.getX();
                        init_y = event.getY();

                    case MotionEvent.ACTION_MOVE:
                        float pre_x = cur_x;
                        float pre_y = cur_y;
                        cur_x = event.getX();
                        cur_y = event.getY();

                        float diff_x = cur_x - pre_x;
                        float diff_y = cur_y - pre_y;
                        move_x = init_x - cur_x;
                        move_y = init_y - cur_y;
                        float moveDistance = (float) Math.sqrt(move_x * move_x + move_y * move_y);


                        if (cur_y >= ((CircularListView) v).layoutCenter_y) diff_x = -diff_x;
                        if (cur_x <= ((CircularListView) v).layoutCenter_x) diff_y = -diff_y;

                        // should rotate the layout
                        if (moveDistance > minMoveDistance && can_rotate) {
                            isCircularMoving = true;
                            // default is 2000, larger > faster
                            float mMovingSpeed = 2000.0f;
                            CircularListView.MoveAccumulator += (diff_x + diff_y) / mMovingSpeed;

                            // calculate new position around circle
                            for (int i = 0; i < ((CircularListView) v).itemViewList.size(); i++) {
                                final int idx = i;
                                final View itemView = ((CircularListView) v).itemViewList.get(i);
                                itemView.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams)
                                                itemView.getLayoutParams();
                                        params.setMargins(
                                                (int) (((CircularListView) v).layoutCenter_x - (((CircularListView) v).itemWith / 2) +
                                                        (((CircularListView) v).radius * Math.cos(idx * ((CircularListView) v).getIntervalAngle() +
                                                                CircularListView.MoveAccumulator * Math.PI * 2))),
                                                (int) (((CircularListView) v).layoutCenter_y - (((CircularListView) v).itemHeight / 2) +
                                                        (((CircularListView) v).radius * Math.sin(idx * ((CircularListView) v).getIntervalAngle() +
                                                                CircularListView.MoveAccumulator * Math.PI * 2))),
                                                0,
                                                0);
                                        itemView.setLayoutParams(params);
                                        itemView.requestLayout();
                                    }
                                });
                            }
                        }

                        return true;

                    case MotionEvent.ACTION_UP:

                        // it is an click action if move distance < min distance
                        moveDistance = (float) Math.sqrt(move_x * move_x + move_y * move_y);
                        if (moveDistance < minClickDistance && !isCircularMoving) {
                            for (int i = 0; i < ((CircularListView) v).itemViewList.size(); i++) {
                                View view = ((CircularListView) v).itemViewList.get(i);
                                if (isTouchInsideView(cur_x, cur_y, view)) {
                                    can_rotate = false;
                                    float curr_size = view.getScaleX();
                                    if (curr_size == (float) 1) {
                                        for (int j = 0; j < ((CircularListView) v).itemViewList.size(); j++) {
                                            View view1 = ((CircularListView) v).itemViewList.get(j);
                                            view1.setScaleX((float) 1);
                                            view1.setScaleY((float) 1);
                                        }
                                        view.setScaleX((float) 1.5);
                                        view.setScaleY((float) 1.5);
                                    }
                                    else {
                                        view.setScaleX((float) 1);
                                        view.setScaleY((float) 1);
                                    }
                                    social_selected = Text[i];
                                    icon_selected = socials.get(i);
                                    TextView socialChoosen = findViewById(R.id.socialChoosen);
                                    socialChoosen.setText(social_selected);
                                    break;
                                }
                            }
                        }
                        isCircularMoving = false; // reset moving state when event ACTION_UP
                        return true;
                }
                return false;
            }

            private boolean isTouchInsideView(float x, float y, View view){
                float left = view.getX();
                float top  = view.getY();
                float wid = view.getWidth();
                float h = view.getHeight();
                return (x > left && x < left + wid && y > top && y < top+h);
            }

        });

    }

    public static int getMaxValue(int[] numbers){
        int maxValue = numbers[0];
        for(int i=1;i < numbers.length;i++){
            if(numbers[i] > maxValue){
                maxValue = numbers[i];
            }
        }
        return maxValue;
    }
}
