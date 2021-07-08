package com.example.pins.ui.sign_up;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pins.R;
import com.example.pins.models.UserModel;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class SignUpActivity extends AppCompatActivity {

    TextInputLayout firstname_til,lastname_til,email_til,password_til;
    TextInputEditText firstname_tie,lastname_tie,email_tie,password_tie;

    Button SignUp;
    TextView SignIn;

    FirebaseAuth mAuth;
    FirebaseFirestore db;

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
        SignIn = findViewById(R.id.SignIN);

        mAuth = FirebaseAuth.getInstance();

        db = FirebaseFirestore.getInstance();

        SignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(SignUpActivity.this, SignInActivity.class));
            }
        });

        SignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                firstname_til.setError(null);
                lastname_til.setError(null);
                email_til.setError(null);
                password_til.setError(null);

                if(firstname_tie.getText().toString().isEmpty()){
                    firstname_til.setError("First Name cannot be empty!");
                }else if(lastname_tie.getText().toString().isEmpty()){
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
                    firstname_til.setError(null);
                    lastname_til.setError(null);
                    email_til.setError(null);
                    password_til.setError(null);
                    mAuth.createUserWithEmailAndPassword(email_tie.getText().toString(),password_tie.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull @NotNull Task<AuthResult> task) {

                                    FirebaseUser user = mAuth.getCurrentUser();
                                    String User = user.getUid();
                                    UserModel userModel = new UserModel(User,firstname_tie.getText().toString(),lastname_tie.getText().toString(),email_tie.getText().toString());
                                    db.collection("Users").document(User).set(userModel)
                                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<Void> task) {
                                               startActivity(new Intent(SignUpActivity.this, HomeActivity.class));
                                                }
                                            });
                                }
                            });
                }
            }
        });

    }
}