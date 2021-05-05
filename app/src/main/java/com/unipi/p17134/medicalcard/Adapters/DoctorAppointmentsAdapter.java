package com.unipi.p17134.medicalcard.Adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DoctorAppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecyclerViewItem> mDataset;
    private ClickListener endOfList;

    // Constructor of Adapter class
    public DoctorAppointmentsAdapter(ArrayList<RecyclerViewItem> mDataset, ClickListener endOfList) {
        this.mDataset = mDataset;
        this.endOfList = endOfList;
    }

    // Static class used to hold the ids of each useful element in the view
    public static class DateSplitterViewHolder extends RecyclerView.ViewHolder {
        TextView dateString;

        public DateSplitterViewHolder(View v) {
            super(v);
            dateString = v.findViewById(R.id.date_splitter_date);
        }
    }

    // Static class used to hold the ids of each useful element in the view
    public static class DoctorViewHolder extends RecyclerView.ViewHolder {
        TextView name, time;

        public DoctorViewHolder(View v) {
            super(v);
            name = v.findViewById(R.id.patient_row_patientName);
            time = v.findViewById(R.id.patient_row_time);
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
        if (item.getItemType() == RecyclerViewItem.DATE_SPLITTER)
            return 0;
        else if (item.getItemType() == RecyclerViewItem.DOCTOR_APPOINTMENT)
            return 1;
        else
            return 2;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_row_splitter, parent, false);
            return new DateSplitterViewHolder(view);
        }
        else if (viewType == 1) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.doctor_appointment_row, parent, false);
            return new DoctorViewHolder(view);
        }
        else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.end_of_list_row, parent, false);
            return new EndViewHolder(view, endOfList);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecyclerViewItem item = mDataset.get(position);

        switch (item.getItemType()) {
            // Set date splitter's date
            case RecyclerViewItem.DATE_SPLITTER:
                DateSplitterViewHolder dateHolder = (DateSplitterViewHolder)holder;
                dateHolder.dateString.setText(mDataset.get(position).getDateString());
                break;
            // Set appointment's data
            case RecyclerViewItem.DOCTOR_APPOINTMENT:
                DoctorViewHolder patientHolder = (DoctorViewHolder)holder;
                Appointment appointment = mDataset.get(position).getAppointmentData();

                patientHolder.name.setText(appointment.getPatient().getUser().getFullname());
                String time = DateTimeParsing.dateToTimeString(appointment.getStartDate());
                time += "-" + DateTimeParsing.dateToTimeString(appointment.getEndDate());
                patientHolder.time.setText(time);
                break;
        }
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return mDataset.size();
    }

    // Clear the dataset and notify the adapter about the change
    public void clear() {
        int size = mDataset.size();
        mDataset.clear();
        notifyItemRangeRemoved(0, size);
    }

    public ArrayList<RecyclerViewItem> getDataset() {
        return mDataset;
    }
}