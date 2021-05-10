package com.example.paindairy.fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.MainActivity;
import com.example.paindairy.R;
import com.example.paindairy.databinding.AccountFragmentBinding;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Fragment to logout the user.
 */
public class AccountFragment extends Fragment implements View.OnClickListener {
    private AccountFragmentBinding accountFragmentBinding;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        accountFragmentBinding = AccountFragmentBinding.inflate(inflater, container, false);
        View view = accountFragmentBinding.getRoot();

        accountFragmentBinding.logoutButton.setOnClickListener(this);
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        accountFragmentBinding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.logoutButton) {
            FirebaseAuth.getInstance().signOut();
            startActivity(new Intent(getActivity(), MainActivity.class));
        }
    }
}
