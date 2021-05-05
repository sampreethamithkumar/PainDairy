package com.example.paindairy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.databinding.ActivityDashboardBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class Dashboard extends AppCompatActivity implements View.OnClickListener, TimePickerDialog.OnTimeSetListener{
    private ActivityDashboardBinding binding;
    private AppBarConfiguration mAppBarConfiguration;

    private FirebaseUser user;
    private DatabaseReference reference;

    private String userID;

    private FirebaseAuth mAuth;



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
//        mAuth = FirebaseAuth.getInstance();
//
//        binding.signOut.setOnClickListener(this);
//
//        getUserDetails();
    }


    @Override
    public void onClick(View v) {
//        if (v.getId() == R.id.signOut) {
//            FirebaseAuth.getInstance().signOut();
//            startActivity(new Intent(Dashboard.this, MainActivity.class));
//        }

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

//        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), pendingIntent);

        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

}