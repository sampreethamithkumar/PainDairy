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
public class
PainRecord {
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

    @ColumnInfo(name = "step_goal", defaultValue = "10000")
    @NonNull
    public int stepGoal;

    @ColumnInfo(name = "current_date")
    @NonNull
    public Date currentDate;

    @NonNull
    public double temperature;

    @NonNull
    public double humidity;

    @NonNull
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

    @Ignore
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

    @Ignore
    public PainRecord(@NonNull String emailId, int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel, int stepsPerDay, double temperature, double humidity, double pressure) {

        try {
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            this.currentDate = formatter.parse(formatter.format(new Date()));
            this.emailId = emailId;
            this.painIntensityLevel = painIntensityLevel;
            this.painLocation = painLocation;
            this.moodLevel = moodLevel;
            this.stepsPerDay = stepsPerDay;
            this.temperature = temperature;
            this.humidity = humidity;
            this.pressure = pressure;
        }
        catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public PainRecord(@NonNull String emailId, int painIntensityLevel, @NonNull String painLocation, @NonNull String moodLevel, int stepsPerDay, int stepGoal , @NonNull Date currentDate, @NonNull  double temperature, @NonNull double humidity, @NonNull double pressure) {
        this.emailId = emailId;
        this.painIntensityLevel = painIntensityLevel;
        this.painLocation = painLocation;
        this.moodLevel = moodLevel;
        this.stepsPerDay = stepsPerDay;
        this.currentDate = currentDate;
        this.temperature = temperature;
        this.humidity = humidity;
        this.pressure = pressure;
        this.stepGoal = stepGoal;
    }



}
