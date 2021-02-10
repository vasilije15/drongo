package dsd.mdhfer.drongo.ui.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.security.crypto.EncryptedSharedPreferences;
import androidx.security.crypto.MasterKey;

import com.stfalcon.chatkit.commons.models.IMessage;
import com.stfalcon.chatkit.messages.MessageInput;
import com.stfalcon.chatkit.messages.MessagesList;
import com.stfalcon.chatkit.messages.MessagesListAdapter;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutionException;


import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.models.Message;
import dsd.mdhfer.drongo.models.MessageWrapper;
import dsd.mdhfer.drongo.models.UserWrapper;
import dsd.mdhfer.drongo.repo.ContactRepository;
import dsd.mdhfer.drongo.viewModels.MessageViewModel;
import es.dmoral.toasty.Toasty;


import static dsd.mdhfer.drongo.utility.CryptoUtil.*;
import static dsd.mdhfer.drongo.utility.Utility.disableScreenCapture;
import static dsd.mdhfer.drongo.utility.Utility.getAvatar;
import static dsd.mdhfer.drongo.utility.Utility.copyToClipboard;


public class ChatActivity extends AppCompatActivity {

    private String contactIp;

    // adapter for ChatKit
    private MessagesListAdapter<IMessage> messagesAdapter;
    // user wrapper class that ChatKit uses to differentiate between sender and receiver
    private UserWrapper sender;
    private UserWrapper receivedFrom;
    private MessageViewModel messageViewModel;


    private String publicKeyString;
    private String uid;
    private String avatar;
    private Contact contact;
    private ContactRepository contactRepository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (!checkWifiConnected()){
            Toasty.warning(this, "You are not connected to a WiFi AP!", Toast.LENGTH_LONG).show();
            finish();
        }

        if (!disableScreenCapture(getWindow())) {
            Log.e("Disabled screen capture", "No screenshots");
        }

