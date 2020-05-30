package com.example.huc_project.profile;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<Uri> imgArray;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

    /*public ArrayList<Uri> imgArray = {
            R.drawable.ic_travel, R.drawable.ic_travel, R.drawable.ic_add, R.drawable.ic_ads, R.drawable.ic_chat, R.drawable.add_img
    };*/

    public ImageAdapter(Context mContext, ArrayList<Uri> imgArray) {
        this.mContext = mContext;
        this.imgArray = imgArray;
    }

    @Override
    public int getCount() { return imgArray.size(); }

    @Override
    public Object getItem(int position) { return imgArray.get(position); }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final ImageView imageView = new ImageView(mContext);

        StorageReference ref = storage.getReference().child(current_user.getUid() + "/" + "ads.svg");

        Uri uri = imgArray.get(position);
        Log.d("lola", String.valueOf(uri));
                //imageView.setImageURI(uri);
                //Glide.with(mContext).load(uri).into(imageView);

        ref.getDownloadUrl().addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Log.d("lola", "mi sa che ho sbagliato");
            }
        });

        //imageView.setImageResource(imgArray[position]);
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
        imageView.setLayoutParams(new GridView.LayoutParams(340,350));

        return imageView;
    }
}
