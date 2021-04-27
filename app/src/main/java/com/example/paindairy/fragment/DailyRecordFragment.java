package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.databinding.DailyRecordFragmentBinding;

public class DailyRecordFragment extends Fragment {
    private DailyRecordFragmentBinding dailyRecordFragmentBinding;

    public DailyRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dailyRecordFragmentBinding = DailyRecordFragmentBinding.inflate(inflater,container, false);
        View view = dailyRecordFragmentBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();;
        dailyRecordFragmentBinding = null;
    }

}
