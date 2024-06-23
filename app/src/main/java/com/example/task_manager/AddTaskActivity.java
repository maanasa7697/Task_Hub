package com.example.task_manager;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.util.Calendar;

public class AddTaskActivity extends AppCompatActivity {

    EditText etTaskInput;
    Button saveBtn, selectedDateTextView, selectedTimeTextView;
    TextView dayTextView, monthTextView, dateTextView;
    FirebaseFirestore db;
    String TAG = "Task_Hub";
    Calendar calendar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_task);
        db = FirebaseFirestore.getInstance();

        selectedDateTextView = findViewById(R.id.selectedDateTextView);
        selectedTimeTextView = findViewById(R.id.selectedTimeTextView);
        dayTextView = findViewById(R.id.dayTextView);
        monthTextView = findViewById(R.id.monthTextView);
        dateTextView = findViewById(R.id.dateTextView);

        etTaskInput = findViewById(R.id.inputTaskName);
        saveBtn = findViewById(R.id.taskSave);

        // Initialize calendar instance
        calendar = Calendar.getInstance();

        // Set click listeners for pickDate and pickTime buttons
        selectedDateTextView.setOnClickListener(v -> showDatePickerDialog());
        selectedTimeTextView.setOnClickListener(v -> showTimePickerDialog());

        saveBtn.setOnClickListener(v -> {
            String taskName = etTaskInput.getText().toString().trim();
            String Day = dayTextView.getText().toString().trim();
            String Month = monthTextView.getText().toString().trim();
            String Date = dateTextView.getText().toString().trim();

            if (!taskName.isEmpty()) {
                // Create TaskModel instance with selected date and time
                TaskModel taskModel = new TaskModel("", taskName, "PENDING", FirebaseAuth.getInstance().getUid(), calendar.getTime(), selectedTimeTextView.getText().toString(), Date, Day, Month);
                saveTaskToFirebase(taskModel);
            } else {
                Toast.makeText(AddTaskActivity.this, "Please enter a task name", Toast.LENGTH_SHORT).show();
            }
        });

        // Enable the up button in the action bar
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setTitle("Add Task");
            actionBar.setDisplayHomeAsUpEnabled(true);
        }
    }

    private void showDatePickerDialog() {
        DatePickerDialog datePickerDialog = new DatePickerDialog(this, (view, year, month, dayOfMonth) -> {
            // Set selected date to the calendar
            calendar.set(Calendar.YEAR, year);
            calendar.set(Calendar.MONTH, month);
            calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

            // Update selected date text view
            selectedDateTextView.setText(DateFormat.getDateInstance().format(calendar.getTime()));
            // Update day, month, and date TextViews
            dayTextView.setText(String.format("%1$tA", calendar));
            monthTextView.setText(String.format("%1$tb", calendar));
            dateTextView.setText(String.format("%1$td", calendar));

        }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH));

        // Show date picker dialog
        datePickerDialog.show();
    }

    private void showTimePickerDialog() {
        TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minute) -> {
            // Set selected time to the calendar
            calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
            calendar.set(Calendar.MINUTE, minute);

            // Update selected time text view
            selectedTimeTextView.setText(String.format("%02d:%02d", hourOfDay, minute));
        }, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE), true);

        // Show time picker dialog
        timePickerDialog.show();
    }

    private void saveTaskToFirebase(TaskModel taskModel) {
        // Show progress bar
        findViewById(R.id.progress).setVisibility(View.VISIBLE);

        // Add task to Firestore
        db.collection("tasks").add(taskModel).addOnSuccessListener(documentReference -> {
            // Task added successfully
            taskModel.setTaskId(documentReference.getId()); // Set the generated task ID
            Intent resultIntent = new Intent();
            resultIntent.putExtra("newTask", taskModel); // Pass the new task data
            setResult(Activity.RESULT_OK, resultIntent); // Set result to OK
            findViewById(R.id.progress).setVisibility(View.GONE);
            finish(); // Close AddTaskActivity
        }).addOnFailureListener(e -> {
            // Error adding task
            Toast.makeText(AddTaskActivity.this, "Failed to add task", Toast.LENGTH_SHORT).show();
            findViewById(R.id.progress).setVisibility(View.GONE);
            Log.e(TAG, "Error adding task", e);
        });
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
