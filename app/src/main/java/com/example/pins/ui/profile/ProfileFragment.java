package com.example.pins.ui.profile;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.pins.R;
import com.example.pins.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import org.jetbrains.annotations.NotNull;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
 private    TextView name_i,email_i;

      FirebaseDatabase database;
 DatabaseReference reference;
FirebaseAuth firebaseAuth;
FirebaseUser user;
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();





        name_i=root.findViewById(R.id.profile_email);
email_i=root.findViewById(R.id.profile_email);
        firebaseAuth=FirebaseAuth.getInstance();
        user=FirebaseAuth.getInstance().getCurrentUser();
        database=FirebaseDatabase.getInstance();
        reference=database.getReference("Users");
        Query query=reference.orderByChild("email").equalTo(user.getEmail());
query.addValueEventListener(new ValueEventListener() {
    @Override
    public void onDataChange(@NonNull @NotNull DataSnapshot snapshot) {

        for(DataSnapshot ds : snapshot.getChildren()){
            String name=""+ ds.child("firstname").getValue();
            String email=""+ ds.child("email").getValue();
name_i.setText(name);
email_i.setText(email);


        }

    }

    @Override
    public void onCancelled(@NonNull @NotNull DatabaseError error) {

    }
});

        return root;

    }

    }

