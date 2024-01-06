package com.taas.petsconnect;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.taas.petsconnect.Model.User;

public class Signup extends AppCompatActivity {

    private FirebaseAuth auth;
    private FirebaseDatabase database;
    private EditText etName, etProfession, etEmail, etPassword,et_phone;
    private Button btnSignup;
    private DatabaseReference usersRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();

        etName = findViewById(R.id.uname);
        etProfession = findViewById(R.id.uprofeesion);
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);
        btnSignup = findViewById(R.id.btn_signup);
        et_phone=findViewById(R.id.et_phone);
        TextView btnAlready = findViewById(R.id.btnalradyhaveacc);

        usersRef = database.getReference("users");

        btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = etName.getText().toString().trim();
                String profession = etProfession.getText().toString().trim();
                String email = etEmail.getText().toString().trim();
                String password = etPassword.getText().toString().trim();
                String phone=et_phone.getText().toString().trim();

                if (name.isEmpty() || profession.isEmpty() || email.isEmpty() || password.isEmpty()||phone.isEmpty()) {
                    Toast.makeText(Signup.this, "Please fill in all fields", Toast.LENGTH_SHORT).show();
                    return; // Stop further execution
                }

                auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Send email verification
                            FirebaseUser currentUser = auth.getCurrentUser();
                            if (currentUser != null) {
                                currentUser.sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                                    @Override
                                    public void onComplete(@NonNull Task<Void> emailTask) {
                                        if (emailTask.isSuccessful()) {
                                            // Email sent successfully, inform the user
                                            Toast.makeText(Signup.this, "Verification email sent", Toast.LENGTH_SHORT).show();
                                        } else {
                                            // Failed to send verification email, inform the user
                                            Toast.makeText(Signup.this, "Failed to send verification email", Toast.LENGTH_SHORT).show();
                                        }
                                    }
                                });
                            }

                            // Continue with user creation
                            User user = new User(name,email,password,profession,phone);
                            String id = task.getResult().getUser().getUid();
                            usersRef.child(id).setValue(user);

                            Toast.makeText(Signup.this, "Signup Successful", Toast.LENGTH_SHORT).show();

                        } else {
                            Toast.makeText(Signup.this, "User creation failed", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });

        btnAlready.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }
}
