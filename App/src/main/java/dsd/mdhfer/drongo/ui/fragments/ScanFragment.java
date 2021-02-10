package dsd.mdhfer.drongo.ui.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.Result;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import me.dm7.barcodescanner.zxing.ZXingScannerView;


public class ScanFragment extends Fragment   implements ZXingScannerView.ResultHandler {


    private ContactViewModel contactViewModel;


    private ZXingScannerView scannerView;

    public ScanFragment() {
        // Required empty public constructor
    }




    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);

    }

    @Override
    public void onResume() {
        super.onResume();
        scannerView.setResultHandler(this);
        List<BarcodeFormat> listOfFormats = new ArrayList<>();
        listOfFormats.add(BarcodeFormat.QR_CODE);
        scannerView.setFormats(listOfFormats);
        scannerView.setAutoFocus(true);
        scannerView.startCamera();
    }


    @Override
    public void onPause() {
        super.onPause();
        scannerView.stopCamera();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        scannerView = new ZXingScannerView(getActivity());

        // request camera permission
        if (ContextCompat.checkSelfPermission(getContext(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_DENIED) {
            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.CAMERA}
                    , 1);
        }

        return scannerView;
    }


    @Override
    public void handleResult(Result rawResult) {

        if (checkQRCode(rawResult.toString())) {


            Intent intent = new Intent();
            Contact contactToAdd = contactDetailsToAdd(rawResult.toString());
            Contact alreadyExists = null;

            try {
                alreadyExists = contactViewModel.checkIfContactExists(contactToAdd.contactUuid);
            } catch (ExecutionException | InterruptedException e) {
                e.printStackTrace();
            }


            if (alreadyExists != null) {

                alreadyExists.setContactUsername(contactToAdd.contactUsername);
                alreadyExists.setIpAddress(contactToAdd.ipAddress);
                contactViewModel.update(alreadyExists);
                intent.putExtra("QR", "Exists");
            } else {
                contactViewModel.insert(contactToAdd);
                intent.putExtra("QR", contactToAdd.getContactUsername());
            }

            getActivity().setResult(69, intent);
            getActivity().finish();//finishing activity

        } else {
            Intent intent = new Intent();
            intent.putExtra("QR", "Invalid");
            getActivity().setResult(69, intent);
            getActivity().finish();
        }

    }


    public boolean checkQRCode(String result) {

        if (!result.contains("%")) {
            return false;
        }

        String[] results = result.split("%");


        if (results[2].equals("Drongo")) {
            return true;
        }
        return false;
    }

    public Contact contactDetailsToAdd(String textFromQrCode) {

        String[] contactDetails = textFromQrCode.split("%");

        Contact contact = new Contact();

        Date date = new Date();
        long timestamp = date.getTime();

        contact.setContactUsername(contactDetails[0]);
        contact.setContactUuid(contactDetails[1]);
        contact.setTimestampAddedAt(timestamp + "");
        contact.setKey(contactDetails[5]);
        contact.setGivenUsername("N/A");
        contact.setProfilePictureUri(contactDetails[3]);
        contact.setIpAddress(contactDetails[4]);

        return contact;
    }


}