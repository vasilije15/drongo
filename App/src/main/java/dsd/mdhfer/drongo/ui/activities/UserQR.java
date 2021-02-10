package dsd.mdhfer.drongo.ui.activities;

import androidx.appcompat.app.AppCompatActivity;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.Toast;


import java.io.IOException;
import java.security.GeneralSecurityException;

import dsd.mdhfer.drongo.R;
import es.dmoral.toasty.Toasty;


import static dsd.mdhfer.drongo.utility.Utility.disableScreenCapture;
import static dsd.mdhfer.drongo.utility.Utility.generateQrCode;
import static dsd.mdhfer.drongo.utility.Utility.getLocalIPAddress;

public class UserQR extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        /* Disable screenshot functionality */
        if (!disableScreenCapture(getWindow())) {
            Log.e("Disabled screen capture", "No screenshots");
        }

        if (!checkWifiConnected()){
            Toasty.warning(this, "You are not connected to a WiFi AP!", Toast.LENGTH_LONG).show();
            finish();
        }


        setContentView(R.layout.qr_user);

        String username = getPreferences().getString("username", null);
        String uid = getPreferences().getString("uid", null);
        String avatar = getPreferences().getString("avatar", null);
        String currentIpAddress = getLocalIPAddress(true);

        String publicKey = getPreferences().getString("public_key", null);

        String textToEncode = username + "%" + uid + "%Drongo%" + avatar + "%" + currentIpAddress + "%" + publicKey;
        ImageView userQRCodeImageView = findViewById(R.id.user_qr_code_image_view);

        // picture that is embedded in QR code
        Bitmap drongoLogoImage = BitmapFactory.decodeResource(getResources(), R.drawable.logic_logo_512);

        generateQrCode(drongoLogoImage, userQRCodeImageView, textToEncode);


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

    private boolean checkWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return false;
        }
            Network network = connMgr.getActiveNetwork();
            if (network == null) return false;
            NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(network);
            return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }


}