package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.view.Menu;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.VolleyError;
import com.unipi.p17134.medicalcard.Custom.LoadingDialog;
import com.unipi.p17134.medicalcard.Custom.LoadingDialogEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.json.JSONObject;

public class BaseClass extends AppCompatActivity {
    protected LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        loadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    protected void onPause() {
        super.onPause();
        loadingDialog.dismissLoadingDialog();
    }

    @Override
    protected void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onMessageEvent(LoadingDialogEvent loading) {
        if (loading.isLoading())
            loadingDialog.startLoadingDialog();
        else
            loadingDialog.dismissLoadingDialog();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        backButton();
        return true;
    }
/*
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            Toast.makeText(this, "Settings", Toast.LENGTH_SHORT).show();
            return true;
        }
        else
            return super.onOptionsItemSelected(item);
    }
 */
    @Override
    public void onBackPressed() {
        backButton();
    }

    protected void backButton() {
        finish();
    }

    protected <T> boolean errorMessage(T error) {
        try {
            VolleyError volleyError = (VolleyError) error;
            String responseBody = new String(volleyError.networkResponse.data, "utf-8");
            JSONObject data = new JSONObject(responseBody);
            String message = data.getString("message");
            Toast.makeText(this, message, Toast.LENGTH_LONG).show();
            return true;
        }
        catch (Exception e) {
            return false;
        }
    }
}