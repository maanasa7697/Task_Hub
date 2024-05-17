package com.example.task_manager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_manager.model.TaskModel;

import java.util.ArrayList;

public class HomeActivity extends AppCompatActivity {

    private RecyclerView taskRv;
    private ArrayList<TaskModel> dataList = new ArrayList<>();
    private TaskListAdapter taskListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        taskRv = findViewById(R.id.taskListRv);
        dataList.add(new TaskModel("testId", "demo task", "pending"));
        dataList.add(new TaskModel("Id", "shopping", "pending"));
        dataList.add(new TaskModel("Id2", "reading", "completed"));

        taskListAdapter = new TaskListAdapter(dataList);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false);
        taskRv.setLayoutManager(layoutManager);
        taskRv.setAdapter(taskListAdapter);

        findViewById(R.id.addTaskFAB).setOnClickListener(view -> {
            Intent intent = new Intent(HomeActivity.this, AddTaskActivity.class);
            startActivity(intent);
        });
    }
}
