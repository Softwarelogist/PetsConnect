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
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class OTPverification extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText etOtp1, etOtp2, etOtp3, etOtp4;
    private Button btnVerify;
    private TextView otpEmail, btnResend;

    private String verificationId;
    private String userEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        auth = FirebaseAuth.getInstance();

        etOtp1 = findViewById(R.id.etotp1);
        etOtp2 = findViewById(R.id.etotp2);
        etOtp3 = findViewById(R.id.etotp3);
        etOtp4 = findViewById(R.id.etotp4);

        btnVerify = findViewById(R.id.btnverify);
        otpEmail = findViewById(R.id.otpemail);
        btnResend = findViewById(R.id.btnresend);

        Intent intent = getIntent();
        verificationId = intent.getStringExtra("verificationId");
        userEmail = intent.getStringExtra("userEmail");

        otpEmail.setText(userEmail);

        btnVerify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String otp1 = etOtp1.getText().toString().trim();
                String otp2 = etOtp2.getText().toString().trim();
                String otp3 = etOtp3.getText().toString().trim();
                String otp4 = etOtp4.getText().toString().trim();

                String otp = otp1 + otp2 + otp3 + otp4;

                if (otp.length() != 6) {
                    Toast.makeText(OTPverification.this, "Please enter a valid OTP", Toast.LENGTH_SHORT).show();
                    return;
                }

                PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, otp);
                signInWithPhoneAuthCredential(credential);
            }
        });

        btnResend.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PhoneAuthProvider.getInstance().verifyPhoneNumber(
                        "+92" + userEmail,
                        60,
                        TimeUnit.SECONDS,
                        OTPverification.this,
                        new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
                            @Override
                            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
                                signInWithPhoneAuthCredential(phoneAuthCredential);
                            }

                            @Override
                            public void onVerificationFailed(@NonNull FirebaseException e) {
                                Toast.makeText(OTPverification.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }

                            @Override
                            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                                verificationId = s;
                                Toast.makeText(OTPverification.this, "OTP sent to your phone", Toast.LENGTH_SHORT).show();
                            }
                        }
                );
            }
        });
    }

    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(OTPverification.this, "Phone number verified", Toast.LENGTH_SHORT).show();
                            // Proceed to your desired activity after successful verification
                            // For example, you can start a new activity using Intent
                            // Example: startActivity(new Intent(OTPverification.this, YourNextActivity.class));
                        } else {
                            if (task.getException() != null) {
                                Toast.makeText(OTPverification.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
