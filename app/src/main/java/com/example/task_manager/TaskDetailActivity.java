package com.example.task_manager;

import android.os.Bundle;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.example.task_manager.model.TaskModel;

import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView taskNameTv, taskStatusTv, taskDateTextView, taskDayTextView, taskMonthTextView;
    private ImageView capturedImageView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        // Initialize views
        taskNameTv = findViewById(R.id.taskNameTv);
        taskStatusTv = findViewById(R.id.taskStatusTv);
        taskDateTextView = findViewById(R.id.taskDateTextView);
        taskDayTextView = findViewById(R.id.taskDayTextView);
        taskMonthTextView = findViewById(R.id.taskMonthTextView);
        capturedImageView = findViewById(R.id.imageView);

        // Retrieve TaskModel from Intent
        TaskModel task = getIntent().getParcelableExtra("task");

        if (task != null) {
            // Set task details to UI elements
            taskNameTv.setText(task.getTaskName());
            taskStatusTv.setText("Status: " + task.getTaskStatus());
            taskDateTextView.setText("Date: " + task.getDate());
            taskDayTextView.setText("Day: " + task.getDay());
            taskMonthTextView.setText("Month: " + task.getMonth());

            // Load captured images if available
            List<String> photoUrls = task.getPhotoUris();
            if (photoUrls != null && !photoUrls.isEmpty()) {
                // Assuming only one image for simplicity
                String firstPhotoUrl = photoUrls.get(0);
                loadCapturedImage(firstPhotoUrl);
            } else {
                // If no photos are available, you can set a default image or handle this case
                capturedImageView.setImageResource(R.drawable.placeholder_image);
            }
        }
    }

    private void loadCapturedImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                        .error(R.drawable.error_image)) // Error image if loading fails
                .into(capturedImageView);
    }
}
