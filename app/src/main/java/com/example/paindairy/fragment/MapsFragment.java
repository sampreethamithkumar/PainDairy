package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.databinding.MapFragmentBinding;

public class MapsFragment extends Fragment {
    private MapFragmentBinding mapFragmentBinding;

    public MapsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mapFragmentBinding = MapFragmentBinding.inflate(inflater, container,false);
        View view = mapFragmentBinding.getRoot();
        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mapFragmentBinding = null;
    }
}
