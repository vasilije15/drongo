package dsd.mdhfer.drongo.ui.fragments;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.ui.activities.LoginActivity;
import dsd.mdhfer.drongo.ui.activities.RegisterActivity;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SettingsFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SettingsFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;

    private ContactViewModel contactViewModel;


    public SettingsFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PasswordFragment.
     */

    public static SettingsFragment newInstance(String param1, String param2) {
        SettingsFragment fragment = new SettingsFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_settings, container, false);
    }

    @SuppressLint("CommitPrefEdits")
    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        contactViewModel = new ViewModelProvider(getActivity()).get(ContactViewModel.class);

        TextView deleteAccount = view.findViewById(R.id.deleteAccount);
        deleteAccount.setOnClickListener(v -> {

            new SweetAlertDialog(getActivity(), SweetAlertDialog.WARNING_TYPE)
                    .setTitleText("Delete account")
                    .setContentText("Be careful! This action can't be undone!")
                    .setConfirmText("I will stay!")
                    .setCancelText("Yes, delete it! ")
                    .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss())
                    .setCancelClickListener(sDialog -> {

                        sDialog.dismiss();
                        SharedPreferences sharedPreferences = getPreferences();
                        sharedPreferences.edit().clear().apply();
                        contactViewModel.deleteAll();
//                        Toast.makeText(getActivity(), "Goodbye!", Toast.LENGTH_LONG).show();
                        Toasty.normal(getActivity(), "Goodbye!", Toast.LENGTH_LONG).show();
                        startActivity(new Intent(getActivity(), RegisterActivity.class));
                        getActivity().finish();
                    })
                    .show();
        });


        SharedPreferences sharedPreferences = getPreferences();

        SharedPreferences.Editor editor = sharedPreferences.edit();

        CheckBox checkBoxNotifications = view.findViewById(R.id.checkBoxShowNotifications);
        CheckBox checkBoxNotificationsUnknownContact = view.findViewById(R.id.checkBoxNotificationsFromUnknownContact);

        // default is checked
        if (!sharedPreferences.contains("unknown_contact")) {
            editor.putBoolean("unknown_contact", true);
            checkBoxNotificationsUnknownContact.setChecked(true);
            editor.apply();
        } else {
            boolean notificationsForUnknownContact = sharedPreferences.getBoolean("unknown_contact", false);

            checkBoxNotificationsUnknownContact.setChecked(notificationsForUnknownContact);

            checkBoxNotificationsUnknownContact.setOnClickListener(view1 -> {
                editor.putBoolean("unknown_contact", checkBoxNotificationsUnknownContact.isChecked());
                editor.apply();
            });
        }


        // default is unchecked
        if (!sharedPreferences.contains("notifications")){
            editor.putBoolean("notifications", false);
            editor.apply();
        }else{
            boolean notificationsSet = sharedPreferences.getBoolean("notifications", false);
            // if check box is checked then notifications will not be shown
            checkBoxNotifications.setChecked(notificationsSet);

            checkBoxNotifications.setOnClickListener(view1 -> {
                if (checkBoxNotifications.isChecked()) {
                    editor.putBoolean("notifications", true);
                    editor.apply();
                } else {
                    editor.putBoolean("notifications", false);
                    editor.apply();
                }
            });
        }



    }


    public SharedPreferences getPreferences() {
        MasterKey mainKey;
        SharedPreferences sharedPreferences = null;
        try {
            mainKey = new MasterKey.Builder(getActivity())
                    .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
                    .build();

            sharedPreferences = EncryptedSharedPreferences.create(
                    getActivity(),
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