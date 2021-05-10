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

import com.example.paindairy.databinding.ActivityRegisterUserBinding;
import com.example.paindairy.entity.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterUser extends AppCompatActivity implements View.OnClickListener, CompoundButton.OnCheckedChangeListener {

    private ActivityRegisterUserBinding binding;

    private FirebaseAuth mAuth;

    /**
     * Activity On create life cycle
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterUserBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        mAuth = FirebaseAuth.getInstance();

        binding.appTitle.setOnClickListener(this);
        binding.registerButton.setOnClickListener(this);

        binding.checkbox.setOnCheckedChangeListener(this);
    }

    /**
     * Button On click listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.appTitle)
            startActivity(new Intent(RegisterUser.this, MainActivity.class));
        else if (v.getId() == R.id.registerButton)
            registerUser();

    }

    /**
     * Register User on sign up button click.
     */
    private void registerUser() {
        String fullName = binding.fullNameEditText.getText().toString().trim();
        String age = binding.ageEditText.getText().toString().trim();
        String email = binding.emailIdEditText.getText().toString().trim();
        String password = binding.passwordEditText.getText().toString().trim();
        String confirmPassword = binding.passwordConfirmation.getText().toString().trim();


        if (fullName.isEmpty()) {
            binding.fullNameEditText.setError("Full name is required");
            binding.fullNameEditText.requestFocus();
            return;
        } else if (age.isEmpty()) {
            binding.ageEditText.setError("Age is required");
            binding.ageEditText.requestFocus();
            return;
        } else if (email.isEmpty()) {
            binding.emailIdEditText.setError("Email is required");
            binding.emailIdEditText.requestFocus();
            return;
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.emailIdEditText.setError("Please provide valid email");
            binding.emailIdEditText.requestFocus();
            return;
        } else if (password.isEmpty()) {
            binding.passwordEditText.setError("Password is required");
            binding.passwordEditText.requestFocus();
            return;
        } else if (password.length() < 6) {
            binding.passwordEditText.setError("Password length is less than 6 characters");
            binding.passwordEditText.requestFocus();
            return;
        } else if (!password.equals(confirmPassword)) {
            binding.passwordEditText.setError("Password don't match");
            binding.passwordConfirmation.setError("Password don't match");

            binding.passwordEditText.requestFocus();
            binding.passwordConfirmation.requestFocus();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            User user = new User(fullName, age, email);


                            FirebaseDatabase.getInstance().getReference("Users")
                                    .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                                    .setValue(user).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    // Toast User successfully Registered
                                    if (task.isSuccessful()) {
                                        binding.progressBar.setVisibility(View.GONE);
                                        Toast.makeText(RegisterUser.this, "User has been registered successfully", Toast.LENGTH_LONG).show();
//                                        startActivity(new Intent(RegisterUser.this, MainActivity.class));
                                    } else {
                                        Toast.makeText(RegisterUser.this, "User registration Failed", Toast.LENGTH_LONG).show();
                                        binding.progressBar.setVisibility(View.GONE);
                                    }
                                }
                            });
                        } else {
                            Toast.makeText(RegisterUser.this, "User registration Failed", Toast.LENGTH_LONG).show();
                            binding.progressBar.setVisibility(View.GONE);
                        }
                    }
                });
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(RegisterUser.this, MainActivity.class));

    }


    /**
     * Checkbox on change listener
     * @param buttonView
     * @param isChecked
     */
    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
        if (isChecked) {
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT);
            binding.passwordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT);
        } else {
            binding.passwordEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            binding.passwordConfirmation.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
        }
    }
}