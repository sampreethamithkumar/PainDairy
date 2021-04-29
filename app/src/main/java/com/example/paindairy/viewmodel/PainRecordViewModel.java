package com.example.paindairy.viewmodel;

import android.app.Application;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;

import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.repository.PainRecordRepository;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;

public class PainRecordViewModel extends AndroidViewModel {
    private PainRecordRepository painRecordRepository;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordViewModel(@NonNull Application application) {
        super(application);
        painRecordRepository = new PainRecordRepository(application);
        allPainRecords = painRecordRepository.getAllPainRecords();
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int painRecordId) {
        return painRecordRepository.findByIDFuture(painRecordId);
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public void insert(PainRecord painRecord) {
        painRecordRepository.insert(painRecord);
    }

    public void deleteAll() {
        painRecordRepository.deleteAll();
    }

    public void update(PainRecord painRecord) {
        painRecordRepository.updatePainRecord(painRecord);
    }

    public LiveData<PainRecord> getByDate(Date date) {
        return painRecordRepository.getByDate(date);
    }

    public LiveData<Integer> getLastId() {
        return painRecordRepository.getLastId();
    }
}
