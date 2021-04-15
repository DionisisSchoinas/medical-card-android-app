package com.unipi.p17134.medicalcard.custom;

import android.content.Context;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

public class MyRequestHandler {
    private static MyRequestHandler instance;
    private RequestQueue requestQueue;

    private MyRequestHandler(Context ctx) {
        requestQueue = getRequestQueue(ctx);
    }

    public static synchronized MyRequestHandler getInstance(Context context) {
        if (instance == null) {
            instance = new MyRequestHandler(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue(Context ctx) {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Context ctx, Request<T> req) {
        getRequestQueue(ctx).add(req);
    }
}
