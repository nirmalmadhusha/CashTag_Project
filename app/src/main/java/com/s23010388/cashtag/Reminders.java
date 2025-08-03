package com.s23010388.cashtag;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.s23010388.cashtag.models.Reminder;
import com.s23010388.cashtag.notifications.ReminderReceiver;
import com.s23010388.cashtag.storage.AppDatabase;
import com.s23010388.cashtag.storage.ReminderManager;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Reminders#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Reminders extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private RecyclerView recyclerView;
    private ReminderManager adapter;
    private List<Reminder> reminderList;
    public Reminders() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Reminders.
     */
    // TODO: Rename and change types and number of parameters
    public static Reminders newInstance(String param1, String param2) {
        Reminders fragment = new Reminders();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_reminders, container, false);

        //Database connection
        AppDatabase db = AppDatabase.getInstance(getContext());
        reminderList = new ArrayList<>(db.reminderDao().getAllReminders());

        // recycle view
        recyclerView = view.findViewById(R.id.recyclerReminders);

        // Setup RecyclerView
        recyclerView = view.findViewById(R.id.recyclerReminders);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        // Set adapter
        adapter = new ReminderManager(getContext(), reminderList);
        recyclerView.setAdapter(adapter);

        // Floating Action Button to add reminders
        FloatingActionButton fabAddReminder = view.findViewById(R.id.fabAddReminder);
        fabAddReminder.setOnClickListener(v -> {
            View dialogView = inflater.inflate(R.layout.add_reminder_dialog, null);

            EditText editTitle = dialogView.findViewById(R.id.editReminderTitle);
            EditText editDate = dialogView.findViewById(R.id.editReminderDate);
            EditText editTime = dialogView.findViewById(R.id.editReminderTime);
            EditText editDescription = dialogView.findViewById(R.id.editDescription);


            // Show date picker
            editDate.setOnClickListener(view1 -> {
                Calendar calendar = Calendar.getInstance();
                DatePickerDialog datePickerDialog = new DatePickerDialog(getContext(),
                        (datePicker, year, month, day) -> {
                            String selectedDate = year + "-" + (month + 1) + "-" + day;
                            editDate.setText(selectedDate);
                        },
                        calendar.get(Calendar.YEAR),
                        calendar.get(Calendar.MONTH),
                        calendar.get(Calendar.DAY_OF_MONTH)
                );
                datePickerDialog.show();
            });

            // Show time picker
            editTime.setOnClickListener(view1 -> {
                Calendar calendar = Calendar.getInstance();
                TimePickerDialog timePickerDialog = new TimePickerDialog(getContext(),
                        (timePicker, hour, minute) -> {
                            String selectedTime = String.format("%02d:%02d", hour, minute);
                            editTime.setText(selectedTime);
                        },
                        calendar.get(Calendar.HOUR_OF_DAY),
                        calendar.get(Calendar.MINUTE),
                        true
                );
                timePickerDialog.show();
            });

            // Build the dialog
            new MaterialAlertDialogBuilder(requireContext())
                    .setTitle("Add Reminder")
                    .setView(dialogView)
                    .setPositiveButton("Add", (dialogInterface, i) -> {
                        String title = editTitle.getText().toString().trim();
                        String date = editDate.getText().toString().trim();
                        String time = editTime.getText().toString().trim();
                        String description = editDescription.getText().toString().trim();

                        if (title.isEmpty() || date.isEmpty() || time.isEmpty()) {
                            Toast.makeText(getContext(), "All fields required", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Reminder newReminder = new Reminder(title, date, time, description);
                        long insertedId = db.reminderDao().insert(newReminder);
                        newReminder.setId((int) insertedId);

                        // format date and time
                        String[] dateParts = date.split("-");
                        String[] timeParts = time.split(":");

                        int year = Integer.parseInt(dateParts[0]);
                        int month = Integer.parseInt(dateParts[1]) - 1;
                        int day = Integer.parseInt(dateParts[2]);

                        int hour = Integer.parseInt(timeParts[0]);
                        int minute = Integer.parseInt(timeParts[1]);

                        // Set calendar for alarm
                        Calendar cal = Calendar.getInstance();
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, day);
                        cal.set(Calendar.HOUR_OF_DAY, hour);
                        cal.set(Calendar.MINUTE, minute);
                        cal.set(Calendar.SECOND, 0);

                        // Intent to fire
                        int alarmId = newReminder.getId();
                        Intent intent = new Intent(getContext(), ReminderReceiver.class);
                        intent.putExtra("title", title);
                        intent.putExtra("description", description);

                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                getContext(),
                                alarmId,
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );

                        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, cal.getTimeInMillis(), pendingIntent);

                        // Refresh list
                        reminderList.clear();
                        reminderList.addAll(db.reminderDao().getAllReminders());
                        adapter.notifyDataSetChanged();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });
        return view;
    }
}