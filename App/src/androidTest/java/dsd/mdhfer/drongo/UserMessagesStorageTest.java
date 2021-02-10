package dsd.mdhfer.drongo;

import android.content.Context;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Calendar;
import java.util.List;

import dsd.mdhfer.drongo.dao.ContactDao;
import dsd.mdhfer.drongo.dao.MessageDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Message;

@RunWith(AndroidJUnit4.class)
public class UserMessagesStorageTest {


    private AppDatabase database;
    private MessageDao messageDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = AppDatabase.getDatabase(context);
        messageDao = database.messageDao();
    }



    @Test
    public void createMessageAndSaveItToDatabase() {

        Message message = new Message();
        message.setMessageContent("Message content");
        message.setContactUuid("12345689");
        message.setReceivedAt(Calendar.getInstance().getTime());
        messageDao.insertMessage(message);
        List<Message> messages = messageDao.getMessagesForContact("12345689");
        assert(messages.contains(message));
    }
}
