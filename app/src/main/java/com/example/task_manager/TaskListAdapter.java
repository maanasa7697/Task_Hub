package com.example.task_manager;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.task_manager.model.TaskModel;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<TaskModel> taskDataset;
    private OnItemClickListener mListener;

    public interface OnItemClickListener {
        void onItemClick(TaskModel task, int position);

        void onItemLongClick(View view, int position);
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView taskNameTv, taskStatusTv, taskDateTextView, taskDayTextView, taskMonthTextView;

        public ViewHolder(View view) {
            super(view);
            taskNameTv = view.findViewById(R.id.taskNameTv);
            taskStatusTv = view.findViewById(R.id.taskStatusTv);
            taskDateTextView = view.findViewById(R.id.date);
            taskDayTextView = view.findViewById(R.id.day);
            taskMonthTextView = view.findViewById(R.id.month);
        }
    }

    public TaskListAdapter(ArrayList<TaskModel> dataset, OnItemClickListener listener) {
        taskDataset = dataset;
        mListener = listener;
    }

    @NonNull
    @Override
    public TaskListAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_task, parent, false);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        TaskModel task = taskDataset.get(position);
        viewHolder.taskNameTv.setText(task.getTaskName());
        viewHolder.taskStatusTv.setText(task.getTaskStatus());

        // Set day, date, and month TextViews
        viewHolder.taskDateTextView.setText(task.getDate());
        viewHolder.taskDayTextView.setText(task.getDay());
        viewHolder.taskMonthTextView.setText(task.getMonth());

        // Set background color based on task status
        int backgroundColor = task.getTaskStatus().equalsIgnoreCase("Completed") ? Color.GREEN : Color.YELLOW;
        viewHolder.taskStatusTv.setBackgroundColor(backgroundColor);

        // Handle click on item to open detailed task page
        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mListener.onItemClick(task, viewHolder.getAdapterPosition());
            }
        });

        // Handle long click on cardView
        viewHolder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                mListener.onItemLongClick(view, viewHolder.getAdapterPosition());
                return true;
            }
        });
    }

    @Override
    public int getItemCount() {
        return taskDataset.size();
    }
}
