package dsd.mdhfer.drongo.ui.fragments;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import android.security.keystore.KeyGenParameterSpec;
import android.security.keystore.KeyProperties;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.security.NoSuchAlgorithmException;
import java.security.NoSuchProviderException;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.RSAKeyGenParameterSpec;
import java.util.Map;
import java.util.UUID;
import java.util.regex.Pattern;

import javax.crypto.BadPaddingException;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;

import cn.pedant.SweetAlert.SweetAlertDialog;
import dsd.mdhfer.drongo.R;
import es.dmoral.toasty.Toasty;

import static dsd.mdhfer.drongo.utility.CryptoUtil.generateKeyPair;
import static dsd.mdhfer.drongo.utility.Utility.generatePasswordHash;
import static dsd.mdhfer.drongo.utility.Utility.getAvatar;
import static java.security.spec.RSAKeyGenParameterSpec.F4;


public class PasswordFragment extends Fragment {

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";


    private String mParam1;
    private String mParam2;


    public PasswordFragment() {
        // Required empty public constructor
    }


    public static PasswordFragment newInstance(String param1, String param2) {
        PasswordFragment fragment = new PasswordFragment();
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
        return inflater.inflate(R.layout.create_password, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        NavController navController = Navigation.findNavController(view);

        if (getArguments() != null) {
            PasswordFragmentArgs args = PasswordFragmentArgs.fromBundle(getArguments());
            String avatar = args.getAvatar();
            int picture = getAvatar("avatar@" + avatar);
            ImageView chosenAvatar = view.findViewById(R.id.avatar_create_password);
            chosenAvatar.setImageResource(picture);
        }

        ImageView infoDialog = view.findViewById(R.id.info_create_password);
        infoDialog.setOnClickListener(view1 -> new SweetAlertDialog(getActivity(), SweetAlertDialog.NORMAL_TYPE)
                .setContentText("Password needs at least 4 letters, with one uppercase, 3 numbers and one special character!")
                .show());

        ImageView registerButton = view.findViewById(R.id.button_create_account);
        registerButton.setOnClickListener(v -> {

            EditText passwordEditText = view.findViewById(R.id.password_register);
            String password = passwordEditText.getText().toString();

            EditText confirmationEditText = view.findViewById(R.id.confirmation_register);
            String confirmation = confirmationEditText.getText().toString();

            String username = "";
            String avatar = "";

            if (getArguments() != null) {
                PasswordFragmentArgs args = PasswordFragmentArgs.fromBundle(getArguments());
                username = args.getUsername();
                avatar = args.getAvatar();
            }

            if (checkIfPasswordsMatch(password, confirmation)) {

                String uid = generateUserID();
                String profilePictureUri = "avatar@" + avatar;
                String hashedPassword = confirmation;
                try {
                    hashedPassword = generatePasswordHash(confirmation);
                } catch (NoSuchAlgorithmException | InvalidKeySpecException e) {
                    e.printStackTrace();
                }


                SharedPreferences sharedPreferences = getPreferences();

                SharedPreferences.Editor editor = sharedPreferences.edit();

                editor.putString("uid", uid);
                editor.putString("username", username);
                editor.putString("hash", hashedPassword);
                editor.putString("avatar", profilePictureUri);
                editor.putBoolean("logged_in", true);


                Map<String, String> keys = null;
                try {
                    keys = generateKeyPair();
                } catch (NoSuchAlgorithmException | NoSuchPaddingException | InvalidKeyException | IllegalBlockSizeException | BadPaddingException e) {
                    e.printStackTrace();
                }

                assert keys != null;
                String privateKey = keys.get("privateKey");
                String publicKey = keys.get("publicKey");

                editor.putString("public_key", publicKey);
                editor.putString("private_key", privateKey);
                editor.apply();



                // TODO change this here to not use navController
                navController.navigate(R.id.action_passwordFragment_to_homeActivity);
                getActivity().finish();
            } else {
//                    Toast.makeText(getContext(), "Passwords don't match!", Toast.LENGTH_LONG).show();
                Toasty.error(getContext(), "Passwords don't match!", Toast.LENGTH_SHORT, true).show();
            }
        });


        //Listener for field password (passwordEditText)
        //used for making password strength checker
        EditText passwordEditText = view.findViewById(R.id.password_register);
        passwordEditText.addTextChangedListener(new TextWatcher() {
            public void afterTextChanged(Editable s) {
            }

            public void beforeTextChanged(CharSequence s, int start,
                                          int count, int after) {
            }

            public void onTextChanged(CharSequence s, int start,
                                      int before, int count) {
                ProgressBar progressBar = view.findViewById(R.id.progress_bar);
                String password = passwordEditText.getText().toString();
                TextView passStrength = view.findViewById(R.id.lbl_password_strength);
                ImageView registerButton = view.findViewById(R.id.button_create_account);
                registerButton.setEnabled(false);
                registerButton.setVisibility(View.GONE);
                PasswordFragment.checkStrength(progressBar, password, passStrength, registerButton);
            }
        });

        ImageView backButton = view.findViewById(R.id.back_button_create_password);
        backButton.setOnClickListener(v -> navController.popBackStack());

    }


    public boolean checkIfPasswordsMatch(String password, String confirmation) {
        if (password.length() == 0 || confirmation.length() == 0) {
            return false;
        }

        return password.equals(confirmation);
    }

    //Password Strength
    @SuppressLint("SetTextI18n")
    private static void checkStrength(ProgressBar progressBar, String password, TextView passStrength, ImageView registerButton) {
        if (Pattern.matches("^(?=(.*?[A-Z]){3,})(?=(.*[a-z]){4,})(?=(.*[\\d]){2,})(?=(.*[\\W]){2,})(?!.*\\s).{16,}$", password)) {
            progressBar.setProgress(100);
            passStrength.setText("Strong");
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setEnabled(true);
            progressBar.getProgressDrawable().setColorFilter(Color.GREEN, android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (Pattern.matches("^(?=(.*?[A-Z]){2,})(?=(.*[a-z]){2,})(?=(.*[\\d]){2,})(?=(.*[\\W]){2,})(?!.*\\s).{10,}$", password)) {
            progressBar.setProgress(80);
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setEnabled(true);
            passStrength.setText("High");
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(153, 255, 153), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (Pattern.matches("^(?=(.*?[A-Z]){1,})(?=(.*[a-z]){2,})(?=(.*[\\d]){1,})(?=(.*[\\W]){1,})(?!.*\\s).{8,}$", password)) {
            progressBar.setProgress(60);
            passStrength.setText("Medium");
            registerButton.setVisibility(View.VISIBLE);
            registerButton.setEnabled(true);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(253, 255, 153), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (Pattern.matches("^(?=(.*?[A-Z]){1,})(?=(.*[a-z]){1,}).{4,}$", password)) {
            progressBar.setProgress(40);
            passStrength.setText("Weak");
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(255, 153, 102), android.graphics.PorterDuff.Mode.SRC_IN);
        } else if (password.length() > 0) {
            progressBar.setProgress(20);
            passStrength.setText("Very weak");
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), android.graphics.PorterDuff.Mode.SRC_IN);
        }
        if (password.length() == 0) {
            progressBar.setProgress(0);
            progressBar.getProgressDrawable().setColorFilter(Color.rgb(255, 0, 0), android.graphics.PorterDuff.Mode.SRC_IN);
        }
    }

    public String generateUserID() {
        return UUID.randomUUID().toString();
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


    static void generateKeys(String alias) {
        try {
            KeyPairGenerator keyPairGenerator = KeyPairGenerator.getInstance(
                    KeyProperties.KEY_ALGORITHM_RSA, "AndroidKeyStore");
            keyPairGenerator.initialize(
                    new KeyGenParameterSpec.Builder(
                            alias,
                            KeyProperties.PURPOSE_ENCRYPT | KeyProperties.PURPOSE_DECRYPT)
                            .setAlgorithmParameterSpec(new RSAKeyGenParameterSpec(1024, F4))
                            .setBlockModes(KeyProperties.BLOCK_MODE_CBC)
                            .setEncryptionPaddings(KeyProperties.ENCRYPTION_PADDING_RSA_PKCS1)
                            .setDigests(KeyProperties.DIGEST_SHA256,
                                    KeyProperties.DIGEST_SHA384,
                                    KeyProperties.DIGEST_SHA512)
                            // Only permit the private key to be used if the user authenticated
                            // within the last five minutes.
                            .setUserAuthenticationRequired(false)
                            .build());
            KeyPair keyPair = keyPairGenerator.generateKeyPair();

        } catch (NoSuchProviderException | NoSuchAlgorithmException | InvalidAlgorithmParameterException e) {
            throw new RuntimeException(e);
        }
    }


}
