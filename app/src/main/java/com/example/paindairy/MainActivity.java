package com.example.paindairy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.util.Patterns;
import android.view.View;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.paindairy.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainActivity extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener{
    private  ActivityMainBinding binding;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        mAuth = FirebaseAuth.getInstance();

        FirebaseUser user = mAuth.getCurrentUser();

        if (user != null)
            startActivity(new Intent(this, Dashboard.class));

        binding.signUpText.setOnClickListener(this);
        binding.forgotPasswordText.setOnClickListener(this);
        binding.loginButton.setOnClickListener(this);

        binding.checkbox.setOnCheckedChangeListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signUpText)
            startActivity(new Intent(MainActivity.this, RegisterUser.class));
        else if (v.getId() == R.id.forgotPasswordText)
            startActivity(new Intent(MainActivity.this, ForgotPassword.class));
        else if (v.getId() == R.id.loginButton)
            userLogin();
    }

    private void userLogin() {
        String email = binding.emailIdEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();

        if (!inputChecking(email,password))
            return;

        binding.progressBar.setVisibility(View.VISIBLE);

        mAuth.signInWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                if (task.isSuccessful()) {
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

                    if (user.isEmailVerified()) {
                        binding.progressBar.setVisibility(View.GONE);
                        startActivity(new Intent(MainActivity.this, Dashboard.class));
                    }
                    else {
                        user.sendEmailVerification();
                        Toast.makeText(MainActivity.this, "Check your email to verify your account!", Toast.LENGTH_LONG).show();
                        binding.progressBar.setVisibility(View.GONE);
                    }
                }
                else {
                    Toast.makeText(MainActivity.this, "Failed to login! Please check your credentials", Toast.LENGTH_LONG).show();
                    binding.progressBar.setVisibility(View.GONE);
                }
            }
        });
    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked)
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
        else
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    public boolean inputChecking(String email, String password) {
        if (email.isEmpty()) {
            binding.emailIdEditText.setError("Email Id Is requried");
            binding.emailIdEditText.requestFocus();
            return false;
        }
        else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailIdEditText.setError("Please enter a valid email");
            binding.emailIdEditText.requestFocus();
            return false;
        }
        else if (password.isEmpty()) {
            binding.passwordEditText.setError("Passwrod is required");
            binding.passwordEditText.requestFocus();
            return false;
        }
        return true;
    }
}