package com.example.huc_project.posts;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextMenu;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toolbar;

import com.bumptech.glide.Glide;
import com.example.huc_project.R;
import com.example.huc_project.homepage.Post;
import com.example.huc_project.ui.login.CircularItemAdapter;
import com.example.huc_project.ui.login.PaintText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.jh.circularlist.CircularListView;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class edit_post extends AppCompatActivity {

    Post post;
    ImageView post_image_view;
    final int PICK_IMAGE= 100;
    Uri imageUri;
    TextView title_view;
    TextView desc_view;
    Dialog popChooseCategories ;
    TextView categories_selected;
    private HashMap<String,Object> interests_selected = new HashMap<>();
    private String[] Text = {"Sport", "Fashion", "Food", "Movies", "Music", "Science & IT", "Nature" };

    FirebaseStorage storage = FirebaseStorage.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth mAuth = FirebaseAuth.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_new_post);

        Intent intent = getIntent();
        this.post = new Post(intent.getStringExtra("title"), intent.getStringExtra("storageref"),
                intent.getStringExtra("desc"), intent.getStringExtra("user"), intent.getBooleanExtra("isPackage", false), intent.getStringArrayListExtra("categories"),
                intent.getStringExtra("role"), intent.getStringExtra("country"), intent.getStringExtra("city"));

        Button post_button = findViewById(R.id.postBtn);
        post_button.setText("Save");
        post_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //TODO: se dalla homepage salviamo id dei document possiamo passarli avanti
                // cosi quì possiamo fare db.blabla.set() con le merge options
            }
        });

        this.post_image_view = findViewById(R.id.imageBtn);
        if(post.getStorageref() != null){
            StorageReference storageRef = storage.getReference();
            StorageReference islandRef = storageRef.child("images/" + post.getStorageref());
            Glide.with(this).load(islandRef).into(post_image_view);
        }
        post_image_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openGallery();
            }
        });
        this.title_view = findViewById(R.id.textTitle);
        title_view.setText(post.getTitle());
        this.desc_view = findViewById(R.id.textDesc);
        desc_view.setText(post.getPostdesc());
        ( (AutoCompleteTextView) findViewById(R.id.countries_list) ).setText(post.getCountry());
        ( (AutoCompleteTextView) findViewById(R.id.cities_list) ).setText(post.getCity());
        ( (CheckBox) findViewById(R.id.checkpackage)).setChecked(post.getIsPackage());
        if(post.getRole().equals("sponsor")) {
            ( (CheckBox) findViewById(R.id.sponsor)).setChecked(true);
            ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(false);
        } else {
            ( (CheckBox) findViewById(R.id.sponsor)).setChecked(false);
            ( (CheckBox) findViewById(R.id.sponsorship)).setChecked(true);
        }

        ArrayList<String> categories = post.getCategories();
        for(int u = 0 ; u < categories.size(); u++){
            interests_selected.put(categories.get(u),true);
            Log.d("CAT", categories.get(u));
        }
        iniPopup();
        categories_selected = findViewById(R.id.categories_selected);
        TextView choose = findViewById(R.id.choose);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popChooseCategories.show();
            }
        });
        setCategories();

    }

    private void openGallery() {
        Intent gallery = new Intent();//(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
        gallery.setAction(Intent.ACTION_GET_CONTENT);
        gallery.setType("image/*");
        startActivityForResult(gallery, PICK_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == -1 && requestCode == PICK_IMAGE && data!=null) {
            imageUri = data.getData();
            CropImage.activity(imageUri)
                    .setGuidelines(CropImageView.Guidelines.ON)
                    .setAspectRatio(1,1)
                    .setRequestedSize(500,500, CropImageView.RequestSizeOptions.RESIZE_EXACT)
                    .start(this);

        }

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);

            if(resultCode == -1){
                imageUri = result.getUri();
                this.post_image_view.setImageURI(imageUri);
            }
        }
    }


    @SuppressLint("ClickableViewAccessibility")
    private void iniPopup() {

        popChooseCategories = new Dialog(this);
        popChooseCategories.setContentView(R.layout.new_post_choose_categories);
        popChooseCategories.getWindow().setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.backgroundColor)));
        popChooseCategories.getWindow().setLayout(Toolbar.LayoutParams.MATCH_PARENT, Toolbar.LayoutParams.MATCH_PARENT);
        popChooseCategories.getWindow().getAttributes().gravity = Gravity.TOP;


        popChooseCategories.setOnKeyListener(new Dialog.OnKeyListener() {

            @Override
            public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    //doNothing
                }
                return true;
            }});

        final Context c = this.getBaseContext();
        final int[] children = new int[]{ 6,6,6,6,6,6,6 };
        final ArrayList<Integer> interests = new ArrayList<>(Arrays.asList(R.drawable.ic_sport , R.drawable.ic_fashion, R.drawable.ic_food, R.drawable.ic_movie, R.drawable.ic_music, R.drawable.ic_technology, R.drawable.ic_nature));
        final CircularListView circularListView = popChooseCategories.findViewById(R.id.circle_interests_new_post);
        final Button ok_categories = popChooseCategories.findViewById(R.id.ok_categories);
        circularListView.setRadius(80);
        CircularItemAdapter adapter = new CircularItemAdapter(getLayoutInflater(), interests);
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
                                    max = getMaxValue(children);
                                    if(curr_size == (float) 1) {
                                        view.setScaleX((float) 1.5);
                                        view.setScaleY((float) 1.5);
                                        circularListView.addView(new PaintText( c , i,
                                                view.getLeft()-60, view.getTop()-60,view.getRight()+60, view.getBottom()+60,
                                                -145,135) );
                                        children[i] = max + 1;
                                        interests_selected.put(Text[i], true);
                                    } else {
                                        view.setScaleX((float) 1);
                                        view.setScaleY((float) 1);
                                        circularListView.removeViewAt(children[i]);
                                        for (int elem = 0; elem < children.length; elem++) {
                                            if (children[elem] > children[i]) {
                                                children[elem] -= 1;
                                            }
                                        }
                                        children[i] = 6;
                                        interests_selected.remove(Text[i]);
                                        interests_selected.put(Text[i], false);
                                    }
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

        ok_categories.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popChooseCategories.cancel();
                setCategories();
            }
        });
    }


    private void setCategories(){
        String cat= new String();
        for (Map.Entry<String, Object> entry : interests_selected.entrySet()) {
            String k = entry.getKey();
            boolean value = (boolean) entry.getValue();
            if(value){

                cat+="#"+k;
                cat+=" ";
            }
        }

        if(cat.length()>0){
            cat =  cat.substring(0, cat.length() - 1);
            categories_selected.setText(cat);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) categories_selected.getLayoutParams();
            params.height = FrameLayout.LayoutParams.WRAP_CONTENT;
            categories_selected.setLayoutParams(params);
            Log.d("TAAC", String.valueOf(interests_selected.containsKey("Fashion")));
            Log.d("TAAC", String.valueOf(interests_selected));
        }else {
            int margin = getResources().getDimensionPixelSize(R.dimen._1sdp);
            FrameLayout.LayoutParams params = (FrameLayout.LayoutParams) categories_selected.getLayoutParams();
            params.height = margin;
            categories_selected.setLayoutParams(params);
            categories_selected.setText("");
        }
        //Toast.makeText(CreateNewPostActivity.this, "Chosen " + cat, Toast.LENGTH_SHORT).show();
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