package com.smartcity.engine.manager;

import android.animation.ArgbEvaluator;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.*;
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

        TextView startButton = (TextView) Manager.activity().findViewById(R.id.welcomeButton);
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

        TextView sendButton = (TextView) Manager.activity().findViewById(R.id.sendButton);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ProgressDialog p = new ProgressDialog(Manager.activity());
                p.setTitle(R.string.dialog_title);
                p.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
                p.setCancelable(false);
                p.show();

                EditText comment = (EditText) Manager.activity().findViewById(R.id.commentEditText);
                Spinner spinner = (Spinner) Manager.activity().findViewById(R.id.types_spinner);

                Manager.network().sendComment(comment.getText().toString(), Manager.location().getLastLocation(), Manager.location().getLastLocationAddress(), spinner.getSelectedItem().toString(), p);
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

        Spinner spinner = (Spinner) Manager.activity().findViewById(R.id.types_spinner);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(Manager.activity(), R.array.types_array, R.layout.type_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);
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

    public void promptServer() {
        AlertDialog.Builder alert = new AlertDialog.Builder(Manager.activity());
        alert.setTitle("Server");
        alert.setMessage("Server address :");

        final EditText input = new EditText(Manager.activity());
        input.setText("http://192.168.1.152:5001");
        alert.setView(input);

        alert.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                String value = input.getText().toString();

                Manager.network().setServer(value);
            }
        });

        alert.setNegativeButton("Default", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                Manager.network().setServer("http://192.168.1.152:5001");
            }
        });

        alert.setCancelable(false);

        alert.show();
    }
}
