package com.unipi.p17134.medicalcard.Adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.RecyclerViewItem;
import com.unipi.p17134.medicalcard.Listeners.ClickListener;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class DoctorScheduleAppointmentsAdapter extends RecyclerView.Adapter<DoctorScheduleAppointmentsAdapter.TimeViewHolder> {
    private Context context;
    private ArrayList<RecyclerViewItem> mDataset;
    private ClickListener listener;

    // Constructor of Adapter class
    public DoctorScheduleAppointmentsAdapter(Context context, ArrayList<RecyclerViewItem> mDataset, ClickListener listener) {
        this.context = context;
        this.mDataset = mDataset;
        this.listener = listener;
    }

    // Static class used to hold the ids of each useful element in the view
    public static class TimeViewHolder extends RecyclerView.ViewHolder {
        CardView cardView;
        TextView time;
        ConstraintLayout constraintLayout;
        private WeakReference<ClickListener> listenerRef;

        public TimeViewHolder(View v, ClickListener listener) {
            super(v);
            cardView = v.findViewById(R.id.appointment_time_view);
            time = v.findViewById(R.id.time_display);
            constraintLayout = v.findViewById(R.id.appointment_time_constraint);

            listenerRef = new WeakReference<>(listener);
            // OnClickListeners to trigger the Listener given in the constructor
            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(getAdapterPosition());
                }
            });
            time.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(getAdapterPosition());
                }
            });
            constraintLayout.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    listenerRef.get().onClick(getAdapterPosition());
                }
            });
        }
    }

    // Create new views (invoked by the layout manager)
    @Override
    public TimeViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.appointment_time_card, parent, false);
        return new TimeViewHolder(view, listener);
    }

    @Override
    public void onBindViewHolder(@NonNull TimeViewHolder holder, int position) {
        Appointment appointment = mDataset.get(position).getAppointmentData();

        String time = DateTimeParsing.dateToTimeString(appointment.getStartDate()) + " - " + DateTimeParsing.dateToTimeString(appointment.getEndDate());
        holder.time.setText(time);

        if (mDataset.get(position).isBooked()) {
            holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.red_transparent));
            holder.time.setTextColor(context.getResources().getColor(R.color.white));
        }
        else {
            holder.constraintLayout.setBackgroundColor(context.getResources().getColor(R.color.white));
            holder.time.setTextColor(context.getResources().getColor(R.color.black));
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



