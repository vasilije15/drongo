package dsd.mdhfer.drongo.ui.activities;

import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.NoSuchAlgorithmException;
import java.security.spec.InvalidKeySpecException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;

import static dsd.mdhfer.drongo.utility.Utility.hideSoftKeyboard;
import static dsd.mdhfer.drongo.utility.Utility.validatePassword;

public class LoginActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_login);

        TextView passwordTextView = findViewById(R.id.password_login);


        SharedPreferences sharedPreferences = getPreferences();

        String userHash = sharedPreferences.getString("hash", null);
        String username = sharedPreferences.getString("username", null);

        ContactViewModel contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

        ImageView nextButtonLogin = findViewById(R.id.next_button_login);
        nextButtonLogin.setOnClickListener(v -> {
            hideSoftKeyboard(v);

            try {
                String password = passwordTextView.getText().toString();

                if (password.length() == 0) {
                    Toasty.normal(getApplicationContext(), username + " please enter your password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (validatePassword(password, userHash)) {
                    Toasty.normal(getApplicationContext(), "Welcome back " + username, Toast.LENGTH_LONG).show();
                    sharedPreferences.edit().putBoolean("logged_in", true).apply();
                    startActivity(new Intent(this, HomeActivity.class));
                    finish();

                } else {
                    Toasty.error(getApplicationContext(), "Incorrect password", Toast.LENGTH_SHORT).show();
                }
            } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                e.printStackTrace();
            }
        });

        TextView forgottenPasswordTextView = findViewById(R.id.forgotten_password);
        forgottenPasswordTextView.setOnClickListener(v -> {


            new SweetAlertDialog(LoginActivity.this, SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete account")
                    .setContentText("Be careful! This action can't be undone!")
                    .setConfirmText("I will stay!")
                    .setCancelText("Yes, delete it! ")
                    .setCancelClickListener(sweetAlertDialog -> {
                        sharedPreferences.edit().clear().apply();
                        contactViewModel.deleteAll();
                        Toasty.success(this, "Goodbye!", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(this, RegisterActivity.class));
                        finishAffinity();
                    })
                    .setConfirmClickListener(Dialog::dismiss)
                    .show();

        });

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
