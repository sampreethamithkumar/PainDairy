package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Pie;
import com.example.paindairy.R;
import com.example.paindairy.databinding.AccountFragmentBinding;
import com.example.paindairy.databinding.FragmentPieBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

/**
 * Pie Chart Display fragment
 */
public class PieChartFragment extends Fragment implements Observer<List<PainRecord>> {
    private FragmentPieBinding pieBinding;

    private FirebaseUser firebaseUser;

    private PainRecordViewModel painRecordViewModel;

    final private String[] painLocations = {"Abdomen", "Back", "Elbows", "Facial", "Hips", "Jaw", "Knees", "Neck", "Shins", "Shoulder"};
    private int[] painLocationCount = new int[painLocations.length];


    /**
     * Fragment onCreate Life Cycle
     * @param inflater
     * @param container
     * @param savedInstaceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        pieBinding = FragmentPieBinding.inflate(inflater, container, false);
        View view = pieBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        painRecordViewModel.getAllPainRecords().observe(getViewLifecycleOwner(), this);


        return view;
    }

    /**
     * initial setup of pie chart
     */
    public void setupPieChart() {
        Pie pie = AnyChart.pie();
        List<DataEntry> dataEntries = new ArrayList<>();


        for (int i = 0; i < painLocations.length; i++) {
            dataEntries.add(new ValueDataEntry(painLocations[i], painLocationCount[i]));
        }

        pie.data(dataEntries);
        pieBinding.anyChartView.setChart(pie);

    }

    /**
     * On data change listener
     * @param painRecords
     */
    @Override
    public void onChanged(List<PainRecord> painRecords) {

        for (PainRecord painRecord : painRecords)
            if (painRecord.emailId.equals(firebaseUser.getEmail()))
                if (painRecord.painLocation.equals("Abdomen"))
                    painLocationCount[0] += 1;
                else if (painRecord.painLocation.equals("Back"))
                    painLocationCount[1] += 1;
                else if (painRecord.painLocation.equals("Elbows"))
                    painLocationCount[2] += 1;
                else if (painRecord.painLocation.equals("Facial"))
                    painLocationCount[3] += 1;
                else if (painRecord.painLocation.equals("Hips"))
                    painLocationCount[4] += 1;
                else if (painRecord.painLocation.equals("Jaw"))
                    painLocationCount[5] += 1;
                else if (painRecord.painLocation.equals("Knees"))
                    painLocationCount[6] += 1;
                else if (painRecord.painLocation.equals("Neck"))
                    painLocationCount[7] += 1;
                else if (painRecord.painLocation.equals("Shins"))
                    painLocationCount[8] += 1;
                else if (painRecord.painLocation.equals("Shoulders"))
                    painLocationCount[9] += 1;

        setupPieChart();
    }
}
