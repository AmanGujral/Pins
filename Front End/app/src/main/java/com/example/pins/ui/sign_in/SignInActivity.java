package com.example.pins.ui.sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pins.R;
import com.example.pins.models.UserModel;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.sign_up.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    RelativeLayout parentLayout;
    private TextView signUpBtn;
    private Button signInBtn;
    private EditText emailTie;
    private EditText passwordTie;
    private TextInputLayout emailTil;
    private TextInputLayout passwordTil;
    private ProgressDialog mProgressDialog;

    UserModel userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        parentLayout = findViewById(R.id.activity_signin_layout);
        signUpBtn = findViewById(R.id.signin_signup_btn);
        signInBtn = findViewById(R.id.signin_btn);
        emailTie = findViewById(R.id.signin_email_editText);
        passwordTie = findViewById(R.id.signin_password_editText);
        emailTil = findViewById(R.id.signin_email_layout);
        passwordTil = findViewById(R.id.signin_password_layout);

        mProgressDialog = new ProgressDialog(this);

        userInstance = UserModel.getUserInstance();


        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(SignInActivity.this, SignUpActivity.class);
                intent.putExtra("ROLE", UserModel.ROLE_EMPLOYEE);
                startActivity(intent);
                finish();
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                clearErrors();
                String email = emailTie.getText().toString().trim();
                String password  = passwordTie.getText().toString().trim();

                if(TextUtils.isEmpty(email)){
                    emailTil.setError(getResources().getString(R.string.please_enter_a_valid_email));
                    return;
                }

                if(TextUtils.isEmpty(password)){
                    passwordTil.setError(getResources().getString(R.string.password_cannot_be_empty));
                    return;
                }

                if(!TextUtils.isEmpty(password) && password.length() < 6){
                    passwordTil.setError(getResources().getString(R.string.password_must_be_atleast_6_characters_long));
                    return;
                }

                mProgressDialog.setMessage(getResources().getString(R.string.signing_in_please_wait));
                mProgressDialog.show();

                FirebaseAuth.getInstance()
                        .signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<AuthResult> task) {
                                if(task.isSuccessful() && task.getResult() != null && task.getResult().getUser() != null) {
                                    FirebaseFirestore.getInstance()
                                            .collection("Users")
                                            .document(task.getResult().getUser().getUid())
                                            .get()
                                            .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                                    if(task.isSuccessful() && task.getResult() != null) {
                                                        userInstance.setUserInstance(task.getResult().toObject(UserModel.class));
                                                        startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                                                        finish();
                                                    }
                                                    mProgressDialog.dismiss();
                                                }
                                            });
                                }
                                else {
                                    Snackbar.make(parentLayout, R.string.authentication_failed, Snackbar.LENGTH_SHORT)
                                            .setBackgroundTint(getResources().getColor(R.color.green))
                                            .show();
                                }
                            }
                        });
            }
        });
    }

    private void clearErrors(){
        passwordTil.setError(null);
        emailTil.setError(null);
    }
}