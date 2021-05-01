package com.example.paindairy.dao;

import androidx.lifecycle.LiveData;
import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import com.example.paindairy.entity.PainRecord;

import java.util.Date;
import java.util.List;

@Dao
public interface PainRecordDAO {

    @Query("SELECT * FROM PainRecord ORDER BY email_id ASC")
    LiveData<List<PainRecord>> getAll();

    @Query("SELECT * FROM PainRecord WHERE uid = :painId LIMIT 1")
    PainRecord findByID(int painId);

    @Insert
    void insert(PainRecord painRecord);

    @Delete
    void delete(PainRecord painRecord);

    @Update
    void updatePainRecord(PainRecord painRecord);

    @Query("DELETE FROM PainRecord")
    void deleteAll();

    @Query("SELECT * FROM PainRecord WHERE current_date = :date LIMIT 1")
    LiveData<PainRecord> getByDate(Date date);

    @Query("SELECT Max(uid) FROM PainRecord WHERE email_id = :emailid")
    LiveData<Integer> getLastId(String emailid);

    @Query("SELECT * FROM PainRecord WHERE uid = (SELECT Max(uid) FROM PainRecord WHERE email_id = :emailid)")
    LiveData<PainRecord> getLastUpdatedDate(String emailid);
}
