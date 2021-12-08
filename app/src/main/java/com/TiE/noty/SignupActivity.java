package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.TiE.noty.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;

public class SignupActivity extends AppCompatActivity {

    ActivitySignupBinding binding;
    FirebaseAuth auth;
    FirebaseDatabase database;



    FirebaseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().hide();

        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        currentUser=auth.getCurrentUser();


        binding.gotoLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.signupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name=binding.signupName.getText().toString();
                String email=binding.signupEmail.getText().toString();
                String password=binding.signupPassword.getText().toString();
                if (name.isEmpty()|email.isEmpty() | password.isEmpty()){

                    binding.signupName.setError("Please enter your name");
                    binding.signupName.requestFocus();

                    binding.signupEmail.setError("Please enter your email");
                    binding.signupEmail.requestFocus();

                    binding.signupPassword.setError("Please enter Password");
                    binding.signupPassword.requestFocus();

                }else if (password.length()<6){
                    binding.signupPassword.setError("Password should grater then 5 digits");
                    binding.signupPassword.requestFocus();

                }else {
                    binding.progressBarofSignup.setVisibility(View.VISIBLE);

                    auth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                currentUser=auth.getCurrentUser();
                                UserDetails userDetails=new UserDetails(email,password,name);
                               String id=task.getResult().getUser().getUid();
                                database.getReference().child("Users").child(id).setValue(userDetails);
                                Toast.makeText(getApplicationContext(), "Signup successfully", Toast.LENGTH_SHORT).show();
                                sendEmailVerification();

                            }else {
                                Toast.makeText(getApplicationContext(), "Failed to Signup", Toast.LENGTH_SHORT).show();
                                binding.progressBarofSignup.setVisibility(View.INVISIBLE);
                            }
                        }
                    });

                }
            }

        });
    }
    //send email verification
    private void sendEmailVerification(){

        if (currentUser!=null){

            currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    Toast.makeText(getApplicationContext(), "Verification Email is send, Verify and Login Again", Toast.LENGTH_LONG).show();
                    auth.signOut();
                    finish();
                    Intent intent=new Intent(SignupActivity.this,LoginActivity.class);
                    startActivity(intent);

                }
            });
        }else {
            binding.progressBarofSignup.setVisibility(View.INVISIBLE);
            Toast.makeText(getApplicationContext(), "Failed to sent Verification email", Toast.LENGTH_SHORT).show();
        }
    }
}

