package dsd.mdhfer.drongo.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;
import androidx.navigation.NavController;
import androidx.navigation.NavDestination;
import androidx.navigation.Navigation;
import androidx.navigation.ui.NavigationUI;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.google.android.material.navigation.NavigationView;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.concurrent.ExecutionException;


import cn.pedant.SweetAlert.SweetAlertDialog;
import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.services.ServerService;


import static dsd.mdhfer.drongo.utility.Utility.getAvatar;


public class HomeActivity extends AppCompatActivity {

    Intent startServer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);


        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);

        setContentView(R.layout.activity_home);

        // settings for drawer menu

        final DrawerLayout drawerLayout = findViewById(R.id.drawer_layout_new);

        findViewById(R.id.imageMenu).setOnClickListener(v -> drawerLayout.openDrawer(GravityCompat.START));

        NavigationView navigationView = findViewById(R.id.navView);
        navigationView.setItemIconTintList(null);

        View headerView = navigationView.getHeaderView(0);
        TextView usernameForHeader = headerView.findViewById(R.id.username_header_drawer);
        ImageView profilePictureHeader = headerView.findViewById(R.id.profile_picture_header);

        NavController navController = Navigation.findNavController(this, R.id.navHostFragment);
        NavigationUI.setupWithNavController(navigationView, navController);

        ConstraintLayout headerSideNavigation = headerView.findViewById(R.id.headerSideNavigation);
        headerSideNavigation.setOnClickListener(v -> navController.navigate(R.id.updateUserInfoFragment));

        TextView textTittle = findViewById(R.id.fragmentTitle);

        navController.addOnDestinationChangedListener((controller, destination, arguments) -> textTittle.setText(destination.getLabel()));

        Menu menu = navigationView.getMenu();
        MenuItem menuItemLogout = menu.findItem(R.id.logOut);

        menuItemLogout.setOnMenuItemClickListener(item -> {

            SweetAlertDialog alertDialog = new SweetAlertDialog(this, SweetAlertDialog.CUSTOM_IMAGE_TYPE);
            alertDialog.setTitleText("Are you sure?");
            alertDialog.setContentText("Confirm to logout!");
            alertDialog.setConfirmText("Logout");
            alertDialog.setCustomImage(R.drawable.ic_logout);
            alertDialog.setConfirmClickListener(sDialog -> {
                sDialog.dismissWithAnimation();
                getPreferences().edit().putBoolean("logged_in", false).apply();
                finish();
            });
            alertDialog.show();

            Button btn = alertDialog.findViewById(R.id.confirm_button);
            btn.setBackgroundColor(ContextCompat.getColor(HomeActivity.this, R.color.yellow));

            return false;
        });

        usernameForHeader.setText(getPreferences().getString("username", null));
        profilePictureHeader.setImageResource(getAvatar(getPreferences().getString("avatar", null)));

        startServer = new Intent(this, ServerService.class);
        startServer.setAction(ServerService.START_SERVER);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.startForegroundService(startServer);
        } else {
            this.startService(startServer);
        }

    }


    @Nullable
    @Override
    public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
        return super.onCreateView(name, context, attrs);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(startServer);
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
