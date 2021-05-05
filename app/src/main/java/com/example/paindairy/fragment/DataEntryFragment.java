package com.example.paindairy.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Toast;

import com.example.paindairy.R;
import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.alarm.TimePickerFragment;
import com.example.paindairy.databinding.FragmentDataEntryBinding;
import com.example.paindairy.databinding.PainDataEntryBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.entity.Weather;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.example.paindairy.weatherApi.Main;
import com.example.paindairy.weatherApi.RetrofitClient;
import com.example.paindairy.weatherApi.RetrofitInterface;
import com.example.paindairy.weatherApi.WeatherAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class DataEntryFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private static final String API_KEY = "af0cfc6611defe4fe50200aec8c78d50";
    private static final String KEYWORD = "melbourne";

    private FragmentDataEntryBinding fragmentDataEntryBinding;
    private PainRecordViewModel painRecordViewModel;

    private FirebaseUser firebaseUser;

    private int lastId;

    private RetrofitInterface retrofitInterface;

    private PainRecord currentDayPainRecord;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragmentDataEntryBinding = FragmentDataEntryBinding.inflate(inflater,container, false);
        View view = fragmentDataEntryBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        retrofitInterface = RetrofitClient.getRetrofitService();
        getWeatherDetails();

        painLocationSpinner();
        moodLevelSpinner();
        enableOrDisableButton();

        fragmentDataEntryBinding.saveButton.setOnClickListener(this);

        fragmentDataEntryBinding.editButton.setOnClickListener(this);

        fragmentDataEntryBinding.seekBar.setOnSeekBarChangeListener(this);

        fragmentDataEntryBinding.deleteButton.setOnClickListener(this);

        return view;
    }



    @Override
    public void onDestroyView() {
        super.onDestroyView();
        fragmentDataEntryBinding = null;
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.saveButton) {
            insertPainRecord();
        }
        else if (v.getId() == R.id.editButton) {
            editPainRecord();
        }
        else if (v.getId() == R.id.deleteButton) {
            deleteRecords();
        }

    }

    private void deleteRecords() {
        painRecordViewModel.deleteAll();
    }

    @Override
    public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        fragmentDataEntryBinding.seekBarCurrentValue.setText(Integer.toString(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void painLocationSpinner() {
        List<String> locations = new ArrayList<>();
        locations.add("Abdomen");
        locations.add("Back");
        locations.add("Elbows");
        locations.add("Facial");
        locations.add("Hips");
        locations.add("Jaw");
        locations.add("Knees");
        locations.add("Neck");
        locations.add("Shins");
        locations.add("Shoulders");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,locations);
        fragmentDataEntryBinding.painLocationSpinner.setAdapter(arrayAdapter);
    }

    public void moodLevelSpinner() {
        List<String> moods = new ArrayList<>();
        moods.add("Very low");
        moods.add("Low");
        moods.add("Average");
        moods.add("Good");
        moods.add("Very good");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,moods);
        fragmentDataEntryBinding.moodLevelSpinner.setAdapter(arrayAdapter);
    }

    public void getData() {
        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        painRecordViewModel.getAllPainRecords().observe(this, new Observer<List<PainRecord>>() {
            @Override
            public void onChanged(List<PainRecord> painRecords) {
                StringBuffer allPainRecords = new StringBuffer("");
                for (PainRecord painRecord : painRecords) {
                    StringBuffer userDetails = new StringBuffer(painRecord.uid + " " + painRecord.emailId + " "
                            + painRecord.painIntensityLevel + " " + painRecord.currentDate + " " + painRecord.stepsPerDay + " "
                            + painRecord.moodLevel + " " + painRecord.painLocation );
                    allPainRecords = allPainRecords.append(System.getProperty("line.separator") + userDetails.toString());
                }
                fragmentDataEntryBinding.textViewRead.setText("All data:" + allPainRecords.toString());
            }
        });
    }

    private void enableOrDisableButton() {
        String userEmailId = firebaseUser.getEmail();
        Date currentDate;
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            currentDate = formatter.parse(formatter.format(new Date()));

            if (userEmailId != null) {
                painRecordViewModel.getLastUpdatedDate(userEmailId).observe(getViewLifecycleOwner(), new Observer<PainRecord>() {
                    @Override
                    public void onChanged(PainRecord painRecord) {
                        if (painRecord != null)
                            if (currentDate.compareTo(painRecord.currentDate) == 0) {
                                fragmentDataEntryBinding.editButton.setEnabled(true);
                                fragmentDataEntryBinding.saveButton.setEnabled(false);

                                currentDayPainRecord = painRecord;
                            }
                            else{
                                fragmentDataEntryBinding.editButton.setEnabled(false);
                                fragmentDataEntryBinding.saveButton.setEnabled(true);
                            }
                        else{
                            fragmentDataEntryBinding.editButton.setEnabled(false);
                            fragmentDataEntryBinding.saveButton.setEnabled(true);
                        }
                    }
                });
            }
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public void insertPainRecord() {
        String painLevel = fragmentDataEntryBinding.seekBarCurrentValue.getText().toString();
        String painLocation = fragmentDataEntryBinding.painLocationSpinner.getSelectedItem().toString();
        String moodLevel = fragmentDataEntryBinding.moodLevelSpinner.getSelectedItem().toString();
        String stepsTaken = fragmentDataEntryBinding.stepsTakenEditText.getText().toString();
        double temp = Double.parseDouble(fragmentDataEntryBinding.currentTemperature.getText().toString());
        double humidity = Double.parseDouble(fragmentDataEntryBinding.currentHumidity.getText().toString());
        double pressure = Double.parseDouble(fragmentDataEntryBinding.currentPressure.getText().toString());

        try{
            Integer.parseInt(stepsTaken);
        }
        catch (Exception exception) {
            fragmentDataEntryBinding.stepsTakenEditText.setError("Should be Numbers only");
            fragmentDataEntryBinding.stepsTakenEditText.requestFocus();
            return;
        }

        if ((!painLevel.isEmpty() && painLevel != null) && (!painLocation.isEmpty() && painLocation != null) && (!moodLevel.isEmpty() && moodLevel != null)) {
            int painLevelInt = Integer.parseInt(painLevel);
            int stepsTakenInt = Integer.parseInt(stepsTaken);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date currentDate = formatter.parse(formatter.format(new Date()));
                PainRecord painRecord = new PainRecord(firebaseUser.getEmail(), painLevelInt, painLocation, moodLevel, stepsTakenInt, currentDate,temp, humidity, pressure);
                painRecordViewModel.insert(painRecord);
                Toast.makeText(getActivity(), "Pain Record Inserted successfully",Toast.LENGTH_LONG).show();
                fragmentDataEntryBinding.saveButton.setEnabled(false);
            }
            catch (Exception exception){
                Toast.makeText(getActivity(), "Unexpected error occured inserting the data",Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(getContext(), "Unexceptional error occured inseting the record", Toast.LENGTH_LONG).show();
    }

    public void editPainRecord() {

        /**
         * Getting last Id of the Pain Record
         */
        painRecordViewModel.getLastId(firebaseUser.getEmail()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                lastId = integer;
            }
        });


        String painLevel = fragmentDataEntryBinding.seekBarCurrentValue.getText().toString();
        String painLocation = fragmentDataEntryBinding.painLocationSpinner.getSelectedItem().toString();
        String moodLevel = fragmentDataEntryBinding.moodLevelSpinner.getSelectedItem().toString();
        String stepsTaken = fragmentDataEntryBinding.stepsTakenEditText.getText().toString();
        double temp = Double.parseDouble(fragmentDataEntryBinding.currentTemperature.getText().toString());
        double humidity = Double.parseDouble(fragmentDataEntryBinding.currentHumidity.getText().toString());
        double pressure = Double.parseDouble(fragmentDataEntryBinding.currentPressure.getText().toString());


        try{
            Integer.parseInt(stepsTaken);
        }
        catch (Exception exception) {
            fragmentDataEntryBinding.stepsTakenEditText.setError("Should be Numbers only");
            fragmentDataEntryBinding.stepsTakenEditText.requestFocus();
            return;
        }

        if ((!painLevel.isEmpty() && painLevel != null) && (!painLocation.isEmpty() && painLocation != null) && (!moodLevel.isEmpty() && moodLevel != null)) {
            int painLevelInt = Integer.parseInt(painLevel);
            int stepsTakenInt = Integer.parseInt(stepsTaken);
            currentDayPainRecord.painIntensityLevel = painLevelInt;
            currentDayPainRecord.painLocation = painLocation;
            currentDayPainRecord.moodLevel = moodLevel;
            currentDayPainRecord.stepsPerDay = stepsTakenInt;
            currentDayPainRecord.temperature = temp;
            currentDayPainRecord.humidity = humidity;
            currentDayPainRecord.pressure = pressure;

            try {
                painRecordViewModel.update(currentDayPainRecord);
                Toast.makeText(getActivity(), "Pain Record Updated", Toast.LENGTH_LONG).show();
            }
            catch (Exception exception) {
                Toast.makeText(getActivity(), "Unexpected error occured!", Toast.LENGTH_LONG).show();
            }

        }
        else
            Toast.makeText(getActivity(), "Please make changes to the value before editing", Toast.LENGTH_LONG).show();
    }

    private void getWeatherDetails() {
        Call<WeatherAPI> callAsync = retrofitInterface.weatherApi(API_KEY,KEYWORD);

        callAsync.enqueue(new Callback<WeatherAPI>() {
            @Override
            public void onResponse(Call<WeatherAPI> call, Response<WeatherAPI> response) {
                if (response.isSuccessful()) {
                    Main main = response.body().main;
                    Weather weather = new Weather(main.getTemp(), main.getPressure(), main.getHumidity());

                    displayWeatherDetails(weather);
                }
                else
                    Log.i("Error", "Response Failed");
            }

            @Override
            public void onFailure(Call<WeatherAPI> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    private void displayWeatherDetails(Weather weather) {
        double tempDouble = Double.parseDouble(weather.getTemperature()) - 273.15;
        DecimalFormat decimalFormat = new DecimalFormat("##.##");
        fragmentDataEntryBinding.currentTemperature.setText(decimalFormat.format(tempDouble));
        fragmentDataEntryBinding.currentHumidity.setText(weather.getHumidity());
        fragmentDataEntryBinding.currentPressure.setText(weather.getPressure());
    }
}