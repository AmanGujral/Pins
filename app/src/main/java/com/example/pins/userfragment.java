package com.example.pins;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;

import com.example.pins.adapter.useradapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.auth.User;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class userfragment<UserAdapter> extends Fragment {

private RecyclerView recyclerView;
private UserAdapter userAdapter;
private List<User> muser;



    public userfragment() {
        // Required empty public constructor
    }

    public static userfragment newInstance(String param1, String param2) {
        userfragment fragment = new userfragment();

        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view=inflater.inflate(R.layout.fragment_userfragment,container,false);
        recyclerView=view.findViewById(R.id.activity_project_search_recyclerview);
        recyclerView.setHasFixedSize(true);

        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        muser= new ArrayList<>();

        readusers();
        return  view;


       // return inflater.inflate(R.layout.fragment_userfragment, container, false);
    }

    private void readusers() {
        FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference("Users");

        ValueEventListener valueEventListener = reference.addValueEventListener(new ValueEventListener()
        {
            private Object Adapter;
            private Object UserAdapter;

            @SuppressLint("RestrictedApi")
            @Override
            public void onDataChange(@NonNull @NotNull DataSnapshot snapshot)
            {
                muser.clear();
                Object dataSnapshot;
                for (DataSnapshot snapshot1 : snapshot.getChildren())
                {
                User user = snapshot.getValue(User.class);

                assert user != null;
                assert firebaseUser != null;

                if (!user.getUid().equals(firebaseUser.getUid()))
                {
                    muser.add(user);


                }






    }
                userAdapter= new UserAdapter(getContext(), muser);
                recyclerView.setAdapter(Adapter);
}

            @Override
            public void onCancelled(@NonNull @NotNull DatabaseError error) {

            }
        });
    }
}