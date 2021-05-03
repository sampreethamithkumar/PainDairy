package com.example.paindairy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Patterns;
import android.view.View;
import android.widget.Toast;

import com.example.paindairy.databinding.ActivityForgotPasswordBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class ForgotPassword extends AppCompatActivity implements View.OnClickListener, OnCompleteListener<Void> {
    private ActivityForgotPasswordBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityForgotPasswordBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.resetButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.resetButton)
            resetPassword();
    }

    private void resetPassword() {
        String email = binding.emailIdEditText.getText().toString().trim();

        if (email.isEmpty()) {
            binding.emailIdEditText.setError("Email is required");
            binding.emailIdEditText.requestFocus();
            return;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailIdEditText.setError("Please provide a valid email");
            binding.emailIdEditText.requestFocus();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener(this);
    }

    @Override
    public void onComplete(@NonNull Task<Void> task) {
        if (task.isSuccessful()) {
            Toast.makeText(ForgotPassword.this, "Check your email to reset your password", Toast.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
        }
        else {
            Toast.makeText(ForgotPassword.this, "Something Wrong happened!", Toast.LENGTH_LONG).show();
            binding.progressBar.setVisibility(View.GONE);
        }
    }
}