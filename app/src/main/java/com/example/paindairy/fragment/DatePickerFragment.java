package com.example.paindairy.fragment;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.widget.DatePicker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.paindairy.forwarding.OnDateSetListener;

import java.util.Calendar;

//public class DatePickerFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {
//    private static final String ARGUMENT_TAG = "ARGUMENT_TAG";
//
//    public static DatePickerFragment newInstance(String tag) {
//        Bundle args = new Bundle();
//        args.putString(ARGUMENT_TAG, tag);
//        DatePickerFragment fragment = new DatePickerFragment();
//        fragment.setArguments(args);
//        return fragment;
//    }
//
////    @Override
////    public void show(@NonNull FragmentManager manager, @Nullable String tag) {
////
////        FragmentTransaction ft = manager.beginTransaction();
////        ft.add(this, tag);
////        ft.commit();
////    }
//
//    @NonNull
//    @Override
//    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
//        Calendar calendar = Calendar.getInstance();
//        int year = calendar.get(Calendar.YEAR);
//        int month = calendar.get(Calendar.MONTH);
//        int day = calendar.get(Calendar.DAY_OF_MONTH);
//        return new DatePickerDialog(requireActivity(), this, year, month, day);
//    }
//
//    @Override
//    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//        final String tag = getArguments().getString(ARGUMENT_TAG);
//        OnDateSetListener listener = (OnDateSetListener) requireActivity();
//        listener.onDateSet(tag, view, year, month, dayOfMonth);
//    }
//}

public class DatePickerFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        Calendar c = Calendar.getInstance();
        int year = c.get(Calendar.YEAR);
        int month = c.get(Calendar.MONTH);
        int day = c.get(Calendar.DAY_OF_MONTH);
        return new DatePickerDialog(getActivity(), (DatePickerDialog.OnDateSetListener) getActivity(), year, month, day);
    }
}
