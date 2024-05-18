package com.example.task_manager;


import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.task_manager.model.TaskModel;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class TaskListAdapter extends RecyclerView.Adapter<TaskListAdapter.ViewHolder> {

    private ArrayList<TaskModel> taskDataset;

    /**
     * Provide a reference to the type of views that you are using
     * (custom ViewHolder)
     */
    public static class ViewHolder extends RecyclerView.ViewHolder {
        LinearLayout containerLL;

        private final TextView taskNameTv,taskStatusTv;

        public ViewHolder(View view) {
            super(view);
            // Define click listener for the ViewHolder's View

            taskNameTv = (TextView) view.findViewById(R.id.taskNameTv);
            taskStatusTv = (TextView) view.findViewById(R.id.taskStatusTv);
            containerLL=(LinearLayout)view.findViewById(R.id.containerLL);

        }

    }


    public TaskListAdapter(ArrayList<TaskModel> taskDataset) {
        this.taskDataset = taskDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        // Create a new view, which defines the UI of the list item
        View view = LayoutInflater.from(viewGroup.getContext())
                .inflate(R.layout.item_task, viewGroup, false);

        return new ViewHolder(view);
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder viewHolder,final int position) {

        // Get element from your dataset at this position and replace the
        // contents of the view with that element
        viewHolder.taskNameTv.setText(taskDataset.get(position).getTaskName());
        viewHolder.taskStatusTv.setText(taskDataset.get(position).getTaskStatus());
        String status=taskDataset.get(position).getTaskStatus();
        if(status.toLowerCase().equals("pending")){
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FFD700"));
        }else if(status.toLowerCase().equals("completed")){
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#32CD32"));
        }
        else{
            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#FF6347"));
        }
        viewHolder.containerLL.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                PopupMenu popupMenu=new PopupMenu(view.getContext(),viewHolder.containerLL);
                popupMenu.inflate(R.menu.taskmenu);
                popupMenu.show();
                popupMenu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem item) {
                        if(item.getItemId()==R.id.deleteMenu){
                            FirebaseFirestore.getInstance().collection("tasks").document(taskDataset.get(position).getTaskId()).delete()
                                    .addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(),"Task Deleted Successfully!!",Toast.LENGTH_SHORT).show();
                                            viewHolder.containerLL.setVisibility(View.GONE);
                                        }
                                    });
                        }
                        if(item.getItemId()==R.id.markCompleteMenu){
                            TaskModel completedTask=taskDataset.get(position);
                            completedTask.setTaskStatus("COMPLETED");
                            FirebaseFirestore.getInstance().collection("tasks").document(taskDataset.get(position).getTaskId())
                                    .set(completedTask).addOnSuccessListener(new OnSuccessListener<Void>() {
                                        @Override
                                        public void onSuccess(Void unused) {
                                            Toast.makeText(view.getContext(),"Task Marked As Completed",Toast.LENGTH_SHORT).show();

                                        }
                                    });
                            viewHolder.taskStatusTv.setBackgroundColor(Color.parseColor("#32CD32"));
                            viewHolder.taskStatusTv.setText("COMPLETED");

                        }
                        return false;
                    }
                });
                return false;

            }
        });

    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return taskDataset.size();
    }
}
