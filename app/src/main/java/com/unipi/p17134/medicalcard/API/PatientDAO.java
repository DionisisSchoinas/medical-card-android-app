package com.unipi.p17134.medicalcard.API;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.unipi.p17134.medicalcard.Adapters.PatientAppointmentsAdapter;
import com.unipi.p17134.medicalcard.ClickListener;
import com.unipi.p17134.medicalcard.Custom.DateTimeParsing;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Custom.MyRequestHandler;
import com.unipi.p17134.medicalcard.Custom.RecycleViewItem;
import com.unipi.p17134.medicalcard.R;
import com.unipi.p17134.medicalcard.Singletons.Appointment;
import com.unipi.p17134.medicalcard.Singletons.Doctor;
import com.unipi.p17134.medicalcard.Singletons.User;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class PatientDAO extends BaseDAO {
    private static final SimpleDateFormat formatter = new SimpleDateFormat(APPOINTMENT_TIME_FORMAT);
    private static int totalPages = -1;
    private static int currentPage = 0;
    private static int queueItems = 0;

    public static void appointments(Activity activity, RecyclerView display, int page) {
        // Already loading appointments
        if (queueItems > 0)
            return;

        // If not the first request and the page is out of bounds return
        if (totalPages != -1 && page > totalPages)
            return;

        // If page not specifically given (go to next page)
        if (page == -1) {
            // If last read page and total pages are the same (we are at the last page)
            if (currentPage == totalPages)
                return;
            else
                page = currentPage + 1;
        }

        String appointmentsUrl = url + "/appointments?page="+page;
        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, appointmentsUrl, null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    JSONObject meta = response.getJSONObject("meta");
                    currentPage = meta.getInt("current_page");
                    totalPages = meta.getInt("total_pages");

                    // Get appointments array
                    JSONArray appointments = response.getJSONArray("appointments");

                    // Fill list with appointments
                    JSONObject object;

                    // If display has items on it
                    PatientAppointmentsAdapter oldAdapter = (PatientAppointmentsAdapter)display.getAdapter();
                    ArrayList<RecycleViewItem> items;
                    String lastDate;
                    if (oldAdapter != null) {
                        items = oldAdapter.getDataset();
                        lastDate = DateTimeParsing.dateToDateString(items.get(items.size()-1).getAppointmentData().getStartDate());
                    }
                    else {
                        items = new ArrayList<>();
                        lastDate = "";
                    }

                    for (int i=0; i<appointments.length(); i++) {
                        object = appointments.getJSONObject(i);
                        Appointment app = new Appointment(
                                object.getInt("id"),
                                new Doctor()
                                        .setId(object.getJSONObject("doctor").getInt("id"))
                                        .setOfficeAddress(object.getJSONObject("doctor").getString("office_address"))
                                        .setSpeciality(object.getJSONObject("doctor").getString("speciality"))
                                        .setUser(new User()
                                                .setFullname(object.getJSONObject("doctor").getJSONObject("user").getString("fullname"))
                                        )
                                ,
                                null,
                                formatter.parse(object.getString("appointment_date_time_start")),
                                formatter.parse(object.getString("appointment_date_time_end"))
                        );

                        // Check if date has changed
                        // If it has add a date splitter
                        String currentDate = DateTimeParsing.dateToDateString(app.getStartDate());
                        if (!currentDate.equals(lastDate)) {
                            RecycleViewItem item = new RecycleViewItem();
                            if (DateTimeParsing.currentDate().equals(currentDate)) {
                                item.setDateSplitterType("Today");
                            }
                            else {
                                item.setDateSplitterType(currentDate);
                            }
                            items.add(item);
                            lastDate = currentDate;
                        }

                        // Add appointment item
                        RecycleViewItem item = new RecycleViewItem();
                        item.setPatientAppointmentType(app);
                        items.add(item);
                    }

                    // Fill display with appointments
                    // Create adapter passing in the sample user data
                    PatientAppointmentsAdapter mAdapter = new PatientAppointmentsAdapter(activity, items, new ClickListener() {
                        @Override
                        public void onMoreInfoClicked(int index) {
                            Appointment appointment = items.get(index).getAppointmentData();
                            Toast.makeText(activity, "Clicked appointment with id = " + appointment.getId(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    // Attach the adapter to the recyclerview to populate items
                    display.setAdapter(mAdapter);
                }
                catch (JSONException | ParseException e) {
                    Toast.makeText(activity, activity.getResources().getString(R.string.fatal_error), Toast.LENGTH_LONG).show();
                    Log.e("Error on appointments", e.getMessage());
                }
                catch (Exception e) {
                    Log.e("Error not sure what", e.getMessage());
                }
                queueItems--;
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                queueItems--;
                errorResponse(activity, error);
            }
        })
        {    //this is the part, that adds the header to the request
            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("AuthorizationToken", MyPrefs.getToken(activity));
                params.put("content-type", "application/json");
                return params;
            }
        };
        MyRequestHandler.getInstance(activity).addToRequestQueue(activity, jsonObjectRequest);
        queueItems++;
    }
}
