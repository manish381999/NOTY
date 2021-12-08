package com.TiE.noty;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.TiE.noty.databinding.ActivityLoginBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class LoginActivity extends AppCompatActivity {
ActivityLoginBinding binding;

    FirebaseAuth auth;

    FirebaseUser currentUser;

    FirebaseDatabase database;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding=ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());



        getSupportActionBar().hide();
        auth=FirebaseAuth.getInstance();
        database=FirebaseDatabase.getInstance();

        if(auth.getCurrentUser()!=null){
            startActivity(new Intent(LoginActivity.this,NotesActivity.class));
            finish();
        }

        binding.gotoSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,SignupActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.gotoForgotPassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent=new Intent(LoginActivity.this,ForgotPasswordActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.loginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (binding.loginEmail.getText().toString().isEmpty() | binding.loginPassword.getText().toString().isEmpty()){

                    binding.loginEmail.setError("Please enter your Email");
                    binding.loginEmail.requestFocus();

                    binding.loginPassword.setError("Please enter Password");
                    binding.loginPassword.requestFocus();
                }else if(binding.loginPassword.getText().toString().length()<6){

                    binding.loginPassword.setError("Invalid Password");
                    binding.loginPassword.requestFocus();

                }else {
                    auth.signInWithEmailAndPassword(binding.loginEmail.getText().toString(),binding.loginPassword.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()){
                                        currentUser= auth.getCurrentUser();

                        String password=binding.loginPassword.getText().toString();
                    //  UserDetails userDetails=new UserDetails(password);
                        String id=task.getResult().getUser().getUid();
                        DatabaseReference  reference=database.getReference().child("Users");
                        reference.child(id).child("password").setValue(password);

                                        checkemailverifiction();
                                    }else {
                                        Toast.makeText(getApplicationContext(), "Account Doesn't exist First Create Account", Toast.LENGTH_LONG).show();
                                    }

                                }
                            });
                }

            }
        });
    }

    private void checkemailverifiction(){
        if (currentUser.isEmailVerified()==true){

            Toast.makeText(getApplicationContext(), "Logged In", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(LoginActivity.this,NotesActivity.class));
            finish();

        }else {
            Toast.makeText(getApplicationContext(), "verify your mail first", Toast.LENGTH_SHORT).show();
        }

    }


}

