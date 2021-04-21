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
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class PatientAppointmentsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private ArrayList<RecycleViewItem> mDataset;
    private Context context;
    private ClickListener listener;

    // Constructor of Adapter class
    public PatientAppointmentsAdapter(Context context, ArrayList<RecycleViewItem> mDataset, ClickListener listener) {
        this.context = context;
        this.mDataset = mDataset;
        this.listener = listener;
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
    public static class PatientViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, address, speciality;
        ImageButton moreInfo;
        private WeakReference<ClickListener> listenerRef;

        public PatientViewHolder(View v, ClickListener listener) {
            super(v);
            name = v.findViewById(R.id.patient_row_doctorName);
            time = v.findViewById(R.id.patient_row_time);
            address = v.findViewById(R.id.patient_row_adress);
            speciality = v.findViewById(R.id.patient_row_doctorSpeciality);
            moreInfo = v.findViewById(R.id.patient_row_moreInfo);

            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            moreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onMoreInfoClicked(getAdapterPosition());
                }
            });
        }
    }

    @Override
    public int getItemViewType(int position) {
        RecycleViewItem item = mDataset.get(position);
        if (item.getItemType() == RecycleViewItem.DATE_SPLITTER)
            return 0;
        else
            return 1;
    }


    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        if (viewType == 0) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.date_row_splitter, parent, false);
            return new DateSplitterViewHolder(view);
        }

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_row, parent, false);
        return new PatientViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        RecycleViewItem item = mDataset.get(position);

        switch (item.getItemType()) {
            // Set date splitter's date
            case RecycleViewItem.DATE_SPLITTER:
                DateSplitterViewHolder dateHolder = (DateSplitterViewHolder)holder;
                dateHolder.dateString.setText(mDataset.get(position).getDateString());
                break;
            // Set appointment's data
            case RecycleViewItem.PATIENT_APPOINTMENT:
                PatientViewHolder patientHolder = (PatientViewHolder)holder;
                Appointment appointment = mDataset.get(position).getAppointmentData();

                patientHolder.name.setText(appointment.getDoctor().getUser().getFullname());
                String time = DateTimeParsing.dateToTimeString(appointment.getStartDate());
                time += "-" + DateTimeParsing.dateToTimeString(appointment.getEndDate());
                patientHolder.time.setText(time);
                patientHolder.address.setText(appointment.getDoctor().getOfficeAddress());
                patientHolder.speciality.setText(appointment.getDoctor().getSpeciality());
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

    public ArrayList<RecycleViewItem> getDataset() {
        return mDataset;
    }
}

