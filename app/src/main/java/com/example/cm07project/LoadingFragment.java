package com.example.cm07project;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ProgressBar;
import android.widget.TextView;

public class LoadingFragment extends Fragment {

    ProgressBar progressBar;
    TextView textView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_loading, container, false);

//        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
//                WindowManager.LayoutParams.FLAG_FULLSCREEN);

        this.progressBar = view.findViewById(R.id.progress_bar);
        this.textView = view.findViewById(R.id.text_view);


        this.progressBar.setMax(100);
        this.progressBar.setScaleY(3f);

        progresAnimation();
        return view;
    }

    public void progresAnimation(){
        MainActivity mainActivity = (MainActivity) getActivity();
        ProgressBarAnimation amin = new ProgressBarAnimation(mainActivity,
                progressBar,textView,0, 100f);
        amin.setDuration(5000);
        progressBar.setAnimation(amin);
    }
}