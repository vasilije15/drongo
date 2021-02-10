package dsd.mdhfer.drongo;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import dsd.mdhfer.drongo.dao.ContactDao;
import dsd.mdhfer.drongo.dao.MessageDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.models.Message;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;

@RunWith(AndroidJUnit4.class)
public class MessageDbTest {

    private MessageDao messageDao;
    private ContactDao contactDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        messageDao = db.messageDao();

        contactDao = db.contactDao();
        Contact contact = new Contact();
        contact.setContactUsername("Emulator");
        contact.setContactUuid("123-456-XYZ");
        contactDao.insertContact(contact);
        Contact contact2 = new Contact();
        contact2.setContactUsername("Emulator2");
        contact2.setContactUuid("789-456-ABC");
        contactDao.insertContact(contact2);

    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void storeMessageInDatabaseTest() throws Exception {
        Message message = new Message();
        message.setContactUuid("123-456-XYZ");
        message.setId(111);
        message.setMessageContent("Test message content !!!");
        messageDao.insertMessage(message);

        Message message2 = new Message();
        message2.setContactUuid("123-456-XYZ");
        message2.setId(222);
        message2.setMessageContent("Test message content, the new one !!!");
        messageDao.insertMessage(message2);

        List<Message> fromDatabase = messageDao.getAll();
        assertEquals(fromDatabase.get(0).getMessageContent(), "Test message content !!!");
        assertEquals(fromDatabase.get(1).getMessageContent(), "Test message content, the new one !!!");
    }

    @Test(expected = SQLiteConstraintException.class)
    public void storeMessageFromNonExistingContactTest() throws Exception {
        Message message = new Message();
        message.setContactUuid("345-456-XYZ");
        message.setId(111);
        message.setMessageContent("Test message content !!!");
        messageDao.insertMessage(message);

        List<Message> fromDatabase = messageDao.getAll();
        assertNull(fromDatabase.get(0));
    }

    @Test
    public void getMessagesForSpecificContactTest() throws Exception {
        Message message = new Message();
        message.setContactUuid("123-456-XYZ");
        message.setId(111);
        message.setMessageContent("Test message content !!!");
        messageDao.insertMessage(message);

        Message message2 = new Message();
        message.setContactUuid("789-456-ABC");
        message.setId(222);
        message.setMessageContent("Test message content, from second contact !!!");
        messageDao.insertMessage(message);

        List<Message> fromDatabase1 = messageDao.getMessagesForContact("123-456-XYZ");
        List<Message> fromDatabase2 = messageDao.getMessagesForContact("789-456-ABC");

        assertEquals(fromDatabase1.get(0).getMessageContent(), "Test message content !!!");
        assertEquals(fromDatabase2.get(0).getMessageContent(), "Test message content, from second contact !!!");
    }

    @Test
    public void deleteMessageTest() throws Exception {
        Message message = new Message();
        message.setContactUuid("123-456-XYZ");
        message.setId(111);
        message.setMessageContent("Test message content !!!");
        messageDao.insertMessage(message);

        messageDao.delete(message);
        List<Message> fromDatabase = messageDao.getAll();
        assertFalse(fromDatabase.contains(message));
    }

    @Test
    public void deleteAllMessagesTest() throws Exception {
        Message message = new Message();
        message.setContactUuid("123-456-XYZ");
        message.setId(111);
        message.setMessageContent("Test message content !!!");
        messageDao.insertMessage(message);

        Message message2 = new Message();
        message.setContactUuid("789-456-ABC");
        message.setId(222);
        message.setMessageContent("Test message content, from second contact !!!");
        messageDao.insertMessage(message);

        messageDao.deleteAll();
        List<Message> fromDatabase = messageDao.getAll();
        assertFalse(fromDatabase.contains(message));
        assertFalse(fromDatabase.contains(message2));
    }



}
