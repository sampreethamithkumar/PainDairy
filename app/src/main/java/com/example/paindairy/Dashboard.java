package com.example.paindairy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.example.paindairy.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class Dashboard extends AppCompatActivity implements View.OnClickListener{
    private ActivityDashboardBinding binding;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private FirebaseAuth mAuth;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        ActionBar actionBar = getSupportActionBar();


        mAuth = FirebaseAuth.getInstance();

        binding.signOut.setOnClickListener(this);

        getUserDetails();
    }


    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.signOut) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(Dashboard.this, MainActivity.class));
        }

    }

    private void getUserDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();



        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);

                if (userProfile != null){
                    String fullName = userProfile.fullName;
                    String email = userProfile.email;
                    String age = userProfile.age;

                    binding.emailIdTextView.setText(email);
                    binding.fullNameTextView.setText(fullName);
                    binding.ageTextView.setText(age);

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(Dashboard.this, "Something Wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }
}