package com.example.androidappnau;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


public class Randomfragment extends Fragment implements View.OnClickListener {

    private TextView tvRandom;
    private Button btnRandom;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       View rootView = inflater.inflate(R.layout.fragment_randomfragment, container, false);
        init(rootView);

        return rootView;
    }
    private void init(View rootView){
        btnRandom = rootView.findViewById(R.id.btn_random);
        btnRandom.setOnClickListener(this);
        tvRandom = rootView.findViewById(R.id.tv_random);
    }

    @Override
    public void onClick(View v) {
        if(v == btnRandom){
            int a;
            a = (int) (Math.random() * (100-1))+1;
            String b = String.valueOf(a);
            tvRandom.setText(b);
        }
    }
}