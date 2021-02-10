package dsd.mdhfer.drongo.services;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.Date;
import java.util.concurrent.ExecutionException;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.models.Message;
import dsd.mdhfer.drongo.models.MessageWrapper;
import dsd.mdhfer.drongo.repo.ContactRepository;
import dsd.mdhfer.drongo.repo.MessageRepository;
import dsd.mdhfer.drongo.ui.activities.HomeActivity;
import dsd.mdhfer.drongo.viewModels.ContactViewModel;
import es.dmoral.toasty.Toasty;

import static dsd.mdhfer.drongo.utility.CryptoUtil.decrypt;

public class ServerService extends Service {

    public static final String START_SERVER = "startServer";
    public static final String STOP_SERVER = "stopServer";
    public static final int SERVERPORT = 9700;

    Thread serverThread;
    ServerSocket serverSocket;
    private ContactRepository contactRepository;
    private MessageRepository messageRepository;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    public ServerService() {

    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        contactRepository = new ContactRepository(getApplication());
        messageRepository = new MessageRepository(getApplication());

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel();
            createNotificationChannelForMessages();
        }

        Intent homeActivityIntent = new Intent(this, HomeActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, homeActivityIntent, 0);

        Notification notification = new NotificationCompat.Builder(this, "ChannelId1")
                .setContentTitle("Listening for incoming messages")
                .setContentText("Server running")
                .setSmallIcon(R.drawable.notification_icon)
                .setContentIntent(pendingIntent).build();

        startForeground(1, notification);

        String action = intent.getAction();
        if (action.equals(START_SERVER)) {
            this.serverThread = new Thread(new MyServer());
            this.serverThread.start();
        }
        if (action.equals(STOP_SERVER)) {
            if (serverSocket != null) {
                try {
                    serverSocket.close();
                } catch (IOException ignored) {
                }
            }
        }

        return START_STICKY;
    }

    @Override
    public void onDestroy() {
        stopForeground(true);
        stopSelf();
        super.onDestroy();
    }

    private void createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel notificationChannel = new NotificationChannel(
                    "ChannelId1", "Foreground", NotificationManager.IMPORTANCE_LOW
            );
            notificationChannel.setSound(null, null);

            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(notificationChannel);
        }

    }

    class MyServer implements Runnable {

        Socket socket;
        DataInputStream dataInputStream;
        String message;
        Handler handler = new Handler();

        @Override
        public void run() {

            try {
                serverSocket = new ServerSocket(SERVERPORT);
                handler.post(() -> Log.e("Service", "Started"));

                while (!Thread.currentThread().isInterrupted()) {

                    socket = serverSocket.accept();
                    dataInputStream = new DataInputStream(socket.getInputStream());
                    message = dataInputStream.readUTF();
                    handler.post(() -> {


                        if (message.contains("%")) {

                            String[] messageContent = message.split("%");
                            String uuid = messageContent[1];
                            String actualMessage = messageContent[0];

                            Contact contactCheck = null;

                            try {
                                contactCheck = contactRepository.findContact(uuid);

                            } catch (ExecutionException | InterruptedException e) {
                                e.printStackTrace();
                            }
                            boolean notificationForUnknownContact = getPreferences().getBoolean("unknown_contact", false);
                            boolean hasSetNotificationForUnknownContact = getPreferences().contains("unknown_contact");

                            if (contactCheck == null) {
                                if (notificationForUnknownContact || !hasSetNotificationForUnknownContact) {
                                    Toasty.error(getApplicationContext(), "Received a message from an unknown contact", Toast.LENGTH_SHORT).show();
                                }
                            } else {

                                String privateKeyString = getPreferences().getString("private_key", null);
                                String decryptedMessage = null;
                                try {
                                    decryptedMessage = decrypt(actualMessage, privateKeyString);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }

                                if (decryptedMessage != null) {


                                    Message receivedMessageObject = new Message();
                                    Date receivedAt = new Date();
                                    receivedMessageObject.setReceivedAt(receivedAt);
                                    receivedMessageObject.setContactUuid(uuid);
                                    receivedMessageObject.setMessageContent(decryptedMessage);

                                    messageRepository.insert(receivedMessageObject);
                                    contactCheck.setLastMessage(decryptedMessage);
                                    contactRepository.update(contactCheck);

                                    boolean notificationForIncomingMessages = !getPreferences().getBoolean("notifications", false);
                                    boolean loggedIn = getPreferences().getBoolean("logged_in", false);

                                    if (notificationForIncomingMessages && loggedIn) {

                                        NotificationCompat.Builder builder = new NotificationCompat.Builder(getApplicationContext(), "messagesChannel")
                                                .setSmallIcon(R.drawable.notification_icon)
                                                .setContentTitle(contactCheck.getContactUsername())
                                                .setContentText(decryptedMessage)
                                                .setPriority(NotificationCompat.PRIORITY_MAX)
                                                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                                                .setVibrate(new long[0])
                                                .setAutoCancel(true);


                                        NotificationManagerCompat notificationManagerCompat = NotificationManagerCompat.from(getApplicationContext());
                                        notificationManagerCompat.notify(100, builder.build());
                                    }
                                } else {
                                    Toasty.error(getApplicationContext(), "Unable to decrypt a message from " + contactCheck.getContactUsername(), Toast.LENGTH_SHORT).show();
                                }
                            }
                        }

                    });

                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
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

    private void createNotificationChannelForMessages() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            CharSequence name = "Messages";
            String description = "Channel for messages notifications";
            int importance = NotificationManager.IMPORTANCE_HIGH;
            NotificationChannel channel = new NotificationChannel("messagesChannel", name, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = getSystemService(NotificationManager.class);
            notificationManager.createNotificationChannel(channel);
        }
    }

}

