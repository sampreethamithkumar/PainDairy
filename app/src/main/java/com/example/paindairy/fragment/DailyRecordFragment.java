package com.example.paindairy.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.paindairy.adapter.RecyclerViewAdapter;
import com.example.paindairy.databinding.DailyRecordFragmentBinding;
import com.example.paindairy.entity.PainRecord;
import com.example.paindairy.viewmodel.PainRecordViewModel;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;
import java.util.List;

public class DailyRecordFragment extends Fragment implements Observer<List<PainRecord>> {
    private DailyRecordFragmentBinding dailyRecordFragmentBinding;

    private FirebaseUser firebaseUser;

    private PainRecordViewModel painRecordViewModel;

    private RecyclerViewAdapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;

    public DailyRecordFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        dailyRecordFragmentBinding = DailyRecordFragmentBinding.inflate(inflater,container, false);
        View view = dailyRecordFragmentBinding.getRoot();

        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        painRecordViewModel = new ViewModelProvider(this).get(PainRecordViewModel.class);

        painRecordViewModel.getAllPainRecords().observe(getViewLifecycleOwner(), this);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();;
        dailyRecordFragmentBinding = null;
    }

    @Override
    public void onChanged(List<PainRecord> painRecords) {

        List<PainRecord> currentUserPainRecord = new ArrayList<>();
        for(PainRecord painRecord : painRecords)
            if (painRecord.emailId.equals(firebaseUser.getEmail()))
                currentUserPainRecord.add(painRecord);

        if (currentUserPainRecord.size() == 0)
            dailyRecordFragmentBinding.noData.setVisibility(View.VISIBLE);
        else {
            dailyRecordFragmentBinding.recyclerView.setVisibility(View.VISIBLE);
            recyclerViewAdapter = new RecyclerViewAdapter(currentUserPainRecord);
            dailyRecordFragmentBinding.recyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), LinearLayoutManager.VERTICAL));
            dailyRecordFragmentBinding.recyclerView.setAdapter(recyclerViewAdapter);

            layoutManager = new LinearLayoutManager(getActivity());
            dailyRecordFragmentBinding.recyclerView.setLayoutManager(layoutManager);
        }

    }
}