        setContentView(R.layout.activity_chat);

        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);

        messageViewModel = new ViewModelProvider(this).get(MessageViewModel.class);
        contactRepository = new ContactRepository(getApplication());


        Intent intent = getIntent();

        String contactName = intent.getStringExtra("username");
        contactIp = intent.getStringExtra("ip");
        String contactAvatar = intent.getStringExtra("avatar");
        String contactUuid = intent.getStringExtra("uuid");
        publicKeyString = intent.getStringExtra("key");

        try {
            contact = contactRepository.findContact(contactUuid);
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        // TODO rewrite here to show image from URI if that was set
        ImageView contactAvatarImageView = findViewById(R.id.chatActivityAvatar);
        contactAvatarImageView.setImageResource(getAvatar(contactAvatar));


        receivedFrom = new UserWrapper(contactUuid, contactName, contactAvatar);

        String username = getPreferences().getString("username", null);
        uid = getPreferences().getString("uid", null);
        avatar = getPreferences().getString("avatar", null);

        sender = new UserWrapper(uid, username, avatar);


        TextView contactNameTextView = findViewById(R.id.textview_contact_name);
        contactNameTextView.setText(contactName);

        contactNameTextView.setOnClickListener(view -> {
            Intent intentContactDetails = new Intent(getApplicationContext(), ContactDetailsActivity.class);
            intentContactDetails.putExtra("uuid", contactUuid);
            startActivity(intentContactDetails);
            overridePendingTransition(R.anim.slide_in_right, R.anim.slide_out_left);
        });

        initViews();
        try {
            addPreviousMessagesToList();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }


        // observer that checks if new message has arrived and then adds that message to messageAdapter which ChatKit uses
        messageViewModel.getMessages().observe(this, messages -> {

            if (messages.size() > 0) {
                Message lastMessage = messages.get(messages.size() - 1);

                // TODO rewrite this to make it work completely

                if (lastMessage.contactUuid.equals(contactUuid) && lastMessage.sentAt == null) {

                    MessageWrapper messageWrapper = new MessageWrapper(lastMessage, receivedFrom);
                    addMessageToMessageList(messageWrapper);
                }

            }
        });

        ImageView backButton = findViewById(R.id.chatActivityBackButton);
        backButton.setOnClickListener(v -> finish());

    }


    private void initViews() {

        MessageInput inputView = findViewById(R.id.messageInputChatKit);
        MessagesList messagesList = findViewById(R.id.messagesListChatKit);
        inputView.setInputListener(input -> {
            //validate and send message

            if (!checkWifiConnected()){
                Toasty.error(this, "You are not connected to a WiFi AP!", Toast.LENGTH_SHORT).show();
                return false;
            }

            if (input.length() > 200) {
                Toasty.error(ChatActivity.this, "Max message length is 200 characters", Toast.LENGTH_SHORT).show();
                return false;
            }
            sendMessage(input.toString());
            return true;
        });

        // uncomment line below and put imageLoader as second parameter in messageAdapter to show avatar in UI
//        ImageLoader imageLoader = (imageView, url, payload) -> imageView.setImageResource(getAvatar(avatar));

        messagesAdapter = new MessagesListAdapter<>(sender.getId(), null);
        messagesList.setAdapter(messagesAdapter);

        messagesAdapter.enableSelectionMode(count -> {
            if (count > 0) {
                String selectedMessage = messagesAdapter.getSelectedMessagesText(getMessageStringFormatter(), false);
                copyToClipboard(ChatActivity.this, selectedMessage);
            }
        });

    }

    // Send messages here
    public void sendMessage(String messageContent) {

        BackgroundTask backgroundTask = new BackgroundTask();
        String encryptedStringMessage = null;

        try {
            encryptedStringMessage = encrypt(messageContent, publicKeyString);
        } catch (Exception e) {
            e.printStackTrace();
        }

        backgroundTask.execute(encryptedStringMessage + "%" + uid);



        Message messageObject = new Message();
        Date sentAtDate = new Date();

        messageObject.setSentAt(sentAtDate);
        messageObject.setContactUuid(receivedFrom.getId());
        messageObject.setMessageContent(messageContent);

        messageViewModel.insert(messageObject);
        contact.setLastMessage(messageContent);
        contactRepository.update(contact);

        MessageWrapper messageWrapper = new MessageWrapper(messageObject, sender);
        addMessageToMessageList(messageWrapper);

    }

    // fetch previous messages
    public List<IMessage> getPreviousMessages() throws ExecutionException, InterruptedException {

        List<Message> previousMessagesList = messageViewModel.getMessagesForContact(receivedFrom.getId());
        List<IMessage> messagesWrapperList = new ArrayList<>();
        int index = 0;
        for (Message message : previousMessagesList) {

            // remove last message because observer will add it
            if (index == previousMessagesList.size() - 1) {
                break;
            }

            // sent messages
            if (message.getSentAt() != null) {
                messagesWrapperList.add(new MessageWrapper(message, sender));
            } else {
                messagesWrapperList.add(new MessageWrapper(message, receivedFrom));
            }
            index++;
        }
        return messagesWrapperList;

    }

    public void addMessageToMessageList(MessageWrapper messageWrapper) {
        messagesAdapter.addToStart(messageWrapper, true);
    }

    public void addPreviousMessagesToList() throws ExecutionException, InterruptedException {
        List<IMessage> messagesWrapperList = getPreviousMessages();
        messagesAdapter.addToEnd(messagesWrapperList, true);
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.slide_in_left, R.anim.slide_out_right);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }

    private MessagesListAdapter.Formatter<IMessage> getMessageStringFormatter() {
        return message -> message.getText();
    }

    class BackgroundTask extends AsyncTask<String, Void, String> {

        Socket socket;
        DataOutputStream dataOutputStream;
        String message;

        @Override
        protected String doInBackground(String... strings) {

            message = strings[0];

            try {
                socket = new Socket(contactIp, 9700);
                dataOutputStream = new DataOutputStream(socket.getOutputStream());
                dataOutputStream.writeUTF(message);

                dataOutputStream.close();
                socket.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
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

    private boolean checkWifiConnected() {
        ConnectivityManager connMgr = (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connMgr == null) {
            return false;
        }
        Network network = connMgr.getActiveNetwork();
        if (network == null) return false;
        NetworkCapabilities capabilities = connMgr.getNetworkCapabilities(network);
        return capabilities != null && capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI);
    }


}



