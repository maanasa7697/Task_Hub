package com.example.task_manager;

import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<TaskModel> taskDataset;

    public static class ViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        private final TextView taskNameTv, taskStatusTv, taskDateTextView, taskDayTextView, taskMonthTextView;

        public ViewHolder(View view) {
            super(view);
            cardView = view.findViewById(R.id.containerLL);
            taskNameTv = view.findViewById(R.id.taskNameTv);
            taskStatusTv = view.findViewById(R.id.taskStatusTv);
            taskDateTextView = view.findViewById(R.id.date);
            taskDayTextView = view.findViewById(R.id.day);
            taskMonthTextView = view.findViewById(R.id.month);
        }
    }

    public TaskListAdapter(ArrayList<TaskModel> taskDataset) {
        this.taskDataset = taskDataset;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int position) {
        TaskModel task = taskDataset.get(position);
        viewHolder.taskNameTv.setText(task.getTaskName());
        viewHolder.taskStatusTv.setText(task.getTaskStatus());

        // Set date, day, and month (first three letters)
        if (task.getDay() != null && task.getDay().length() >= 3) {
            viewHolder.taskDayTextView.setText(task.getDay().substring(0, 3));
        } else {
            viewHolder.taskDayTextView.setText(task.getDay());
        }

        viewHolder.taskDateTextView.setText(task.getDate());

        if (task.getMonth() != null && task.getMonth().length() >= 3) {
            viewHolder.taskMonthTextView.setText(task.getMonth().substring(0, 3));
        } else {
            viewHolder.taskMonthTextView.setText(task.getMonth());
        }

        // Set background color based on task status
        int backgroundColor = task.getTaskStatus().equalsIgnoreCase("completed") ? Color.GREEN : Color.YELLOW;
        viewHolder.taskStatusTv.setBackgroundColor(backgroundColor);

        viewHolder.cardView.setOnLongClickListener(view -> {
            PopupMenu popupMenu = new PopupMenu(view.getContext(), viewHolder.cardView);
            popupMenu.inflate(R.menu.taskmenu);
            popupMenu.show();
            popupMenu.setOnMenuItemClickListener(item -> {
                if (item.getItemId() == R.id.deleteMenu) {
                    FirebaseFirestore.getInstance().collection("tasks").document(task.getTaskId()).delete()
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(view.getContext(), "Task Deleted Successfully!!", Toast.LENGTH_SHORT).show();
                                taskDataset.remove(position);
                                notifyItemRemoved(position);
                                notifyItemRangeChanged(position, taskDataset.size());
                            });
                }
                if (item.getItemId() == R.id.markCompleteMenu) {
                    task.setTaskStatus("COMPLETED");
                    FirebaseFirestore.getInstance().collection("tasks").document(task.getTaskId())
                            .set(task)
                            .addOnSuccessListener(unused -> {
                                Toast.makeText(view.getContext(), "Task Marked As Completed", Toast.LENGTH_SHORT).show();
                                notifyItemChanged(position);
                            });
                }
                return false;
            });
            return false;
        });
    }

    @Override
    public int getItemCount() {
        return taskDataset.size();
    }
}
