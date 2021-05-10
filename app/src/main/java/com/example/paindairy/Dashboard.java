package com.example.paindairy;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.fragment.NavHostFragment;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkManager;
import androidx.work.WorkRequest;

import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;

import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.databinding.ActivityDashboardBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.fragment.DatePickerFragment;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.example.paindairy.worker.PushPainRecordToFirebaseWorker;
import com.google.firebase.auth.FirebaseAuth;
import com.google.gson.Gson;

import java.text.DateFormat;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.concurrent.TimeUnit;

public class Dashboard extends AppCompatActivity implements TimePickerDialog.OnTimeSetListener, DatePickerDialog.OnDateSetListener, Observer<PainRecord> {

    private ActivityDashboardBinding binding;
    private AppBarConfiguration mAppBarConfiguration;

    private PainRecordViewModel painRecordViewModel;

    String datePickerType;

    private Calendar startDate;
    private Calendar endDate;

    SharedPreferences sharedPreferencesDashboard;

    /**
     * Activity oncreate lifecycle
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDashboardBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);
        setSupportActionBar(binding.appBar.toolbar);

        sharedPreferencesDashboard = getSharedPreferences("sharedPreferencesDashboard", Context.MODE_PRIVATE);

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        painRecordViewModel.getLastUpdatedDate(FirebaseAuth.getInstance().getCurrentUser().getEmail()).observe(this, this);

        mAppBarConfiguration = new AppBarConfiguration.Builder(R.id.nav_home_fragment, R.id.nav_account_fragment,R.id.nav_daily_record_fragment, R.id.nav_map_fragment, R.id.nav_pain_data_entry_fragment, R.id.nav_report_fragment)
                .setOpenableLayout(binding.drawerLayout)
                .build();


        FragmentManager fragmentManager = getSupportFragmentManager();
        NavHostFragment navHostFragment = (NavHostFragment) fragmentManager.findFragmentById(R.id.nav_host_fragment);

        NavController navController = navHostFragment.getNavController();

        NavigationUI.setupWithNavController(binding.navView, navController);

        NavigationUI.setupWithNavController(binding.appBar.toolbar, navController,mAppBarConfiguration);

    }

    /**
     * Time Picker fragment called
     * @param view
     * @param hourOfDay
     * @param minute
     */
    @Override
    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
        Calendar calendar = Calendar.getInstance();
        calendar.set(Calendar.HOUR_OF_DAY, hourOfDay);
        calendar.set(Calendar.MINUTE, minute - 2);
        calendar.set(Calendar.SECOND, 0);

        startAlarm(calendar);
    }

    /**
     * Start Alarm Clock
     * @param calendar
     */
    private void startAlarm(Calendar calendar) {
        AlarmManager alarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(this, AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(this, 1, intent, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }

    /**
     * On Date picker fragment
     * @param view
     * @param year
     * @param month
     * @param dayOfMonth
     */
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
            }
        }
    }

    /**
     * Get the difference of days selected
     * to generate the line graph
     * @return
     */
    public long difference() {
        long differenceBetweenDate = ChronoUnit.DAYS.between(startDate.toInstant(), endDate.toInstant());

        return differenceBetweenDate;

    }

    /**
     * Data picker fragment pop's up.
     * @param picker
     */
    public void selectionFromFragment(String picker) {
        datePickerType = picker;
        DialogFragment datePicker = new DatePickerFragment();
        datePicker.show(getSupportFragmentManager(), "date picker");
    }

    /**
     * Live Data Change Observer
     * @param painRecord
     */
    @Override
    public void onChanged(PainRecord painRecord) {

        if(painRecord == null)
            return;


        SharedPreferences.Editor editor = sharedPreferencesDashboard.edit();
        Gson gson = new Gson();
        String json = gson.toJson(painRecord);

        editor.putString("painRecord", json);
        editor.commit();

        Calendar dueDate = Calendar.getInstance();

        // Set Execution around 05:00:00 AM
        dueDate.set(Calendar.HOUR_OF_DAY, 22);
        dueDate.set(Calendar.MINUTE, 00);
        dueDate.set(Calendar.SECOND, 0);

        if (dueDate.before(Calendar.getInstance().getTime())) {
            dueDate.add(Calendar.HOUR_OF_DAY, 24);
        }

        long timeDiff = dueDate.getTimeInMillis() - Calendar.getInstance().getTimeInMillis();

        WorkRequest uploadWorkRequest =
                new OneTimeWorkRequest.Builder(PushPainRecordToFirebaseWorker.class).setInitialDelay(timeDiff, TimeUnit.MILLISECONDS)
                        .build();
        WorkManager
                .getInstance(getApplicationContext())
                .enqueue(uploadWorkRequest);
    }

    public Calendar getStartDate() {
        return startDate;
    }

    public Calendar getEndDate() {
        return endDate;
    }

}