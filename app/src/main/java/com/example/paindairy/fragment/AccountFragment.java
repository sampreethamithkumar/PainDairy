package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.databinding.AccountFragmentBinding;

public class AccountFragment extends Fragment {
    private AccountFragmentBinding accountFragmentBinding;

    public AccountFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        accountFragmentBinding = AccountFragmentBinding.inflate(inflater,container, false);
        View view = accountFragmentBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        accountFragmentBinding = null;
    }
}
