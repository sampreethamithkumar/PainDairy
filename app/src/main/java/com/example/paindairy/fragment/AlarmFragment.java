package com.example.paindairy.fragment;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.paindairy.R;
import com.example.paindairy.alarm.AlertReceiver;
import com.example.paindairy.alarm.TimePickerFragment;
import com.example.paindairy.databinding.FragmentAlarmBinding;

/**
 * Alarm setter Fragment
 */
public class AlarmFragment extends Fragment implements View.OnClickListener {

    private FragmentAlarmBinding fragmentAlarmBinding;

    public AlarmFragment() {

    }

    /**
     * Fragment onCreate life cycle
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        fragmentAlarmBinding = FragmentAlarmBinding.inflate(inflater, container, false);
        View view = fragmentAlarmBinding.getRoot();


        fragmentAlarmBinding.alarmCanceller.setOnClickListener(this);

        fragmentAlarmBinding.alarmSetter.setOnClickListener(this);

        return view;

    }

    /**
     * Button On click listener
     * @param v
     */
    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.alarmSetter) {
            alarmSetter();
        }
        if (v.getId() == R.id.alarmCanceller) {
            alarmCaneller();
        }
    }

    /**
     * Set's the alarm for specifed time.
     */
    private void alarmSetter() {
        DialogFragment timePicker = new TimePickerFragment();
        if (getFragmentManager() != null) {
            timePicker.show(getFragmentManager(), "time picker");
        }
    }

    /**
     * Option to cancel the alarm.
     */
    private void alarmCaneller() {
        AlarmManager alarmManager = (AlarmManager) getContext().getSystemService(Context.ALARM_SERVICE);
        Intent intent = new Intent(getActivity(), AlertReceiver.class);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(getContext(), 1, intent, 0);
        alarmManager.cancel(pendingIntent);
    }
}