package com.smartcity.engine.manager;

import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;
import com.smartcity.R;

/**
 * Created by Pierre-Olivier on 12/10/2014.
 */
public class ViewManager {
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
            }
        });
    }

    public void welcomeMessageView() {
        hideTitleOverlay();
        showWelcomeOverlay();
    }

    public void takePictureView() {
        hideWelcomeOverlay();
        showTitleOverlay(R.string.title_picture);
    }

    public void commentView() {
        hideWelcomeOverlay();
        showTitleOverlay(R.string.title_comment);
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
}
