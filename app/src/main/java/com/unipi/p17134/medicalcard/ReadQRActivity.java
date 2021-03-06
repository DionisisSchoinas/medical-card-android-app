package com.unipi.p17134.medicalcard;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.budiyev.android.codescanner.CodeScanner;
import com.budiyev.android.codescanner.CodeScannerView;
import com.budiyev.android.codescanner.DecodeCallback;
import com.google.zxing.Result;
import com.unipi.p17134.medicalcard.API.QrDAO;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.QR;
import com.unipi.p17134.medicalcard.Singletons.QrResponse;

public class ReadQRActivity extends ConnectedBaseClass {
    private CodeScannerView scannerView;
    private CodeScanner mCodeScanner;

    private DAOResponseListener responseListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read_qr);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getResources().getString(R.string.read_qr_activity));

        responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                parseData((QrResponse) object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };

        scannerView = findViewById(R.id.qr_scanner_view);
        mCodeScanner = new CodeScanner(this, scannerView);
        mCodeScanner.setDecodeCallback(new DecodeCallback() {
            @Override
            public void onDecoded(@NonNull final Result result) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        manageDecodedQR(result);
                    }
                });
            }
        });
        scannerView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startPreview();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        startPreview();
    }

    @Override
    protected void onPause() {
        mCodeScanner.releaseResources();
        super.onPause();
    }

    private void startPreview() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.INTERNET}, MyPermissions.ACCESS_CAMERA_REQUEST);
                return;
            }
        }

        mCodeScanner.startPreview();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == MyPermissions.ACCESS_CAMERA_REQUEST && resultCode == RESULT_OK) {
            startPreview();
        }
    }

    private void manageDecodedQR(Result result) {
        loadingDialog.startLoadingDialog();
        QR qr = new QR(result.getText());
        QrDAO.read(this, qr, responseListener);
    }

    private void parseData(QrResponse response) {
        Intent intent = new Intent(this, QRAppointmentInfoActivity.class);

        if (response.getCurrentAppointment() != null)
            intent.putExtra("current", response.getCurrentAppointment());

        if (response.getPreviousAppointment() != null)
            intent.putExtra("previous", response.getPreviousAppointment());

        if (response.getPatient() != null)
            intent.putExtra("patient", response.getPatient());

        startActivity(intent);
        finish();
    }
}