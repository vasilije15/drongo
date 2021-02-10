package dsd.mdhfer.drongo.ui.fragments;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import dsd.mdhfer.drongo.R;

import static dsd.mdhfer.drongo.utility.Utility.generateQrCode;

public class UserQRFragment extends Fragment {


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.qr_user, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        ImageView userQRCodeImageView = view.findViewById(R.id.user_qr_code_image_view);
        String textToEncode = "Drongo";

        Bitmap drongoLogoImage = BitmapFactory.decodeResource(getResources(), R.drawable.logic_logo_512);

        generateQrCode(drongoLogoImage, userQRCodeImageView, textToEncode);

    }


}
