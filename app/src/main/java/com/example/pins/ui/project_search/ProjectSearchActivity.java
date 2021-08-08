package com.example.pins.ui.project_search;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.AlertDialog;
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

import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.ProjectAdapter;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.sign_up.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

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

    Boolean isManagerSignup = false;

    public static final String KEY_ROLE = "ROLE";
    public static final String KEY_PROJECT_ID = "PROJECT_ID";

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

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(SignUpActivity.KEY_MANAGER_SIGNUP)) {
            isManagerSignup = intentThatStartedThisActivity.getBooleanExtra(SignUpActivity.KEY_MANAGER_SIGNUP, false);
        }

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
                    showSnackBar("Enter a valid Project Code.");
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
                if (!isManagerSignup) {
                    Intent intent = new Intent(getApplicationContext(), HomeActivity.class);
                    startActivity(intent);
                }
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
                                    errorMsg.setVisibility(View.VISIBLE);
                                    searchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                errorMsg.setVisibility(View.VISIBLE);
                                searchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            errorMsg.setVisibility(View.VISIBLE);
                            searchResultsRecyclerview.setVisibility(View.GONE);
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
                                    errorMsg.setVisibility(View.VISIBLE);
                                    searchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                errorMsg.setVisibility(View.VISIBLE);
                                searchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            errorMsg.setVisibility(View.VISIBLE);
                            searchResultsRecyclerview.setVisibility(View.GONE);
                        }
                    }
                });
    }

    @Override
    public void onProjectAdapterItemClick(View view, int position) {
        if(isManagerSignup) {
            if(query.equalsIgnoreCase("")) {
                if(allProjects.get(position).getManagerName().isEmpty()) {
                    showJoinProjectDialogBox(allProjects.get(position));
                }
                else {
                    showManagerAlreadyExistsDialogBox();
                }
            }
            else {
                if(searchedProjects.get(position).getManagerName().isEmpty()) {
                    showJoinProjectDialogBox(searchedProjects.get(position));
                }
                else {
                    showManagerAlreadyExistsDialogBox();
                }
            }
        }
        else {
            if(query.equalsIgnoreCase("")) {
                showJoinProjectDialogBox(allProjects.get(position));
            }
            else {
                showJoinProjectDialogBox(searchedProjects.get(position));
            }
        }
    }

    public void showJoinProjectDialogBox(ProjectModel project) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectSearchActivity.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_add_project, null);
        alertDialogBuilder.setView(dialogView);

        TextView projectCode = dialogView.findViewById(R.id.alert_dialog_add_project_project_code_tv);
        TextView projectName = dialogView.findViewById(R.id.alert_dialog_add_project_project_name_tv);
        TextView projectManager = dialogView.findViewById(R.id.alert_dialog_add_project_project_manager_tv);
        SwitchMaterial managerSwitch = dialogView.findViewById(R.id.alert_dialog_add_project_manager_switch);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_add_project_no_btn);
        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_add_project_yes_btn);

        projectCode.setText(project.getProjectCode());
        projectName.setText(project.getProjectName());
        projectManager.setText(project.getManagerName());

        if(isManagerSignup) {
            managerSwitch.setChecked(true);
            managerSwitch.setClickable(false);
        }
        else {
            managerSwitch.setChecked(false);
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //addProject(project);
                if(isManagerSignup) {
                    showAuthorizationCodeDialogBox(project);
                }
                else {
                    if(managerSwitch.isChecked()) {
                        if(project.getManagerName().isEmpty()) {
                            showAuthorizationCodeDialogBox(project);
                            alertDialog.dismiss();
                        }
                        else {
                            showManagerAlreadyExistsDialogBox();
                        }
                    }
                    else {
                        sendProjectJoinRequest(project);
                        alertDialog.dismiss();
                    }
                }
            }
        });

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void showAuthorizationCodeDialogBox(ProjectModel project) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectSearchActivity.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_authorization, null);
        alertDialogBuilder.setView(dialogView);

        EditText authCode = dialogView.findViewById(R.id.alert_dialog_authorization_code_edittext);
        Button submitBtn = dialogView.findViewById(R.id.alert_dialog_authorization_submit_btn);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        firestoreInstance.collection("AuthCodes")
                .document(project.getProjectCode())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            submitBtn.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    if(authCode.getText().toString().isEmpty()) {
                                        showSnackBar("Enter a valid Authorization Code!");
                                    }
                                    if(!authCode.getText().toString().equalsIgnoreCase(task.getResult().getString("code"))) {
                                        showSnackBar("Authorization Code Invalid!");
                                    }
                                    else {
                                        if(isManagerSignup) {
                                            showAuthorizationSuccessfulDialogBox(project);
                                        }
                                        else {
                                            joinProjectAsManager(project);
                                        }
                                        alertDialog.dismiss();
                                    }
                                }
                            });
                        }
                    }
                });
    }

    public void showManagerAlreadyExistsDialogBox() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectSearchActivity.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_manager_already_exists, null);
        alertDialogBuilder.setView(dialogView);

        Button closeBtn = dialogView.findViewById(R.id.alert_dialog_manager_already_exists_close_btn);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    public void showAuthorizationSuccessfulDialogBox(ProjectModel project) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ProjectSearchActivity.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_authorization_successful, null);
        alertDialogBuilder.setView(dialogView);

        Button continueBtn = dialogView.findViewById(R.id.alert_dialog_authorization_successful_continue_btn);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(false);
        alertDialog.show();

        continueBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), SignUpActivity.class);
                intent.putExtra(KEY_ROLE,UserModel.ROLE_MANAGER);
                intent.putExtra(KEY_PROJECT_ID, project.getProjectId());
                startActivity(intent);
                finish();
            }
        });
    }

    public void sendProjectJoinRequest(ProjectModel project) {
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
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(project.getProjectId())
                .collection("Join Requests")
                .document(userInstance.getUserid())
                .set(userInstance, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            Snackbar.make(parentLayout, "Join Request Sent.", Snackbar.LENGTH_SHORT)
                                    .setBackgroundTint(getResources().getColor(R.color.green_dark))
                                    .show();
                        }
                    }
                });
    }

    public void showSnackBar(String text) {
        Snackbar.make(parentLayout, text, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.green_dark))
                .show();
    }

    public void joinProjectAsManager(ProjectModel project) {
        projectMembers.clear();
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
                UserModel.ROLE_MANAGER,
                userInstance.getImageUrl()
        );

        // Update Current User Data in DB
        firestoreInstance.collection("Users")
                .document(userInstance.getUserid())
                .set(userInstance, SetOptions.merge());

        firestoreInstance.collection("Projects")
                .document(project.getProjectId())
                .update("managerName", userInstance.getFirstname() + " " + userInstance.getLastname(),
                        "managerEmail", userInstance.getEmail());

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