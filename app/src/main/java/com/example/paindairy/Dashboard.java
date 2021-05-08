package com.example.paindairy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.databinding.ActivityDashboardBinding;
import com.example.paindairy.databinding.FragmentLineGraphBinding;
import com.example.paindairy.forwarding.OnDateSetListener;
import com.example.paindairy.fragment.DatePickerFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener {
    private ActivityDashboardBinding binding;
    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private FirebaseAuth mAuth;

    String datePickerType;

    private Calendar startDate;
    private Calendar endDate;

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);


        setSupportActionBar(binding.appBar.toolbar);
        
        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home_fragment, R.id.nav_account_fragment,R.id.nav_daily_record_fragment, R.id.nav_map_fragment, R.id.nav_pain_data_entry_fragment, R.id.nav_report_fragment)
                .setOpenableLayout(binding.drawerLayout)
                .build();

        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.navView, navController);

        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController,mAppBarConfiguration);
    }


    @Override
    public void onClick(View v) {

    }


    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute - 2);
        calendar.set(Calendar.SECOND, 0);

        startAlarm(calendar);
    }

    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }



    @Override
    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
        Calendar c = Calendar.getInstance();
        c.set(Calendar.YEAR, year);
        c.set(Calendar.MONTH, month);
        c.set(Calendar.DAY_OF_MONTH, dayOfMonth);
        String currentDateString = DateFormat.getDateInstance(DateFormat.FULL).format(c.getTime());

        if (datePickerType != null) {
            if (datePickerType.equals("startDatePicker")) {
                TextView textView = findViewById(R.id.textViewDatePicker);
                textView.setText(currentDateString);
                startDate = c;
            }
            else if (datePickerType.equals("endDatePicker")) {
                TextView textView = findViewById(R.id.textViewDatePickerEndDate);
                textView.setText(currentDateString);
                endDate = c;

//                difference();
            }
        }
    }

    public long difference() {
        long differenceBetweenDate = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());

        return differenceBetweenDate;

    }

    public void selectionFromFragment(String picker) {
        datePickerType = picker;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }
}