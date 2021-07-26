package com.example.pins.ui.profile;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.nfc.Tag;
import android.os.Bundle;
import android.util.Log;
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
import com.bumptech.glide.request.RequestOptions;
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
import com.squareup.picasso.Picasso;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import static android.content.ContentValues.TAG;

public class ProfileFragment extends Fragment {

    private FragmentProfileBinding binding;
    private TextView usernameTv, emailTv;
    private ImageView profilepic;
    Button logoutBtn;
    public Uri imguri;
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
        String dwnldLink=userInstance.getImageUrl();
        Toast.makeText(getContext(), "link: "+ dwnldLink, Toast.LENGTH_LONG).show();
        String fullname = userInstance.getFirstname() + " " + userInstance.getLastname();
        usernameTv.setText(fullname);
        emailTv.setText(userInstance.getEmail());
        Picasso.get().load(userInstance.getImageUrl()).fit().placeholder(R.drawable.profiledefault).into(profilepic);
        String temp = userInstance.getImageUrl();
        Log.d(TAG, "onCreateView: Url fetched from database: "+ temp);
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
            Glide.with(requireContext()).load(userInstance.getImageUrl()).into(profilepic);
        }
    }

    //method to upload data picture selected by user to firestore and updating imgurl in database
    private void uploadpic() {
        final ProgressDialog progressDialog = new ProgressDialog(getContext());
        progressDialog.setTitle("Uploading Image");
        progressDialog.show();

        String userid= userInstance.getUserid();
        StorageReference upldref = storageReference.child("Profile_Pictures/" + userid);

        upldref.putFile(imguri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                Toast.makeText(getContext(), "Upload Successful", Toast.LENGTH_LONG).show();
                progressDialog.dismiss();
                Task<Uri> dwnUrl = taskSnapshot.getMetadata().getReference().getDownloadUrl();
                dwnUrl.addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        String imgLink = uri.toString();
                        Log.d(TAG, "onSuccess: img link: "+ imgLink);
                        if (userInstance.getImageUrl()!=null){
                            DocumentReference documentReference = FirebaseFirestore.getInstance().collection("Users").document(userInstance.getUserid());
                            Map<String, Object> map = new HashMap<>();
                            map.put("imageUrl", imgLink);

                            documentReference.update(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(getContext(), "Firestore Uploaded", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(getContext(), "Upload to Firestore failed", Toast.LENGTH_LONG).show();
                                }
                            });
                        }else{
                            Toast.makeText(getContext(), "url is null", Toast.LENGTH_LONG).show();
                        }
                    }
                });
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

