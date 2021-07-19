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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.databinding.FragmentMyboardBinding;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.TaskModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.TaskAdapter;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class MyBoardFragment extends Fragment implements TaskAdapter.ItemClickListener {

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

    LinearLayout todoCompressed;
    LinearLayout todoExpanded;
    LinearLayout doingCompressed;
    LinearLayout doingExpanded;
    LinearLayout doneCompressed;
    LinearLayout doneExpanded;

    ImageButton todoExpandBtn;
    ImageButton doingExpandBtn;
    ImageButton doneExpandBtn;

    RecyclerView todoRecyclerview;
    RecyclerView doingRecyclerview;
    RecyclerView doneRecyclerview;

    TaskAdapter todoTaskAdapter;
    TaskAdapter doingTaskAdapter;
    TaskAdapter doneTaskAdapter;

    UserModel userInstance;
    ProjectModel currentProject;
    List<TaskModel> allTaskList = new ArrayList<>();
    List<TaskModel> todoTaskList = new ArrayList<>();
    List<TaskModel> doingTaskList = new ArrayList<>();
    List<TaskModel> doneTaskList = new ArrayList<>();
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
        todoCompressed = binding.fragmentMyboardTodoLayoutCompressed;
        todoExpanded = binding.fragmentMyboardTodoLayoutExpanded;
        doingCompressed = binding.fragmentMyboardDoingLayoutCompressed;
        doingExpanded = binding.fragmentMyboardDoingLayoutExpanded;
        doneCompressed = binding.fragmentMyboardDoneLayoutCompressed;
        doneExpanded = binding.fragmentMyboardDoneLayoutExpanded;
        todoExpandBtn = binding.fragmentMyboardTodoExpandBtn;
        doingExpandBtn = binding.fragmentMyboardDoingExpandBtn;
        doneExpandBtn = binding.fragmentMyboardDoneExpandBtn;
        todoRecyclerview = binding.fragmentMyboardTodoRecyclerview;
        doingRecyclerview = binding.fragmentMyboardDoingRecyclerview;
        doneRecyclerview = binding.fragmentMyboardDoneRecyclerview;

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

        todoExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTodoBoard();
            }
        });

        doingExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDoingBoard();
            }
        });

        doneExpandBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDoneBoard();
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
        if(userInstance.getCurrentProjectId() == null || userInstance.getCurrentProjectId().isEmpty()) {
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

    public void getTasks() {
        allTaskList.clear();
        todoTaskList.clear();
        doingTaskList.clear();
        doneTaskList.clear();

        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Tasks")
                .orderBy("priority")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                allTaskList.add(doc.toObject(TaskModel.class));
                            }
                            for(TaskModel taskModel : allTaskList) {
                                if(taskModel.getStatus().equals(TaskModel.STATUS_DONE))
                                    doneTaskList.add(taskModel);
                                else if(taskModel.getStatus().equals(TaskModel.STATUS_DOING))
                                    doingTaskList.add(taskModel);
                                else
                                    todoTaskList.add(taskModel);
                            }
                            todoRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                            doingRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));
                            doneRecyclerview.setLayoutManager(new LinearLayoutManager(getActivity().getApplicationContext()));

                            todoTaskAdapter = new TaskAdapter(getContext(), todoTaskList, MyBoardFragment.this);
                            doingTaskAdapter = new TaskAdapter(getContext(), doingTaskList, MyBoardFragment.this);
                            doneTaskAdapter = new TaskAdapter(getContext(), doneTaskList, MyBoardFragment.this);

                            todoRecyclerview.setAdapter(todoTaskAdapter);
                            doingRecyclerview.setAdapter(doingTaskAdapter);
                            doneRecyclerview.setAdapter(doneTaskAdapter);
                        }
                    }
                });
    }

    public void initLayout() {
        if(currentProject != null) {
            projectName.setText(currentProject.getProjectName());
            getTasks();
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

    public void showTodoBoard() {
        todoCompressed.setVisibility(View.GONE);
        todoExpanded.setVisibility(View.VISIBLE);

        doingCompressed.setVisibility(View.VISIBLE);
        doingExpanded.setVisibility(View.GONE);

        doneCompressed.setVisibility(View.VISIBLE);
        doneExpanded.setVisibility(View.GONE);
    }

    public void showDoingBoard() {
        todoCompressed.setVisibility(View.VISIBLE);
        todoExpanded.setVisibility(View.GONE);

        doingCompressed.setVisibility(View.GONE);
        doingExpanded.setVisibility(View.VISIBLE);

        doneCompressed.setVisibility(View.VISIBLE);
        doneExpanded.setVisibility(View.GONE);
    }

    public void showDoneBoard() {
        todoCompressed.setVisibility(View.VISIBLE);
        todoExpanded.setVisibility(View.GONE);

        doingCompressed.setVisibility(View.VISIBLE);
        doingExpanded.setVisibility(View.GONE);

        doneCompressed.setVisibility(View.GONE);
        doneExpanded.setVisibility(View.VISIBLE);
    }

    @Override
    public void onItemClick(View view, int position) {
        Log.e("Task Name", todoTaskList.get(position).getTaskName());
    }
}