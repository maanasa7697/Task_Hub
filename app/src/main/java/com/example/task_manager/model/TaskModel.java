package com.example.task_manager.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class TaskModel implements Parcelable {
    private String taskId;
    private String taskName;
    private String taskStatus;
    private String userId;
    private Date taskDate;
    private String taskTime;
    private String date;
    private String day;
    private String month;
    private int year;
    private List<String> attachedFileUris; // List of attached file URIs
    private List<String> photoUris; // List of photo URIs

    // Default constructor
    public TaskModel() {
        this.attachedFileUris = new ArrayList<>();
        this.photoUris = new ArrayList<>();
    }

    // Constructor with fields
    public TaskModel(String taskId, String taskName, String taskStatus, String userId, Date taskDate, String taskTime, String date, String day, String month, int year, List<String> attachedFileUris, List<String> photoUris) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.userId = userId;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.date = date;
        this.day = day;
        this.month = month;
        this.year = year;
        this.attachedFileUris = attachedFileUris;
        this.photoUris = photoUris;
    }

    protected TaskModel(Parcel in) {
        taskId = in.readString();
        taskName = in.readString();
        taskStatus = in.readString();
        userId = in.readString();
        long tmpTaskDate = in.readLong();
        taskDate = tmpTaskDate != -1 ? new Date(tmpTaskDate) : null;
        taskTime = in.readString();
        date = in.readString();
        day = in.readString();
        month = in.readString();
        year = in.readInt();
        attachedFileUris = in.createStringArrayList();
        photoUris = in.createStringArrayList();
    }

    public static final Creator<TaskModel> CREATOR = new Creator<TaskModel>() {
        @Override
        public TaskModel createFromParcel(Parcel in) {
            return new TaskModel(in);
        }

        @Override
        public TaskModel[] newArray(int size) {
            return new TaskModel[size];
        }
    };

    public String getTaskId() {
        return taskId;
    }

    public void setTaskId(String taskId) {
        this.taskId = taskId;
    }

    public String getTaskName() {
        return taskName;
    }

    public void setTaskName(String taskName) {
        this.taskName = taskName;
    }

    public String getTaskStatus() {
        return taskStatus;
    }

    public void setTaskStatus(String taskStatus) {
        this.taskStatus = taskStatus;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public Date getTaskDate() {
        return taskDate;
    }

    public void setTaskDate(Date taskDate) {
        this.taskDate = taskDate;
    }

    public String getTaskTime() {
        return taskTime;
    }

    public void setTaskTime(String taskTime) {
        this.taskTime = taskTime;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getMonth() {
        return month;
    }

    public void setMonth(String month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public List<String> getAttachedFileUris() {
        return attachedFileUris;
    }

    public void setAttachedFileUris(List<String> attachedFileUris) {
        this.attachedFileUris = attachedFileUris;
    }

    public List<String> getPhotoUris() {
        return photoUris;
    }

    public void setPhotoUris(List<String> photoUris) {
        this.photoUris = photoUris;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(taskId);
        dest.writeString(taskName);
        dest.writeString(taskStatus);
        dest.writeString(userId);
        dest.writeLong(taskDate != null ? taskDate.getTime() : -1);
        dest.writeString(taskTime);
        dest.writeString(date);
        dest.writeString(day);
        dest.writeString(month);
        dest.writeInt(year);
        dest.writeStringList(attachedFileUris);
        dest.writeStringList(photoUris);
    }
}
