package com.example.paindairy.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

@Entity
public class PainRecord {
    @PrimaryKey (autoGenerate = true)
    public int uid;

    @ColumnInfo(name = "email_id")
    @NonNull
    public String emailId;

    @ColumnInfo(name = "pain_intensity_level")
    @NonNull
    public int painIntensityLevel;

    @ColumnInfo(name = "pain_location")
    @NonNull
    public String painLocation;

    @ColumnInfo(name = "moode_level")
    @NonNull
    public String moodLevel;

    @ColumnInfo(name = "steps_per_day")
    @NonNull
    public int stepsPerDay;

    @ColumnInfo(name = "current_date")
    @NonNull
    public Date currentDate;

    public double temperature;

    public double humidity;

    public double pressure;

    @Ignore
    public PainRecord(@NonNull int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel) {
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
    }

    @Ignore
    public PainRecord(@NonNull String emailId, @NonNull int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel) {
        this.emailId = emailId;
        try {
//            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
//            this.currentDate = formatter.parse(formatter.format(new Date()));
            this.currentDate = new Date();
            this.painIntensityLevel = painIntensityLevel;
            this.painLocation = painLocation;
            this.moodLevel = moodLevel;
        }
        catch (Exception e)  {

        }
    }
    public PainRecord(@NonNull String emailId, @NonNull int painIntensityLevel,
                      @NonNull String painLocation, @NonNull String moodLevel, @NonNull int stepsPerDay) {
        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            this.currentDate = formatter.parse(formatter.format(new Date()));
            this.emailId = emailId;
            this.painIntensityLevel = painIntensityLevel;
            this.painLocation = painLocation;
            this.moodLevel = moodLevel;
            this.stepsPerDay = stepsPerDay;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

}
