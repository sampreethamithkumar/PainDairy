package com.example.paindairy.entity;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.Ignore;
import androidx.room.PrimaryKey;

import java.util.Date;

@Entity
public class User {
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
    public int stepsPerDay;

    @ColumnInfo(name = "current_date")
    @NonNull
    public Date currentDate;

    public double temperature;

    public double humidity;

    public double pressure;

    @Ignore
    public User( @NonNull int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel) {
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
    }

    public User(@NonNull String emailId, @NonNull int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel) {
        this.emailId = emailId;
        this.currentDate = new Date();
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
    }
}
