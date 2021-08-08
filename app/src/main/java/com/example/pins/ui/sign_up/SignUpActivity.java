package com.example.pins.ui.sign_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.onboarding.OnboardingActivity;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout firstname_til,lastname_til,email_til,password_til;
    TextInputEditText firstname_tie,lastname_tie,email_tie,password_tie;

    Button SignUp;
    TextView SignIn;
    TextView managerSignupBtn;

    RelativeLayout parentLayout;

    List<ProjectMemberModel> projectMembers = new ArrayList<>();
    List<ContactModel> contactList = new ArrayList<>();

    FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();

    private UserModel userInstance;

    public static final String KEY_MANAGER_SIGNUP = "MANAGER_SIGNUP";

    String USER_ROLE = UserModel.ROLE_EMPLOYEE;
    String PROJECT_ID = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        firstname_til = findViewById(R.id.signup_firstname_til);
        firstname_tie = findViewById(R.id.signup_firstname_tie);

        lastname_til = findViewById(R.id.signup_lastname_til);
        lastname_tie = findViewById(R.id.signup_lastname_tie);

        email_til = findViewById(R.id.signup_email_til);
        email_tie = findViewById(R.id.signup_email_tie);

        password_til = findViewById(R.id.signup_password_til);
        password_tie = findViewById(R.id.signup_password_tie);

        SignUp = findViewById(R.id.signup_btn);
        SignIn = findViewById(R.id.signup_signin_btn);

        //Manager Login
        managerSignupBtn =findViewById(R.id.activity_signin_manager_btn);


        parentLayout = findViewById(R.id.activity_signup_parent_layout);

        userInstance = UserModel.getUserInstance();

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra(ProjectSearchActivity.KEY_ROLE)) {
            USER_ROLE = intentThatStartedThisActivity.getStringExtra(ProjectSearchActivity.KEY_ROLE);
        }
        if(intentThatStartedThisActivity.hasExtra(ProjectSearchActivity.KEY_PROJECT_ID)) {
            PROJECT_ID = intentThatStartedThisActivity.getStringExtra(ProjectSearchActivity.KEY_PROJECT_ID);
        }

        if(PROJECT_ID.isEmpty() && !USER_ROLE.equals(UserModel.ROLE_MANAGER)) {
            managerSignupBtn.setVisibility(View.VISIBLE);
        }
        else {
            managerSignupBtn.setVisibility(View.INVISIBLE);
        }

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
                finish();
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearErrors();

                if(firstname_tie.getText().toString().isEmpty()){
                    firstname_til.setError("First Name cannot be empty!");
                }
                else if(lastname_tie.getText().toString().isEmpty()){
                    lastname_til.setError("Last Name cannot be empty!");
                }
                else if(email_tie.getText().toString().isEmpty()){
                    email_til.setError("Please enter a valid Email Id!");
                }
                else if(password_tie.getText().toString().isEmpty() || password_tie.getText().toString().length() < 6){
                    email_til.setError(null);
                    password_til.setError("Password must be at least 6 characters long!");
                }
                else{
                    clearErrors();

                    FirebaseAuth.getInstance()
                            .createUserWithEmailAndPassword(
                                    email_tie.getText().toString(),
                                    password_tie.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                    if (task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {

                                        userInstance.setUserid(task.getResult().getUser().getUid());
                                        userInstance.setFirstname(firstname_tie.getText().toString());
                                        userInstance.setLastname(lastname_tie.getText().toString());
                                        userInstance.setEmail(email_tie.getText().toString());
                                        userInstance.setRole(USER_ROLE);
                                        userInstance.setImageUrl("");
                                        userInstance.setCurrentProjectId("");
                                        userInstance.setAllProjects(null);

                                        FirebaseFirestore.getInstance()
                                                .collection("Users")
                                                .document(task.getResult().getUser().getUid())
                                                .set(new UserModel(
                                                        userInstance.getUserid(),
                                                        userInstance.getFirstname(),
                                                        userInstance.getLastname(),
                                                        userInstance.getEmail(),
                                                        userInstance.getRole(),
                                                        userInstance.getImageUrl(),
                                                        userInstance.getCurrentProjectId(),
                                                        userInstance.getAllProjects()
                                                ))
                                                .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                        if(PROJECT_ID.isEmpty() && !USER_ROLE.equals(UserModel.ROLE_MANAGER)) {
                                                            startActivity(new Intent(SignUpActivity.this, OnboardingActivity.class));
                                                            finish();
                                                        }
                                                        else {
                                                            firestoreInstance.collection("Projects")
                                                                    .document(PROJECT_ID)
                                                                    .get()
                                                                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                                        @Override
                                                                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                                            if(task.isSuccessful() && task.getResult() != null) {
                                                                                ProjectModel project = task.getResult().toObject(ProjectModel.class);
                                                                                joinProjectAsManager(project);
                                                                            }
                                                                        }
                                                                    });
                                                        }
                                                    }
                                                });
                                    }
                                    else {
                                        Snackbar.make(parentLayout, "Authentication Failed!", Snackbar.LENGTH_SHORT)
                                                .setBackgroundTint(getResources().getColor(R.color.green))
                                                .show();
                                    }
                                }
                            });

                }
            }
        });

        managerSignupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignUpActivity.this, ProjectSearchActivity.class);
                intent.putExtra(KEY_MANAGER_SIGNUP, true);
                startActivity(intent);
            }
        });
    }

    public void joinProjectAsManager(ProjectModel project) {
        projectMembers.clear();
        List<String> allProjects = new ArrayList<>();

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

    private void clearErrors(){
        firstname_til.setError(null);
        lastname_til.setError(null);
        email_til.setError(null);
        password_til.setError(null);
    }
}