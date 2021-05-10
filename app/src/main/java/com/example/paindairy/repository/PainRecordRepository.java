package com.example.paindairy.repository;

import android.app.Application;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.lifecycle.LiveData;

import com.example.paindairy.dao.PainRecordDAO;
import com.example.paindairy.database.PainRecordDatabase;
import com.example.paindairy.entity.PainRecord;

import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

public class PainRecordRepository {
    private PainRecordDAO painRecordDao;
    private LiveData<List<PainRecord>> allPainRecords;

    public PainRecordRepository(Application application) {
        PainRecordDatabase db = PainRecordDatabase.getInstance(application);
        painRecordDao = db.painRecordDAO();
        allPainRecords = painRecordDao.getAll();
    }

    public LiveData<List<PainRecord>> getAllPainRecords() {
        return allPainRecords;
    }

    public void insert(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.insert(painRecord);
            }
        });
    }

    public void deleteAll() {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.deleteAll();
            }
        });
    }

    public void delete(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.delete(painRecord);
            }
        });
    }

    public void updatePainRecord(final PainRecord painRecord) {
        PainRecordDatabase.databaseWriteExecutor.execute(new Runnable() {
            @Override
            public void run() {
                painRecordDao.updatePainRecord(painRecord);
            }
        });
    }

    @RequiresApi(api = Build.VERSION_CODES.N)
    public CompletableFuture<PainRecord> findByIDFuture(final int painRecordId) {
        return CompletableFuture.supplyAsync(new Supplier<PainRecord>() {
            @Override
            public PainRecord get() {
                return painRecordDao.findByID(painRecordId);
            }
        }, PainRecordDatabase.databaseWriteExecutor);
    }

    public LiveData<PainRecord> getByDate(Date date) {
        return painRecordDao.getByDate(date);
    }

    public LiveData<Integer> getLastId(String emailid) {
        return painRecordDao.getLastId(emailid);
    }

    public LiveData<PainRecord> getLastUpdatedDate(String emailid) {
        return painRecordDao.getLastUpdatedDate(emailid);
    }
}

