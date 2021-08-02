package com.example.pins.ui.team_board;

import android.app.AlertDialog;
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
import com.example.pins.databinding.FragmentTeamboardBinding;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.TaskModel;
import com.example.pins.models.UserModel;
import com.example.pins.structures.NameAdapter;
import com.example.pins.structures.TaskAdapter;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class TeamBoardFragment extends Fragment implements TaskAdapter.ItemClickListener {

    private FragmentTeamboardBinding binding;

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
    RecyclerView searchedTasksRecyclerview;

    TaskAdapter todoTaskAdapter;
    TaskAdapter doingTaskAdapter;
    TaskAdapter doneTaskAdapter;
    TaskAdapter searchedTaskAdapter;

    FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();

    UserModel userInstance;
    ProjectModel currentProject;
    List<TaskModel> allTaskList = new ArrayList<>();
    List<TaskModel> todoTaskList = new ArrayList<>();
    List<TaskModel> doingTaskList = new ArrayList<>();
    List<TaskModel> doneTaskList = new ArrayList<>();
    List<TaskModel> searchedTaskList = new ArrayList<>();
    String query = "";
    String currentTaskStatus;

    Boolean isTodoBoardActive = true;
    Boolean isDoingBoardActive = false;
    Boolean isDoneBoardActive = false;


    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentTeamboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        projectName = binding.fragmentTeamboardProjectName;
        errorMsgLayout = binding.fragmentTeamboardErrorMsg;
        joinNowBtn = binding.fragmentTeamboardJoinNowBtn;
        searchField = binding.fragmentTeamboardSearchbarEdittext;
        searchBtn = binding.fragmentTeamboardSearchbarSearchBtn;
        closeSearchBtn = binding.fragmentTeamboardSearchbarCloseBtn;
        parentLayout = binding.fragmentTeamboardParentLayout;
        searchLayout = binding.fragmentTeamboardSearchLayout;
        boardLayout = binding.fragmentTeamboardBoardLayout;
        todoCompressed = binding.fragmentTeamboardTodoLayoutCompressed;
        todoExpanded = binding.fragmentTeamboardTodoLayoutExpanded;
        doingCompressed = binding.fragmentTeamboardDoingLayoutCompressed;
        doingExpanded = binding.fragmentTeamboardDoingLayoutExpanded;
        doneCompressed = binding.fragmentTeamboardDoneLayoutCompressed;
        doneExpanded = binding.fragmentTeamboardDoneLayoutExpanded;
        todoExpandBtn = binding.fragmentTeamboardTodoExpandBtn;
        doingExpandBtn = binding.fragmentTeamboardDoingExpandBtn;
        doneExpandBtn = binding.fragmentTeamboardDoneExpandBtn;
        todoRecyclerview = binding.fragmentTeamboardTodoRecyclerview;
        doingRecyclerview = binding.fragmentTeamboardDoingRecyclerview;
        doneRecyclerview = binding.fragmentTeamboardDoneRecyclerview;
        searchedTasksRecyclerview = binding.fragmentTeamboardSearchRecyclerview;

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
                    showSearchLayout();
                    searchTasks(query);
                }
                else {
                    searchBtn.setVisibility(View.VISIBLE);
                    closeSearchBtn.setVisibility(View.GONE);
                    showBoardLayout();
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
                    showSearchLayout();
                    searchTasks(query);
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
                showBoardLayout();
            }
        });

        joinNowBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), ProjectSearchActivity.class));
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

    private void searchTasks(String taskName) {
        searchedTaskList.clear();
        String userFullName = userInstance.getFirstname() + " " + userInstance.getLastname();

        firestoreInstance.collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Tasks")
                .orderBy("taskName")
                .whereGreaterThanOrEqualTo("taskName", taskName)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            for (QueryDocumentSnapshot doc : task.getResult()) {
                                searchedTaskList.add(doc.toObject(TaskModel.class));
                            }
                            searchedTasksRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

                            searchedTaskAdapter = new TaskAdapter(getContext(), searchedTaskList,TeamBoardFragment.this);

                            searchedTasksRecyclerview.setAdapter(searchedTaskAdapter);
                        }
                    }
                });
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
                            todoRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
                            doingRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));
                            doneRecyclerview.setLayoutManager(new LinearLayoutManager(requireContext()));

                            todoTaskAdapter = new TaskAdapter(getContext(), todoTaskList, TeamBoardFragment.this);
                            doingTaskAdapter = new TaskAdapter(getContext(), doingTaskList, TeamBoardFragment.this);
                            doneTaskAdapter = new TaskAdapter(getContext(), doneTaskList, TeamBoardFragment.this);

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

        isTodoBoardActive = true;
        isDoingBoardActive = false;
        isDoneBoardActive = false;
    }

    public void showDoingBoard() {
        todoCompressed.setVisibility(View.VISIBLE);
        todoExpanded.setVisibility(View.GONE);

        doingCompressed.setVisibility(View.GONE);
        doingExpanded.setVisibility(View.VISIBLE);

        doneCompressed.setVisibility(View.VISIBLE);
        doneExpanded.setVisibility(View.GONE);

        isTodoBoardActive = false;
        isDoingBoardActive = true;
        isDoneBoardActive = false;
    }

    public void showDoneBoard() {
        todoCompressed.setVisibility(View.VISIBLE);
        todoExpanded.setVisibility(View.GONE);

        doingCompressed.setVisibility(View.VISIBLE);
        doingExpanded.setVisibility(View.GONE);

        doneCompressed.setVisibility(View.GONE);
        doneExpanded.setVisibility(View.VISIBLE);

        isTodoBoardActive = false;
        isDoingBoardActive = false;
        isDoneBoardActive = true;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void showDialog(TaskModel task) {
        // Get current task status
        currentTaskStatus = task.getStatus();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_task_details, null);
        alertDialogBuilder.setView(dialogView);

        TextView dialogTaskDescription = dialogView.findViewById(R.id.alert_dialog_task_description_tv);
        TextView dialogPriorityTV = dialogView.findViewById(R.id.alert_dialog_task_details_priority_tv);
        TextView dialogTodoBtn = dialogView.findViewById(R.id.alert_dialog_task_details_todo_btn);
        TextView dialogDoingBtn = dialogView.findViewById(R.id.alert_dialog_task_details_doing_btn);
        TextView dialogDoneBtn = dialogView.findViewById(R.id.alert_dialog_task_details_done_btn);
        RecyclerView dialogRV = dialogView.findViewById(R.id.alert_dialog_task_details_names_rv);


        // Set task name/description
        dialogTaskDescription.setText(task.getTaskName());

        // Set task priority
        if(task.getPriority().equals(TaskModel.PRIORITY_HIGH)) {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.red));
            dialogPriorityTV.setText("High");
        }
        else if (task.getPriority().equals(TaskModel.PRIORITY_MEDIUM)) {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.yellow));
            dialogPriorityTV.setText("Medium");
        }
        else {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.green));
            dialogPriorityTV.setText("Low");
        }

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        NameAdapter nameAdapter = new NameAdapter(requireContext(), task.getAssignedTo());
        dialogRV.setAdapter(nameAdapter);

        // Set status
        if(currentTaskStatus.equals(TaskModel.STATUS_TODO)) {
            dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));
            dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));

            dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
            dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
        }
        else if(currentTaskStatus.equals(TaskModel.STATUS_DOING)) {
            dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));
            dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));

            dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
            dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
        }
        else {
            dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
            dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));

            dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
            dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
            dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));
        }

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        dialogTodoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));
                dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));

                dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
                dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));

                currentTaskStatus = TaskModel.STATUS_TODO;

                if(!currentTaskStatus.equals(task.getStatus())) {
                    updateTaskDetails(task);
                    alertDialog.dismiss();
                }
            }
        });

        dialogDoingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));
                dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));

                dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
                dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));

                currentTaskStatus = TaskModel.STATUS_DOING;

                if(!currentTaskStatus.equals(task.getStatus())) {
                    updateTaskDetails(task);
                    alertDialog.dismiss();
                }
            }
        });

        dialogDoneBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogTodoBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoingBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.white));
                dialogDoneBtn.setBackgroundColor(requireActivity().getResources().getColor(R.color.green));

                dialogTodoBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
                dialogDoingBtn.setTextColor(requireActivity().getResources().getColor(R.color.dark_grey));
                dialogDoneBtn.setTextColor(requireActivity().getResources().getColor(R.color.white));

                currentTaskStatus = TaskModel.STATUS_DONE;

                if(!currentTaskStatus.equals(task.getStatus())) {
                    updateTaskDetails(task);
                    alertDialog.dismiss();
                }
            }
        });
    }

    void updateTaskDetails(TaskModel task) {
        task.setStatus(currentTaskStatus);
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Tasks")
                .document(task.getTaskId())
                .set(task, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            getTasks();
                        }
                    }
                });
    }

    @Override
    public void onItemClick(View view, int position) {
        if(isTodoBoardActive) {
            Log.e("Task Name", todoTaskList.get(position).getTaskName());
            showDialog(todoTaskList.get(position));
        }
        if(isDoingBoardActive) {
            Log.e("Task Name", doingTaskList.get(position).getTaskName());
            showDialog(doingTaskList.get(position));
        }
        if(isDoneBoardActive) {
            Log.e("Task Name", doneTaskList.get(position).getTaskName());
            showDialog(doneTaskList.get(position));
        }
    }
}