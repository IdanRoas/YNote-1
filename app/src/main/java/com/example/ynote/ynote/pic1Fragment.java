package com.example.ynote.ynote;


import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;


public class pic1Fragment extends Fragment {
    private String url;
   



   

    public static pic1Fragment newInstance( String url) {
        pic1Fragment pic1Fragment = new pic1Fragment();
        Bundle args = new Bundle();
        args.putString("url", url);
        pic1Fragment.setArguments(args);
        return pic1Fragment;

    }
    @Override
    public void onCreate(Bundle savedInstanceState)throws NullPointerException {
        super.onCreate(savedInstanceState);
        url = getArguments().getString("url");

    }



    public pic1Fragment() {}// Required empty public constructor

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


       View view = inflater.inflate(R.layout.fragment_pic1, container, false);

        ImageView imageView= view.findViewById(R.id.image1);
        if(url!="null") {
            Glide.with(view)
                    .load(Uri.parse(url)).thumbnail()
                    .into(imageView);
            }

        return view;
    }
}
