package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.paindairy.R;
import com.example.paindairy.databinding.AccountFragmentBinding;
import com.example.paindairy.databinding.FragmentPieBinding;

import java.util.ArrayList;
import java.util.List;

public class PieChartFragment extends Fragment {
    private FragmentPieBinding pieBinding;

    private String[] months = {"Jan" , "Feb", "March"};
    private int[] earning = {200,300,400};


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        pieBinding = FragmentPieBinding.inflate(inflater,container,false);
        View view = pieBinding.getRoot();

        setupPieChart();

       return view;
    }

    public void setupPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();


        for (int i = 0; i < months.length; i++) {
            dataEntries.add(new ValueDataEntry(months[i], earning[i]));
        }

        pie.data(dataEntries);
        pieBinding.anyChartView.setChart(pie);

    }
}
