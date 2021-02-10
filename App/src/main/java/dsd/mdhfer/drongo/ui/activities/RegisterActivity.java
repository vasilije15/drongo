package dsd.mdhfer.drongo.ui.activities;


import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;
import java.security.GeneralSecurityException;

import dsd.mdhfer.drongo.R;


public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        /* Disable screenshot functionality */
//        if (!disableScreenCapture(getWindow())) {
//            Toast.makeText(getApplicationContext(), "Unable to disable screen capture",
//                    Toast.LENGTH_LONG).show();
//        }


        SharedPreferences sharedPreferences = getPreferences();


        if (sharedPreferences.getBoolean("logged_in", false) && sharedPreferences.contains("username")) {
            // skips Register Activity if user is already registered
            startActivity(new Intent(this, HomeActivity.class));
            finish();
        } else if (!sharedPreferences.getBoolean("logged_in", false) && sharedPreferences.contains("username")) {
            // navigate to login
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        } else {
            // continue to register screen
            // this is here because it showed the register layout when skipping to home activity
            setContentView(R.layout.activity_register);
        }

    }

    public SharedPreferences getPreferences() {
        MasterKey mainKey;
        SharedPreferences sharedPreferences = null;
        try {
            mainKey = new MasterKey.Builder(this)
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    this,
                    "user_data",
                    mainKey,
                    EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
                    EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM

            );
        } catch (GeneralSecurityException | IOException e) {
            e.printStackTrace();
        }
        return sharedPreferences;
    }

}
