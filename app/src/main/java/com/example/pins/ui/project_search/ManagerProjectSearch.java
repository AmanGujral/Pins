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
import android.widget.Toast;

import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.ProjectAdapter;
import com.example.pins.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentId;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ManagerProjectSearch extends AppCompatActivity implements ProjectAdapter.ItemClickListener {
    private static final String TAG = null;
    ProjectModel projectModel;
    ImageButton McloseBtn;
    ImageButton MsearchBtn;
    ImageButton McloseSearchBtn;
    EditText MsearchField;
    RecyclerView MsearchResultsRecyclerview;
    TextView MerrorMsg;
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

        McloseBtn = findViewById(R.id.activity_project_search_close_btn);
        MsearchBtn = findViewById(R.id.activity_project_search_searchbar_search_btn);
        McloseSearchBtn = findViewById(R.id.activity_project_search_searchbar_close_btn);
        MsearchField = findViewById(R.id.activity_project_search_searchbar_edittext);
        MsearchResultsRecyclerview = findViewById(R.id.activity_project_search_recyclerview);
        MerrorMsg = findViewById(R.id.activity_project_search_error_msg);
        parentLayout = findViewById(R.id.activity_project_search_parent_layout);

        userInstance = UserModel.getUserInstance();

        MsearchBtn.setVisibility(View.VISIBLE);
        McloseSearchBtn.setVisibility(View.GONE);

        getAllProjects();

        MsearchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = MsearchField.getText().toString();
                if(!query.equalsIgnoreCase("")){
                    MsearchBtn.setVisibility(View.GONE);
                    McloseSearchBtn.setVisibility(View.VISIBLE);
                    searchProjects(query);
                }
                else {
                    MsearchBtn.setVisibility(View.VISIBLE);
                    McloseSearchBtn.setVisibility(View.GONE);
                    getAllProjects();
                }
                Log.e("QUERY: ", query);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        MsearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!query.equalsIgnoreCase("")) {
                    MsearchBtn.setVisibility(View.GONE);
                    McloseSearchBtn.setVisibility(View.VISIBLE);
                    searchProjects(query);
                }
                else {
                    Snackbar.make(parentLayout, "Enter a valid Project Code.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.green_dark))
                            .show();
                }
            }
        });

        McloseSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                MsearchField.setText("");
            }
        });

        // Close Button Click Listener
        McloseBtn.setOnClickListener(new View.OnClickListener() {
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
        MerrorMsg.setVisibility(View.GONE);
        MsearchResultsRecyclerview.setVisibility(View.VISIBLE);
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
                                    MsearchResultsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    adapter = new ProjectAdapter(
                                            getApplicationContext(),
                                            allProjects,
                                            ManagerProjectSearch.this);
                                    MsearchResultsRecyclerview.setAdapter(adapter);
                                }
                                else {
                                    Log.e("NUM OF PROJECTS: ", String.valueOf(allProjects.size()));
                                    MerrorMsg.setVisibility(View.VISIBLE);
                                    MsearchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                Log.e("TASK FAILED 83: ", task.getException().getMessage());
                                MerrorMsg.setVisibility(View.VISIBLE);
                                MsearchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            MerrorMsg.setVisibility(View.VISIBLE);
                            MsearchResultsRecyclerview.setVisibility(View.GONE);
                            Log.e("TASK FAILED 91: ", task.getException().getMessage());
                        }
                    }
                });
    }

    void searchProjects(String projectCode) {
        searchedProjects.clear();
        MerrorMsg.setVisibility(View.GONE);
        MsearchResultsRecyclerview.setVisibility(View.VISIBLE);
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
                                    MsearchResultsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                                    adapter = new ProjectAdapter(
                                            getApplicationContext(),
                                            searchedProjects,
                                            ManagerProjectSearch.this);
                                    MsearchResultsRecyclerview.setAdapter(adapter);
                                }
                                else {
                                    Log.e("NUM OF PROJECTS: ", String.valueOf(searchedProjects.size()));
                                    MerrorMsg.setVisibility(View.VISIBLE);
                                    MsearchResultsRecyclerview.setVisibility(View.GONE);
                                }
                            }
                            else {
                                Log.e("TASK FAILED 83: ", task.getException().getMessage());
                                MerrorMsg.setVisibility(View.VISIBLE);
                                MsearchResultsRecyclerview.setVisibility(View.GONE);
                            }
                        }
                        else {
                            MerrorMsg.setVisibility(View.VISIBLE);
                            MsearchResultsRecyclerview.setVisibility(View.GONE);
                            Log.e("TASK FAILED 91: ", task.getException().getMessage());
                        }
                    }
                });
    }


    @Override
    public void onItemClick(View view, int position) {
        if (query.equalsIgnoreCase("")){
            showDialog(allProjects.get(position));
        }
        else{
            showDialog(searchedProjects.get(position));
        }
    }

    public void showDialog(ProjectModel Project){
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(ManagerProjectSearch.this);
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_authorization, null);
        alertDialogBuilder.setView(dialogView);

        TextView projectCode = dialogView.findViewById(R.id.alert_dialog_auth_project_code_tv);
        EditText AuthCode = dialogView.findViewById(R.id.ManagerAuthCode);
        TextView projectName = dialogView.findViewById(R.id.alert_dialog_auth_project_name_tv);
        TextView projectManager = dialogView.findViewById(R.id.alert_dialog_auth_project_manager_tv);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_auth_no_btn);
        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_Auth_yes_btn);

        projectCode.setText(Project.getProjectCode());
        projectName.setText(Project.getProjectName());
        projectManager.setText(Project.getManagerName());



        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();
        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Acod=AuthCode.getText().toString();
                DocumentReference documentReference = firestoreInstance.collection("AuthCodes").document(Project.getProjectCode());
                documentReference.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                       String A= documentSnapshot.getString("code");
                        Log.e("Message:","Value"+String.valueOf(A));
                        if (A.equalsIgnoreCase(Acod)){
                            Log.e("Message:","Welcome");
                        }
                    }
                });
                Log.e("Message:","Value"+String.valueOf(Acod));
            }
        });
    }
}