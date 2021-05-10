package com.example.paindairy.adapter;


import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindairy.databinding.RvLayoutBinding;
import com.example.paindairy.entity.PainRecord;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.text.SimpleDateFormat;
import java.util.List;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.ViewHolder> {

    private List<PainRecord> painRecords;


    public RecyclerViewAdapter(List<PainRecord> painRecords) {
        this.painRecords = painRecords;
    }

    @NonNull
    @Override
    public RecyclerViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RvLayoutBinding binding =
                RvLayoutBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);


        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final PainRecord painRecord = painRecords.get(position);
        holder.binding.painIntensityLevelTextView.setText(Integer.toString(painRecord.painIntensityLevel));
        holder.binding.painLocationTextView.setText(painRecord.painLocation);
        holder.binding.moodLevelTextView.setText(painRecord.moodLevel);
        holder.binding.dateTextView.setText(new SimpleDateFormat("dd/MM/yyyy").format(painRecord.currentDate));
        holder.binding.stepsTakenTextView.setText(Integer.toString(painRecord.stepsPerDay));
        holder.binding.temperatureTextView.setText(Double.toString(painRecord.temperature));
        holder.binding.humidityTextView.setText(Double.toString(painRecord.humidity));
        holder.binding.pressureTextView.setText(Double.toString(painRecord.pressure));
    }


    @Override
    public int getItemCount() {
        return painRecords.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private RvLayoutBinding binding;

        public ViewHolder(RvLayoutBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
