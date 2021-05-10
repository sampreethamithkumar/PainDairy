package com.example.paindairy.worker;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Build;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.NotificationCompat;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelStore;
import androidx.work.Worker;
import androidx.work.WorkerParameters;

import com.example.paindairy.Dashboard;
import com.example.paindairy.R;
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

/**
 * Woker class
 */
public class PushPainRecordToFirebaseWorker extends Worker {

    public PushPainRecordToFirebaseWorker(@NonNull Context context, @NonNull WorkerParameters workerParams) {
        super(context, workerParams);
    }

    @NonNull
    @Override
    public Result doWork() {
        storeDataToFirebase();

        return Result.success();
    }

    /**
     * Send data to server
     */
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
                    displayNotification("Inserted", new Date().toString() );
                }
                else {
                    displayNotification("Data Insert Failed: ", new Date().toString() );
                   Log.i("Info", "Insertion failed");
                }
            }
        });

    }


    /**
     * Send Notification once data is inserted
     * @param task
     * @param desc
     */
    private void displayNotification(String task, String desc) {
        NotificationManager manger = (NotificationManager) getApplicationContext().getSystemService(Context.NOTIFICATION_SERVICE);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel("painDairy", "PainDairy",NotificationManager.IMPORTANCE_DEFAULT );

            manger.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "painDairy")
                .setContentTitle(task)
                .setContentText(desc)
                .setSmallIcon(R.drawable.ic_health);

        manger.notify(1, builder.build());

    }
}
