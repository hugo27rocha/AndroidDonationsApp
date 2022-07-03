package com.example.cm07project;

import android.content.Context;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.ProgressBar;
import android.widget.TextView;

public class ProgressBarAnimation extends Animation {
    private MainActivity mActivity;
    private ProgressBar progressBar;
    private TextView textView;
    private float from;
    private float to;

    public ProgressBarAnimation(MainActivity mActivity, ProgressBar progressbar,
                                TextView textView,float from, float to){
        this.mActivity = mActivity;
        this.progressBar = progressbar;
        this.textView = textView;
        this.from = from;
        this.to = to;
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        super.applyTransformation(interpolatedTime, t);
        float value = from + (to - from) * interpolatedTime;
        int valueInt = (int) value;
        progressBar.setProgress(valueInt);
        textView.setText(valueInt + " %");

        if(value == to){
            this.mActivity.appInit();
        }
    }
}
