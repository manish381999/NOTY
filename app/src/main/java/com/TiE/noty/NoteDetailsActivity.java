package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import com.TiE.noty.databinding.ActivityNoteDetailsBinding;

import java.util.Objects;

public class NoteDetailsActivity extends AppCompatActivity {
ActivityNoteDetailsBinding binding;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityNoteDetailsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.toolbarofnotedetail);
        Objects.requireNonNull(getSupportActionBar()).setDisplayHomeAsUpEnabled(true);

        Intent data=getIntent();

        binding.gotoeditenote.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(v.getContext(),EditNoteActivity.class);
                intent.putExtra("title",data.getStringExtra("title"));
                intent.putExtra("date",data.getStringExtra("date"));
                intent.putExtra("time",data.getStringExtra("time"));
                intent.putExtra("content",data.getStringExtra("content"));
                intent.putExtra("noteId",data.getStringExtra("noteId"));
                v.getContext().startActivity(intent);

            }
        });

        binding.contentofnotedetail.setText(data.getStringExtra("content"));
        binding.titleofnotedetail.setText(data.getStringExtra("title"));
        binding.notedate.setText(data.getStringExtra("date"));
        binding.notetime.setText(data.getStringExtra("time"));
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        if (item.getItemId()==android.R.id.home){
            onBackPressed();

        }
        return super.onOptionsItemSelected(item);
    }
}

