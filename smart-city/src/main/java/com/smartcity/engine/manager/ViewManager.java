package com.smartcity.engine.manager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.TransitionDrawable;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.smartcity.R;

/**
 * Created by Pierre-Olivier on 12/10/2014.
 */
public class ViewManager {
    public enum Views {WELCOME, PICTURE, COMMENT}

    private Views mCurrentView;

    private boolean mTakePictureDisable;

    protected ViewManager() {
        super();
    }

    public void init() {
        welcomeMessageView();

        Button startButton = (Button) Manager.activity().findViewById(R.id.welcomeButton);
        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                takePictureView();

                Manager.location().startLocationUpdate();
            }
        });

        ImageView takePictureButton = (ImageView) Manager.activity().findViewById(R.id.takePictureButton);
        takePictureButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!mTakePictureDisable) {
                    Manager.activity().onTakePicture();

                    // commentView();

                    mTakePictureDisable = true;
                }
            }
        });
    }

    public void welcomeMessageView() {
        hideTitleOverlay();
        hideTakePictureOverlay();
        hideCommentOverlay();
        showWelcomeOverlay();

        mCurrentView = Views.WELCOME;
    }

    public void takePictureView() {
        Manager.activity().reloadCamera();

        hideWelcomeOverlay();
        hideCommentOverlay();
        showTitleOverlay(R.string.title_picture);
        showTakePictureOverlay();

        mCurrentView = Views.PICTURE;
        mTakePictureDisable = false;
    }

    public void commentView() {
        hideWelcomeOverlay();
        hideTakePictureOverlay();
        showTitleOverlay(R.string.title_comment);
        showCommentOverlay();

        mCurrentView = Views.COMMENT;
    }

    public void showWelcomeOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.welcomeOverlay);
        welcomeOverlay.setVisibility(View.VISIBLE);
    }

    public void hideWelcomeOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.welcomeOverlay);
        welcomeOverlay.setVisibility(View.INVISIBLE);
    }

    public void showTitleOverlay(int resId) {
        RelativeLayout titleOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.titleOverlay);
        titleOverlay.setVisibility(View.VISIBLE);

        TextView titleTextView = (TextView) Manager.activity().findViewById(R.id.titleOverlayTextView);
        titleTextView.setText(resId);
    }

    public void hideTitleOverlay() {
        RelativeLayout titleOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.titleOverlay);
        titleOverlay.setVisibility(View.INVISIBLE);
    }

    public void showTakePictureOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.takePictureOverlay);
        welcomeOverlay.setVisibility(View.VISIBLE);
    }

    public void hideTakePictureOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.takePictureOverlay);
        welcomeOverlay.setVisibility(View.INVISIBLE);
    }

    public void flashLayout() {
        RelativeLayout flashLayout = (RelativeLayout) Manager.activity().findViewById(R.id.flashAnimationLayout);

        if(android.os.Build.VERSION.SDK_INT >= 11) {
            int start = Color.argb(200, 255, 255, 255);
            int end = Color.argb(0, 255, 255, 255);
            ValueAnimator valueAnimator = ObjectAnimator.ofInt(flashLayout, "backgroundColor", start, end);
            valueAnimator.setDuration(1300);
            valueAnimator.setEvaluator(new ArgbEvaluator());
            valueAnimator.start();
        }
    }

    public void showCommentOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.commentOverlay);
        welcomeOverlay.setVisibility(View.VISIBLE);
    }

    public void hideCommentOverlay() {
        RelativeLayout welcomeOverlay = (RelativeLayout) Manager.activity().findViewById(R.id.commentOverlay);
        welcomeOverlay.setVisibility(View.INVISIBLE);

        ImageView view = (ImageView) Manager.activity().findViewById(R.id.cameraImage);
        if(android.os.Build.VERSION.SDK_INT >= 11) {
            view.setAlpha(0.0f);
        }
    }

    public void setPositionTextView(String address) {
        TextView textView = (TextView) Manager.activity().findViewById(R.id.positionTextView);
        textView.setText(address);
    }

    public boolean back() {
        if (mCurrentView == Views.COMMENT) {
            takePictureView();
            return true;
        } else if (mCurrentView == Views.PICTURE) {
            return false;
        } else {
            return false;
        }
    }
}
