package com.example.paindairy.worker;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkRequest;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindairy.Dashboard;

public class UploadWorker extends Worker {

    public UploadWorker(
            @NonNull Context context,
            @NonNull WorkerParameters params) {
        super(context, params);
    }

    @NonNull
    @Override
    public Result doWork() {

        Log.i("INFO", "Called the do worker method");

        return null;
    }



}
