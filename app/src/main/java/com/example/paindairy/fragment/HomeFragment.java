package com.example.paindairy.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Data;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import com.example.paindairy.R;
import com.example.paindairy.entity.User;
import com.example.paindairy.databinding.HomeFragmentBinding;
import com.example.paindairy.entity.Weather;
import com.example.paindairy.viewmodel.SharedViewModel;
import com.example.paindairy.weatherApi.Main;
import com.example.paindairy.weatherApi.RetrofitClient;
import com.example.paindairy.weatherApi.RetrofitInterface;
import com.example.paindairy.weatherApi.WeatherAPI;
import com.example.paindairy.worker.PushPainRecordToFirebaseWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Dashboard Home Fragment
 * Welcomes the user with his/her name
 * Shows the weather
 */
public class HomeFragment extends Fragment {
    private static final String API_KEY = "af0cfc6611defe4fe50200aec8c78d50";
    private static final String KEYWORD = "melbourne";

    private HomeFragmentBinding addBinding;

    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private String userID;

    private RetrofitInterface retrofitInterface;

    private SharedViewModel model;

    public HomeFragment() {

    }

    /**
     * Fragment onCreate lifeCycle
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        addBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();

        model = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        addBinding.imageView.setImageResource(R.drawable.download);

        setDate();

        mAuth = FirebaseAuth.getInstance();
        retrofitInterface = RetrofitClient.getRetrofitService();
        getUserDetails();
        getWeatherDetails();

        return view;
    }

    private void setDate() {
        try {
            Calendar c = Calendar.getInstance();
            String currentDate = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());
            addBinding.date.setText("Date: " + currentDate);
        }
        catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }

    /**
     * Get's the user details from the firebase
     */
    private void getUserDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null) {
                    String fullName = userProfile.fullName;
                    greetTheUser(fullName);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(getActivity(), "Something Wrong happened", Toast.LENGTH_LONG).show();
            }
        });
    }

    /**
     * Greets the loged in user.
     * @param name
     */
    private void greetTheUser(String name) {
        addBinding.userName.setText(name);
    }


    /**
     * Retrofit
     * Calls the open.weather api
     * to get the weather data of melbourne
     */
    private void getWeatherDetails() {
        Call<WeatherAPI> callAsync = retrofitInterface.weatherApi(API_KEY, KEYWORD);

        callAsync.enqueue(new Callback<WeatherAPI>() {
            @Override
            public void onResponse(Call<WeatherAPI> call, Response<WeatherAPI> response) {
                if (response.isSuccessful()) {
                    Main main = response.body().main;

                    double tempDouble = Double.parseDouble(main.getTemp()) - 273.15;
                    DecimalFormat decimalFormat = new DecimalFormat("##.##");
                    model.setWeatherApi(Double.parseDouble(decimalFormat.format(tempDouble)), Double.parseDouble(main.getPressure()), Double.parseDouble(main.getHumidity()));
                    displayWeatherDetails();
                } else
                    Log.i("Error", "Response Failed");
            }

            @Override
            public void onFailure(Call<WeatherAPI> call, Throwable t) {
                Toast.makeText(getContext(), t.getMessage(), Toast.LENGTH_SHORT);
            }
        });
    }

    /**
     * Weather details got from API is displayed on the screen.
     */
    private void displayWeatherDetails() {
        model.getWeatherApi().observe(getViewLifecycleOwner(), new Observer<Map<String, Double>>() {
            @Override
            public void onChanged(Map<String, Double> weather) {
                addBinding.currentTemperature.setText(Double.toString(weather.get("temperature")) + " C");
                addBinding.currentHumidity.setText(Double.toString(weather.get("humidity")) + "%");
                addBinding.currentPressure.setText(Double.toString(weather.get("pressure")) + " pa");
            }
        });
    }

}
