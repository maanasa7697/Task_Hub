package com.example.task_manager.model;

import java.util.Date;

public class TaskModel {
    String taskId, taskName, taskStatus, userId;
    Date taskDate;
    String taskTime;
    private String date; // Add date field
    private String day; // Add day field
    private String Month;

    public TaskModel() {

    }

    public TaskModel(String taskId, String taskName, String taskStatus, String userId, Date taskDate, String taskTime, String date, String day, String Month) {
        this.taskId = taskId;
        this.taskName = taskName;
        this.taskStatus = taskStatus;
        this.userId = userId;
        this.taskDate = taskDate;
        this.taskTime = taskTime;
        this.date = date;
        this.day = day;
        this.Month = Month;
    }

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
        return Month;
    }

    public void setMonth(String Month) {
        this.Month = Month;
    }
}
