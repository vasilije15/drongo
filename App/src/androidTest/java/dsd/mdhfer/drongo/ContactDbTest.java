package dsd.mdhfer.drongo;

import android.content.Context;

import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;

import dsd.mdhfer.drongo.dao.ContactDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Contact;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;


@RunWith(AndroidJUnit4.class)
public class ContactDbTest {
    private ContactDao contactDao;
    private AppDatabase db;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase.class).build();
        contactDao = db.contactDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void writeContactAndReadInList() throws Exception {
        Contact contact = new Contact();
        contact.setContactUsername("Emulator");
        contact.setContactUuid("123-456-XYZ");
        contactDao.insertContact(contact);
        Contact fromDatabase = contactDao.findByName("Emulator");
        assertEquals(fromDatabase.getContactUsername(), "Emulator");
        assertEquals(fromDatabase.getContactUuid(), "123-456-XYZ");
    }

    @Test
    public void deleteContactTest() throws Exception {
        Contact contact = new Contact();
        contact.setContactUsername("Emulator");
        contact.setContactUuid("123-456-XYZ");
        contactDao.insertContact(contact);

        contactDao.delete(contact);
        Contact fromDatabase = contactDao.findByID("123-456-XYZ");
        assertNull(fromDatabase);
    }

    @Test
    public void deleteAllContactsTest() throws Exception {
        Contact contact = new Contact();
        contact.setContactUsername("Emulator");
        contact.setContactUuid("123-456-XYZ");
        contactDao.insertContact(contact);

        Contact contact2 = new Contact();
        contact2.setContactUsername("Emulator2");
        contact2.setContactUuid("789-456-ABC");
        contactDao.insertContact(contact2);

        contactDao.deleteAll();
        Contact fromDatabase = contactDao.findByID("123-456-XYZ");
        Contact fromDatabase2 = contactDao.findByID("789-456-ABC");
        assertNull(fromDatabase);
        assertNull(fromDatabase2);
    }

    @Test
    public void updateContactUsernameTest() throws Exception {
        Contact contact = new Contact();
        contact.setContactUsername("Emulator");
        contact.setContactUuid("123-456-XYZ");
        contactDao.insertContact(contact);

        contact.setContactUsername("Newtestname");
        contactDao.update(contact);

        Contact fromDatabase = contactDao.findByName("Newtestname");
        assertEquals(fromDatabase.getContactUsername(), "Newtestname");
        assertEquals(fromDatabase.getContactUuid(), "123-456-XYZ");
    }

}

