package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.databinding.PainDataEntryBinding;

public class PainDataEntryFragment extends Fragment {
    private PainDataEntryBinding painDataEntryBinding;

    public PainDataEntryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        painDataEntryBinding = PainDataEntryBinding.inflate(inflater,container, false);
        View view = painDataEntryBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        painDataEntryBinding = null;
    }
}
