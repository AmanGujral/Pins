package com.example.pins.ui.onboarding;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.example.pins.R;
import com.example.pins.models.UserModel;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.project_search.ProjectSearchActivity;

public class OnboardingActivity extends AppCompatActivity {

    TextView username;
    CardView continueBtn;
    TextView skipBtn;

    UserModel userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);

        username = findViewById(R.id.activity_onboarding_username_tv);
        continueBtn = findViewById(R.id.activity_onboarding_continue_btn);
        skipBtn = findViewById(R.id.activity_onboarding_skip_btn);

        userInstance = UserModel.getUserInstance();

        if(!userInstance.getFirstname().isEmpty() && !userInstance.getLastname().isEmpty()) {
            String fullName = userInstance.getFirstname() + " " + userInstance.getLastname();
            username.setText(fullName);
        }

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProjectSearchActivity.class));
                finish();
            }
        });

        skipBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
    }
}