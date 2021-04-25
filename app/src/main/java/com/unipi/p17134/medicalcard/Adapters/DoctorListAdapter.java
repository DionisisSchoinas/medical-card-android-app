package com.unipi.p17134.medicalcard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<DoctorListAdapter.DoctorViewHolder> {
    private ArrayList<Doctor> mDataset;
    private ClickListener listener;

    // Constructor of Adapter class
    public DoctorListAdapter(ArrayList<Doctor> mDataset, ClickListener listener) {
        this.mDataset = mDataset;
        this.listener = listener;
    }

    // Static class used to hold the ids of each useful element in the view
    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView name, address, speciality, cost;
        ImageButton addAppointment;
        private WeakReference<ClickListener> listenerRef;

        public DoctorViewHolder(View v, ClickListener listener) {
            super(v);
            name = v.findViewById(R.id.doctor_row_name);
            address = v.findViewById(R.id.doctor_row_address);
            speciality = v.findViewById(R.id.doctor_row_speciality);
            cost = v.findViewById(R.id.doctor_row_cost);
            addAppointment = v.findViewById(R.id.doctor_row_add_appointment);

            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            addAppointment.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(getAdapterPosition());
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public DoctorViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row, parent, false);
        return new DoctorViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull DoctorViewHolder holder, int position) {
        Doctor doctor = mDataset.get(position);

        holder.name.setText(doctor.getUser().getFullname());
        holder.speciality.setText(doctor.getSpeciality());
        holder.address.setText(doctor.getOfficeAddress());
        holder.cost.setText(doctor.getCost() + " €");
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<Doctor> getDataset() {
        return mDataset;
    }
}


