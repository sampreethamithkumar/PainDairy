package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindairy.Dashboard;
import com.example.paindairy.R;
import com.example.paindairy.databinding.FragmentLineGraphBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.github.mikephil.charting.charts.CombinedChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.CombinedData;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.github.mikephil.charting.utils.ViewPortHandler;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.lang.reflect.Array;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class LineGraphFragment extends Fragment implements View.OnClickListener, Observer<List<PainRecord>> {
    private FragmentLineGraphBinding fragmentLineGraphBinding;
    private FirebaseUser firebaseUser;
    private PainRecordViewModel painRecordViewModel;

    private String weatherType;

    private LineChart lineChart;
//    private CombinedChart chart;

    private List<PainRecord> userRecords;
    private ArrayList<Entry> yValue1;
    private ArrayList<Entry> yValue2;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        fragmentLineGraphBinding = FragmentLineGraphBinding.inflate(inflater,container,false);
        View view = fragmentLineGraphBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        weatherDetails();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);
//
//        setData(40,60);
//        lineChart.animateX(1000);

//        XAxis xAxis = lineChart.getXAxis();
//        YAxis yAxisLeft = lineChart.getAxisLeft();
//        YAxis yAxisRight = lineChart.getAxisRight();

//        xAxis.setValueFormatter(new MyAxisValueFormatter());

        lineChart = fragmentLineGraphBinding.chart1;
//        chart = fragmentLineGraphBinding.chart1;

        fragmentLineGraphBinding.startdatePicker.setOnClickListener(this);
        fragmentLineGraphBinding.endDatePicker.setOnClickListener(this);

        fragmentLineGraphBinding.generate.setOnClickListener(this);


       return view;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startdatePicker) {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.selectionFromFragment("startDatePicker");
        }
        else if (v.getId() == R.id.endDatePicker) {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.selectionFromFragment("endDatePicker");
        }
        else if (v.getId() == R.id.generate) {
            weatherType = fragmentLineGraphBinding.weatherVariableSpineer.getSelectedItem().toString();
            updateUserRecord();

        }
    }

    private void updateUserRecord() {
        if ((!fragmentLineGraphBinding.textViewDatePicker.getText().equals(" ")) && (!fragmentLineGraphBinding.textViewDatePickerEndDate.getText().equals(" ")))
            painRecordViewModel.getAllPainRecords().observe(getViewLifecycleOwner(), this);
    }

    public void weatherDetails() {
        List<String> weather = new ArrayList<>();
        weather.add("Temperature");
        weather.add("Humidity");
        weather.add("Pressure");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, weather);
        fragmentLineGraphBinding.weatherVariableSpineer.setAdapter(arrayAdapter);
    }

    @Override
    public void onChanged(List<PainRecord> painRecords) {
        List<PainRecord> userPainRecord = new ArrayList<>();

        HashMap<String, Date> dates = getDates();
        if (dates.size() == 0)
            return;

        for (PainRecord painRecord: painRecords) {
            if (painRecord.emailId.equals(firebaseUser.getEmail())) {
                if ((painRecord.currentDate.after(dates.get("startDate")) && painRecord.currentDate.before(dates.get("endDate"))) ||
                        (painRecord.currentDate.equals(dates.get("startDate"))) ||
                        (painRecord.currentDate.equals(dates.get("endDate")))) {
                    userPainRecord.add(painRecord);
                }
            }
        }
        userRecords = userPainRecord;
        setData();
    }

    private HashMap<String, Date> getDates() {

        HashMap<String, Date> dates = new HashMap<>();

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Dashboard dashboard = (Dashboard) getActivity();
            Calendar startDateCalendar = dashboard.getStartDate();
            Calendar endDateCalendar = dashboard.getEndDate();

            if (startDateCalendar != null && endDateCalendar != null) {
                Date startDate = formatter.parse(formatter.format(startDateCalendar.getTime()));
                Date endDate = formatter.parse(formatter.format(endDateCalendar.getTime()));
                dates.put("startDate", startDate);
                dates.put("endDate", endDate);
            }
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return dates;
    }


    private void setData() {
        yValue1 = new ArrayList<>();

        for (int i = 0; i < userRecords.size(); i ++) {
            yValue1.add(new Entry(i, userRecords.get(i).painIntensityLevel));
        }

        yValue2 = new ArrayList<>();

        if (weatherType.equals("Temperature")) {
            for (int i = 0; i< userRecords.size(); i++) {
                yValue2.add(new Entry(i, (float) userRecords.get(i).temperature));
            }
        }
        else if (weatherType.equals("Humidity")) {
            for (int i = 0; i < userRecords.size(); i++){
                yValue2.add(new Entry(i, (float) userRecords.get(i).humidity));
            }
        }
        else if (weatherType.equals("Pressure")) {
            for (int i = 0; i < userRecords.size(); i++) {
                yValue2.add(new Entry(i, (float) userRecords.get(i).pressure));
            }
        }

        LineDataSet set_1 = new LineDataSet(yValue1, "Pain Level");
        LineDataSet set_2 = new LineDataSet(yValue2, weatherType);
        set_2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData data = new LineData(set_1,set_2);
        set_1.setColor(ColorTemplate.rgb("#FF0000"));
        set_1.setCircleColor(ColorTemplate.rgb("#FF0000"));

        set_2.setCircleColor(ColorTemplate.rgb("#0000FF"));
        set_2.setColor(ColorTemplate.rgb("#0000FF"));


        lineChart.getAxisLeft().setAxisMinimum(0f);
        lineChart.getAxisLeft().setAxisMaximum(10.0f);

        lineChart.getAxisRight().setLabelCount(5,true);
        lineChart.getAxisRight().setDrawGridLines(false);


        Dashboard dashboard = (Dashboard) getActivity();
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);

        lineChart.getXAxis().setLabelCount((int) dashboard.difference(), true);
        lineChart.setData(data);
        lineChart.setVisibility(View.VISIBLE);
    }

    private ArrayList<String> getLeftAxisFormatter() {
        ArrayList<String> yLeftAxisFormatter = new ArrayList<>();
        for (Entry entry:yValue1)
            yLeftAxisFormatter.add(Float.toString(entry.getY()));

        return yLeftAxisFormatter;
    }

    private ArrayList<String> getRightAxisFormatter() {
        ArrayList<String> yRightAxisFormatter = new ArrayList<>();
        for (Entry entry:yValue2)
            yRightAxisFormatter.add(Float.toString(entry.getY()));

        return yRightAxisFormatter;
    }

}

