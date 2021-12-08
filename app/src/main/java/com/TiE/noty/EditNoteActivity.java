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

import com.TiE.noty.databinding.ActivityEditNoteBinding;
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

public class EditNoteActivity extends AppCompatActivity {
ActivityEditNoteBinding binding;

    Intent data;

    FirebaseAuth auth;
    FirebaseFirestore firebaseFirestore;
    FirebaseUser currentUser;

    DatePickerDialog.OnDateSetListener onDateSetListener;

    private AlarmManager alarmManager;
    private PendingIntent pendingIntent;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityEditNoteBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        final Calendar calendar=Calendar.getInstance();
        int day= calendar.get(Calendar.DATE);
        int month= calendar.get(Calendar.MONTH);
        int year= calendar.get(Calendar.YEAR);

// Date
        binding.notedate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog=new DatePickerDialog(
                        EditNoteActivity.this, android.R.style.Theme_Holo_Light_Dialog_MinWidth,
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
                binding.notedate.setText(date);
            }
        };


        createNotificationChannel();
        // Time
        binding.notetime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int hour=calendar.get(Calendar.HOUR_OF_DAY);
                int mins=calendar.get(Calendar.MINUTE);
                TimePickerDialog timePickerDialog = new TimePickerDialog(EditNoteActivity.this, R.style.
                        Theme_AppCompat_DayNight_Dialog_MinWidth, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);
                        calendar.setTimeZone(TimeZone.getDefault());
                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("hh:mm a");
                        String time=simpleDateFormat.format(calendar.getTime());
                        binding.notetime.setText(time);
                    }
                },hour,mins,false);
                timePickerDialog.show();
            }
        });

        auth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();
        currentUser=auth.getCurrentUser();

        getSupportActionBar().setTitle("Update your list");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        data=getIntent();

        binding.saveeditnote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Toast.makeText(getApplicationContext(), "saved btn clicked", Toast.LENGTH_SHORT).show();
                String newtitle = binding.edittitleofnote.getText().toString();
                String newcontent = binding.editcontentofnote.getText().toString();
                String newdate = binding.notedate.getText().toString();
                String newtime = binding.notetime.getText().toString();

                if (newtitle.isEmpty() || newcontent.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Something is Empty", Toast.LENGTH_SHORT).show();
                } else {
                    setAlarm();
                    DocumentReference documentReference = firebaseFirestore.collection("notes").document(auth.getUid())
                            .collection("myNotes").document(data.getStringExtra("noteId"));


                    Map<String, Object> note = new HashMap<>();
                    note.put("title", newtitle);
                    note.put("content", newcontent);
                    note.put("date", newdate);
                    note.put("time", newtime);

                    documentReference.set(note).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            Toast.makeText(getApplicationContext(), "Note is updated", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(v.getContext(), NotesActivity.class));
                            finishAffinity();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(getApplicationContext(), "Failed to update", Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }



            private void setAlarm(){
                alarmManager=(AlarmManager) getSystemService(Context.ALARM_SERVICE);

                Intent intent=new Intent(EditNoteActivity.this,AlarmReceiver.class);
                pendingIntent =PendingIntent.getBroadcast(EditNoteActivity.this,0,intent,0);

                alarmManager.set(AlarmManager.ELAPSED_REALTIME_WAKEUP, SystemClock.elapsedRealtime()+ 60*1000,pendingIntent);

            }

        });


        binding.edittitleofnote.setText(data.getStringExtra("title"));
        binding.editcontentofnote.setText(data.getStringExtra("content"));
        binding.notedate.setText(data.getStringExtra("date"));
        binding.notetime.setText(data.getStringExtra("time"));


    }

    private void createNotificationChannel() {
        if(Build.VERSION.SDK_INT>= Build.VERSION_CODES.O){
            CharSequence name="NotyReminderChannel1";
            String description="Channel For Alarm Manager";
            int importance= NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel=new NotificationChannel("Noty",name,importance);
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
        return super.onOptionsItemSelected(item);
    }
}

