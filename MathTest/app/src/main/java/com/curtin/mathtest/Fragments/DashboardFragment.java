package com.curtin.mathtest.Fragments;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.curtin.mathtest.R;

public class DashboardFragment extends Fragment {
    private ImageView profileImage;
    private TextView userInfo;

    private Bitmap image;
    private String fullname;
    public DashboardFragment(Bitmap profileImage, String fullname) {
        // Required empty public constructor
        this.image = profileImage;
        this.fullname = fullname;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_dashboard, container, false);
        profileImage = (ImageView) view.findViewById(R.id.profileImage);
        userInfo = (TextView) view.findViewById(R.id.fullNameTextView);
        if(image != null) {
            profileImage.setImageBitmap(image);
        }
        userInfo.setText(fullname);

        return view;
    }



}