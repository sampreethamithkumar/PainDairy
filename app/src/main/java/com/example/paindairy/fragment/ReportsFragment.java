package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.databinding.ReportFragmentBinding;

public class ReportsFragment extends Fragment {
    private ReportFragmentBinding reportFragmentBinding;

    public ReportsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reportFragmentBinding = ReportFragmentBinding.inflate(inflater,container,false);
        View view = reportFragmentBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportFragmentBinding = null;
    }
}
