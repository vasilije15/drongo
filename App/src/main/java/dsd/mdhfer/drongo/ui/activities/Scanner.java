package dsd.mdhfer.drongo.ui.activities;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.lifecycle.ViewModelProvider;

import com.google.zxing.Result;

import java.util.Date;
import java.util.concurrent.ExecutionException;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;
import me.dm7.barcodescanner.zxing.ZXingScannerView;

public class Scanner extends AppCompatActivity implements ZXingScannerView.ResultHandler {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            Toasty.error(this, "Please provide Camera permission in app settings!", Toast.LENGTH_SHORT).show();
        }

        setContentView(R.layout.activity_scanner);

        Button showQr = findViewById(R.id.show_qr);
        showQr.setOnClickListener(v -> {

            Intent intent = new Intent(getApplicationContext(), UserQR.class);
            startActivity(intent);

        });

        ImageView backButton = findViewById(R.id.back_button_scanner);
        backButton.setOnClickListener(view -> finish());

    }

    @Override
    public void onResume() {
        super.onResume();
        // scannerView.setResultHandler(this);
        // scannerView.startCamera();

    }


    @Override
    public void onPause() {
        super.onPause();
        // scannerView.stopCamera();
    }

    @Override
    public void handleResult(Result result) {

    }


}

