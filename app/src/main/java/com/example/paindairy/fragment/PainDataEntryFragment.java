package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindairy.R;
import com.example.paindairy.databinding.PainDataEntryBinding;
import com.example.paindairy.entity.User;
import com.example.paindairy.viewmodel.UserViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.List;

public class PainDataEntryFragment extends Fragment implements View.OnClickListener{
    private PainDataEntryBinding painDataEntryBinding;
    private UserViewModel userViewModel;

    private FirebaseUser firebaseUser;
    private FirebaseAuth mAuth;

    public PainDataEntryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        painDataEntryBinding = PainDataEntryBinding.inflate(inflater,container, false);
        View view = painDataEntryBinding.getRoot();

        mAuth = FirebaseAuth.getInstance();
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        painDataEntryBinding.saveButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        painDataEntryBinding = null;
    }

    public void getData() {
        painDataEntryBinding.idTextField.setPlaceholderText("This is only used for Edit");

//        Creating ViewModelProvider in fragment.
        userViewModel = new ViewModelProvider(this).get(UserViewModel.class);

        userViewModel.getAllUsers().observe(this, new Observer<List<User>>() {
            @Override
            public void onChanged(List<User> users) {
                StringBuffer allUsers = new StringBuffer("");
                for (User user: users) {
                    StringBuffer userDetails = new StringBuffer(user.uid + " " + user.emailId + " " + user.stepsPerDay + " " + user.painIntensityLevel);
                    allUsers = allUsers.append(System.getProperty("line.separator") + userDetails.toString());
                }
                painDataEntryBinding.textViewRead.setText("All data:" + allUsers.toString());
            }
        });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveButton) {
            String painLevel = painDataEntryBinding.painLevelTextField.getEditText().getText().toString();
            String painLocation = painDataEntryBinding.painLocationTextField.getEditText().getText().toString();
            String moodLevel = painDataEntryBinding.moodLevelTextField.getEditText().getText().toString();

            if ((!painLevel.isEmpty() && painLevel != null) && (!painLocation.isEmpty() && painLocation != null) && (!moodLevel.isEmpty() && moodLevel != null)) {
                int painLevelInt = Integer.parseInt(painLevel);
                User user = new User(firebaseUser.getEmail(), painLevelInt, painLocation, moodLevel);
                userViewModel.insert(user);
            }

        }
    }
}
