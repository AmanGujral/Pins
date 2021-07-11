package com.example.pins.ui.profile;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.pins.R;
import com.example.pins.databinding.FragmentProfileBinding;
import com.example.pins.models.UserModel;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView usernameTv, emailTv;
    Button logoutBtn;

    UserModel userInstance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        logoutBtn = binding.logoutBtn;
        usernameTv = binding.profileUsername;
        emailTv = binding.profileEmail;

        userInstance = UserModel.getUserInstance();

        String fullname = userInstance.getFirstname() + " " + userInstance.getLastname();
        usernameTv.setText(fullname);
        emailTv.setText(userInstance.getEmail());

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity().getApplicationContext(), SignInActivity.class));
            }
        });

        return root;
    }
}

