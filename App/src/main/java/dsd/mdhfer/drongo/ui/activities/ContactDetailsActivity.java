package dsd.mdhfer.drongo.ui.activities;

import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.net.Uri;
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

import com.esafirm.imagepicker.features.ImagePicker;
import com.esafirm.imagepicker.features.ReturnMode;
import com.esafirm.imagepicker.model.Image;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;
import de.hdodenhof.circleimageview.CircleImageView;
import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;

import static dsd.mdhfer.drongo.utility.Utility.checkURIResource;
import static dsd.mdhfer.drongo.utility.Utility.getAvatar;
import static dsd.mdhfer.drongo.utility.Utility.hideSoftKeyboard;

public class ContactDetailsActivity extends AppCompatActivity {

    private ContactViewModel contactViewModel;
    private Contact contact;

    private CircleImageView contactProfilePictureUpdate;
    private String uriToNewPicture;
    private String currentGivenUsername;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_details);


        contactViewModel = new ViewModelProvider(this).get(ContactViewModel.class);
        contactProfilePictureUpdate = findViewById(R.id.contact_picture_contact_details);

        TextView contactUsernameTextView = findViewById(R.id.contact_details_username);
        TextView givenUsernameTextView = findViewById(R.id.contact_details_given_username);

        String uuid = getIntent().getStringExtra("uuid");
        try {
            contact = contactViewModel.checkIfContactExists(uuid);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }



        contactUsernameTextView.setText(contact.getContactUsername());
        givenUsernameTextView.setText(contact.getGivenUsername());


        String profilePictureUri = contact.getProfilePictureUri();
        uriToNewPicture = profilePictureUri;

        currentGivenUsername = contact.getGivenUsername();

        if (profilePictureUri != null) {
            if (profilePictureUri.contains("avatar@")) {
                int picture = getAvatar(profilePictureUri);
                contactProfilePictureUpdate.setImageResource(picture);
            } else {
                Uri uri = Uri.parse(profilePictureUri);
                if (checkURIResource(this, uri)){
                    contactProfilePictureUpdate.setImageURI(uri);
                }else{
                    contactProfilePictureUpdate.setImageResource(R.mipmap.luki);
                }

            }
        } else {
            contactProfilePictureUpdate.setImageResource(R.mipmap.luki);
        }

        ImageView nextButton = findViewById(R.id.button_update_contact_info);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                hideSoftKeyboard(v);

                String givenUsername = givenUsernameTextView.getText().toString();

                boolean noUpdates = false;

                if (checkUsername(givenUsername)) {

                    boolean profilePictureChanged = !uriToNewPicture.equals(contact.getProfilePictureUri());
                    boolean givenUsernameChanged = !currentGivenUsername.equals(givenUsername);

                    if (!givenUsernameChanged) {
                        if (!profilePictureChanged) {
                            Toasty.info(getApplicationContext(), "No updates", Toast.LENGTH_LONG).show();
                            noUpdates = true;
                        } else {
                            Toasty.info(getApplicationContext(), "Contact picture for " + contact.getContactUsername() + " has been changed", Toast.LENGTH_LONG).show();
                            contact.setProfilePictureUri(uriToNewPicture);
                        }
                    } else {
                        if (profilePictureChanged) {
                            Toasty.info(getApplicationContext(), "Contact picture and username for " + contact.getContactUsername() + " have been changed to " + givenUsername, Toast.LENGTH_LONG).show();
                            contact.setGivenUsername(givenUsername);
                            contact.setProfilePictureUri(uriToNewPicture);
                        } else {
                            Toasty.info(getApplicationContext(), "Username for " + contact.getContactUsername() + " has been changed to " + givenUsername, Toast.LENGTH_LONG).show();
                            contact.setGivenUsername(givenUsername);
                        }
                    }

                    // only update if there are changes
                    if (!noUpdates) {
                        contactViewModel.update(contact);
                    }


                    finish();

                } else {
                    Toasty.error(getApplicationContext(), "Nickname must start with a capital letter and be minimum 3 characters! Only dash and underscore is allowed for special characters!", Toast.LENGTH_LONG).show();
                }
            }
        });

        contactProfilePictureUpdate.setOnClickListener(v -> pickImage());

        ImageView deleteImageView = findViewById(R.id.deleteContact);
        deleteImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new SweetAlertDialog(ContactDetailsActivity.this, SweetAlertDialog.WARNING_TYPE)
                        .setTitleText("Delete contact " + contact.getContactUsername())
                        .setContentText("Be careful! This action can't be undone!")
                        .setConfirmText("Cancel")
                        .setCancelText("Delete")
                        .setConfirmClickListener(sweetAlertDialog -> sweetAlertDialog.dismiss())
                        .setCancelClickListener(sDialog -> {

                            sDialog.dismiss();
                            contactViewModel.delete(contact);
                            Toasty.success(ContactDetailsActivity.this, "Contact with username " + contact.getContactUsername() + " has been deleted", Toast.LENGTH_LONG).show();
                            finishAffinity();
                            startActivity(new Intent(ContactDetailsActivity.this, HomeActivity.class));

                        })
                        .show();
            }
        });

        ImageView backButton = findViewById(R.id.back_button_contact_details);
        backButton.setOnClickListener(v -> finish());

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
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

    @Override
    public void onActivityResult(int requestCode, final int resultCode, Intent data) {
        if (ImagePicker.shouldHandle(requestCode, resultCode, data)) {

            Image image = ImagePicker.getFirstImageOrNull(data);
            uriToNewPicture = image.getUri().toString();
            contactProfilePictureUpdate.setImageURI(image.getUri());

        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    public boolean checkUsername(String username) {
        return Pattern.matches("^[A-Z][a-z0-9_-]{2,15}$", username);
    }

    public void pickImage() {
        ImagePicker.create(this)
                .returnMode(ReturnMode.ALL) // set whether pick and / or camera action should return immediate result or not.
                .folderMode(false) // folder mode (false by default)
                .toolbarFolderTitle("Folder") // folder selection title
                .toolbarImageTitle("Update contact picture") // image selection title
                .toolbarArrowColor(Color.WHITE) // Toolbar 'up' arrow color
                .includeVideo(false) // Show video on image picker
                .single() // single mode
                .showCamera(false) // show camera or not (true by default)

//                .theme(R.style.CustomImagePickerTheme) // must inherit ef_BaseTheme. please refer to sample
                .enableLog(true) // disabling log
                .start(); // start image picker activity with request code
    }

}
