package com.example.pins.ui.profile;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.pins.R;
import com.example.pins.databinding.FragmentProfileBinding;
import com.example.pins.models.UserModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.util.ArrayList;
import java.util.List;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    private static final int SELECT_IMAGE = 1;

    private FragmentProfileBinding binding;
    TextView usernameTv;
    TextView emailTv;
    ImageView profileImageView;
    Spinner projectSpinner;
    RelativeLayout parentLayout;
    FirebaseFirestore firestoreInst = FirebaseFirestore.getInstance();

    UserModel userInstance = UserModel.getUserInstance();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usernameTv = binding.profileUsername;
        emailTv = binding.profileEmail;
        profileImageView = binding.profileImage;
        projectSpinner = binding.fragmentProfileSpinner;
        parentLayout = binding.fragmentProfileParent;

        String fullName = userInstance.getFirstname() + " " + userInstance.getLastname();
        usernameTv.setText(fullName);
        emailTv.setText(userInstance.getEmail());

        Glide.with(this)
                .load(userInstance.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.profiledefault)
                .into(profileImageView);

        //loading data into the spinner
        CollectionReference collectionReference = firestoreInst.collection(userInstance.getUserid());
        List<String> projects = new ArrayList<>();
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_dropdown_item, projects);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        projectSpinner.setAdapter(adapter);
        collectionReference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()){
                    for(QueryDocumentSnapshot documentSnapshot: task.getResult()){
                        String project = documentSnapshot.getString("allProjects");
                        projects.add(project);
                        Log.d(TAG, "onComplete: array values: " + project);
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    // end of spinner code

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        return root;
    }


    private void chooseImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, "Select Picture"),SELECT_IMAGE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SELECT_IMAGE) {
            if (resultCode == Activity.RESULT_OK) {
                if (data != null) {
                    Uri file = data.getData();
                    uploadImage(file);
                }
            } else if (resultCode == Activity.RESULT_CANCELED)  {
                Snackbar.make(parentLayout, "Cancelled", Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.green_dark))
                        .show();
            }
        }
    }

    //method to upload data picture selected by user to firestore and updating imgurl in database
    private void uploadImage(Uri file) {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        //String storagePath = "Profile_Pictures/" + userInstance.getUserid() + "/" + file.getLastPathSegment() + ".jpg";
        String storagePath = "Profile_Pictures/" + userInstance.getUserid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, e.getMessage(), Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.green_dark))
                        .show();
            }
        }).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                storageRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        userInstance.setImageUrl(uri.toString());
                        FirebaseFirestore.getInstance().collection("Users")
                                .document(userInstance.getUserid())
                                .update("imageUrl", userInstance.getImageUrl())
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        progressDialog.dismiss();
                                        Glide.with(requireContext())
                                                .load(userInstance.getImageUrl())
                                                .centerCrop()
                                                .placeholder(R.drawable.profiledefault)
                                                .into(profileImageView);
                                    }
                                });
                    }
                });
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                double ProgPercent = (snapshot.getBytesTransferred()/ snapshot.getTotalByteCount()) * 100;
                progressDialog.setMessage("Percentage: " + ProgPercent + "%");
            }
        });
    }
}

