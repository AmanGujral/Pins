package com.example.pins.ui.profile;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.pins.R;
import com.example.pins.databinding.FragmentProfileBinding;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.ShowProjectMembersAdapter;
import com.example.pins.ui.HomeActivity;
import com.example.pins.ui.all_chats.AllChatsActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class ProfileFragment extends Fragment implements ShowProjectMembersAdapter.ItemClickListener{

    private static final int SELECT_IMAGE = 1;

    private FragmentProfileBinding binding;
    TextView usernameTv;
    TextView emailTv;
    TextView currentProjectCode;
    TextView currentProjectName;
    TextView staticText;
    CardView leaveProjectBtn;
    CardView projectNameCV;
    ImageView profileImageView;
    ImageButton chatBtn;
    RelativeLayout parentLayout;

    UserModel userInstance = UserModel.getUserInstance();
    ProjectModel currentProject;

    ProjectMemberModel currentUserProjectMember;
    String currentUserProjectRole = UserModel.ROLE_EMPLOYEE;

    List<ProjectMemberModel> projectMembersList = new ArrayList<>();
    List<ProjectMemberModel> projectMemberListWithoutCurrentUser = new ArrayList<>();

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        usernameTv = binding.profileUsername;
        emailTv = binding.profileEmail;
        currentProjectCode = binding.fragmentProfileCurrentProjectCode;
        currentProjectName = binding.fragmentProfileCurrentProjectName;
        staticText = binding.fragmentProfileCurrentProjectText;
        leaveProjectBtn = binding.fragmentProfileLeaveProjectCv;
        projectNameCV = binding.fragmentProfileCurrentProjectCv;
        profileImageView = binding.profileImage;
        parentLayout = binding.fragmentProfileParent;
        chatBtn = binding.fragmentProfileChatBtn;

        getCurrentProject();

        String fullName = userInstance.getFirstname() + " " + userInstance.getLastname();
        usernameTv.setText(fullName);
        emailTv.setText(userInstance.getEmail());

        Glide.with(this)
                .load(userInstance.getImageUrl())
                .centerCrop()
                .placeholder(R.drawable.profiledefault)
                .into(profileImageView);

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AllChatsActivity.class));
            }
        });

        profileImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                chooseImage();
            }
        });

        leaveProjectBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentProject != null) {
                    FirebaseFirestore.getInstance()
                        .collection("Projects")
                        .document(currentProject.getProjectId())
                        .collection("Project Members")
                        .document(userInstance.getUserid())
                        .get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                                if(task.isSuccessful() && task.getResult() != null) {
                                    showConfirmLeaveProjectDialogBox(task.getResult().toObject(ProjectMemberModel.class));
                                }
                            }
                        });
                }
            }
        });

        return root;
    }

    public void getCurrentProject() {
        if(userInstance.getCurrentProjectId() != null && !userInstance.getCurrentProjectId().isEmpty()) {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                currentProject = task.getResult().toObject(ProjectModel.class);
                                if(currentProject != null) {
                                    currentProjectCode.setText(currentProject.getProjectCode());
                                    currentProjectName.setText(currentProject.getProjectName());
                                    getCurrentProjectMembers();
                                    getCurrentProjectMemberRole();
                                }
                            }
                        }
                    });
        }
        else {
            projectNameCV.setVisibility(View.GONE);
            staticText.setVisibility(View.GONE);
            leaveProjectBtn.setVisibility(View.GONE);
            leaveProjectBtn.setEnabled(false);
        }
    }

    public void getCurrentProjectMembers() {
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(userInstance.getCurrentProjectId())
                .collection("Project Members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                if(doc.toObject(ProjectMemberModel.class).getUserid().equals(userInstance.getUserid())) {
                                    currentUserProjectMember = doc.toObject(ProjectMemberModel.class);
                                }
                                else {
                                    projectMemberListWithoutCurrentUser.add(doc.toObject(ProjectMemberModel.class));
                                }
                                projectMembersList.add(doc.toObject(ProjectMemberModel.class));
                            }
                        }
                    }
                });
    }

    public void getCurrentProjectMemberRole() {
        if(userInstance != null) {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .collection("Project Members")
                    .document(userInstance.getUserid())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if(task.isSuccessful() && task.getResult() != null) {
                                currentUserProjectRole = task.getResult().toObject(ProjectMemberModel.class).getRole();
                            }
                        }
                    });
        }
    }

    private void chooseImage() {

        Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        intent.setType("image/*");
        startActivityForResult(Intent.createChooser(intent, getResources().getString(R.string.select_picture)),SELECT_IMAGE);
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
                Snackbar.make(parentLayout, R.string.cancelled, Snackbar.LENGTH_SHORT)
                        .setBackgroundTint(getResources().getColor(R.color.green_dark))
                        .show();
            }
        }
    }

    //method to upload data picture selected by user to firestore and updating imgurl in database
    private void uploadImage(Uri file) {
        final ProgressDialog progressDialog = new ProgressDialog(requireContext());
        progressDialog.setTitle(R.string.uploading_image);
        progressDialog.show();

        //String storagePath = "Profile_Pictures/" + userInstance.getUserid() + "/" + file.getLastPathSegment() + ".jpg";
        String storagePath = "Profile_Pictures/" + userInstance.getUserid();
        StorageReference storageRef = FirebaseStorage.getInstance().getReference().child(storagePath);

        UploadTask uploadTask = storageRef.putFile(file);

        uploadTask.addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                progressDialog.dismiss();
                Snackbar.make(parentLayout, e.getLocalizedMessage(), Snackbar.LENGTH_SHORT)
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
                progressDialog.setMessage(R.string.percentage + ": " + ProgPercent + "%");
            }
        });
    }

    public void removeMemberFromProject(ProjectMemberModel projectMember) {

        // Remove projectMember as a contact from all other Project Members
        for(ProjectMemberModel member : projectMembersList) {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(currentProject.getProjectId())
                    .collection("Project Members")
                    .document(member.getUserid())
                    .collection("Contacts")
                    .document(projectMember.getUserid())
                    .delete();
        }

        // Remove projectMember from Project Members collection of Project
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Project Members")
                .document(projectMember.getUserid())
                .delete();

        // Update User data for projectMember (remove current project from projectMember's user data)
        userInstance.getAllProjects().remove(currentProject.getProjectId());

        if(userInstance.getAllProjects().size() > 0) {
            userInstance.setCurrentProjectId(userInstance.getAllProjects().get(0));
        }
        else {
            userInstance.setCurrentProjectId("");
        }

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(projectMember.getUserid())
                .set(userInstance, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            showSnackBar(getResources().getString(R.string.project_left));
                            startActivity(new Intent(requireContext(), HomeActivity.class));
                            requireActivity().finish();
                        }
                    }
                });


    }

    public void changeProjectManager(ProjectMemberModel projectMember) {
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .update("managerName", projectMember.getFirstname() + " " + projectMember.getLastname(),
                        "managerEmail", projectMember.getEmail())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            FirebaseFirestore.getInstance()
                                    .collection("Projects")
                                    .document(currentProject.getProjectId())
                                    .collection("Project Members")
                                    .document(projectMember.getUserid())
                                    .update("role", ProjectMemberModel.ROLE_MANAGER)
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                removeMemberFromProject(currentUserProjectMember);
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void showSnackBar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.green_dark))
                .show();
    }

    public void showConfirmLeaveProjectDialogBox(ProjectMemberModel projectMember) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_confirm_leave_project, null);
        alertDialogBuilder.setView(dialogView);

        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_confirm_leave_project_yes_btn);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_confirm_leave_project_no_btn);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        noBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        yesBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(currentUserProjectRole.equals(ProjectMemberModel.ROLE_MANAGER)) {
                    showProjectMembersDialogBox(false, true);
                }
                else {
                    removeMemberFromProject(projectMember);
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void showProjectMembersDialogBox(Boolean showRemoveBtn, Boolean showAcceptBtn) {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_show_project_members, null);
        alertDialogBuilder.setView(dialogView);

        TextView titleTv = dialogView.findViewById(R.id.alert_dialog_show_project_members_tv);
        RecyclerView dialogRV = dialogView.findViewById(R.id.alert_dialog_show_project_members_rv);
        Button doneBtn = dialogView.findViewById(R.id.alert_dialog_show_project_members_yes_btn);

        titleTv.setText(R.string.select_a_person_to_make_manager);

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        ShowProjectMembersAdapter showProjectMembersAdapter = new ShowProjectMembersAdapter(requireContext(),
                projectMemberListWithoutCurrentUser, showRemoveBtn, showAcceptBtn, this);
        dialogRV.setAdapter(showProjectMembersAdapter);

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        doneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });
    }

    @Override
    public void onShowProjectMembersAdapterItemClick(View view, int position, Boolean showRemoveBtn, Boolean showAcceptBtn) {
        changeProjectManager(projectMemberListWithoutCurrentUser.get(position));
    }
}

