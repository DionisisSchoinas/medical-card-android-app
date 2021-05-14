package com.unipi.p17134.medicalcard;

import android.Manifest;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.unipi.p17134.medicalcard.API.DoctorDAO;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.Popup.VerificationPopup;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Listeners.VerificationPopupListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class MyAccountActivity extends ConnectedBaseClass {
    ImageView image;
    EditText speciality, office, phone, email, cost;
    Bitmap profilePic;
    ImageButton loadImage, takeImage;
    Button updateAccount;

    private Doctor doctor;
    private Doctor newDoctor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_account);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        newDoctor = new Doctor();

        image = findViewById(R.id.imageDoctorUpdateInput);
        speciality = findViewById(R.id.specialityDoctorUpdateInput);
        office = findViewById(R.id.officeDoctorUpdateInput);
        phone = findViewById(R.id.phoneDoctorUpdateInput);
        email = findViewById(R.id.emailDoctorUpdateInput);
        cost = findViewById(R.id.costDoctorUpdateInput);

        speciality.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (speciality.getText().toString().equals(doctor.getSpeciality())) {
                    newDoctor.setSpeciality(null);
                    updateAccount.setEnabled(false);
                }
                else {
                    newDoctor.setSpeciality(speciality.getText().toString());
                    updateAccount.setEnabled(true);
                }
            }
        });
        office.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (office.getText().toString().equals(doctor.getOfficeAddress())) {
                    newDoctor.setOfficeAddress(null);
                    updateAccount.setEnabled(false);
                }
                else {
                    newDoctor.setOfficeAddress(office.getText().toString());
                    updateAccount.setEnabled(true);
                }
            }
        });
        phone.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (phone.getText().toString().equals(doctor.getPhone())) {
                    newDoctor.setPhone(null);
                    updateAccount.setEnabled(false);
                }
                else {
                    newDoctor.setPhone(phone.getText().toString());
                    updateAccount.setEnabled(true);
                }
            }
        });
        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                if (email.getText().toString().equals(doctor.getEmail())) {
                    newDoctor.setEmail(null);
                    updateAccount.setEnabled(false);
                }
                else {
                    newDoctor.setEmail(email.getText().toString());
                    updateAccount.setEnabled(true);
                }
            }
        });
        cost.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    if (Float.parseFloat(cost.getText().toString()) == doctor.getCost()) {
                        newDoctor.setCost(0);
                        updateAccount.setEnabled(false);
                    }
                    else {
                        newDoctor.setCost(Float.parseFloat(cost.getText().toString()));
                        updateAccount.setEnabled(true);
                    }
                }
                catch (Exception e){
                }
            }
        });

        loadImage = findViewById(R.id.loadImage);
        takeImage = findViewById(R.id.takePicture);

        updateAccount = findViewById(R.id.update_account_button);
        updateAccount.setEnabled(false);

        profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_profile);
        image.setImageBitmap(profilePic);

        disableAll();

        DAOResponseListener responseListener = new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                loadingDialog.dismissLoadingDialog();
                populateData((Doctor)object);
            }

            @Override
            public <T> void onErrorResponse(T error) {
                loadingDialog.dismissLoadingDialog();
                if (errorResponse(error))
                    return;

                Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
            }
        };
        loadingDialog.startLoadingDialog();
        DoctorDAO.doctor(this, responseListener);

        this.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
    }

    private void disableAll() {
        speciality.setEnabled(false);
        office.setEnabled(false);
        phone.setEnabled(false);
        email.setEnabled(false);
        cost.setEnabled(false);
        loadImage.setEnabled(false);
        takeImage.setEnabled(false);
    }

    private void enableAll() {
        speciality.setEnabled(true);
        office.setEnabled(true);
        phone.setEnabled(true);
        email.setEnabled(true);
        cost.setEnabled(true);
        loadImage.setEnabled(true);
        takeImage.setEnabled(true);
    }

    private void populateData(Doctor doctor) {
        this.doctor = doctor;

        speciality.setText(doctor.getSpeciality());
        office.setText(doctor.getOfficeAddress());
        phone.setText(doctor.getPhone());
        email.setText(doctor.getEmail());
        cost.setText(doctor.getCost()+"");
        if (doctor.getImage() != null)
            image.setImageBitmap(doctor.getImage());

        enableAll();
    }

    public void pickImage(View view) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, MyPermissions.RESULT_LOAD_IMG);
    }

    public void takePicture(View view) {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M ) {
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                    ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, MyPermissions.ACCESS_CAMERA_REQUEST);
                    return;
                }
            }
            startActivityForResult(takePictureIntent, MyPermissions.RESULT_TAKE_PHOTO);
        }
        catch (ActivityNotFoundException e) {
            Toast.makeText(this, getResources().getString(R.string.unable_to_open_camera), Toast.LENGTH_LONG).show();
        }
        catch (Exception e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_LONG).show();
        }
    }

    @Override
    protected void onActivityResult(int reqCode, int resultCode, Intent data) {
        super.onActivityResult(reqCode, resultCode, data);
        switch (reqCode) {
            case MyPermissions.RESULT_LOAD_IMG:
                if (resultCode == RESULT_OK) {
                    try {
                        final Uri imageUri = data.getData();
                        final InputStream imageStream = getContentResolver().openInputStream(imageUri);
                        profilePic = BitmapFactory.decodeStream(imageStream);
                        image.setImageBitmap(profilePic);

                        if (profilePic == doctor.getImage())
                            newDoctor.setImage(null);
                        else
                            newDoctor.setImage(profilePic);
                        updateAccount.setEnabled(profilePic != doctor.getImage());
                    } catch (FileNotFoundException e) {
                        Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.unable_to_load_image), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.image_not_picked),Toast.LENGTH_LONG).show();
                }
                break;
            case MyPermissions.RESULT_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    profilePic = (Bitmap) extras.get("data");
                    image.setImageBitmap(profilePic);

                    if (profilePic == doctor.getImage())
                        newDoctor.setImage(null);
                    else
                        newDoctor.setImage(profilePic);
                    updateAccount.setEnabled(profilePic != doctor.getImage());
                }
                else {
                    Toast.makeText(MyAccountActivity.this, getResources().getString(R.string.picture_not_taken),Toast.LENGTH_LONG).show();
                }
                break;
            case MyPermissions.ACCESS_CAMERA_REQUEST:
                if (resultCode == RESULT_OK) {
                    Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                    startActivityForResult(takePictureIntent, MyPermissions.RESULT_TAKE_PHOTO);
                }
                break;
        }
    }

    public void updateDoctor(View view) {
        try {
            Float.parseFloat(cost.getText().toString());
        }
        catch (Exception e) {
            Toast.makeText(this, R.string.register_form_error_need_cost, Toast.LENGTH_SHORT).show();
            return;
        }

        Activity activity = this;

        String message = getResources().getString(R.string.doctor_update_popup_message) + "\n\n" + getChangedData();
        VerificationPopup.showPopup(
                activity,
                getResources().getString(R.string.doctor_update_popup_title),
                message,
                getResources().getString(R.string.popup_positive_confirm),
                getResources().getString(R.string.popup_negative_cancel),
                new VerificationPopupListener() {
                    @Override
                    public void onPositive() {
                        loadingDialog.startLoadingDialog();
                        DoctorDAO.updateDoctor(
                                activity,
                                newDoctor,
                                new DAOResponseListener() {
                                    @Override
                                    public <T> void onResponse(T object) {
                                        loadingDialog.dismissLoadingDialog();
                                        Toast.makeText(activity, R.string.update_doctor_info_success, Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public <T> void onErrorResponse(T error) {
                                        loadingDialog.dismissLoadingDialog();
                                        populateData(doctor);

                                        if (errorResponse(error))
                                            return;

                                        if (errorMessage(error))
                                            return;

                                        Toast.makeText(getApplicationContext(), R.string.problem_with_request, Toast.LENGTH_SHORT).show();
                                    }
                                }
                        );
                    }

                    @Override
                    public void onNegative() {
                        populateData(doctor);
                    }
                }
        );
    }

    private String getChangedData() {
        String data = "";

        if (newDoctor.getSpeciality() != null)
            data += doctor.getSpeciality() + " -> " + newDoctor.getSpeciality() + "\n";
        if (newDoctor.getOfficeAddress() != null)
            data += doctor.getOfficeAddress() + " -> " + newDoctor.getOfficeAddress() + "\n";
        if (newDoctor.getEmail() != null)
            data += doctor.getEmail() + " -> " + newDoctor.getEmail() + "\n";
        if (newDoctor.getPhone() != null)
            data += doctor.getPhone() + " -> " + newDoctor.getPhone() + "\n";
        if (newDoctor.getCost() != 0)
            data += doctor.getCost() + " -> " + newDoctor.getCost() + "\n";
        if (newDoctor.getImage() != null)
            data += getResources().getString(R.string.update_doctor_new_image) + "\n";

        return data;
    }
}