package com.example.task_manager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.documentfile.provider.DocumentFile;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.task_manager.model.TaskModel;
import com.google.firebase.firestore.FirebaseFirestore;

import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

public class TaskDetailActivity extends AppCompatActivity {

    private TextView taskNameTv, taskStatusTv, taskDateTextView, taskDayTextView, taskMonthTextView;
    private ImageView capturedImageView;
    private Button saveToInternalStorageBtn, loadFromInternalStorageBtn;

    private static final int REQUEST_STORAGE_PERMISSION = 100;
    private static final int REQUEST_LOAD_IMAGE = 102;
    private static final int REQUEST_DIRECTORY = 103;

    private TaskModel taskModel;
    private FirebaseFirestore db;
    private Uri directoryUri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_task_detail);

        db = FirebaseFirestore.getInstance();

        // Initialize views
        taskNameTv = findViewById(R.id.taskNameTv);
        taskStatusTv = findViewById(R.id.taskStatusTv);
        taskDateTextView = findViewById(R.id.taskDateTextView);
        taskDayTextView = findViewById(R.id.taskDayTextView);
        taskMonthTextView = findViewById(R.id.taskMonthTextView);
        capturedImageView = findViewById(R.id.imageView);
        saveToInternalStorageBtn = findViewById(R.id.saveToInternalStorageBtn);
        loadFromInternalStorageBtn = findViewById(R.id.loadFromInternalStorageBtn);

        // Retrieve TaskModel from Intent
        taskModel = getIntent().getParcelableExtra("task");

        if (taskModel != null) {
            // Set task details to UI elements
            taskNameTv.setText(taskModel.getTaskName());
            taskStatusTv.setText("Status: " + taskModel.getTaskStatus());
            taskDateTextView.setText("Date: " + taskModel.getDate());
            taskDayTextView.setText("Day: " + taskModel.getDay());
            taskMonthTextView.setText("Month: " + taskModel.getMonth());

            // Load captured images if available
            List<String> photoUrls = taskModel.getPhotoUris();
            if (photoUrls != null && !photoUrls.isEmpty()) {
                // Assuming only one image for simplicity
                String firstPhotoUrl = photoUrls.get(0);
                loadCapturedImage(firstPhotoUrl);
            } else {
                // If no photos are available, you can set a default image or handle this case
                capturedImageView.setImageResource(R.drawable.placeholder_image);
            }
        }

        // Set onClickListener for Save to Internal Storage Button
        saveToInternalStorageBtn.setOnClickListener(v -> performSaveToInternalStorage());

        // Set onClickListener for Load from Internal Storage Button
        loadFromInternalStorageBtn.setOnClickListener(v -> performLoadFromInternalStorage());
    }

    private void loadCapturedImage(String imageUrl) {
        Glide.with(this)
                .load(imageUrl)
                .apply(new RequestOptions()
                        .placeholder(R.drawable.placeholder_image) // Placeholder image while loading
                        .error(R.drawable.error_image)) // Error image if loading fails
                .into(capturedImageView);
    }

    // Method to handle saving image to internal storage
    private void performSaveToInternalStorage() {
        // Check permission to write to internal storage
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {
            // Request permission if not granted
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_STORAGE_PERMISSION);
        } else {
            // Permission already granted, open directory selector
            openDirectorySelector();
        }
    }

    private void openDirectorySelector() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT_TREE);
        startActivityForResult(intent, REQUEST_DIRECTORY);
    }

    private void saveImageToDirectory(Uri directoryUri) {
        if (directoryUri == null) {
            Toast.makeText(this, "Directory not selected", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            // Convert capturedImageView to Bitmap
            capturedImageView.setDrawingCacheEnabled(true);
            capturedImageView.buildDrawingCache();
            Bitmap bitmap = capturedImageView.getDrawingCache();

            // Create a unique file name
            String fileName = "task_image_" + System.currentTimeMillis() + ".jpg";

            // Create a new document in the selected directory
            DocumentFile pickedDir = DocumentFile.fromTreeUri(this, directoryUri);
            DocumentFile newFile = pickedDir.createFile("image/jpeg", fileName);

            // Write the bitmap to the document
            if (newFile != null) {
                try (OutputStream outputStream = getContentResolver().openOutputStream(newFile.getUri())) {
                    if (outputStream != null) {
                        bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                        outputStream.flush();
                        updatePhotoUriInTaskModel(newFile.getUri().toString());
                        Toast.makeText(this, "Image saved to storage", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to save image", Toast.LENGTH_SHORT).show();
        } finally {
            capturedImageView.setDrawingCacheEnabled(false);
        }
    }

    // Method to handle loading image from internal storage
    private void performLoadFromInternalStorage() {
        Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        intent.setType("image/*");
        startActivityForResult(intent, REQUEST_LOAD_IMAGE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_LOAD_IMAGE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                if (selectedImageUri != null) {
                    loadSelectedImage(selectedImageUri);
                }
            }
        } else if (requestCode == REQUEST_DIRECTORY && resultCode == RESULT_OK) {
            if (data != null) {
                directoryUri = data.getData();
                saveImageToDirectory(directoryUri);
            }
        }
    }

    private void loadSelectedImage(Uri selectedImageUri) {
        try {
            // Load image using Glide or other library
            Glide.with(this)
                    .load(selectedImageUri)
                    .into(capturedImageView);

            // Update the TaskModel or any other storage mechanism to save the new image URI
            updatePhotoUriInTaskModel(selectedImageUri.toString());

            Toast.makeText(this, "Image loaded from internal storage", Toast.LENGTH_SHORT).show();
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Failed to load image", Toast.LENGTH_SHORT).show();
        }
    }

    // Method to update photoUri in TaskModel or other data structure
    private void updatePhotoUriInTaskModel(String newPhotoUri) {
        if (taskModel != null) {
            // Update photoUri in TaskModel or your data structure
            List<String> photoUris = new ArrayList<>();
            photoUris.add(newPhotoUri);
            taskModel.setPhotoUris(photoUris);

            // Update the taskModel in Firestore
            db.collection("tasks").document(taskModel.getTaskId())
                    .set(taskModel)
                    .addOnSuccessListener(aVoid -> {
                        Toast.makeText(TaskDetailActivity.this, "Task updated successfully", Toast.LENGTH_SHORT).show();
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(TaskDetailActivity.this, "Failed to update task", Toast.LENGTH_SHORT).show();
                        Log.e("TaskDetailActivity", "Error updating task", e);
                    });
        }
    }

    // Handle permission request result
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_STORAGE_PERMISSION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                openDirectorySelector();
            } else {
                Toast.makeText(this, "Storage permission denied", Toast.LENGTH_SHORT).show();
            }
        }
    }
}
