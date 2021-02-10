package dsd.mdhfer.drongo.ui.fragments;


import android.content.SharedPreferences;

import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ImageView;

import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;


import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;

import java.util.regex.Pattern;

import de.hdodenhof.circleimageview.CircleImageView;
import dsd.mdhfer.drongo.R;

import es.dmoral.toasty.Toasty;


import static dsd.mdhfer.drongo.utility.Utility.getAvatar;
import static dsd.mdhfer.drongo.utility.Utility.hideSoftKeyboard;


public class UpdateUserInfoFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = "UpdateUserInfoFragment";


    private String mParam1;
    private String mParam2;


    public UpdateUserInfoFragment() {
        // Required empty public constructor
    }


    public static UpdateUserInfoFragment newInstance(String param1, String param2) {
        UpdateUserInfoFragment fragment = new UpdateUserInfoFragment();
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
        return inflater.inflate(R.layout.update_user_info, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        TextView usernameTextView = view.findViewById(R.id.update_username);

        CircleImageView userProfilePictureUpdate = view.findViewById(R.id.user_profile_picture_update_user_info);


        SharedPreferences sharedPreferences = getPreferences();


        String usernameFromPref = sharedPreferences.getString("username", null);
        String avatarFromPref = sharedPreferences.getString("avatar", null);

        
        usernameTextView.setText(usernameFromPref);
        userProfilePictureUpdate.setImageResource(getAvatar(avatarFromPref));


        ImageView updateUserInfoButton = view.findViewById(R.id.button_update_user_info);

        updateUserInfoButton.setOnClickListener(v -> {

            hideSoftKeyboard(v);

            String newUsername = usernameTextView.getText().toString();

            if (newUsername.equals(usernameFromPref)) {
                Toasty.normal(getContext(), "No updates!", Toast.LENGTH_LONG).show();
                navController.popBackStack();
                return;
            }

            if (checkNewUsername(newUsername)) {

                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("username", newUsername);
                editor.apply();

                // Update username in side navigation header
                NavigationView navigationView = getActivity().findViewById(R.id.navView);
                navigationView.setItemIconTintList(null);

                View headerView = navigationView.getHeaderView(0);
                TextView usernameForHeader = headerView.findViewById(R.id.username_header_drawer);

                usernameForHeader.setText(newUsername);

                Toasty.success(getContext(), "All right " + newUsername + " your name has been changed!", Toast.LENGTH_LONG).show();
                navController.popBackStack();

            } else {
                Toasty.error(getContext(), "Username must start with a capital letter and be minimum 3 characters! Only dash and underscore is allowed for special characters!", Toast.LENGTH_LONG).show();
            }

        });

        ImageView backButton = view.findViewById(R.id.back_button_update_user_info);
        backButton.setOnClickListener(view1 -> {
            navController.popBackStack();
            hideSoftKeyboard(view1);
        });


    }

    public boolean checkNewUsername(String username) {
        return Pattern.matches("^[A-Z][a-z0-9_-]{2,15}$", username);
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
