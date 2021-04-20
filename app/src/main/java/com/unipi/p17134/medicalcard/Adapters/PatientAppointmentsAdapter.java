package com.unipi.p17134.medicalcard.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.toolbox.ImageRequest;
import com.unipi.p17134.medicalcard.ClickListener;
import com.unipi.p17134.medicalcard.Custom.TimeParsing;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class PatientAppointmentsAdapter extends RecyclerView.Adapter<PatientAppointmentsAdapter.MyViewHolder> {
    private ArrayList<Appointment> mDataset;
    private Context context;
    private ClickListener listener;

    // Constructor of Adapter class
    public PatientAppointmentsAdapter(Context context, ArrayList<Appointment> mDataset, ClickListener listener) {
        this.context = context;
        this.mDataset = mDataset;
        this.listener = listener;
    }

    // Static class used to hold the ids of each useful element in the view
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        TextView name, time, address, speciality;
        ImageButton moreInfo;
        private WeakReference<ClickListener> listenerRef;

        public MyViewHolder(View v, ClickListener listener) {
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

    // Create new views (invoked by the layout manager)
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.patient_appointment_row, parent, false);
        return new MyViewHolder(view, listener);
    }

    // Replace the contents of a view with the data from the dataset's current position (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        Appointment appointment = mDataset.get(position);

        holder.name.setText(appointment.getDoctor().getUser().getFullname());
        String time = TimeParsing.dateToShort(appointment.getStartDate());
        time += " - " + TimeParsing.dateToShort(appointment.getEndDate());
        holder.time.setText(time);
        holder.address.setText(appointment.getDoctor().getOfficeAddress());
        holder.speciality.setText(appointment.getDoctor().getSpeciality());
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

    public ArrayList<Appointment> getDataset() {
        return mDataset;
    }
}

