package com.unipi.p17134.medicalcard.custom;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;

import java.io.ByteArrayOutputStream;

public class BitmapConversion {
    public static String bitmapToBase64(Bitmap image) {
        if (image == null)
            return "";

        try
        {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            image.compress(Bitmap.CompressFormat.JPEG, 100, baos); // bm is the bitmap object
            byte[] byteArrayImage = baos.toByteArray();

            return Base64.encodeToString(byteArrayImage, Base64.DEFAULT);
        }
        catch (Exception e)
        {
            return "";
        }
    }

    public static Bitmap base64ToBitmap(String base64String) {
        if (base64String.equals(""))
            return null;

        try
        {
            byte[] decodedString = Base64.decode(base64String, Base64.DEFAULT);
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        }
        catch (Exception e)
        {
            return null;
        }
    }
}
