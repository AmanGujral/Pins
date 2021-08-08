package com.example.pins.ui.all_chats;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.example.pins.structures.ProjectAdapter;
import com.example.pins.ui.chat_room.ChatRoomActivity;
import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.ContactAdapter;
import com.example.pins.ui.HomeActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class AllChatsActivity extends AppCompatActivity implements ContactAdapter.ItemClickListener {

    RecyclerView contactsRV;
    TextView projectName;
    ImageButton closeBtn;
    RelativeLayout errorLayout;
    List<ContactModel> contactList = new ArrayList<>();
    ContactAdapter contactAdapter;

    UserModel userInstance = UserModel.getUserInstance();
    ProjectModel currentProject;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_all_chats);

        contactsRV = findViewById(R.id.activity_all_chats_recyclerview);
        projectName = findViewById(R.id.activity_all_chats_project_name);
        closeBtn = findViewById(R.id.activity_all_chats_close_btn);
        errorLayout = findViewById(R.id.activity_all_chats_error_layout);

        getCurrentProject();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), HomeActivity.class));
                finish();
            }
        });
    }

    public void getCurrentProject() {
        if(userInstance.getCurrentProjectId() == null || userInstance.getCurrentProjectId().isEmpty()) {
            initLayout();
        }
        else {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                currentProject = task.getResult().toObject(ProjectModel.class);
                                initLayout();
                            }
                        }
                    });
        }
    }

    public void initLayout() {
        if(currentProject != null) {
            projectName.setText(currentProject.getProjectName());
            getContacts();
            showContactsListLayout();
        }
        else {
            projectName.setText("");
            showErrorLayout();
        }
    }

    public void showErrorLayout() {
        errorLayout.setVisibility(View.VISIBLE);
        contactsRV.setVisibility(View.GONE);
    }

    public void showContactsListLayout() {
        contactsRV.setVisibility(View.VISIBLE);
        errorLayout.setVisibility(View.GONE);
    }

    public void getContacts() {
        contactsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));

        /*FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Project Members")
                .document(userInstance.getUserid())
                .collection("Contacts")
                .orderBy("userid")
                .orderBy("lastMsgTimestamp")
                .whereNotEqualTo("userid", userInstance.getUserid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            contactList.clear();
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                contactList.add(doc.toObject(ContactModel.class));
                            }
                            contactsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            contactAdapter = new ContactAdapter(getApplicationContext(), contactList, AllChatsActivity.this);
                            contactsRV.setAdapter(contactAdapter);
                        }
                    }
                });*/

        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Project Members")
                .document(userInstance.getUserid())
                .collection("Contacts")
                .orderBy("userid")
                .orderBy("lastMsgTimestamp")
                .whereNotEqualTo("userid", userInstance.getUserid())
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Log.e( "Listen failed: ", error.toString());
                            return;
                        }
                        if(snapshot != null) {
                            contactList.clear();
                            for(QueryDocumentSnapshot doc : snapshot) {
                                contactList.add(doc.toObject(ContactModel.class));
                            }
                            //contactsRV.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            contactAdapter = new ContactAdapter(getApplicationContext(), contactList, AllChatsActivity.this);
                            contactsRV.setAdapter(contactAdapter);
                            /*if(contactAdapter != null) {
                                contactAdapter.notifyDataSetChanged();
                            }*/
                        }
                    }
                });
    }

    @Override
    public void onContactAdapterItemClick(View view, int position) {
        Intent intent = new Intent(getApplicationContext(), ChatRoomActivity.class);
        intent.putExtra("CONTACT_ID", contactList.get(position).getUserid());
        startActivity(intent);
    }
}
