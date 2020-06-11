package com.example.huc_project.profile;

import android.content.Context;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.example.huc_project.R;
import com.example.huc_project.homepage.DataGettingActivity;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ImageAdapter extends BaseAdapter {

    private Context mContext;
    public ArrayList<String> imgArray;
    private LayoutInflater inflater;

    FirebaseStorage storage = FirebaseStorage.getInstance();
    final FirebaseUser current_user = FirebaseAuth.getInstance().getCurrentUser();

    /*public ArrayList<Uri> imgArray = {
            R.drawable.ic_travel, R.drawable.ic_travel, R.drawable.ic_add, R.drawable.ic_ads, R.drawable.ic_chat, R.drawable.add_img
    };*/

    public ImageAdapter(Context mContext, ArrayList<String> imgArray) {
        this.mContext = mContext;
        this.imgArray = imgArray;
        inflater = LayoutInflater.from(mContext);
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
        if (convertView ==  null) {
            convertView = inflater.inflate(R.layout.image_list, parent, false);
        }
        ImageView imageView = (ImageView) convertView;



        String uri = imgArray.get(position);
        StorageReference storageRef = storage.getReference();
        StorageReference islandRef = (StorageReference) storageRef.child("images/" + uri);

        Glide.with(mContext)
                .load(islandRef)
                .into(imageView);



        return convertView;
    }

}
