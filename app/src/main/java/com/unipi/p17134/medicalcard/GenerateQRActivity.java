package com.unipi.p17134.medicalcard;

import android.os.Bundle;
import android.os.CountDownTimer;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.content.res.AppCompatResources;
import androidx.appcompat.widget.Toolbar;

import com.google.zxing.WriterException;
import com.unipi.p17134.medicalcard.API.QrDAO;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.QR;

import androidmads.library.qrgenearator.QRGContents;
import androidmads.library.qrgenearator.QRGEncoder;

public class GenerateQRActivity extends ConnectedBaseClass {
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
        getSupportActionBar().setTitle(getResources().getString(R.string.generate_qr_activity));

        newQr = findViewById(R.id.new_qr_code_button);
        expireCountdown = findViewById(R.id.expire_qr_countdown_display);
        qrDisplay = findViewById(R.id.qr_code_display);

        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                manageQR((QR) object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };

        getNewQr(null);
    }

    public void getNewQr(View view) {
        if (timer != null)
            timer.cancel();
        loadingDialog.startLoadingDialog();
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
}