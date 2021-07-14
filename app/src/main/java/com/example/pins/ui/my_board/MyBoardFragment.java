package com.example.pins.ui.my_board;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.pins.R;
import com.example.pins.databinding.FragmentMyboardBinding;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.UserModel;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.NotNull;

public class MyBoardFragment extends Fragment {

    private FragmentMyboardBinding binding;

    TextView projectName;
    TextView joinNowBtn;
    EditText searchField;
    ImageButton searchBtn;
    ImageButton closeSearchBtn;

    RelativeLayout parentLayout;
    RelativeLayout errorMsgLayout;
    RelativeLayout searchLayout;
    RelativeLayout boardLayout;

    UserModel userInstance;
    ProjectModel currentProject;
    String query = "";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentMyboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        projectName = binding.fragmentMyboardProjectName;
        errorMsgLayout = binding.fragmentMyboardErrorMsg;
        joinNowBtn = binding.fragmentMyboardJoinNowBtn;
        searchField = binding.fragmentMyboardSearchbarEdittext;
        searchBtn = binding.fragmentMyboardSearchbarSearchBtn;
        closeSearchBtn = binding.fragmentMyboardSearchbarCloseBtn;
        parentLayout = binding.fragmentMyboardParentLayout;
        searchLayout = binding.fragmentMyboardSearchLayout;
        boardLayout = binding.fragmentMyboardBoardLayout;

        userInstance = UserModel.getUserInstance();

        searchBtn.setVisibility(View.VISIBLE);
        closeSearchBtn.setVisibility(View.GONE);

        getCurrentProject();

        searchField.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                query = searchField.getText().toString();
                if(!query.equalsIgnoreCase("")){
                    searchBtn.setVisibility(View.GONE);
                    closeSearchBtn.setVisibility(View.VISIBLE);
                    //searchProjects(query);
                }
                else {
                    searchBtn.setVisibility(View.VISIBLE);
                    closeSearchBtn.setVisibility(View.GONE);
                    //getAllProjects();
                }
                Log.e("QUERY: ", query);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!query.equalsIgnoreCase("")) {
                    searchBtn.setVisibility(View.GONE);
                    closeSearchBtn.setVisibility(View.VISIBLE);
                    //searchProjects(query);
                }
                else {
                    Snackbar.make(parentLayout, "Enter a valid task name.", Snackbar.LENGTH_SHORT)
                            .setBackgroundTint(getResources().getColor(R.color.green_dark))
                            .show();
                }
            }
        });

        closeSearchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchField.setText("");
            }
        });

        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity().getApplicationContext(), ProjectSearchActivity.class));
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void getCurrentProject() {
        if(userInstance.getCurrentProjectId() == null) {
            initLayout();
        }
        else {
            FirebaseFirestore.getInstance()
                    .collection("Projects")
                    .document(userInstance.getCurrentProjectId())
                    .get()
                    .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                        @Override
                        public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                            if (task.isSuccessful() && task.getResult() != null) {
                                currentProject = task.getResult().toObject(ProjectModel.class);
                                initLayout();
                            }
                        }
                    });
        }
    }

    public void initLayout() {
        if(currentProject != null) {
            projectName.setText(currentProject.getProjectName());
            showBoardLayout();
        }
        else {
            projectName.setText("");
            showErrorLayout();
        }
    }

    public void showErrorLayout() {
        errorMsgLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
        boardLayout.setVisibility(View.GONE);
    }

    public void showSearchLayout() {
        errorMsgLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        boardLayout.setVisibility(View.GONE);
    }

    public void showBoardLayout() {
        errorMsgLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.GONE);
        boardLayout.setVisibility(View.VISIBLE);
    }
}