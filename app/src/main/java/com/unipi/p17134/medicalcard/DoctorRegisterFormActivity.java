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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import com.unipi.p17134.medicalcard.API.UserDAO;
import com.unipi.p17134.medicalcard.Custom.MyPermissions;
import com.unipi.p17134.medicalcard.Custom.MyPrefs;
import com.unipi.p17134.medicalcard.Listeners.DAOResponseListener;
import com.unipi.p17134.medicalcard.Singletons.Doctor;
import com.unipi.p17134.medicalcard.Singletons.User;

import java.io.FileNotFoundException;
import java.io.InputStream;

public class DoctorRegisterFormActivity extends BaseClass {
    ImageView image;
    EditText speciality, office, phone, email, cost;
    Bitmap profilePic;
    TextView connectedAs;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_doctor_register_form);

        Toolbar myToolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(myToolbar);

        image = findViewById(R.id.imageDoctorRegisterInput);
        speciality = findViewById(R.id.specialityDoctorRegisterInput);
        office = findViewById(R.id.officeDoctorRegisterInput);
        phone = findViewById(R.id.phoneDoctorRegisterInput);
        email = findViewById(R.id.emailDoctorRegisterInput);
        cost = findViewById(R.id.costDoctorRegisterInput);

        profilePic = BitmapFactory.decodeResource(getApplicationContext().getResources(), R.drawable.default_profile);
        image.setImageBitmap(profilePic);

        connectedAs = findViewById(R.id.connectedAsDisplay);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (UserDAO.missingToken(this)) {
            Intent intent = new Intent(this, LoginActivity.class);
            intent.putExtra("fromRegister", true);
            startActivity(intent);
            finish();
        }

        if (MyPrefs.isDoctor(this)) {
            Toast.makeText(this, getResources().getString(R.string.already_a_doctor), Toast.LENGTH_LONG).show();
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }

        User user = MyPrefs.getUserData(this);
        connectedAs.setText(user.getFullname() + " - " + user.getDateOfBirth());
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
                    } catch (FileNotFoundException e) {
                        Toast.makeText(DoctorRegisterFormActivity.this, getResources().getString(R.string.unable_to_load_image), Toast.LENGTH_LONG).show();
                    }
                }
                else {
                    Toast.makeText(DoctorRegisterFormActivity.this, getResources().getString(R.string.image_not_picked),Toast.LENGTH_LONG).show();
                }
                break;
            case MyPermissions.RESULT_TAKE_PHOTO:
                if (resultCode == RESULT_OK) {
                    Bundle extras = data.getExtras();
                    profilePic = (Bitmap) extras.get("data");
                    image.setImageBitmap(profilePic);
                }
                else {
                    Toast.makeText(DoctorRegisterFormActivity.this, getResources().getString(R.string.picture_not_taken),Toast.LENGTH_LONG).show();
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

    public void registerDoctor(View view) {
        try {
            Float.parseFloat(cost.getText().toString());
        }
        catch (Exception e) {
            Toast.makeText(this, getResources().getString(R.string.register_form_error_need_cost), Toast.LENGTH_SHORT).show();
            return;
        }
        Activity activity = this;
        UserDAO.registerDoctor(this, new Doctor(
                speciality.getText().toString(),
                office.getText().toString(),
                phone.getText().toString(),
                email.getText().toString(),
                Float.parseFloat(cost.getText().toString()),
                profilePic
        ), new DAOResponseListener() {
            @Override
            public <T> void onResponse(T object) {
                MyPrefs.isDoctor(activity, true);
                //Toast.makeText(activity, response.getString("message"), Toast.LENGTH_SHORT).show();

                startActivity(new Intent(activity, MainActivity.class));
                finish();
            }

            @Override
            public <T> void onErrorResponse(T error) {

            }
        });
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
        MyPrefs.clearLogin(this);
        startActivity(new Intent(this, DoctorRegisterPickActivity.class));
        finish();
    }
}