package com.unipi.p17134.medicalcard.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DoctorListAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecyclerViewItem> mDataset;
    private ClickListener listener;
    private ClickListener endOfList;

    // Constructor of Adapter class
    public DoctorListAdapter(ArrayList<RecyclerViewItem> mDataset, ClickListener listener, ClickListener endOfList) {
        this.mDataset = mDataset;
        this.listener = listener;
        this.endOfList = endOfList;
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

    // Static class used to hold the ids of each useful element in the view
    public static class EndViewHolder extends RecyclerView.ViewHolder {
        ImageButton refresh;
        private WeakReference<ClickListener> listenerRef;

        public EndViewHolder(View v, ClickListener listener) {
            super(v);
            refresh = v.findViewById(R.id.more_items_button);

            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            refresh.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        RecyclerViewItem item = mDataset.get(position);
        if (item.getItemType() == RecyclerViewItem.DOCTOR)
            return 0;
        else
            return 1;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_row, parent, false);
            return new DoctorViewHolder(view, listener);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.end_of_list_row, parent, false);
            return new EndViewHolder(view, endOfList);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewItem item = mDataset.get(position);

        // Set date splitter's date
        if (item.getItemType() == RecyclerViewItem.DOCTOR) {
            DoctorViewHolder dateHolder = (DoctorViewHolder) holder;
            Doctor doctor = mDataset.get(position).getDoctorData();

            dateHolder.name.setText(doctor.getUser().getFullname());
            dateHolder.speciality.setText(doctor.getSpeciality());
            dateHolder.address.setText(doctor.getOfficeAddress());
            dateHolder.cost.setText(doctor.getCost() + " €");
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    public ArrayList<RecyclerViewItem> getDataset() {
        return mDataset;
    }
}


