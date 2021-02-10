package dsd.mdhfer.drongo.utility;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ContentResolver;
import android.content.Context;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.net.Uri;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import com.github.sumimakito.awesomeqr.AwesomeQrRenderer;
import com.github.sumimakito.awesomeqr.RenderResult;
import com.github.sumimakito.awesomeqr.option.RenderOption;
import com.github.sumimakito.awesomeqr.option.background.StillBackground;
import com.github.sumimakito.awesomeqr.option.color.Color;
import com.github.sumimakito.awesomeqr.option.logo.Logo;
import com.google.zxing.BarcodeFormat;

import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.math.BigInteger;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.spec.InvalidKeySpecException;
import java.security.spec.KeySpec;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

import dsd.mdhfer.drongo.R;
import es.dmoral.toasty.Toasty;


/**
 * Contains basic utility functions used by multiple classes.
 */
public class Utility {

    /** String which identifies the toasty message style */
    private static String toastyMessage = "ToastyMessage";

    public static boolean disableScreenCapture(Window window) {
        window.setFlags(WindowManager.LayoutParams.FLAG_SECURE,
                WindowManager.LayoutParams.FLAG_SECURE);
        return true;
    }

    public static void hideSoftKeyboard(View view) {
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
            if (inputManager != null) {
                inputManager.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }


    public static void generateQrCode(Bitmap drongoLogoImage, ImageView imageView, String textToEncode) {


        Bitmap drongoLogo = Bitmap.createBitmap(drongoLogoImage);

        Logo logo = new Logo();
        logo.setBitmap(drongoLogo);
        logo.setBorderRadius(0); // radius for logo's corners
        logo.setBorderWidth(0); // width of the border to be added around the logo
        logo.setScale(0.2f); // scale for the logo in the QR code
        logo.setClippingRect(new RectF(0, 0, 200, 200)); // crop the logo image before applying it to the QR code


        RenderOption renderOption = new RenderOption();
        renderOption.setContent(textToEncode); // content to encode
        renderOption.setSize(800); // size of the final QR code image
        renderOption.setBorderWidth(5); // width of the empty space around the QR code
        renderOption.setEcl(ErrorCorrectionLevel.M); // (optional) specify an error correction level
        renderOption.setPatternScale(1F); // (optional) specify a scale for patterns
        renderOption.setRoundedPatterns(false); // (optional) if true, blocks will be drawn as dots instead
        renderOption.setClearBorder(true); // if set to true, the background will NOT be drawn on the border area


        // yellow 0xFFFFAF12
        // blue 0xFF34558B

        Color color = new Color();
        color.setLight(0xFFFFAF12); // for blank spaces - yellow
        color.setDark(0xFF34558B); // for non-blank spaces - blue
        color.setBackground(0xFFFFFFFF); // for the background (will be overridden by background images, if set)
        color.setAuto(true); // set to true to automatically pick out colors from the background image (will only work if background image is present)

        renderOption.setColor(color);

        renderOption.setLogo(logo); // set a logo, keep reading to find more about it

        try {
            RenderResult render = AwesomeQrRenderer.render(renderOption);
            if (render.getBitmap() != null) {


                imageView.setImageBitmap(render.getBitmap());

            } else if (render.getType() == RenderResult.OutputType.GIF) {
                // If your Background is a GifBackground, the image
                // will be saved to the output file set in GifBackground
                // instead of being returned here. As a result, the
                // result.bitmap will be null.


            } else {
                // Oops, something gone wrong.
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    // TODO rewrite this to check if URI is given instead of avatar

    public static int getAvatar(String avatarName) {
        if (avatarName.equalsIgnoreCase("avatar@abuki")) {
            return R.mipmap.abuki;
        }

        if (avatarName.equalsIgnoreCase("avatar@deki")) {
            return R.mipmap.deki;
        }

        if (avatarName.equalsIgnoreCase("avatar@iki")) {
            return R.mipmap.iki;
        }

        if (avatarName.equalsIgnoreCase("avatar@luki")) {
            return R.mipmap.luki;
        }

        if (avatarName.equalsIgnoreCase("avatar@maki")) {
            return R.mipmap.maki;
        }

        if (avatarName.equalsIgnoreCase("avatar@paki")) {
            return R.mipmap.paki;
        }

        if (avatarName.equalsIgnoreCase("avatar@peki")) {
            return R.mipmap.peki;
        }

        if (avatarName.equalsIgnoreCase("avatar@teki")) {
            return R.mipmap.teki;
        }

        if (avatarName.equalsIgnoreCase("avatar@toki")) {
            return R.mipmap.toki;
        }

        if (avatarName.equalsIgnoreCase("avatar@vaki")) {
            return R.mipmap.vaki;
        }
        return R.mipmap.luki;
    }


    // TODO rewrite this method so that it returns N/A if no address is found (if phone is not connected to WiFi)

    public static String getLocalIPAddress(boolean useIPv4) {
        try {
            List<NetworkInterface> interfaces = Collections.list(NetworkInterface.getNetworkInterfaces());
            for (NetworkInterface intf : interfaces) {
                List<InetAddress> addrs = Collections.list(intf.getInetAddresses());
                for (InetAddress addr : addrs) {
                    if (!addr.isLoopbackAddress()) {
                        String sAddr = addr.getHostAddress().toUpperCase();
                        boolean isIPv4 = addr instanceof Inet4Address;
                        if (useIPv4) {
                            if (isIPv4)
                                return sAddr;
                        } else {
                            if (!isIPv4) {
                                int delim = sAddr.indexOf('%'); // drop ip6 port suffix
                                return delim < 0 ? sAddr : sAddr.substring(0, delim);
                            }
                        }
                    }
                }
            }
        } catch (Exception ex) {
        } // for now eat exceptions
        return "N/A";
    }


    public static String generatePasswordHash(String password) throws NoSuchAlgorithmException, InvalidKeySpecException {
        int iterations = 1000;
        char[] chars = password.toCharArray();
        byte[] salt = getSalt();

        PBEKeySpec spec = new PBEKeySpec(chars, salt, iterations, 64 * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] hash = skf.generateSecret(spec).getEncoded();
        return iterations + ":" + toHex(salt) + ":" + toHex(hash);
    }

    private static byte[] getSalt() {
        return "cG9zb2xpX2dh".getBytes();
    }

    private static String toHex(byte[] array) throws NoSuchAlgorithmException {
        BigInteger bi = new BigInteger(1, array);
        String hex = bi.toString(16);
        int paddingLength = (array.length * 2) - hex.length();
        if (paddingLength > 0) {
            return String.format("%0" + paddingLength + "d", 0) + hex;
        } else {
            return hex;
        }
    }

    public static boolean validatePassword(String originalPassword, String storedPassword) throws NoSuchAlgorithmException, InvalidKeySpecException {
        String[] parts = storedPassword.split(":");
        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = fromHex(parts[1]);
        byte[] hash = fromHex(parts[2]);

        PBEKeySpec spec = new PBEKeySpec(originalPassword.toCharArray(), salt, iterations, hash.length * 8);
        SecretKeyFactory skf = SecretKeyFactory.getInstance("PBKDF2WithHmacSHA1");
        byte[] testHash = skf.generateSecret(spec).getEncoded();

        int diff = hash.length ^ testHash.length;
        for (int i = 0; i < hash.length && i < testHash.length; i++) {
            diff |= hash[i] ^ testHash[i];
        }
        return diff == 0;
    }

    private static byte[] fromHex(String hex) {
        byte[] bytes = new byte[hex.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(hex.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

    public static void configureToastDesign()
    {
        Typeface textStyle = Typeface.create(toastyMessage, Typeface.BOLD);
        Toasty.Config.getInstance().tintIcon(false).setToastTypeface(textStyle)
                .allowQueue(true)
                .apply();

    }

    public static void copyToClipboard(Context context, String copiedText) {
        ClipboardManager clipboard = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clip = ClipData.newPlainText(copiedText, copiedText);
        clipboard.setPrimaryClip(clip);
        Toasty.normal(context, "Copied to clipboard", Toast.LENGTH_SHORT).show();
    }

    public static boolean checkURIResource(Context context, Uri uri) {
        Cursor cursor = context.getContentResolver().query(uri, null, null, null, null);
        boolean doesExist= (cursor != null && cursor.moveToFirst());
        if (cursor != null) {
            cursor.close();
        }
        return doesExist;
    }


}