//    private void setData(int count, int range) {
//        ArrayList<Entry> yVal1 = new ArrayList<>();
//
//
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 250;
//            yVal1.add(new Entry(i, val));
//        }
//
//        ArrayList<Entry> yVal2 = new ArrayList<>();
//        for (int i = 0; i < count; i++) {
//            float val = (float) (Math.random() * range) + 500;
//            yVal2.add(new Entry(i, val));
//        }
//
//        LineDataSet set_1 = new LineDataSet(yVal1, "Data Set 1");
//        LineDataSet set_2 = new LineDataSet(yVal2, "Data Set 2");
//
//
//
//        LineData data = new LineData(set_1,set_2);
//
//        XAxis xAxis = lineChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
//
//        lineChart.getAxisLeft().setValueFormatter(new IndexAxisValueFormatter(getAreaCount()));
//
//
//        String[] stringArray = { "1" , "2", "3", "4","5","6" };
//        lineChart.getAxisLeft().setValueFormatter(new ValueFormatter() {
//            @Override
//            public String getFormattedValue(float value) {
//                if (counter < stringArray.length)
//                    return stringArray[counter++];
//
//                return "0";
//            }
//        });
//
//        lineChart.setData(data);
//    }
//
//    public String[] getAreaCount() {
//        String[] array = { "1" , "2", "3", "4","5","6" };
//
//        return array;
//    }
//
////    private class MyAxisValueFormatter extends ValueFormatter implements IAxisValueFormatter{
////
////        @Override
////        public String getFormattedValue(float value, AxisBase axis) {
////            return "Day" + value;
////        }
////    }
//
//    public class MyXAxisValueFormatter extends IndexAxisValueFormatter {
//
//        @Override
//        public String getFormattedValue(float value) {
//            return "Day" + value;
//        }
//    }
//}
