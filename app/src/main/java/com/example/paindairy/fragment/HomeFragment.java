package com.example.paindairy.fragment;

import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.paindairy.entity.User;
import com.example.paindairy.databinding.HomeFragmentBinding;
import com.example.paindairy.entity.Weather;
import com.example.paindairy.weatherApi.Main;
import com.example.paindairy.weatherApi.RetrofitClient;
import com.example.paindairy.weatherApi.RetrofitInterface;
import com.example.paindairy.weatherApi.WeatherAPI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DecimalFormat;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends Fragment {
    private static final String API_KEY = "af0cfc6611defe4fe50200aec8c78d50";
    private static final String KEYWORD = "melbourne";

    private HomeFragmentBinding addBinding;

    private FirebaseUser user;
    private DatabaseReference reference;
    private FirebaseAuth mAuth;

    private String userID;

    private RetrofitInterface retrofitInterface;

    public HomeFragment(){

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        addBinding = HomeFragmentBinding.inflate(inflater, container, false);
        View view = addBinding.getRoot();
        mAuth = FirebaseAuth.getInstance();

        retrofitInterface = RetrofitClient.getRetrofitService();

        getUserDetails();
        getWeatherDetails();

        return view;
    }
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        addBinding = null;
    }
    private void getUserDetails() {
        user = FirebaseAuth.getInstance().getCurrentUser();
        reference = FirebaseDatabase.getInstance().getReference("Users");
        userID = user.getUid();

        reference.child(userID).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User userProfile = snapshot.getValue(User.class);
                if (userProfile != null){
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

    private void greetTheUser(String name) {
        addBinding.userName.setText(name);
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
        addBinding.currentTemperature.setText(decimalFormat.format(tempDouble) + " C");
        addBinding.currentHumidity.setText(weather.getHumidity() + "%");
        addBinding.currentPressure.setText(weather.getPressure() + " Pa");
    }

}
