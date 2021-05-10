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
import com.github.mikephil.charting.components.Description;
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

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.stat.correlation.PearsonsCorrelation;

public class LineGraphFragment extends Fragment implements View.OnClickListener, Observer<List<PainRecord>> {
    private FragmentLineGraphBinding fragmentLineGraphBinding;
    private FirebaseUser firebaseUser;
    private PainRecordViewModel painRecordViewModel;

    private String weatherType;

    private LineChart lineChart;

    private List<PainRecord> userRecords;
    private ArrayList<Entry> yValue1;
    private ArrayList<Entry> yValue2;

    /**
     * Fragment onCreate lifeCycle
     * @param inflater
     * @param container
     * @param savedInstaceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstaceState) {
        fragmentLineGraphBinding = FragmentLineGraphBinding.inflate(inflater, container, false);
        View view = fragmentLineGraphBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        weatherDetails();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        lineChart = fragmentLineGraphBinding.chart1;

        fragmentLineGraphBinding.startdatePicker.setOnClickListener(this);
        fragmentLineGraphBinding.endDatePicker.setOnClickListener(this);

        fragmentLineGraphBinding.generate.setOnClickListener(this);

        fragmentLineGraphBinding.correlation.setOnClickListener(this);


        return view;
    }

    /**
     * On Button CLick listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.startdatePicker) {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.selectionFromFragment("startDatePicker");
        } else if (v.getId() == R.id.endDatePicker) {
            Dashboard dashboard = (Dashboard) getActivity();
            dashboard.selectionFromFragment("endDatePicker");
        } else if (v.getId() == R.id.generate) {
            weatherType = fragmentLineGraphBinding.weatherVariableSpineer.getSelectedItem().toString();
            updateUserRecord();
        } else if (v.getId() == R.id.correlation) {
            fragmentLineGraphBinding.correlationTextView.setVisibility(View.VISIBLE);
            fragmentLineGraphBinding.correlationTextView.setText(performCorrelation());
        }
    }

    /**
     * Get the user record only if the start date and end date is provided
     */
    private void updateUserRecord() {
        if ((!fragmentLineGraphBinding.textViewDatePicker.getText().equals(" ")) && (!fragmentLineGraphBinding.textViewDatePickerEndDate.getText().equals(" ")))
            painRecordViewModel.getAllPainRecords().observe(getViewLifecycleOwner(), this);
    }

    /**
     * Weather selection spinner
     */
    public void weatherDetails() {
        List<String> weather = new ArrayList<>();
        weather.add("Temperature");
        weather.add("Humidity");
        weather.add("Pressure");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item, weather);
        fragmentLineGraphBinding.weatherVariableSpineer.setAdapter(arrayAdapter);
    }

    /**
     * Live Data of PainRecord on Change listener
     * @param painRecords
     */
    @Override
    public void onChanged(List<PainRecord> painRecords) {
        List<PainRecord> userPainRecord = new ArrayList<>();

        HashMap<String, Date> dates = getDates();
        if (dates.size() == 0)
            return;

        for (PainRecord painRecord : painRecords) {
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

    /**
     * Get the start date and end date selected by user
     * @return
     */
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

    /**
     * Sets the data on the line graph
     */
    private void setData() {
        yValue1 = new ArrayList<>();

        for (int i = 0; i < userRecords.size(); i++) {
            yValue1.add(new Entry(i, userRecords.get(i).painIntensityLevel));
        }

        yValue2 = new ArrayList<>();

        if (weatherType.equals("Temperature")) {
            for (int i = 0; i < userRecords.size(); i++) {
                yValue2.add(new Entry(i, (float) userRecords.get(i).temperature));
            }
        } else if (weatherType.equals("Humidity")) {
            for (int i = 0; i < userRecords.size(); i++) {
                yValue2.add(new Entry(i, (float) userRecords.get(i).humidity));
            }
        } else if (weatherType.equals("Pressure")) {
            for (int i = 0; i < userRecords.size(); i++) {
                yValue2.add(new Entry(i, (float) userRecords.get(i).pressure));
            }
        }

        LineDataSet set_1 = new LineDataSet(yValue1, "Pain Level");
        LineDataSet set_2 = new LineDataSet(yValue2, weatherType);
        set_2.setAxisDependency(YAxis.AxisDependency.RIGHT);
        LineData data = new LineData(set_1, set_2);
        set_1.setColor(ColorTemplate.rgb("#FF0000"));
        set_1.setCircleColor(ColorTemplate.rgb("#FF0000"));

        set_2.setCircleColor(ColorTemplate.rgb("#0000FF"));
        set_2.setColor(ColorTemplate.rgb("#0000FF"));


        lineChart.getAxisLeft().setAxisMinimum(0f);

        lineChart.getAxisLeft().setAxisMaximum(10.0f);

        lineChart.getAxisRight().setLabelCount(5, true);
        lineChart.getAxisRight().setDrawGridLines(false);


        Dashboard dashboard = (Dashboard) getActivity();
        lineChart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
        lineChart.getXAxis().setLabelCount(1, true);
        lineChart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(getXAxisFormatter()));

        lineChart.getXAxis().setLabelCount((int) dashboard.difference(), true);
        lineChart.setData(data);
        lineChart.setVisibility(View.VISIBLE);
        fragmentLineGraphBinding.correlation.setVisibility(View.VISIBLE);
    }

    /**
     * Formate the x-axis based on the date.
     * @return
     */
    private ArrayList<String> getXAxisFormatter() {
        ArrayList<String> date = new ArrayList<>();
        for (int i = 0; i < userRecords.size(); i++) {
            SimpleDateFormat formatter = new SimpleDateFormat("dd");
            Date dateValue = userRecords.get(i).currentDate;
            date.add("Day " + formatter.format(dateValue));
        }

        return date;
    }

    /**
     * Perform Correlation from the data present.
     * @return
     */
    private String performCorrelation() {

        double data[][] = new double[yValue1.size()][2];

        for (int i = 0; i < yValue1.size(); i++) {
            data[i][0] = yValue1.get(i).getY();
            data[i][1] = yValue2.get(i).getY();
        }

        RealMatrix m = MatrixUtils.createRealMatrix(data);

        // correlation test (another method): x-y
        PearsonsCorrelation pc = new PearsonsCorrelation(m);
        RealMatrix corM = pc.getCorrelationMatrix();
        // significant test of the correlation coefficient (p-value)
        RealMatrix pM = pc.getCorrelationPValues();
        return ("p value:" + pM.getEntry(0, 1) + "\n" + " correlation: " + corM.getEntry(0, 1));
    }
}

