package com.example.huc_project;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.huc_project.homepage.PostRow;

import java.util.ArrayList;

public class Profile_photo_frag extends Fragment {

    public Profile_photo_frag() {
        // Required empty public constructor
    }

    ArrayList<PostRow> rowsPostList = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.frag_photo, container, false);
    }
}
