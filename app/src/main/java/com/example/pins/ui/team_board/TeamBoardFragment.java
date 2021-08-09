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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.pins.R;
import com.example.pins.databinding.FragmentTeamboardBinding;
import com.example.pins.models.ContactModel;
import com.example.pins.models.ProjectModel;
import com.example.pins.models.TaskModel;
import com.example.pins.models.UserModel;
import com.example.pins.models.ProjectMemberModel;
import com.example.pins.structures.AssignedToNameAdapter;
import com.example.pins.structures.ShowProjectMembersAdapter;
import com.example.pins.structures.TaskAdapter;
import com.example.pins.ui.all_chats.AllChatsActivity;
import com.example.pins.ui.project_search.ProjectSearchActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TeamBoardFragment extends Fragment implements TaskAdapter.ItemClickListener,
        AssignedToNameAdapter.ItemClickListener, ShowProjectMembersAdapter.ItemClickListener{

    private FragmentTeamboardBinding binding;

    TextView projectName;
    TextView joinNowBtn;
    EditText searchField;
    TextView incomingRequestsTv;
    ImageButton searchBtn;
    ImageButton closeSearchBtn;
    ImageButton chatBtn;
    ImageButton createTaskBtn;
    ImageButton showTeamBtn;
    ImageButton incomingRequestsBtn;

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
    LinearLayout operationsLayout;

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

    AssignedToNameAdapter assignedToNameAdapter;

    ShowProjectMembersAdapter showProjectMembersAdapter;

    FirebaseFirestore firestoreInstance = FirebaseFirestore.getInstance();

    List<TaskModel> allTaskList = new ArrayList<>();
    List<TaskModel> todoTaskList = new ArrayList<>();
    List<TaskModel> doingTaskList = new ArrayList<>();
    List<TaskModel> doneTaskList = new ArrayList<>();
    List<TaskModel> searchedTaskList = new ArrayList<>();
    List<ProjectMemberModel> currentProjectMembersList = new ArrayList<>();
    List<ProjectMemberModel> incomingRequestsProjectMembersList = new ArrayList<>();
    List<String> createTaskAssignedToList = new ArrayList<>();

    UserModel userInstance = UserModel.getUserInstance();
    ProjectModel currentProject;
    String currentUserProjectRole = UserModel.ROLE_EMPLOYEE;

    String query = "";
    String currentTaskStatus;

    Boolean isTodoBoardActive = true;
    Boolean isDoingBoardActive = false;
    Boolean isDoneBoardActive = false;
    Boolean isSearching = false;


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
        chatBtn = binding.fragmentTeamboardMsgBtn;
        operationsLayout = binding.fragmentTeamboardOperationsBar;
        createTaskBtn = binding.fragmentTeamboardCreateTaskBtn;
        showTeamBtn = binding.fragmentTeamboardShowTeamBtn;
        incomingRequestsBtn = binding.fragmentTeamboardRequestsBtn;
        incomingRequestsTv = binding.fragmentTeamboardRequestsTv;


        searchBtn.setVisibility(View.VISIBLE);
        closeSearchBtn.setVisibility(View.GONE);

        getCurrentProject();

        createTaskBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCreateTaskDialogBox();
            }
        });

        showTeamBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectMembersDialogBox(true, false, currentProjectMembersList);
            }
        });

        incomingRequestsBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProjectMembersDialogBox(true, true, incomingRequestsProjectMembersList);
            }
        });

        chatBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(requireContext(), AllChatsActivity.class));
            }
        });

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
                    if(currentProject != null) {
                        showBoardLayout();
                    }
                    else {
                        showErrorLayout();
                    }
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
                    showSnackBar(getResources().getString(R.string.enter_a_valid_task_name));
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
                            if(currentUserProjectRole.equals(UserModel.ROLE_MANAGER)) {
                                operationsLayout.setVisibility(View.VISIBLE);
                                incomingRequestsBtn.setVisibility(View.VISIBLE);
                            }
                            else {
                                operationsLayout.setVisibility(View.INVISIBLE);
                                incomingRequestsBtn.setVisibility(View.GONE);
                            }
                        }
                    });
        }
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
                                getCurrentProjectMembers();
                                getCurrentProjectMemberRole();
                                initLayout();
                            }
                        }
                    });
        }
    }

    public void getCurrentProjectMembers() {
        currentProjectMembersList.clear();
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
                                currentProjectMembersList.add(doc.toObject(ProjectMemberModel.class));
                            }
                            setIncomingRequestsListener();
                        }
                    }
                });

    }

    public void setIncomingRequestsListener() {
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Join Requests")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable @org.jetbrains.annotations.Nullable QuerySnapshot snapshot, @Nullable @org.jetbrains.annotations.Nullable FirebaseFirestoreException error) {
                        if(error != null) {
                            Log.e( "Listen failed: ", error.toString());
                            return;
                        }
                        if(snapshot != null) {
                            incomingRequestsProjectMembersList.clear();
                            for(QueryDocumentSnapshot doc : snapshot) {
                                incomingRequestsProjectMembersList.add(doc.toObject(ProjectMemberModel.class));
                            }
                            if(incomingRequestsProjectMembersList.size() > 0) {
                                incomingRequestsTv.setVisibility(View.VISIBLE);
                                incomingRequestsTv.setText(String.valueOf(incomingRequestsProjectMembersList.size()));
                            }
                            else {
                                incomingRequestsTv.setVisibility(View.GONE);
                            }
                            if(showProjectMembersAdapter != null) {
                                showProjectMembersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
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

    private void searchTasks(String taskName) {
        if(currentProject != null) {
            searchedTaskList.clear();

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
    }

    public void addTaskToDB(TaskModel taskModel) {
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Tasks")
                .add(taskModel)
                .addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentReference> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            taskModel.setTaskId(task.getResult().getId());
                            FirebaseFirestore.getInstance()
                                    .collection("Projects")
                                    .document(currentProject.getProjectId())
                                    .collection("Tasks")
                                    .document(taskModel.getTaskId())
                                    .set(taskModel, SetOptions.merge())
                                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull @NotNull Task<Void> task) {
                                            if(task.isSuccessful()) {
                                                getTasks();
                                            }
                                        }
                                    });
                        }
                    }
                });
    }

    public void addMemberToProject(ProjectMemberModel projectMember) {
        List<ProjectMemberModel> projectMembers = new ArrayList<>();
        List<ContactModel> contactList = new ArrayList<>();

        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(projectMember.getUserid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            UserModel user = task.getResult().toObject(UserModel.class);

                            List<String> allProjects = new ArrayList<>();

                            if (user.getAllProjects() != null) {
                                allProjects = user.getAllProjects();
                            }
                            else {
                                user.setCurrentProjectId(currentProject.getProjectId());
                            }
                            allProjects.add(currentProject.getProjectId());

                            // Update current user info such as currentProjectId
                            user.setAllProjects(allProjects);

                            // Update Current User Data in DB
                            firestoreInstance.collection("Users")
                                    .document(user.getUserid())
                                    .set(user, SetOptions.merge());
                        }
                    }
                });


        // Get all project members and add to contactList + Add current user as a contact to all other project members
        firestoreInstance.collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Project Members")
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<QuerySnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            ContactModel userContactModel = new ContactModel(
                                    projectMember.getUserid(),
                                    projectMember.getFirstname(),
                                    projectMember.getLastname(),
                                    projectMember.getImageUrl(),
                                    "",
                                    "",
                                    0L,
                                    true
                            );
                            for(QueryDocumentSnapshot doc : task.getResult()) {
                                projectMembers.add(doc.toObject(ProjectMemberModel.class));
                            }
                            for(ProjectMemberModel projectMemberModel : projectMembers) {
                                firestoreInstance.collection("Projects")
                                        .document(currentProject.getProjectId())
                                        .collection("Project Members")
                                        .document(projectMemberModel.getUserid())
                                        .collection("Contacts")
                                        .document(projectMember.getUserid())
                                        .set(userContactModel, SetOptions.merge());

                                ContactModel contact = new ContactModel(
                                        projectMemberModel.getUserid(),
                                        projectMemberModel.getFirstname(),
                                        projectMemberModel.getLastname(),
                                        projectMemberModel.getImageUrl(),
                                        "",
                                        "",
                                        0L,
                                        true
                                );
                                contactList.add(contact);
                            }
                        }
                    }
                });

        // Add the user to Project Members + Add all other project members as Contacts to the current user
        firestoreInstance.collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Project Members")
                .document(projectMember.getUserid())
                .set(projectMember, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            for(ContactModel contact : contactList) {
                                firestoreInstance.collection("Projects")
                                        .document(currentProject.getProjectId())
                                        .collection("Project Members")
                                        .document(projectMember.getUserid())
                                        .collection("Contacts")
                                        .document(contact.getUserid())
                                        .set(contact, SetOptions.merge());
                            }
                            getCurrentProjectMembers();
                        }
                    }
                });

        rejectJoinRequest(projectMember);
    }

    public void rejectJoinRequest(ProjectMemberModel projectMember) {
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Join Requests")
                .document(projectMember.getUserid())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            if(showProjectMembersAdapter != null) {
                                showProjectMembersAdapter.notifyDataSetChanged();
                            }
                        }
                    }
                });
    }

    void updateTaskDetails(TaskModel task) {
        //task.setStatus(currentTaskStatus);
        Log.e("TASK ID", task.getTaskId());
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

        if(isSearching) {
            searchedTaskAdapter.notifyDataSetChanged();
        }
    }

    void deleteTask(TaskModel taskModel) {
        //task.setStatus(currentTaskStatus);
        FirebaseFirestore.getInstance()
                .collection("Projects")
                .document(currentProject.getProjectId())
                .collection("Tasks")
                .document(taskModel.getTaskId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<Void> task) {
                        if(task.isSuccessful()) {
                            getTasks();
                        }
                    }
                });

        if(isSearching) {
            searchedTaskAdapter.notifyDataSetChanged();
        }
    }

    public void removeMemberFromProject(ProjectMemberModel projectMember) {

        // Remove projectMember as a contact from all other Project Members
        for(ProjectMemberModel member : currentProjectMembersList) {
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
        FirebaseFirestore.getInstance()
                .collection("Users")
                .document(projectMember.getUserid())
                .get()
                .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull @NotNull Task<DocumentSnapshot> task) {
                        if(task.isSuccessful() && task.getResult() != null) {
                            UserModel projectMemberUserModel = task.getResult().toObject(UserModel.class);
                            if(projectMemberUserModel != null) {

                                projectMemberUserModel.getAllProjects().remove(currentProject.getProjectId());

                                if(projectMemberUserModel.getAllProjects().size() > 0) {
                                    if(projectMemberUserModel.getCurrentProjectId().equals(currentProject.getProjectId())) {
                                        projectMemberUserModel.setCurrentProjectId(projectMemberUserModel.getAllProjects().get(0));
                                    }
                                }
                                else {
                                    projectMemberUserModel.setCurrentProjectId("");
                                }

                                FirebaseFirestore.getInstance()
                                        .collection("Users")
                                        .document(projectMember.getUserid())
                                        .set(projectMemberUserModel, SetOptions.merge())
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull @NotNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    showSnackBar(projectMember.getFirstname() + " "
                                                            + projectMember.getLastname()
                                                            + " " + R.string.removed_from_the_project);
                                                }
                                            }
                                        });
                            }
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
        isSearching = false;
        errorMsgLayout.setVisibility(View.VISIBLE);
        searchLayout.setVisibility(View.GONE);
        boardLayout.setVisibility(View.GONE);
    }

    public void showSearchLayout() {
        isSearching = true;
        errorMsgLayout.setVisibility(View.GONE);
        searchLayout.setVisibility(View.VISIBLE);
        boardLayout.setVisibility(View.GONE);
    }

    public void showBoardLayout() {
        isSearching = false;
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

    public void showCreateTaskDialogBox() {
        createTaskAssignedToList.clear();

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_create_task, null);
        alertDialogBuilder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.alert_dialog_create_task_title);
        TextInputLayout taskNameTil = dialogView.findViewById(R.id.alert_dialog_create_task_til);
        TextInputEditText taskNameTie = dialogView.findViewById(R.id.alert_dialog_create_task_tie);
        RecyclerView dialogRV = dialogView.findViewById(R.id.alert_dialog_create_task_names_rv);
        ImageButton addPerson = dialogView.findViewById(R.id.alert_dialog_create_task_add_person_btn);
        RadioGroup priorityRadioGroup = dialogView.findViewById(R.id.alert_dialog_create_task_radio_group);
        RadioButton highRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_high_radio_btn);
        RadioButton mediumRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_medium_radio_btn);
        RadioButton lowRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_low_radio_btn);
        Button cancelBtn = dialogView.findViewById(R.id.alert_dialog_create_task_no_btn);
        Button createBtn = dialogView.findViewById(R.id.alert_dialog_create_task_yes_btn);

        title.setText(R.string.create_task);

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        assignedToNameAdapter = new AssignedToNameAdapter(requireContext(),
                createTaskAssignedToList, true, this);
        dialogRV.setAdapter(assignedToNameAdapter);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createTaskAssignedToList.size() < 4) {
                    showProjectMembersDialogBox(false, false, currentProjectMembersList);
                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(taskNameTie.getText() == null || Objects.requireNonNull(taskNameTie.getText()).toString().isEmpty()) {
                    // Show Error
                    showSnackBar(getResources().getString(R.string.task_name_cannot_be_empty));
                }
                else if(createTaskAssignedToList.isEmpty()) {
                    // Show Error
                    showSnackBar(getResources().getString(R.string.assign_task_to_atleast_1_person));
                }
                else {
                    String priority = TaskModel.PRIORITY_HIGH;

                    switch (priorityRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.alert_dialog_create_task_high_radio_btn:
                            priority = TaskModel.PRIORITY_HIGH;
                            break;
                        case R.id.alert_dialog_create_task_medium_radio_btn:
                            priority = TaskModel.PRIORITY_MEDIUM;
                            break;
                        case R.id.alert_dialog_create_task_low_radio_btn:
                            priority = TaskModel.PRIORITY_LOW;
                            break;
                    }

                    TaskModel newTask = new TaskModel(
                            "",
                            taskNameTie.getText().toString(),
                            createTaskAssignedToList,
                            TaskModel.STATUS_TODO,
                            priority
                    );

                    addTaskToDB(newTask);
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void showEditTaskDialogBox(TaskModel taskModel) {
        createTaskAssignedToList.clear();
        createTaskAssignedToList.addAll(taskModel.getAssignedTo());

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_create_task, null);
        alertDialogBuilder.setView(dialogView);

        TextView title = dialogView.findViewById(R.id.alert_dialog_create_task_title);
        TextInputLayout taskNameTil = dialogView.findViewById(R.id.alert_dialog_create_task_til);
        TextInputEditText taskNameTie = dialogView.findViewById(R.id.alert_dialog_create_task_tie);
        RecyclerView dialogRV = dialogView.findViewById(R.id.alert_dialog_create_task_names_rv);
        ImageButton addPerson = dialogView.findViewById(R.id.alert_dialog_create_task_add_person_btn);
        RadioGroup priorityRadioGroup = dialogView.findViewById(R.id.alert_dialog_create_task_radio_group);
        RadioButton highRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_high_radio_btn);
        RadioButton mediumRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_medium_radio_btn);
        RadioButton lowRadioBtn = dialogView.findViewById(R.id.alert_dialog_create_task_low_radio_btn);
        Button cancelBtn = dialogView.findViewById(R.id.alert_dialog_create_task_no_btn);
        Button createBtn = dialogView.findViewById(R.id.alert_dialog_create_task_yes_btn);


        title.setText(R.string.edit_task);

        taskNameTie.setText(taskModel.getTaskName());

        if(taskModel.getPriority().equals(TaskModel.PRIORITY_HIGH)) {
            highRadioBtn.setChecked(true);
        }
        else if(taskModel.getPriority().equals(TaskModel.PRIORITY_MEDIUM)) {
            mediumRadioBtn.setChecked(true);
        }
        else {
            lowRadioBtn.setChecked(true);
        }

        createBtn.setText(R.string.save);

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        assignedToNameAdapter = new AssignedToNameAdapter(requireContext(),
                createTaskAssignedToList, true, this);
        dialogRV.setAdapter(assignedToNameAdapter);

        addPerson.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(createTaskAssignedToList.size() < 4) {
                    showProjectMembersDialogBox(false, false, currentProjectMembersList);
                }
            }
        });

        alertDialog = alertDialogBuilder.create();
        alertDialog.setCancelable(true);
        alertDialog.show();

        cancelBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                alertDialog.dismiss();
            }
        });

        createBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(taskNameTie.getText() == null || Objects.requireNonNull(taskNameTie.getText()).toString().isEmpty()) {
                    // Show Error
                    showSnackBar(getResources().getString(R.string.task_name_cannot_be_empty));
                }
                else if(createTaskAssignedToList.isEmpty()) {
                    // Show Error
                    showSnackBar(getResources().getString(R.string.assign_task_to_atleast_1_person));
                }
                else {
                    String priority = TaskModel.PRIORITY_HIGH;

                    switch (priorityRadioGroup.getCheckedRadioButtonId()) {
                        case R.id.alert_dialog_create_task_high_radio_btn:
                            priority = TaskModel.PRIORITY_HIGH;
                            break;
                        case R.id.alert_dialog_create_task_medium_radio_btn:
                            priority = TaskModel.PRIORITY_MEDIUM;
                            break;
                        case R.id.alert_dialog_create_task_low_radio_btn:
                            priority = TaskModel.PRIORITY_LOW;
                            break;
                    }

                    TaskModel newTask = new TaskModel(
                            taskModel.getTaskId(),
                            taskNameTie.getText().toString(),
                            createTaskAssignedToList,
                            TaskModel.STATUS_TODO,
                            priority
                    );

                    updateTaskDetails(newTask);
                    alertDialog.dismiss();
                }
            }
        });
    }

    public void showProjectMembersDialogBox(Boolean showRemoveBtn, Boolean showAcceptBtn, List<ProjectMemberModel> projectMemberModelList) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_show_project_members, null);
        alertDialogBuilder.setView(dialogView);

        TextView titleTv = dialogView.findViewById(R.id.alert_dialog_show_project_members_tv);
        RecyclerView dialogRV = dialogView.findViewById(R.id.alert_dialog_show_project_members_rv);
        Button doneBtn = dialogView.findViewById(R.id.alert_dialog_show_project_members_yes_btn);

        if(showAcceptBtn) {
            titleTv.setText(R.string.user_requests);
        }
        else {
            titleTv.setText(R.string.project_members);
        }

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext()));
        showProjectMembersAdapter = new ShowProjectMembersAdapter(requireContext(),
                projectMemberModelList, showRemoveBtn, showAcceptBtn, this);
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

    public void showConfirmRemoveMemberDialogBox(ProjectMemberModel projectMember) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_confirm_remove_member, null);
        alertDialogBuilder.setView(dialogView);

        TextView nameTv = dialogView.findViewById(R.id.alert_dialog_confirm_remove_member_name_tv);
        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_confirm_remove_member_yes_btn);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_confirm_remove_member_no_btn);

        String fullName = projectMember.getFirstname() + " " + projectMember.getLastname();
        nameTv.setText(fullName);

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
                if(projectMember.getUserid().equals(userInstance.getUserid())) {
                    showSnackBar(getResources().getString(R.string.you_cannot_remove_yourself));
                }
                else {
                    removeMemberFromProject(projectMember);
                    currentProjectMembersList.remove(projectMember);
                    showProjectMembersAdapter.notifyDataSetChanged();
                }
                alertDialog.dismiss();
            }
        });
    }

    public void showConfirmDeleteTaskDialogBox(TaskModel taskModel) {

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(requireActivity());
        AlertDialog alertDialog;

        LayoutInflater inflater = this.getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.alert_dialog_confirm_delete_task, null);
        alertDialogBuilder.setView(dialogView);

        TextView nameTv = dialogView.findViewById(R.id.alert_dialog_confirm_delete_task_name);
        Button yesBtn = dialogView.findViewById(R.id.alert_dialog_confirm_delete_task_yes_btn);
        Button noBtn = dialogView.findViewById(R.id.alert_dialog_confirm_delete_task_no_btn);

        nameTv.setText(taskModel.getTaskName());

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
                deleteTask(taskModel);
                alertDialog.dismiss();
            }
        });
    }

    public void showTaskDetailsDialogBox(TaskModel task) {
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
        LinearLayout operationsBar = dialogView.findViewById(R.id.alert_dialog_task_details_manager_operations_bar);
        Button editBtn = dialogView.findViewById(R.id.alert_dialog_task_details_edit_btn);
        Button deleteBtn = dialogView.findViewById(R.id.alert_dialog_task_details_delete_btn);

        if(userInstance.getRole().equals(UserModel.ROLE_MANAGER)) {
            operationsBar.setVisibility(View.VISIBLE);
        }
        else {
            operationsBar.setVisibility(View.GONE);
        }

        // Set task name/description
        dialogTaskDescription.setText(task.getTaskName());

        // Set task priority
        if(task.getPriority().equals(TaskModel.PRIORITY_HIGH)) {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.red));
            dialogPriorityTV.setText(R.string.high);
        }
        else if (task.getPriority().equals(TaskModel.PRIORITY_MEDIUM)) {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.yellow));
            dialogPriorityTV.setText(R.string.medium);
        }
        else {
            dialogPriorityTV.setTextColor(requireActivity().getResources().getColor(R.color.green));
            dialogPriorityTV.setText(R.string.low);
        }

        // Set names list
        dialogRV.setLayoutManager(new LinearLayoutManager(requireContext(), RecyclerView.HORIZONTAL, false));
        AssignedToNameAdapter assignedToNameAdapter = new AssignedToNameAdapter(requireContext(),
                task.getAssignedTo(), false, this);
        dialogRV.setAdapter(assignedToNameAdapter);

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
                    task.setStatus(currentTaskStatus);
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
                    task.setStatus(currentTaskStatus);
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
                    task.setStatus(currentTaskStatus);
                    updateTaskDetails(task);
                    alertDialog.dismiss();
                }
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showEditTaskDialogBox(task);
                alertDialog.dismiss();
            }
        });

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showConfirmDeleteTaskDialogBox(task);
                alertDialog.dismiss();
            }
        });
    }

    public void showSnackBar(String message) {
        Snackbar.make(parentLayout, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getResources().getColor(R.color.green_dark))
                .show();
    }

    @Override
    public void onTaskAdapterItemClick(View view, int position) {
        String userFullName = userInstance.getFirstname() + " " + userInstance.getLastname();
        if(isSearching) {
            if(searchedTaskList.get(position).getAssignedTo().contains(userFullName)) {
                showTaskDetailsDialogBox(searchedTaskList.get(position));
            }
        }
        else {
            if (isTodoBoardActive) {
                if(todoTaskList.get(position).getAssignedTo().contains(userFullName)) {
                    showTaskDetailsDialogBox(todoTaskList.get(position));
                }
            }
            if (isDoingBoardActive) {
                if(doingTaskList.get(position).getAssignedTo().contains(userFullName)) {
                    showTaskDetailsDialogBox(doingTaskList.get(position));
                }
            }
            if (isDoneBoardActive) {
                if(doneTaskList.get(position).getAssignedTo().contains(userFullName)) {
                    showTaskDetailsDialogBox(doneTaskList.get(position));
                }
            }
        }
    }

    @Override
    public void onAssignedToNameAdapterItemClick(View view, int position) {
        String removedName = createTaskAssignedToList.remove(position);
        assignedToNameAdapter.notifyDataSetChanged();
    }

    @Override
    public void onShowProjectMembersAdapterItemClick(View view, int position, Boolean showRemoveBtn, Boolean showAcceptBtn) {
        if(showRemoveBtn && !showAcceptBtn) {
            showConfirmRemoveMemberDialogBox(currentProjectMembersList.get(position));
        }
        else if(showRemoveBtn && showAcceptBtn) {
            if(view.getId() == R.id.widget_name_container_large_accept_btn) {
                addMemberToProject(incomingRequestsProjectMembersList.get(position));
            }
            else if(view.getId() == R.id.widget_name_container_large_remove_btn) {
                rejectJoinRequest(incomingRequestsProjectMembersList.get(position));
            }
        }
        else if(!showRemoveBtn) {
            String fullName = currentProjectMembersList.get(position).getFirstname() + " " + currentProjectMembersList.get(position).getLastname();
            if(!createTaskAssignedToList.contains(fullName)) {
                createTaskAssignedToList.add(fullName);
                assignedToNameAdapter.notifyDataSetChanged();
            }
            else {
                Log.e("Member Already Added", fullName);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}