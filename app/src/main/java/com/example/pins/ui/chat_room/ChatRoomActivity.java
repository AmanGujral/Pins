package com.example.pins.ui.chat_room;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.pins.R;
import com.example.pins.models.ContactModel;
import com.example.pins.models.MessageModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.MessageAdapter;
import com.example.pins.ui.all_chats.AllChatsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class ChatRoomActivity extends AppCompatActivity implements MessageAdapter.ItemClickListener {

    ImageView profilePic;
    TextView initialsTV;
    TextView personName;
    ImageButton closeBtn;
    RecyclerView msgsRecyclerview;
    EditText msgBox;
    ImageButton sendBtn;
    String contactId;

    UserModel userInstance = UserModel.getUserInstance();
    ContactModel currentContact;

    MessageAdapter messageAdapter;

    List<MessageModel> messageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);

        profilePic = findViewById(R.id.activity_chat_room_imageview);
        initialsTV = findViewById(R.id.activity_chat_room_pic_textview);
        personName = findViewById(R.id.activity_chat_room_name_tv);
        closeBtn = findViewById(R.id.activity_chat_room_close_btn);
        msgsRecyclerview = findViewById(R.id.activity_chat_room_recyclerview);
        msgBox = findViewById(R.id.activity_chat_room_message_box);
        sendBtn = findViewById(R.id.activity_chat_room_send_btn);

        Intent intentThatStartedThisActivity = getIntent();
        if(intentThatStartedThisActivity.hasExtra("CONTACT_ID")) {
            contactId = intentThatStartedThisActivity.getStringExtra("CONTACT_ID");
        }

        getCurrentContact();

        closeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //startActivity(new Intent(getApplicationContext(), AllChatsActivity.class));
                finish();
            }
        });

        sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                sendMessage();
            }
        });

    }

    public void sendMessage() {
        if(!msgBox.getText().toString().equals("") || msgBox.getText() != null) {
            String message = msgBox.getText().toString();
            msgBox.setText("");

            DateFormat df = new SimpleDateFormat("h:mm a", Locale.getDefault());
            String time = df.format(Calendar.getInstance().getTime());

            MessageModel msgSent = new MessageModel(
                    "",
                    message,
                    time,
                    System.currentTimeMillis(),
                    MessageModel.STATUS_SENT
            );

            MessageModel msgReceived = new MessageModel(
                    "",
                    message,
                    time,
                    System.currentTimeMillis(),
                    MessageModel.STATUS_RECEIVED
            );

            // Add Message in Sender's DB
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(userInstance.getUserid())
                    .collection("Contacts")
                    .document(currentContact.getUserid())
                    .collection("Chat")
                    .add(msgSent)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                            if(task.isSuccessful() && task.getResult() != null) {
                                String messageId = task.getResult().getId();
                                FirebaseFirestore.getInstance()
                                        .collection("Projects")
                                        .document(userInstance.getCurrentProjectId())
                                        .collection("Project Members")
                                        .document(userInstance.getUserid())
                                        .collection("Contacts")
                                        .document(currentContact.getUserid())
                                        .collection("Chat")
                                        .document(messageId)
                                        .update("messageId", messageId);
                            }
                        }
                    });

            // // Add Message in Receiver's DB
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(currentContact.getUserid())
                    .collection("Contacts")
                    .document(userInstance.getUserid())
                    .collection("Chat")
                    .add(msgReceived)
                    .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                            if(task.isSuccessful() && task.getResult() != null) {
                                String messageId = task.getResult().getId();
                                FirebaseFirestore.getInstance()
                                        .collection("Projects")
                                        .document(userInstance.getCurrentProjectId())
                                        .collection("Project Members")
                                        .document(currentContact.getUserid())
                                        .collection("Contacts")
                                        .document(userInstance.getUserid())
                                        .collection("Chat")
                                        .document(messageId)
                                        .update("messageId", messageId);
                            }
                        }
                    });
        }
    }

    public void getCurrentContact() {
        if(contactId != null && !contactId.equals("")) {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(userInstance.getUserid())
                    .collection("Contacts")
                    .document(contactId)
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                currentContact = task.getResult().toObject(ContactModel.class);
                                initLayout();
                            }
                        }
                    });
        }
    }

    public void initLayout() {
        if(currentContact != null) {
            String name = currentContact.getFirstname() + " " + currentContact.getLastname();
            personName.setText(name);
            if(!currentContact.getImageUrl().isEmpty()) {
                initialsTV.setVisibility(View.GONE);
                profilePic.setVisibility(View.VISIBLE);
                Glide.with(this)
                        .load(currentContact.getImageUrl())
                        .centerCrop()
                        .placeholder(R.drawable.ic_profile_24)
                        .into(profilePic);
            }
            else {
                initialsTV.setVisibility(View.VISIBLE);
                profilePic.setVisibility(View.GONE);

                initialsTV.setText(String.valueOf(currentContact.getFirstname().trim().toUpperCase().charAt(0)));
            }

            getMessages();
        }
        else {
            personName.setText("");
        }
    }

    public void getMessages() {

        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(userInstance.getCurrentProjectId())
                .collection("Project Members")
                .document(userInstance.getUserid())
                .collection("Contacts")
                .document(currentContact.getUserid())
                .collection("Chat")
                .orderBy("timestamp")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            messageList.clear();
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                messageList.add(doc.toObject(MessageModel.class));
                                Log.e("Message", doc.toObject(MessageModel.class).getMessage());
                            }
                            Log.e("Message List", String.valueOf(messageList.size()));
                            msgsRecyclerview.setLayoutManager(new LinearLayoutManager(getApplicationContext()));
                            messageAdapter = new MessageAdapter(getApplicationContext(), messageList, ChatRoomActivity.this);
                            msgsRecyclerview.setAdapter(messageAdapter);
                        }
                    }
                });

        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(userInstance.getCurrentProjectId())
                .collection("Project Members")
                .document(userInstance.getUserid())
                .collection("Contacts")
                .document(currentContact.getUserid())
                .collection("Chat")
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Log.e( "Listen failed: ", error.toString());
                            return;
                        }
                        if(snapshot != null) {
                            messageList.clear();
                            for(QueryDocumentSnapshot doc : snapshot) {
                                messageList.add(doc.toObject(MessageModel.class));
                            }
                            if(messageAdapter != null) {
                                messageAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    @Override
    public void onMessageAdapterItemLongClick(View view, int position) {
        Log.e("Message Clicked", messageList.get(position).getMessage());
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Update Last Message on All Chats Page
        if(messageList.size() != 0) {
            MessageModel lastMessage = messageList.get(messageList.size() - 1);

            // For Sender
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(userInstance.getUserid())
                    .collection("Contacts")
                    .document(currentContact.getUserid())
                    .update("lastMsg", lastMessage.getMessage(),
                            "lastMsgTime", lastMessage.getTimeSent(),
                            "lastMsgTimestamp", lastMessage.getTimestamp(),
                            "lastMsgSeen", true);

            // For Receiver
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(currentContact.getUserid())
                    .collection("Contacts")
                    .document(userInstance.getUserid())
                    .update("lastMsg", lastMessage.getMessage(),
                            "lastMsgTime", lastMessage.getTimeSent(),
                            "lastMsgTimestamp", lastMessage.getTimestamp(),
                            "lastMsgSeen", false);
        }
    }
}