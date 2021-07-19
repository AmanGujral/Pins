package com.example.pins;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;

import com.example.pins.models.UserMessage;
import com.example.pins.structures.MessageAdapter;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class MessagesActivity extends AppCompatActivity {

    RecyclerView recyclerView;
    ArrayList<UserMessage> userMessageArrayList;
    MessageAdapter messageAdapter;

    private FirebaseFirestore db = FirebaseFirestore.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        recyclerView = findViewById(R.id.recyclerViewMessages);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        db = FirebaseFirestore.getInstance();

        ChangeListner();

    }

    private void ChangeListner() {
        db.collection("Users").orderBy("firstname", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot value, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        userMessageArrayList =new ArrayList<UserMessage>();
                        messageAdapter = new MessageAdapter(MessagesActivity.this,userMessageArrayList);
                        for (DocumentChange dc :value.getDocumentChanges()){
                            if(dc.getType() == DocumentChange.Type.ADDED){

                                userMessageArrayList.add(dc.getDocument().toObject(UserMessage.class));

                            }
                            messageAdapter.notifyDataSetChanged();

                            recyclerView.setAdapter(messageAdapter);

                        }
                    }

                });
    }
}
