package com.example.pins.ui.profile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pins.R;
import com.example.pins.databinding.FragmentProfileBinding;
import com.example.pins.models.UserModel;
import com.example.pins.ui.sign_in.SignInActivity;
import com.google.android.gms.auth.api.signin.internal.Storage;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.net.URL;
import java.util.UUID;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView usernameTv, emailTv;
    private ImageView profilepic;
    Button logoutBtn;
    public Uri imguri;
    FirebaseFirestore firebaseInst = FirebaseFirestore.getInstance();
    private FirebaseStorage storage;
    private StorageReference storageReference;

    UserModel userInstance;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        storage= FirebaseStorage.getInstance();
        storageReference=storage.getReference();

        logoutBtn = binding.logoutBtn;
        usernameTv = binding.profileUsername;
        emailTv = binding.profileEmail;
        profilepic= binding.profileImage;
        userInstance = UserModel.getUserInstance();

        String fullname = userInstance.getFirstname() + " " + userInstance.getLastname();
        usernameTv.setText(fullname);
        emailTv.setText(userInstance.getEmail());
        if (userInstance.getImageUrl()==""){
            Glide.with(this).load(R.drawable.profiledefault).into(profilepic);
        }else {
            Glide.with(this).load(userInstance.getImageUrl()).into(profilepic);
        }

        logoutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseAuth.getInstance().signOut();
                startActivity(new Intent(getActivity().getApplicationContext(), SignInActivity.class));
            }
        });

        profilepic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                choosepic();
            }
        });

        return root;
    }

    private void choosepic() {

        try {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(intent,1);
        }catch (ActivityNotFoundException e){
            Toast.makeText(getContext(), "Image browser intent error", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==-1 && data!=null && data.getData()!=null) {
            imguri = data.getData();
            profilepic.setImageURI(imguri);
            uploadpic();
        }
    }

    private void uploadpic() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        String userid= userInstance.getUserid();
        StorageReference upldref = storageReference.child("Profile_Pictures" + userid);

        upldref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Task<Uri> dwnUrl=upldref.getDownloadUrl();
                try {
                    userInstance.setImageUrl(dwnUrl.toString());
                    UserModel userModel = new UserModel(
                            userInstance.getUserid(),
                            userInstance.getFirstname(),
                            userInstance.getLastname(),
                            userInstance.getEmail(),
                            userInstance.getRole(),
                            userInstance.getImageUrl(),
                            userInstance.getCurrentProjectId(),
                            userInstance.getAllProjects()
                    );
                    firebaseInst.collection("Users").document(userInstance.getUserid()).set(userInstance, SetOptions.merge());
                }catch (NullPointerException e){
                    Toast.makeText(getContext(), "upload to firestore unsuccessful", Toast.LENGTH_LONG).show();
                };
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText((getContext()),"Upload Failed", Toast.LENGTH_LONG);
                progressDialog.dismiss();
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double ProgPercent = (100 * snapshot.getBytesTransferred()/ snapshot.getTotalByteCount());
                progressDialog.setMessage("Percentage: " + ProgPercent + "%");
            }
        });
        }
    }

