package com.example.task_manager;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity {

    private static final int ADD_TASK_REQUEST = 1;
    private RecyclerView taskRv;
    private ArrayList<TaskModel> dataList = new ArrayList<>();
    private TaskListAdapter taskListAdapter;
    private FirebaseFirestore db;
    private String TAG = "Homepage";
    private TextView userNametv;
    private CircleImageView userProfileIv;
    private ProgressBar progressBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        userNametv = findViewById(R.id.userNametv);
        userProfileIv = findViewById(R.id.userProfileIv);
        progressBar = findViewById(R.id.progress);

        // Set up RecyclerView
        taskRv = findViewById(R.id.taskListRv);
        taskListAdapter = new TaskListAdapter(dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        taskRv.setLayoutManager(layoutManager);
        taskRv.setAdapter(taskListAdapter);

        // Fetch tasks from Firestore
        fetchTasksFromFirestore();

        // Handle Floating Action Button click
        FloatingActionButton fab = findViewById(R.id.addTaskFAB);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_pulse);
        fab.startAnimation(pulseAnimation);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(HomeActivity.this, AddTaskActivity.class), ADD_TASK_REQUEST);
            }
        });

        // Handle Profile Image click to show menu
        userProfileIv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPopupMenu(v);
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUserInfo(); // Update user information whenever activity resumes
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == Activity.RESULT_OK) {
            fetchTasksFromFirestore(); // Refresh task list if task was added
        }
    }

    // Method to update user information (name and profile picture)
    private void updateUserInfo() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            if (user.getDisplayName() != null && user.getPhotoUrl() != null) {
                // Use display name and profile picture from Firebase Authentication
                userNametv.setText(user.getDisplayName());
                Picasso.get().load(user.getPhotoUrl()).placeholder(R.drawable.profpic).into(userProfileIv);
            } else {
                // Fetch user name from Firestore and use default profile picture
                db.collection("users").document(user.getUid()).get()
                        .addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                            @Override
                            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                                if (task.isSuccessful()) {
                                    DocumentSnapshot document = task.getResult();
                                    if (document.exists()) {
                                        String userName = document.getString("name");
                                        userNametv.setText(userName);
                                        userProfileIv.setImageResource(R.drawable.profpic); // Default profile picture
                                    } else {
                                        Log.d(TAG, "No such document");
                                        Toast.makeText(HomeActivity.this, "User data not found", Toast.LENGTH_SHORT).show();
                                    }
                                } else {
                                    Log.d(TAG, "Error getting document: ", task.getException());
                                    Toast.makeText(HomeActivity.this, "Error fetching user data", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
            }
        }
    }

    // Method to fetch tasks from Firestore
    private void fetchTasksFromFirestore() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        if (task.isSuccessful()) {
                            ArrayList<TaskModel> updatedTaskList = new ArrayList<>();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                TaskModel taskModel = document.toObject(TaskModel.class);
                                taskModel.setTaskId(document.getId());
                                updatedTaskList.add(taskModel);
                            }
                            // Update adapter with new task list
                            updateTaskList(updatedTaskList);
                        } else {
                            Log.d(TAG, "Error getting documents: ", task.getException());
                            Toast.makeText(HomeActivity.this, "Error getting tasks", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to update task list in adapter
    private void updateTaskList(ArrayList<TaskModel> updatedTaskList) {
        dataList.clear(); // Clear existing data
        dataList.addAll(updatedTaskList); // Add updated data
        taskListAdapter.notifyDataSetChanged(); // Notify adapter of changes
    }

    // Method to show logout menu
    private void showPopupMenu(View view) {
        PopupMenu popup = new PopupMenu(this, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.logout, popup.getMenu());
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if (item.getItemId() == R.id.action_logout) {
                    logout(); // Logout if menu item clicked
                    return true;
                }
                return false;
            }
        });
        popup.show();
    }

    // Method to handle logout
    private void logout() {
        FirebaseAuth.getInstance().signOut(); // Sign out from Firebase
        startActivity(new Intent(HomeActivity.this, MainActivity.class)); // Redirect to MainActivity
        finish(); // Finish current activity
    }
}
