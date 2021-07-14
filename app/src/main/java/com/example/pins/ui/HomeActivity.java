package com.example.pins.ui;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import com.example.pins.R;
import com.example.pins.databinding.ActivityHomeBinding;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;

public class HomeActivity extends AppCompatActivity {

    private ActivityHomeBinding binding;

    ImageButton hidePanelBtn;
    ImageButton showPanelBtn;
    ImageButton logoutBtn;
    ImageButton addProjectBtn;
    RelativeLayout sidePanel;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityHomeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //BottomNavigationView navView = findViewById(R.id.nav_view);
        AppBarConfiguration appBarConfiguration = new AppBarConfiguration.Builder(
                R.id.navigation_myboard, R.id.navigation_teamboard, R.id.navigation_profile)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_activity_home);
        //NavigationUI.setupActionBarWithNavController(this, navController, appBarConfiguration);
        NavigationUI.setupWithNavController(binding.navView, navController);


        hidePanelBtn = binding.hidePanelBtn;
        showPanelBtn = binding.showPanelBtn;
        logoutBtn = binding.sidePanelLogoutBtn;
        sidePanel = binding.sidePanelLayout;
        addProjectBtn = binding.sidePanelAddProjectBtn;

        showPanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sidePanel.setVisibility(View.VISIBLE);
            }
        });

        hidePanelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sidePanel.setVisibility(View.GONE);
            }
        });

        addProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), ProjectSearchActivity.class));
            }
        });

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getApplicationContext(), SignInActivity.class));
            }
        });
    }

}