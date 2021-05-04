package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.paindairy.R;

public class LineGraphFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
       return inflater.inflate(R.layout.fragment_line_graph,container,false);
    }
}
