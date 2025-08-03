package com.s23010388.cashtag.storage;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.s23010388.cashtag.R;
import com.s23010388.cashtag.models.Reminder;
import com.s23010388.cashtag.notifications.ReminderReceiver;

import java.util.List;

public class ReminderManager extends RecyclerView.Adapter<ReminderManager.ReminderViewHolder> {

    private final List<Reminder> reminderList;
    private Context context;
    private AppDatabase db;

    public ReminderManager(Context context, List<Reminder> reminderList) {
        this.context = context;
        this.reminderList = reminderList;
        this.db = AppDatabase.getInstance(context);

    }

    @NonNull
    @Override
    public ReminderViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.reminder_item, parent, false);
        return new ReminderViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ReminderViewHolder holder, int position) {
        Reminder reminder = reminderList.get(position);
        holder.titleText.setText(reminder.getTitle());
        holder.dateText.setText("Date : "+reminder.getDate());
        holder.timeText.setText("Time : "+reminder.getTime());
        holder.descriptionText.setText((reminder.getDescription()));

        holder.delete.setOnClickListener(v -> {
            new MaterialAlertDialogBuilder(context)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setPositiveButton("Delete", (dialog, which) -> {
                        // Cancel the alarm
                        Intent intent = new Intent(context, ReminderReceiver.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(
                                context,
                                reminder.getId(),
                                intent,
                                PendingIntent.FLAG_UPDATE_CURRENT | PendingIntent.FLAG_IMMUTABLE
                        );
                        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
                        alarmManager.cancel(pendingIntent);

                        // Delete from database
                        db.reminderDao().delete(reminder);

                        // Remove from list and update RecyclerView
                        reminderList.remove(position);
                        notifyItemRemoved(position);
                        notifyItemRangeChanged(position, reminderList.size());

                        Toast.makeText(context, "Reminder deleted", Toast.LENGTH_SHORT).show();
                    })
                    .setNegativeButton("Cancel", null)
                    .show();

        });
    }

    @Override
    public int getItemCount() {
        return reminderList.size();
    }

    public static class ReminderViewHolder extends RecyclerView.ViewHolder {
        TextView titleText, dateText, timeText, descriptionText;
        ImageView delete;

        public ReminderViewHolder(@NonNull View itemView) {
            super(itemView);
            titleText = itemView.findViewById(R.id.textReminderTitle);
            dateText = itemView.findViewById(R.id.textReminderDate);
            timeText = itemView.findViewById(R.id.textReminderTime);
            descriptionText = itemView.findViewById(R.id.textReminderDescription);
            delete = itemView.findViewById(R.id.deleteReminderBtn);
        }
    }
}
