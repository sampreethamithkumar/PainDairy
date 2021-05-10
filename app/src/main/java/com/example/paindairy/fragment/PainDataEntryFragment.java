package com.example.paindairy.fragment;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.SeekBar;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.paindairy.R;
import com.example.paindairy.alarm.TimePickerFragment;
import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.alarm.NotificationHelper;
import com.example.paindairy.databinding.FragmentDataEntryBinding;
import com.example.paindairy.databinding.PainDataEntryBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.entity.Weather;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.example.paindairy.weatherApi.Main;
import com.example.paindairy.weatherApi.RetrofitClient;
import com.example.paindairy.weatherApi.RetrofitInterface;
import com.example.paindairy.weatherApi.WeatherAPI;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Pain Data Entry Fragment with two child fragments
 * Data Entry
 * Alarm Fragment
 */
public class PainDataEntryFragment extends Fragment implements BottomNavigationView.OnNavigationItemSelectedListener {

    private PainDataEntryBinding painDataEntryBinding;

    public PainDataEntryFragment() {

    }

    /**
     * Fragment onCreate life cycle view
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        painDataEntryBinding = PainDataEntryBinding.inflate(inflater, container, false);
        View view = painDataEntryBinding.getRoot();

        painDataEntryBinding.bottomNavigation.setOnNavigationItemSelectedListener(this);
        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, new DataEntryFragment()).commit();

        return view;

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        painDataEntryBinding = null;
    }

    /**
     * Bottom Navigation opens fragment based on the fragment selection
     * @param item
     * @return
     */
    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        Fragment selectedFragment = null;

        switch (item.getItemId()) {
            case R.id.nav_alarm:
                selectedFragment = new AlarmFragment();
                break;
            case R.id.nav_input:
                selectedFragment = new DataEntryFragment();
                break;
        }

        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragmentContainer, selectedFragment).commit();

        return true;
    }
}
