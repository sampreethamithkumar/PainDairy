package com.example.paindairy.worker;

import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindairy.Dashboard;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.Date;
import java.util.Map;

public class PushPainRecordToFirebaseWorker extends Worker {

//    SharedPreferences sharedPreferences;

    public PushPainRecordToFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
//        sharedPreferences = getApplicationContext().getSharedPreferences("sharedPreference", Context.MODE_PRIVATE);
    }

    @NonNull
    @Override
    public Result doWork() {
        storeDataToFirebase();

        return null;
    }

    private void storeDataToFirebase() {


        if (FirebaseAuth.getInstance().getCurrentUser() == null)
            return;

        Gson gson = new Gson();
        String json = getApplicationContext().getSharedPreferences("sharedPreferencesDashboard", Context.MODE_PRIVATE).getString("painRecord", "");
        PainRecord painRecord = gson.fromJson(json, PainRecord.class);

        FirebaseDatabase.getInstance().getReference("PainRecords")
                .child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                .child(String.valueOf(painRecord.uid))
                .setValue(painRecord).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Log.i("Info", "Inserted record to database at: " + new Date().toString());
                }
                else {
                   Log.i("Info", "Insertion failed");
                }
            }
        });

    }



//    private void displayNotification(String task, String desc) {

//    }
}
