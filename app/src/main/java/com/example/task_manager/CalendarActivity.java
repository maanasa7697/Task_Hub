package com.example.task_manager;

import android.os.Bundle;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import com.example.task_manager.model.TaskModel;
import com.github.sundeepk.compactcalendarview.CompactCalendarView;
import com.github.sundeepk.compactcalendarview.domain.Event;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class CalendarActivity extends AppCompatActivity {

    private CompactCalendarView compactCalendarView;
    private TextView monthYearTextView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendar);

        compactCalendarView = findViewById(R.id.compactcalendar_view);
        monthYearTextView = findViewById(R.id.month_year_text_view);

        // Set current month and year initially
        updateMonthYearTitle(compactCalendarView.getFirstDayOfCurrentMonth());

        // Fetch tasks from Firestore or wherever you store them
        List<TaskModel> tasks = fetchTasksFromFirestore();

        // Iterate through tasks and mark days on calendar
        for (TaskModel task : tasks) {
            Calendar cal = Calendar.getInstance();
            cal.setTime(task.getTaskDate()); // Assuming getTaskDate() returns a Date object

            Event event = new Event(getResources().getColor(R.color.secondaryColor), cal.getTimeInMillis(), task.getTaskName());
            compactCalendarView.addEvent(event);
        }

        // Listener to update month and year when calendar month changes
        compactCalendarView.setListener(new CompactCalendarView.CompactCalendarViewListener() {
            @Override
            public void onDayClick(java.util.Date dateClicked) {
                // Handle day click if needed
            }

            @Override
            public void onMonthScroll(java.util.Date firstDayOfNewMonth) {
                updateMonthYearTitle(firstDayOfNewMonth);
            }
        });
    }

    // Method to update month and year in the title TextView
    private void updateMonthYearTitle(java.util.Date firstDayOfNewMonth) {
        SimpleDateFormat sdf = new SimpleDateFormat("MMMM yyyy", Locale.getDefault());
        monthYearTextView.setText(sdf.format(firstDayOfNewMonth));
    }

    // Method to fetch tasks from Firestore
    private List<TaskModel> fetchTasksFromFirestore() {
        // Implement your logic to fetch tasks from Firestore
        return new ArrayList<>(); // Replace with actual data retrieval logic
    }
}
