package com.example.pins.ui.logo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.ScaleAnimation;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.pins.R;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.project_search.ProjectSearchActivity;

public class LogoActivity extends AppCompatActivity {

    ImageView appIcon;
    ImageView appLogo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_logo);

        appIcon = findViewById(R.id.activity_logo_app_icon);
        appLogo = findViewById(R.id.activity_logo_app_logo);


        //********************************************************
        Animation logoAnimationEnlarge1 = new ScaleAnimation(
                0.0f, 1.8f, 0.0f, 1.8f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        logoAnimationEnlarge1.setStartOffset(500);
        logoAnimationEnlarge1.setDuration(500);
        logoAnimationEnlarge1.setFillAfter(true);
        //********************************************************


        //********************************************************
        Animation logoAnimationShrink1 = new ScaleAnimation(
                1.8f, 1.0f, 1.8f, 1.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        logoAnimationShrink1.setDuration(500);
        logoAnimationShrink1.setFillAfter(true);
        //********************************************************


        //********************************************************
        Animation logoAnimationEnlarge2 = new ScaleAnimation(
                1.0f, 1.5f, 1.0f, 1.5f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        logoAnimationEnlarge2.setStartOffset(500);
        logoAnimationEnlarge2.setDuration(500);
        logoAnimationEnlarge2.setFillAfter(true);
        //********************************************************


        //********************************************************
        Animation logoAnimationShrink2 = new ScaleAnimation(
                1.5f, 0.0f, 1.5f, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        logoAnimationShrink2.setDuration(200);
        logoAnimationShrink2.setFillAfter(true);
        //********************************************************


        //********************************************************
        Animation iconAnimationEnlarge = new ScaleAnimation(
                0.0f, 1.2f, 0.0f, 1.2f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        iconAnimationEnlarge.setDuration(200);
        iconAnimationEnlarge.setFillAfter(true);
        //********************************************************


        //********************************************************
        Animation iconAnimationCoverScreen = new ScaleAnimation(
                1.2f, 40f, 1.2f, 40f,
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f
        );
        iconAnimationCoverScreen.setStartOffset(1200);
        iconAnimationCoverScreen.setDuration(300);
        iconAnimationCoverScreen.setFillAfter(true);
        //********************************************************


        logoAnimationEnlarge1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                appIcon.setVisibility(View.GONE);
                appLogo.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                appLogo.setAnimation(logoAnimationShrink1);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        logoAnimationShrink1.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                appLogo.setAnimation(logoAnimationEnlarge2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        logoAnimationEnlarge2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                appLogo.setAnimation(logoAnimationShrink2);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        logoAnimationShrink2.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                appIcon.setVisibility(View.VISIBLE);
                appLogo.setVisibility(View.GONE);
                appIcon.setAnimation(iconAnimationEnlarge);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        iconAnimationEnlarge.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                appIcon.setAnimation(iconAnimationCoverScreen);
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        iconAnimationCoverScreen.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) { }

            @Override
            public void onAnimationEnd(Animation animation) {
                Intent intent = new Intent(getApplicationContext(), ProjectSearchActivity.class);
                startActivity(intent);
                finish();
            }

            @Override
            public void onAnimationRepeat(Animation animation) { }
        });

        appLogo.setAnimation(logoAnimationEnlarge1);

    }
}