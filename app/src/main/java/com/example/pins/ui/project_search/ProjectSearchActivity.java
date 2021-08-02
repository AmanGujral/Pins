package com.example.pins.ui.project_search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.ProjectAdapter;
import com.example.pins.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ProjectSearchActivity extends AppCompatActivity implements ProjectAdapter.ItemClickListener {

    ImageButton closeBtn;
    ImageButton searchBtn;
    ImageButton closeSearchBtn;
    EditText searchField;
    RecyclerView searchResultsRecyclerview;
    TextView errorMsg;
    RelativeLayout parentLayout;

    List<ProjectModel> allProjects = new ArrayList<>();
    List<ProjectModel> searchedProjects = new ArrayList<>();
    List<ProjectMemberModel> projectMembers = new ArrayList<>();
    List<ContactModel> contactList = new ArrayList<>();
    String query = "";

    ProjectAdapter adapter;

    FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();
    UserModel userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_project_search);

        closeBtn = findViewById(R.id.activity_project_search_close_btn);
        searchBtn = findViewById(R.id.activity_project_search_searchbar_search_btn);
        closeSearchBtn = findViewById(R.id.activity_project_search_searchbar_close_btn);
        searchField = findViewById(R.id.activity_project_search_searchbar_edittext);
        searchResultsRecyclerview = findViewById(R.id.activity_project_search_recyclerview);
        errorMsg = findViewById(R.id.activity_project_search_error_msg);
        parentLayout = findViewById(R.id.activity_project_search_parent_layout);

        userInstance = UserModel.getUserInstance();

        searchBtn.setVisibility(View.VISIBLE);
        closeSearchBtn.setVisibility(View.GONE);


        getAllProjects();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = searchField.getText().toString();
                if(!query.equalsIgnoreCase("")){
                    searchBtn.setVisibility(View.GONE);
                    closeSearchBtn.setVisibility(View.VISIBLE);
                    searchProjects(query);
                }
                else {
                    searchBtn.setVisibility(View.VISIBLE);
                    closeSearchBtn.setVisibility(View.GONE);
                    getAllProjects();
                }
                Log.e("QUERY: ", query);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!query.equalsIgnoreCase("")) {
                    searchBtn.setVisibility(View.GONE);
                    closeSearchBtn.setVisibility(View.VISIBLE);
                    searchProjects(query);
                }
                else {
                    Snackbar.make(parentLayout, "Enter a valid Project Code.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.green_dark))
                            .show();
                }
            }
        });

        closeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchField.setText("");
            }
        });

        // Close Button Click Listener
        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }

    void getAllProjects() {
        allProjects.clear();
        errorMsg.setVisibility(View.GONE);
        searchResultsRecyclerview.setVisibility(View.VISIBLE);
        firestoreInstance.collection("Projects")
                .orderBy("projectCode")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    allProjects.add(doc.toObject(ProjectModel.class));
                                }
                                if(allProjects.size() != 0) {
                                    searchResultsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    adapter = new ProjectAdapter(
                                            getApplicationContext(),
                                            allProjects,
                                            ProjectSearchActivity.this);
                                    searchResultsRecyclerview.setAdapter(adapter);
                                }
                                else {
                                    Log.e("NUM OF PROJECTS: ", String.valueOf(allProjects.size()));
                                    errorMsg.setVisibility(View.VISIBLE);
                                    searchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                Log.e("TASK FAILED 83: ", task.getException().getMessage());
                                errorMsg.setVisibility(View.VISIBLE);
                                searchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            errorMsg.setVisibility(View.VISIBLE);
                            searchResultsRecyclerview.setVisibility(View.GONE);
                            Log.e("TASK FAILED 91: ", task.getException().getMessage());
                        }
                    }
                });
    }

    void searchProjects(String projectCode) {
        searchedProjects.clear();
        errorMsg.setVisibility(View.GONE);
        searchResultsRecyclerview.setVisibility(View.VISIBLE);
        firestoreInstance.collection("Projects")
                .whereGreaterThanOrEqualTo("projectCode", projectCode.toUpperCase())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful()){
                            if(task.getResult() != null) {
                                for (QueryDocumentSnapshot doc : task.getResult()) {
                                    searchedProjects.add(doc.toObject(ProjectModel.class));
                                }
                                if(searchedProjects.size() != 0) {
                                    searchResultsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    adapter = new ProjectAdapter(
                                            getApplicationContext(),
                                            searchedProjects,
                                            ProjectSearchActivity.this);
                                    searchResultsRecyclerview.setAdapter(adapter);
                                }
                                else {
                                    Log.e("NUM OF PROJECTS: ", String.valueOf(searchedProjects.size()));
                                    errorMsg.setVisibility(View.VISIBLE);
                                    searchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                Log.e("TASK FAILED 83: ", task.getException().getMessage());
                                errorMsg.setVisibility(View.VISIBLE);
                                searchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            errorMsg.setVisibility(View.VISIBLE);
                            searchResultsRecyclerview.setVisibility(View.GONE);
                            Log.e("TASK FAILED 91: ", task.getException().getMessage());
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        if(query.equalsIgnoreCase("")) {
            showDialog(allProjects.get(position));
        }
        else {
            showDialog(searchedProjects.get(position));
        }
    }

    public void showDialog(ProjectModel project) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectSearchActivity.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_project, null);
        alertDialogBuilder.setView(dialogView);

        TextView projectCode = dialogView.findViewById(R.id.alert_dialog_add_project_project_code_tv);
        TextView projectName = dialogView.findViewById(R.id.alert_dialog_add_project_project_name_tv);
        TextView projectManager = dialogView.findViewById(R.id.alert_dialog_add_project_project_manager_tv);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_add_project_no_btn);
        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_add_project_yes_btn);

        projectCode.setText(project.getProjectCode());
        projectName.setText(project.getProjectName());
        projectManager.setText(project.getManagerName());

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addProject(project);

                alertDialog.dismiss();
                Toast toast=Toast.makeText(getApplicationContext(),"Request Sent to Manager",Toast.LENGTH_SHORT);
                toast.show();

                Map<String, String> usermap= new HashMap<>();
                usermap.put("projectId",project.getProjectId());
                usermap.put("projectName",project.getProjectName());
                usermap.put("memberid", userInstance.getUserid());

                firestoreInstance.collection("Projects").
                        document(project.getProjectId()).
                        collection("UserRequests").add(usermap);


            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void addProject(ProjectModel project) {
        List<String> allProjects = new ArrayList<>();
        if (userInstance.getAllProjects() != null) {
            allProjects = userInstance.getAllProjects();
            if(allProjects.contains(project.getProjectId())) {
                Snackbar.make(parentLayout, "You are already added to this project.", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.green_dark))
                        .show();
                return;
            }
        }
        allProjects.add(project.getProjectId());

        // Update current user info such as currentProjectId
        userInstance.setAllProjects(allProjects);
        userInstance.setCurrentProjectId(project.getProjectId());

        // Create Project member model object for current user
        ProjectMemberModel projectMember = new ProjectMemberModel(
                userInstance.getUserid(),
                userInstance.getFirstname(),
                userInstance.getLastname(),
                userInstance.getEmail(),
                userInstance.getRole(),
                userInstance.getImageUrl()
        );

        // Update Current User Data in DB
        firestoreInstance.collection("Users")
                .document(userInstance.getUserid())
                .set(userInstance, SetOptions.merge());

        // Get all project members and add to contactList + Add current user as a contact to all other project members
        firestoreInstance.collection("Projects")
                .document(project.getProjectId())
                .collection("Project Members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            ContactModel currentUserContact = new ContactModel(
                                    userInstance.getUserid(),
                                    userInstance.getFirstname(),
                                    userInstance.getLastname(),
                                    userInstance.getImageUrl(),
                                    "",
                                    "",
                                    0L,
                                    true
                            );
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                projectMembers.add(doc.toObject(ProjectMemberModel.class));
                            }
                            for(ProjectMemberModel projectMemberModel : projectMembers) {
                                firestoreInstance.collection("Projects")
                                        .document(project.getProjectId())
                                        .collection("Project Members")
                                        .document(projectMemberModel.getUserid())
                                        .collection("Contacts")
                                        .document(userInstance.getUserid())
                                        .set(currentUserContact, SetOptions.merge());
                                ContactModel contact = new ContactModel(
                                        projectMemberModel.getUserid(),
                                        projectMemberModel.getFirstname(),
                                        projectMemberModel.getLastname(),
                                        projectMemberModel.getImageUrl(),
                                        "",
                                        "",
                                        0L,
                                        true
                                );
                                contactList.add(contact);
                            }
                        }
                    }
                });

        // Add the user to Project Members + Add all other project members as Contacts to the current user
        firestoreInstance.collection("Projects")
                .document(project.getProjectId())
                .collection("Project Members")
                .document(userInstance.getUserid())
                .set(projectMember, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            for(ContactModel contact : contactList) {
                                firestoreInstance.collection("Projects")
                                        .document(project.getProjectId())
                                        .collection("Project Members")
                                        .document(userInstance.getUserid())
                                        .collection("Contacts")
                                        .document(contact.getUserid())
                                        .set(contact, SetOptions.merge());
                            }
                            startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                            finish();
                        }
                    }
                });
    }
}