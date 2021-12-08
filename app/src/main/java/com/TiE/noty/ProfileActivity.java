package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Toast;


import com.TiE.noty.databinding.ActivityProfileBinding;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.ByteArrayOutputStream;

public class ProfileActivity extends AppCompatActivity {
    ActivityProfileBinding binding;

    FirebaseAuth auth;
    FirebaseDatabase database;
    FirebaseStorage storage;
    private Bitmap bitmap;

    private ProgressDialog pd;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding= ActivityProfileBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();
        storage=FirebaseStorage.getInstance();

        pd=new ProgressDialog(this);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Profile");

        binding.loginPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(getApplicationContext(), "To see your password click on eye icon", Toast.LENGTH_LONG).show();
            }
        });



        database.getReference().child("Users").child(auth.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                UserDetails userDetails=snapshot.getValue(UserDetails.class);
                UserDetails userDetails1=snapshot.getValue(UserDetails.class);

                Picasso.get()
                        .load(userDetails1.getProfile_photo())
                        .placeholder(R.drawable.place_holder)
                        .into(binding.profilePhoto);


                binding.signupName.setText(userDetails.getName());
                binding.signupEmail.setText(userDetails.getEmail());
                binding.loginPassword.setText(userDetails.getPassword());



            }



            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        binding.changeProfilePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,33);

            }
        });


    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==33 && resultCode==RESULT_OK&& data.getData()!=null);
        Uri uri=data.getData();

        try {
            bitmap= MediaStore.Images.Media.getBitmap(getContentResolver(),uri);
        }catch (Exception e){
            e.printStackTrace();
        }
        binding.profilePhoto.setImageBitmap(bitmap);

        ByteArrayOutputStream byteArrayOutputStream= new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG,50,byteArrayOutputStream);
        byte[]finalImg=byteArrayOutputStream.toByteArray();
        pd.setMessage("Uploading profile photo...");
        pd.show();
        StorageReference reference=storage.getReference().child("profile_photo")
                .child(auth.getUid());

        reference.putBytes(finalImg).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {


                reference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        database.getReference().child("Users").child(auth.getUid()).child("profile_photo").setValue(uri.toString());
                    }
                }).addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        Toast.makeText(getApplicationContext(), "Profile photo changed", Toast.LENGTH_SHORT).show();
                        pd.dismiss();
                    }
                });


            }
        });
    }


    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}



