package com.example.paindairy.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.paindairy.R;
import com.example.paindairy.databinding.FragmentLineGraphBinding;
import com.example.paindairy.databinding.ReportFragmentBinding;
import com.google.android.material.bottomnavigation.BottomNavigationView;

import java.text.DateFormat;
import java.util.Calendar;

/**
 * Report fragment with three child fragments
 * Pie
 * Donut
 * Line
 */
public class ReportsFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {
    private ReportFragmentBinding reportFragmentBinding;

    private FragmentLineGraphBinding fragmentLineGraphBinding;


    public ReportsFragment() {

    }

    /**
     * Fragment on Create View lifecycle
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        reportFragmentBinding = ReportFragmentBinding.inflate(inflater, container, false);
        fragmentLineGraphBinding = FragmentLineGraphBinding.inflate(inflater, container, false);
        View view = reportFragmentBinding.getRoot();


        reportFragmentBinding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new PieChartFragment()).commit();

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        reportFragmentBinding = null;
    }

    /**
     * Bottom Navigation of report fragment
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_pie:
                selectedFragment = new PieChartFragment();
                break;
            case R.id.nav_donut:
                selectedFragment = new DonutFragment();
                break;
            case R.id.nav_line_graph:
                selectedFragment = new LineGraphFragment();
                break;
        }

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer,
                selectedFragment).commit();

        return true;
    }


}
