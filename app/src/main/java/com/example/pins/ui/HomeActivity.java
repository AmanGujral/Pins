package com.example.pins.ui;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.databinding.ActivityHomeBinding;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.SidePanelProjectAdapter;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements SidePanelProjectAdapter.ItemClickListener{

    private ActivityHomeBinding binding;
    public static final String USERS_COLLECTION_PATH = "users";
    public static final String CHAT_COLLECTION_PATH = "chatroom";
    public static final String MESSAGES_COLLECTION_PATH = "messages";
    public static final String PROJECTS_COLLECTION_PATH="Projects";
    public static final String LAST_MSG_PATH = "lastMsg";

    ImageButton hidePanelBtn;
    ImageButton showPanelBtn;
    ImageButton logoutBtn;
    ImageButton addProjectBtn;
    RecyclerView sidePanelRecyclerview;
    RelativeLayout sidePanel;

    UserModel userInstance = UserModel.getUserInstance();
    List<ProjectModel> userProjects = new ArrayList<>();

    SidePanelProjectAdapter adapter;

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
        sidePanelRecyclerview = binding.sidePanelProjectList;
        addProjectBtn = binding.sidePanelAddProjectBtn;

        getUserProjects();

        sidePanel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(sidePanel.getVisibility() == View.VISIBLE)
                    sidePanel.setVisibility(View.GONE);
            }
        });

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

    public void getUserProjects() {
        if(userInstance.getAllProjects() != null && userInstance.getAllProjects().size() != 0) {
            Log.e("PROJECTS", String.valueOf(userInstance.getAllProjects().size()));
            List<String> projectIds = userInstance.getAllProjects();

            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .whereIn("projectId", projectIds)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null) {
                                for(QueryDocumentSnapshot doc : task.getResult()) {
                                    userProjects.add(doc.toObject(ProjectModel.class));
                                }

                                sidePanelRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                adapter = new SidePanelProjectAdapter(
                                        getApplicationContext(),
                                        userProjects,
                                        HomeActivity.this);
                                sidePanelRecyclerview.setAdapter(adapter);
                            }
                        }
                    });

        }
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.e("Item Clicked", userProjects.get(position).getProjectName());
        loadProject(userProjects.get(position));
    }

    public void loadProject(ProjectModel project) {
        if(userInstance.getCurrentProjectId().equals(project.getProjectId())) {
            sidePanel.setVisibility(View.GONE);
        }
        else {
            userInstance.setCurrentProjectId(project.getProjectId());

            FirebaseFirestore.getInstance()
                    .collection("Users")
                    .document(userInstance.getUserid())
                    .set(userInstance, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                            if (task.isSuccessful()) {
                                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            }
                        }
                    });
        }
    }
}