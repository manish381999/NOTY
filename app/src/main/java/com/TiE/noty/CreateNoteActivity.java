package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemClock;
import android.view.MenuItem;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TimePicker;
import android.widget.Toast;


import com.TiE.noty.databinding.ActivityCreateNoteBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

public class CreateNoteActivity extends AppCompatActivity {
ActivityCreateNoteBinding binding;
    FirebaseAuth auth;
    FirebaseUser currentUser;
    FirebaseFirestore firebaseFirestore;
    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;


    DatePickerDialog.OnDateSetListener onDateSetListener;

    //int tHour,tMinute;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityCreateNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //Initialize calender
        final Calendar calendar=Calendar.getInstance();
        int year= calendar.get(Calendar.YEAR);
        int month= calendar.get(Calendar.MONTH);
        int day= calendar.get(Calendar.DATE);

//Date
        binding.dateFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        CreateNoteActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
                        onDateSetListener, year, month, day);
                datePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
                datePickerDialog.show();
            }
        });

        onDateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                month=month+1;
                String date=dayOfMonth+"/"+month+"/"+year;
                binding.dateFormat.setText(date);
            }
        };


        createNotificationChannel();

// Time
        binding.timeFormat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int mins=calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(CreateNoteActivity.this, R.style.
                        Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                        String time=simpleDateFormat.format(calendar.getTime());
                        binding.timeFormat.setText(time);
                    }
                },hour,mins,false);
                timePickerDialog.show();
            }
        });


//        binding.timeFormat.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                //Initialize time picker dialog
//                TimePickerDialog  timePickerDialog=new TimePickerDialog(
//                        CreateNoteActivity.this, new TimePickerDialog.OnTimeSetListener() {
//                    @Override
//                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                        // Initialize hour and minute
//                        tHour=hourOfDay;
//                        tMinute=minute;
//
//                   //set hour and minute
//                        calendar.set(0,0,0,tHour,tMinute);
//                        //set selected time on textView
//                        binding.timeFormat.setText(android.text.format.DateFormat.format("hh:mm a",calendar));
//
//                    }
//                },12,0,false
//
//                );
//
//                //Display previous selected time
//                timePickerDialog.updateTime(tHour,tMinute);
//                //show dialog
////                timePickerDialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
//                timePickerDialog.show();
//
//            }
//        });
//


        getSupportActionBar().setTitle("Create New List");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);



        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();


        binding.savenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String title=binding.createtitleofnote.getText().toString();
                String content=binding.createcontentofnote.getText().toString();
                String date=binding.dateFormat.getText().toString();
                String time=binding.timeFormat.getText().toString();

                if (title.isEmpty() || content.isEmpty()){
                    Toast.makeText(getApplicationContext(), "Please write Title and Content", Toast.LENGTH_SHORT).show();
                }else {
                    binding.progressBarofCreatenote.setVisibility(View.VISIBLE);


                    setAlarm();
                    DocumentReference documentReference=firebaseFirestore.collection("notes")
                            .document(auth.getUid()).collection("myNotes").document();

                    Map<String,Object> note=new HashMap<>();

                    note.put("title",title);
                    note.put("content",content);
                    note.put("date",date);
                    note.put("time",time);



                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note created successfully", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(CreateNoteActivity.this,NotesActivity.class));
                        }


                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to create Note", Toast.LENGTH_SHORT).show();
                            binding.progressBarofCreatenote.setVisibility(View.INVISIBLE);
                        }
                    });
                }

            }

            private void setAlarm() {
                alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent=new Intent(CreateNoteActivity.this,AlarmReceiver.class);
                pendingIntent=PendingIntent.getBroadcast(CreateNoteActivity.this,0,intent,0);

                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+60 *1000, pendingIntent);


            }
        });

    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >=Build.VERSION_CODES.O){
            CharSequence name="NotyReminderChannel1";
            String description="Channel For Alarm Manager";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("Noty",name , importance);
            channel.setDescription(description);

            NotificationManager notificationManager=getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            onBackPressed();

        }
        return true;
    }
}



