package com.example.pins.ui.project_search;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.pins.R;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.List;

public class projectrequest extends AppCompatActivity {
    FirebaseAuth fauth;
    FirebaseFirestore fstore = FirebaseFirestore.getInstance();
    TextView uid;
    TextView pid;
    TextView pname;
    ProjectModel pmodel;
    Button accept;
    ProjectModel project;

    UserModel userInstance;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_projectrequest);

        uid = findViewById(R.id.userid);
        pid = findViewById(R.id.projectid);
        pname = findViewById(R.id.projectname);
        accept = findViewById(R.id.acceptbutton);

        fstore = FirebaseFirestore.getInstance();
        DocumentReference documentReference = fstore.collection("Projects")
                .document("EFJoqNtMr9CxgCic3oXk")
                .collection("UserRequests")
                .document("m3c0Pq3hdGwU9V9pFk00");
        documentReference.addSnapshotListener(this, new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException e) {
                        uid.setText(documentSnapshot.getString("memberid"));
                        pid.setText(documentSnapshot.getString("projectId"));
                        pname.setText(documentSnapshot.getString("projectName"));
                    }
                }
        );
        accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }
}