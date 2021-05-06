package com.example.paindairy.fragment;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.graphics.Color;
import android.os.Bundle;

import com.example.paindairy.databinding.FragmentDonutBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.example.paindairy.viewmodel.SharedViewModel;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class DonutFragment extends Fragment {

    private FragmentDonutBinding fragmentDonutBinding;
    private PieChart pieChart;

    private PainRecordViewModel painRecordViewModel;

    private FirebaseUser firebaseUser;

    private PainRecord currentDayPainRecord;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        fragmentDonutBinding = FragmentDonutBinding.inflate(inflater,container,false);
        View view = fragmentDonutBinding.getRoot();

        pieChart = fragmentDonutBinding.donutChart;

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);


        setupPieChart();
        getCurrentDayPainRecord();

       return view;
    }

    private void setupPieChart() {
        pieChart.setDrawHoleEnabled(true);
//        pieChart.setUsePercentValues(true);
        pieChart.setEntryLabelTextSize(12);
        pieChart.setEntryLabelColor(Color.WHITE);
        pieChart.setCenterText("Steps Goal");
        pieChart.setCenterTextSize(24);
        pieChart.getDescription().setEnabled(false);

        Legend l = pieChart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.TOP);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.RIGHT);
        l.setOrientation(Legend.LegendOrientation.VERTICAL);
        l.setDrawInside(false);
        l.setEnabled(true);
    }

    private void loadPieChartData() {
        if (currentDayPainRecord == null)
            return;

        double currentDayStepsTaken = currentDayPainRecord.stepsPerDay;
        double remainingSteps = (currentDayPainRecord.stepGoal - currentDayStepsTaken) <= 0 ? 0 : (currentDayPainRecord.stepGoal - currentDayStepsTaken);

        ArrayList<PieEntry> entries = new ArrayList<>();
        entries.add(new PieEntry((float) currentDayStepsTaken, "Steps Taken"));
        entries.add(new PieEntry((float) remainingSteps, "Steps Remaining"));


        PieDataSet dataSet = new PieDataSet(entries, "");
        dataSet.setColors(ColorTemplate.COLORFUL_COLORS);

        PieData data = new PieData(dataSet);
        data.setDrawValues(true);
        data.setValueFormatter(new PercentFormatter());
        data.setValueTextSize(12f);
        data.setValueTextColor(Color.WHITE);

        pieChart.setData(data);
        pieChart.invalidate();

        pieChart.animateY(1400, Easing.EaseInOutQuad);

    }

    private void getCurrentDayPainRecord() {
        String userEmailId = firebaseUser.getEmail();
        Date currentDate;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            currentDate = formatter.parse(formatter.format(new Date()));

            if (userEmailId != null) {
                painRecordViewModel.getLastUpdatedDate(userEmailId).observe(getViewLifecycleOwner(), new Observer<PainRecord>() {
                    @Override
                    public void onChanged(PainRecord painRecord) {
                        if (painRecord != null) {
                            if (currentDate.compareTo(painRecord.currentDate) == 0) {
                                currentDayPainRecord = painRecord;
                                loadPieChartData();
                            }
                        }
                    }
                });
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }

    }
}
