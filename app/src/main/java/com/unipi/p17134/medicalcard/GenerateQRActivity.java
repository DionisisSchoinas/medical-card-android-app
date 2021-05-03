package com.unipi.p17134.medicalcard;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.zxing.WriterException;
import com.unipi.p17134.medicalcard.API.QrDAO;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.QR;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRActivity extends AppCompatActivity {
    private Button newQr;
    private TextView expireCountdown;
    private ImageView qrDisplay;

    private DAOResponseListener responseListener;
    private CountDownTimer timer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_generate_qr);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newQr = findViewById(R.id.new_qr_code_button);
        expireCountdown = findViewById(R.id.expire_qr_countdown_display);
        qrDisplay = findViewById(R.id.qr_code_display);

        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                manageQR((QR) object);
            }

            @Override
            public <T> void onErrorResponse(T error) {

            }
        };

        getNewQr(null);
    }

    public void getNewQr(View view) {
        if (timer != null)
            timer.cancel();
        QrDAO.generate(this, responseListener);
    }

    private void manageQR(QR qr) {
        DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
        int screenWidthDp = (int) (displayMetrics.widthPixels / displayMetrics.density);

        // setting this dimensions inside our qr code
        // encoder to generate our qr code.
        QRGEncoder qrgEncoder = new QRGEncoder(qr.getToken(), null, QRGContents.Type.TEXT, screenWidthDp - 20);
        try {
            qrDisplay.setImageBitmap(qrgEncoder.encodeAsBitmap());
        }
        catch (WriterException e) {
            Log.e("QR writing error", e.toString());
        }

        expireCountdown.setText(millisToString(qr.getExpiresAfterSeconds() * 1000));
        expireCountdown.setBackgroundTintList(AppCompatResources.getColorStateList(this, R.color.countdown_counting));

        if (timer != null)
            timer.cancel();
        // Create and start  a counter for the seconds given by the API
        timer = new CountDownTimer(qr.getExpiresAfterSeconds() * 1000, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                expireCountdown.setText(millisToString(millisUntilFinished));
            }

            @Override
            public void onFinish() {
                expireCountdown.setText(millisToString(0));
                expireCountdown.setBackgroundTintList(AppCompatResources.getColorStateList(getApplicationContext(), R.color.countdown_stopped));
            }
        }
        .start();
    }

    private String millisToString(long millis) {
        long minutes = (millis / 1000) / 60;
        long seconds = (millis / 1000) % 60;
        return String.format("%02d:%02d", minutes, seconds);
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

    @Override
    public void onBackPressed() {
        backButton();
    }

    private void backButton() {
        finish();
    }
}