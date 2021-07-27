package com.example.pins.structures;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.databinding.ItemReceivedBinding;
import com.example.pins.databinding.ItemSentBinding;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.UserModel;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.message.message;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class chatmessages<bundle> extends AppCompatActivity {

    UserModel userModel = UserModel.getUserInstance();
    ArrayList<message> messageList = new ArrayList<message>();


    FirebaseFirestore db = FirebaseFirestore.getInstance();

    TextView tv;
    ImageButton ib;
    EditText et;
    RecyclerView messagesRecyclerView;
    MessageAdapter messageAdapter;
    private String userid = userModel.getUserid();
    private String username;

    private String contactUserId;
    private String contactUsername;


    @Override
    protected void onCreate(@Nullable @org.jetbrains.annotations.Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);


        et = findViewById(R.id.messagebox);
        ib = findViewById(R.id.sendbtn);

        Intent intentstartedactivity = getIntent();
        if (intentstartedactivity.hasExtra("contact_user_id")) {
            contactUserId = intentstartedactivity.getStringExtra("contact_user_id");
        }
        if (intentstartedactivity.hasExtra("contact_user_name")) {
            contactUsername = intentstartedactivity.getStringExtra("contact_user_name");
        }
        messagesRecyclerView= findViewById(R.id.chatScreenRecycleView);
        messagesRecyclerView.setLayoutManager(new LinearLayoutManager(getApplicationContext()));



        db.collection(HomeActivity.PROJECTS_COLLECTION_PATH)
                .document(userModel.getCurrentProjectId())
                .collection(HomeActivity.CHAT_COLLECTION_PATH)
                .document(userModel.getUserid().concat(ProjectMemberModel.userid))
                .collection(HomeActivity.MESSAGES_COLLECTION_PATH)
                .orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {


                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.e("Listen failed: ", error.toString());
                            return;
                        } else {
                            messageList.clear();
                        }

                        for (QueryDocumentSnapshot doc : value) {
                            if (doc.get("message") != null) {
                                message msg = new message(doc.getString("message"), doc.getTimestamp("timestamp"), doc.getString("timesent"), doc.getBoolean("sent"));

                                messageList.add(msg);
                            }
                        }
                    }


                });

        messageAdapter = new MessageAdapter(getApplicationContext(), messageAdapter.userMessageArrayList);
        messagesRecyclerView.setAdapter(messageAdapter);

        ib.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!et.getText().toString().equals("")) {

                    DateFormat df = new SimpleDateFormat("h:mm a", Locale.getDefault());
                    String date = df.format(Calendar.getInstance().getTime());

                    message newSentMsg = new message(et.getText().toString(), Timestamp.now(), date, true);
                    message newReceivedMsg = new message(et.getText().toString(), Timestamp.now(), date, false);

                    messageList.add(newSentMsg);
                    messageAdapter.notifyDataSetChanged();


                    //Send message to our db
                    db.collection(HomeActivity.PROJECTS_COLLECTION_PATH)
                            .document(userModel.getCurrentProjectId())
                            .collection(HomeActivity.CHAT_COLLECTION_PATH)
                            .document(userModel.getUserid() + ProjectMemberModel.userid)
                            .collection(HomeActivity.MESSAGES_COLLECTION_PATH)
                            .add(newSentMsg)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    messageAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Message not sent: ", e.toString());
                                }
                            });
                    /*Send message to contact's db
                    db.collection(HomeActivity.PROJECTS_COLLECTION_PATH)
                            .document(userModel.getCurrentProjectId())
                            .collection(HomeActivity.CHAT_COLLECTION_PATH)
                            .document(userModel.getUserid())
                            .collection(HomeActivity.MESSAGES_COLLECTION_PATH)
                            .add(newReceivedMsg)
                            .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    //messageAdapter.notifyDataSetChanged();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.e("Message not sent: ", e.toString());
                                }
                            });

*/
                    //update last msg
                    /*if (!messageList.isEmpty()) {
                        db.collection(HomeActivity.PROJECTS_COLLECTION_PATH)
                                .document(userModel.getUserid())
                                .collection(HomeActivity.CHAT_COLLECTION_PATH)
                                .document(userid)
                                .update(HomeActivity.LAST_MSG_PATH, messageList.get(messageList.size() - 1).getMessage()
                                        , HomeActivity.LAST_MSG_TIME_PATH, messageList.get(messageList.size() - 1).getTimesent())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {

                                        Log.e("LastMsgUpdate Success", messageList.get(messageList.size() - 1).getMessage());
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                        Log.e("LastMsgUpdate Failed", e.toString());
                                    }
                                });
                    }

                    tv.setText("");
                }
            }


        });
                              }*/




/*    public chatmessages(Context context, ArrayList<Message> messages){

this.context=context;
this.messages=messages;

    }


    @NonNull
    @NotNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull @NotNull ViewGroup parent, int viewType) {
        if (viewType == itemsent) {
            View view = LayoutInflater.from(context).inflate(R.layout.item_sent, parent, false);
            return new sentviewholder(view);
        }
        else
        {
            View view = LayoutInflater.from(context).inflate(R.layout.item_received, parent, false);
            return new sentviewholder(view);
        }

    }

    @Override


    public int getItemViewType(int position) {
        Message message=messages.get(position);
        if(FirebaseAuth.getInstance().getUid().equals(message.getSenderid()))
        {
            return  itemsent;
        }
        else
        {
            return  itemreceived;
        }

    }

    @Override
    public void onBindViewHolder(@NonNull @NotNull RecyclerView.ViewHolder holder, int position)
    {
        Message message=messages.get(position);
if(holder.getClass() == sentviewholder.class)
{
    sentviewholder  viewholder=(sentviewholder)holder;
    viewholder.binding.message.setText(messages.getMessages());
}*/
    /*
else
{
    receiverviewholder viewholder=(receiverviewholder) holder;
    viewholder.binding.message.setText(messages.getMessage());
}
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    public class  sentviewholder extends RecyclerView.ViewHolder
    {
ItemSentBinding binding;
        public sentviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
            binding=ItemSentBinding.bind(itemView);

        }
    }

    public class receiverviewholder extends RecyclerView.ViewHolder
    {
ItemReceivedBinding binding;
        public receiverviewholder(@NonNull @NotNull View itemView) {
            super(itemView);
        binding=ItemReceivedBinding.bind(itemView);
        }
    }
}
*/
                }


            }
        });
    }
}