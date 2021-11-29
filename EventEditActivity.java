package com.stas.a2_ivy;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.provider.CalendarContract;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;

import java.time.LocalDate;
import java.time.LocalTime;

public class EventEditActivity extends AppCompatActivity
{
    private EditText eventNameET, eventDateTV, eventTimeTV, attendees, location, other;
    private Spinner spinner, spinner2, spinner3;
    private LocalDate date;
    private LocalTime time;
    private Button button, button2;

    TextInputEditText notificationText;
    NotificationManager notificationManager;
    private static final String CHANNEL= "default";
    private static final int NOTIFICATION_DEFAULT = 1100;

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_edit);
        initWidgets();
        time = LocalTime.now();
        date = LocalDate.now();
        eventDateTV.setText("Date: " + CalendarUtils.formattedDate(CalendarUtils.selectedDate));
        eventTimeTV.setText("Time: " + CalendarUtils.formattedTime(time));

    }


    private void initWidgets()
    {
        eventNameET = findViewById(R.id.eventNameET);
        eventDateTV = findViewById(R.id.eventDateTV);
        eventTimeTV = findViewById(R.id.eventTimeTV);
        attendees = findViewById(R.id.attendees);
        location = findViewById(R.id.location);
        other = findViewById(R.id.other);
        spinner=findViewById(R.id.spinner);
        spinner2=findViewById(R.id.spinner2);
        spinner3=findViewById(R.id.spinner3);
        button=findViewById(R.id.button);
        button2=findViewById(R.id.button2);

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!eventNameET.getText().toString().isEmpty() && !location.getText().toString().isEmpty()) {

                    Intent intent = new Intent(Intent.ACTION_INSERT);
                    intent.setData(CalendarContract.Events.CONTENT_URI);
                    intent.putExtra(CalendarContract.Events.TITLE, eventNameET.getText().toString());
                    intent.putExtra(CalendarContract.Events.EVENT_LOCATION, location.getText().toString());
                    intent.putExtra(CalendarContract.Events.ALL_DAY, "true");

                    if (intent.resolveActivity(getPackageManager()) != null) {
                        startActivity(intent);
                    } else {
                        Toast.makeText(EventEditActivity.this, "There is no app that can support this action",
                                Toast.LENGTH_SHORT).show();
                    }
                    } else{
                        Toast.makeText(EventEditActivity.this, "Please fill out all the fields",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }


    public void saveEventAction(View view)
    {
        String eventName = eventNameET.getText().toString();
        Event newEvent = new Event(eventName, CalendarUtils.selectedDate, time);
        Event.eventsList.add(newEvent);
        finish();
    }

    public void scheduleNotificationButton(View view) {
            Toast.makeText(EventEditActivity.this, "Notification Scheduled!", Toast.LENGTH_LONG).show();

            scheduleNotification(NOTIFICATION_DEFAULT, "Scheduled notification", 5000);

    }

    public Notification buildNotification(String title, String body){
        Notification.Builder nb =new Notification.Builder(getApplicationContext(), CHANNEL)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true);
        Notification noti = nb.build();
        return noti;

    }
    public void sendNotification(int id, String title){
        Notification noti = buildNotification(title, notificationText.getText().toString());
        if (noti !=null){
            notificationManager.notify(id, noti);
        }
    }

    public void scheduleNotification(int id, String title, int delay){
        Notification noti = buildNotification(title, notificationText.getText().toString());

        Intent notificationIntent = new Intent(this, NotificationBroadcaster.class);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION_ID, id);
        notificationIntent.putExtra(NotificationBroadcaster.NOTIFICATION, noti);

        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 0,
                notificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

        long myAlarmTime= System.currentTimeMillis()+delay;
        AlarmManager al = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        al.set(AlarmManager.RTC_WAKEUP, myAlarmTime, pendingIntent);
    }

}

