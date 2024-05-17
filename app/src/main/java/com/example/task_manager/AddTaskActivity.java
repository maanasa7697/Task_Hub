package com.example.task_manager;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

public class AddTaskActivity extends AppCompatActivity {
    EditText etTaskInput;
    Button saveBtn;
    FirebaseFirestore db;
    String TAG = "Task_Hub";

    @SuppressLint("RestrictedApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        db = FirebaseFirestore.getInstance();

        saveBtn = findViewById(R.id.taskSave);
        etTaskInput = findViewById(R.id.inputTaskName);

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                findViewById(R.id.progress).setVisibility(View.VISIBLE);
                String taskName = etTaskInput.getText().toString().trim();
                if (taskName != null && !taskName.isEmpty()) {
                    TaskModel taskModel = new TaskModel("", taskName, "PENDING", FirebaseAuth.getInstance().getUid());
                    db.collection("tasks").add(taskModel).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                                @Override
                                public void onSuccess(DocumentReference documentReference) {
                                    Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                                    showSuccessAnimation();
                                }
                            })
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Log.w(TAG, "Error adding document", e);
                                }
                            });
                } else {
                    Toast.makeText(AddTaskActivity.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
                }
            }
        });

        // Enable the up button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Task");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showSuccessAnimation() {
        View successLayout = findViewById(R.id.success);
        Animation fadeInSlideUp = AnimationUtils.loadAnimation(this, R.anim.fade_in_slide_up);
        successLayout.setVisibility(View.VISIBLE);
        successLayout.startAnimation(fadeInSlideUp);
        findViewById(R.id.addTaskLayout).setVisibility(View.GONE);
        findViewById(R.id.progress).setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
