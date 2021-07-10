package com.example.pins.ui.sign_in;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.pins.R;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.logo.LogoActivity;
import com.example.pins.ui.sign_up.SignUpActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignInActivity extends AppCompatActivity {

    private FirebaseAuth mFirebaseAuth;

    private static String TAG = SignInActivity.class.getName();

    private TextView mSignUp;
    private Button mSignInButton;

    private EditText mEditTextEmail;
    private EditText mEditTextPassword;

    private TextInputLayout mTextInputLayoutEmail;
    private TextInputLayout mTextInputLayoutPassword;

    private ProgressDialog mProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        this.init();
    }

    private void init(){
        mFirebaseAuth = FirebaseAuth.getInstance();

        mSignUp = findViewById(R.id.SignIn);
        mSignInButton = findViewById(R.id.signin_btn);

        mEditTextEmail = (EditText) findViewById(R.id.signin_email_editText);
        mEditTextPassword = (EditText) findViewById(R.id.signin_password_editText);

        mTextInputLayoutEmail = findViewById(R.id.signin_email_layout);
        mTextInputLayoutPassword = findViewById(R.id.signin_password_layout);

        mProgressDialog = new ProgressDialog(this);


        mSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SignInActivity.this,SignUpActivity.class);
                startActivity(i);
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                registerUser();
            }
        });
    }


    @Override
    protected void onStart() {
        super.onStart();
    }


    private void clearErrors(){
        mTextInputLayoutPassword.setError(null);
        mTextInputLayoutEmail.setError(null);
    }


    private void registerUser(){

        clearErrors();
        String email = mEditTextEmail.getText().toString().trim();
        String password  = mEditTextPassword.getText().toString().trim();

        if(TextUtils.isEmpty(email)){
            mTextInputLayoutEmail.setError("Please enter email");
            return;
        }

        if(TextUtils.isEmpty(password)){
            mTextInputLayoutPassword.setError("Please enter password");
            return;
        }

        if(!TextUtils.isEmpty(password) && password.length() < 6){
            mTextInputLayoutPassword.setError("Password must be at least 6 characters long!");
            return;
        }

        clearErrors();

        mProgressDialog.setMessage("Signin Please Wait...");
        mProgressDialog.show();

        //creating a new user
        mFirebaseAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if(task.isSuccessful()){
                           goToHomePage();
                        }else{
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(SignInActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                        mProgressDialog.dismiss();
                    }
                });
    }

    private void goToHomePage(){
        Intent intent = new Intent(SignInActivity.this, HomeActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
    }
}