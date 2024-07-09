package com.example.task_manager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;

public class HomeActivity extends AppCompatActivity implements TaskListAdapter.OnItemClickListener {

    private static final int ADD_TASK_REQUEST = 1;
    private RecyclerView taskRv;
    private ArrayList<TaskModel> dataList = new ArrayList<>();
    private TaskListAdapter taskListAdapter;
    private FirebaseFirestore db;
    private String TAG = "Homepage";
    private TextView userNametv;
    private CircleImageView userProfileIv;
    private ProgressBar progressBar;

    private ImageView gifImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        gifImageView = findViewById(R.id.gifImageView);
        Glide.with(this)
                .asGif()
                .load(R.drawable.calender_gif)
                .into(gifImageView);

        // Set OnClickListener to navigate to CalendarActivity
        gifImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                navigateToCalendarActivity();
            }
        });

        // Initialize Firebase
        db = FirebaseFirestore.getInstance();

        // Initialize Views
        userNametv = findViewById(R.id.userNametv);
        userProfileIv = findViewById(R.id.userProfileIv);
        progressBar = findViewById(R.id.progress);

        // Set up RecyclerView
        taskRv = findViewById(R.id.taskListRv);
        taskListAdapter = new TaskListAdapter(dataList, this);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        taskRv.setLayoutManager(layoutManager);
        taskRv.setAdapter(taskListAdapter);

        // Fetch user profile information
        updateUserProfile();

        // Fetch tasks from Firestore
        fetchTasksFromFirestore();

        // Handle Floating Action Button click
        FloatingActionButton fab = findViewById(R.id.addTaskFAB);
        Animation pulseAnimation = AnimationUtils.loadAnimation(this, R.anim.fab_pulse);
        fab.startAnimation(pulseAnimation);
        findViewById(R.id.addTaskFAB).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(HomeActivity.this, AddTaskActivity.class), ADD_TASK_REQUEST);
            }
        });

        // Handle Profile Image long click to show logout menu
        userProfileIv.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                showLogoutMenu(v);
                return true;
            }
        });
    }

    private void updateUserProfile() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        if (user != null) {
            // Set user's name
            String name = user.getDisplayName();
            if (name != null && !name.isEmpty()) {
                userNametv.setText(name);
            } else {
                userNametv.setText("User");
            }

            // Set user's profile picture
            String photoUrl = user.getPhotoUrl() != null ? user.getPhotoUrl().toString() : null;
            if (photoUrl != null) {
                Picasso.get().load(photoUrl).into(userProfileIv);
            } else {
                userProfileIv.setImageResource(R.drawable.profpic); // default image
            }
        }
    }

    private void navigateToCalendarActivity() {
        Intent intent = new Intent(HomeActivity.this, CalendarActivity.class);
        startActivity(intent);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fetchTasksFromFirestore(); // Update tasks on resume
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == ADD_TASK_REQUEST && resultCode == RESULT_OK) {
            fetchTasksFromFirestore(); // Refresh tasks after adding a new task
        }
    }

    // Method to fetch tasks from Firestore
    private void fetchTasksFromFirestore() {
        progressBar.setVisibility(View.VISIBLE); // Show progress bar
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        db.collection("tasks")
                .whereEqualTo("userId", userId)
                .addSnapshotListener(this, new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                        progressBar.setVisibility(View.GONE); // Hide progress bar
                        if (error != null) {
                            Log.e(TAG, "Error getting documents: ", error);
                            Toast.makeText(HomeActivity.this, "Error getting tasks", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        dataList.clear(); // Clear existing data
                        for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                            TaskModel taskModel = document.toObject(TaskModel.class);
                            taskModel.setTaskId(document.getId());
                            dataList.add(taskModel);
                        }
                        taskListAdapter.notifyDataSetChanged(); // Notify adapter of changes
                    }
                });
    }

    // Method to show logout menu
    private void showLogoutMenu(View view) {
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

    // Implementing TaskListAdapter.OnItemClickListener methods
    @Override
    public void onItemClick(TaskModel task, int position) {
        // Handle click on item to open detailed task page
        Intent intent = new Intent(HomeActivity.this, TaskDetailActivity.class);
        intent.putExtra("task", task);
        startActivity(intent);
    }

    @Override
    public void onItemLongClick(View view, int position) {
        // Handle long click on task item to show options
        TaskModel task = dataList.get(position);
        showTaskOptionsMenu(view, task, position);
    }

    // Method to show options menu for tasks
    private void showTaskOptionsMenu(View view, TaskModel task, int position) {
        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.taskmenu, popupMenu.getMenu());
        popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                int itemId = item.getItemId();
                if (itemId == R.id.deleteMenu) {
                    deleteTask(position); // Delete task if delete menu item clicked
                    return true;
                } else if (itemId == R.id.markCompleteMenu) {
                    markTaskAsCompleted(position); // Mark task as complete if mark complete menu item clicked
                    return true;
                }
                return false;
            }
        });
        popupMenu.show();
    }

    // Method to mark a task as completed
    private void markTaskAsCompleted(int position) {
        TaskModel task = dataList.get(position);
        task.setTaskStatus("Completed");
        db.collection("tasks").document(task.getTaskId())
                .update("taskStatus", "Completed")
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Notify adapter only if the position is valid
                            if (position < dataList.size()) {
                                taskListAdapter.notifyItemChanged(position); // Notify adapter of the change
                            }
                            Toast.makeText(HomeActivity.this, "Task marked as completed", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(HomeActivity.this, "Error marking task as completed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    // Method to delete a task
    private void deleteTask(int position) {
        TaskModel task = dataList.get(position);
        db.collection("tasks").document(task.getTaskId())
                .delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            // Only remove the task if the position is still valid
                            if (position < dataList.size()) {
                                dataList.remove(position); // Remove task from the list
                                taskListAdapter.notifyItemRemoved(position); // Notify adapter of the removal
                            }
                            Toast.makeText(HomeActivity.this, "Task deleted", Toast.LENGTH_SHORT).show();

                            // Refresh tasks after deletion
                            fetchTasksFromFirestore();
                        } else {
                            Toast.makeText(HomeActivity.this, "Error deleting task", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }
}
