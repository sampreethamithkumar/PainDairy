package com.example.paindairy.fragment;

import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindairy.R;
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
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class PainDataEntryFragment extends Fragment implements View.OnClickListener, SeekBar.OnSeekBarChangeListener{
    private static final String API_KEY = "af0cfc6611defe4fe50200aec8c78d50";
    private static final String KEYWORD = "melbourne";

    private PainDataEntryBinding painDataEntryBinding;
    private PainRecordViewModel painRecordViewModel;

    private FirebaseUser firebaseUser;

    private int lastId;

    private RetrofitInterface retrofitInterface;

    public PainDataEntryFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        painDataEntryBinding = PainDataEntryBinding.inflate(inflater,container, false);
        View view = painDataEntryBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        retrofitInterface = RetrofitClient.getRetrofitService();
        getWeatherDetails();

        painLocationSpinner();
        moodLevelSpinner();
        enableOrDisableButton();

        painDataEntryBinding.saveButton.setOnClickListener(this);

        painDataEntryBinding.editButton.setOnClickListener(this);

        painDataEntryBinding.seekBar.setOnSeekBarChangeListener(this);

        painDataEntryBinding.deleteButton.setOnClickListener(this);

        return view;
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        painDataEntryBinding = null;
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
        painDataEntryBinding.seekBarCurrentValue.setText(Integer.toString(progress));
    }

    @Override
    public void onStartTrackingTouch(SeekBar seekBar) {

    }

    @Override
    public void onStopTrackingTouch(SeekBar seekBar) {

    }

    public void painLocationSpinner() {
        List<String> locations = new ArrayList<>();
        locations.add("Back");
        locations.add("Neck");
        locations.add("Knees");
        locations.add("Hips");
        locations.add("Abdomen");
        locations.add("Elbows");
        locations.add("Shoulders");
        locations.add("Shins");
        locations.add("Jaw");
        locations.add("Facial");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,locations);
        painDataEntryBinding.painLocationSpinner.setAdapter(arrayAdapter);
    }

    public void moodLevelSpinner() {
        List<String> moods = new ArrayList<>();
        moods.add("Very low");
        moods.add("Low");
        moods.add("Average");
        moods.add("Good");
        moods.add("Very good");

        final ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), android.R.layout.simple_spinner_item,moods);
        painDataEntryBinding.moodLevelSpinner.setAdapter(arrayAdapter);
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
                painDataEntryBinding.textViewRead.setText("All data:" + allPainRecords.toString());
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
                                painDataEntryBinding.editButton.setEnabled(true);
                                painDataEntryBinding.saveButton.setEnabled(false);
                            }
                            else{
                                painDataEntryBinding.editButton.setEnabled(false);
                                painDataEntryBinding.saveButton.setEnabled(true);
                            }
                        else{
                            painDataEntryBinding.editButton.setEnabled(false);
                            painDataEntryBinding.saveButton.setEnabled(true);
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
        String painLevel = painDataEntryBinding.seekBarCurrentValue.getText().toString();
        String painLocation = painDataEntryBinding.painLocationSpinner.getSelectedItem().toString();
        String moodLevel = painDataEntryBinding.moodLevelSpinner.getSelectedItem().toString();
        String stepsTaken = painDataEntryBinding.stepsTakenEditText.getText().toString();
        double temp = Double.parseDouble(painDataEntryBinding.currentTemperature.getText().toString());
        double humidity = Double.parseDouble(painDataEntryBinding.currentHumidity.getText().toString());
        double pressure = Double.parseDouble(painDataEntryBinding.currentPressure.getText().toString());


        if ((!painLevel.isEmpty() && painLevel != null) && (!painLocation.isEmpty() && painLocation != null) && (!moodLevel.isEmpty() && moodLevel != null)) {
            int painLevelInt = Integer.parseInt(painLevel);
            int stepsTakenInt = Integer.parseInt(stepsTaken);
            try {
                SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
                Date currentDate = formatter.parse(formatter.format(new Date()));
                PainRecord painRecord = new PainRecord(firebaseUser.getEmail(), painLevelInt, painLocation, moodLevel, stepsTakenInt, currentDate,temp, humidity, pressure);
                painRecordViewModel.insert(painRecord);
                Toast.makeText(getActivity(), "Pain Record Inserted successfully",Toast.LENGTH_LONG).show();
                painDataEntryBinding.saveButton.setEnabled(false);
                getData();
            }
            catch (Exception exception){
                Toast.makeText(getActivity(), "Unexpected error occured inserting the data",Toast.LENGTH_LONG).show();
            }
        }
        else
            Toast.makeText(getContext(), "Unexceptional error occured inseting the record", Toast.LENGTH_LONG).show();
    }

    public void editPainRecord() {

        getData();

        /**
         * Getting last Id of the Pain Record
         */
        painRecordViewModel.getLastId(firebaseUser.getEmail()).observe(this, new Observer<Integer>() {
            @Override
            public void onChanged(Integer integer) {
                lastId = integer;
            }
        });


        String painLevel = painDataEntryBinding.seekBarCurrentValue.getText().toString();
        String painLocation = painDataEntryBinding.painLocationSpinner.getSelectedItem().toString();
        String moodLevel = painDataEntryBinding.moodLevelSpinner.getSelectedItem().toString();
        String stepsTaken = painDataEntryBinding.stepsTakenEditText.getText().toString();
        double temp = Double.parseDouble(painDataEntryBinding.currentTemperature.getText().toString());
        double humidity = Double.parseDouble(painDataEntryBinding.currentHumidity.getText().toString());
        double pressure = Double.parseDouble(painDataEntryBinding.currentPressure.getText().toString());


        if ((!painLevel.isEmpty() && painLevel != null) && (!painLocation.isEmpty() && painLocation != null) && (!moodLevel.isEmpty() && moodLevel != null)) {
            int painLevelInt = Integer.parseInt(painLevel);
            int stepsTakenInt = Integer.parseInt(stepsTaken);
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.N) {
                CompletableFuture<PainRecord> painRecordCompletableFuture = painRecordViewModel.findByIDFuture(lastId);
                painRecordCompletableFuture.thenApply(painRecord -> {
                    if (painRecord != null) {
                        painRecord.painIntensityLevel = painLevelInt;
                        painRecord.painLocation = painLocation;
                        painRecord.moodLevel = moodLevel;
                        painRecord.stepsPerDay = stepsTakenInt;
                        painRecord.temperature = temp;
                        painRecord.humidity = humidity;
                        painRecord.pressure = pressure;
                        painRecordViewModel.update(painRecord);
                        Toast.makeText(getContext(), "Pain Record Updated", Toast.LENGTH_LONG).show();
                    }
                    return painRecord;
                });
            }
        }
        else
            Toast.makeText(getContext(), "Please make changes to the value before editing", Toast.LENGTH_LONG).show();
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
        painDataEntryBinding.currentTemperature.setText(decimalFormat.format(tempDouble));
        painDataEntryBinding.currentHumidity.setText(weather.getHumidity());
        painDataEntryBinding.currentPressure.setText(weather.getPressure());
    }
}
