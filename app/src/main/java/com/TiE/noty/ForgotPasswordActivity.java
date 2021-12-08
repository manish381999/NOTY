package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.TiE.noty.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class ForgotPasswordActivity extends AppCompatActivity {
ActivityForgotPasswordBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase  database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       binding=ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        binding.gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(ForgotPasswordActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.passwordRecoverBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.forgotPassword.getText().toString().isEmpty()){

                    binding.forgotPassword.setError("Enter your email ");
                    binding.forgotPassword.requestFocus();

                }else {
                    auth.sendPasswordResetEmail(binding.forgotPassword.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(getApplicationContext(), "Please check your mail to reset your password", Toast.LENGTH_LONG).show();
                                startActivity(new Intent(ForgotPasswordActivity.this,LoginActivity.class));
                                finish();

                            }else {
                                Toast.makeText(getApplicationContext(), "Email is wrong or Account Not Exist", Toast.LENGTH_SHORT).show();
                            }

                        }
                    });
                }
            }
        });

    }
}