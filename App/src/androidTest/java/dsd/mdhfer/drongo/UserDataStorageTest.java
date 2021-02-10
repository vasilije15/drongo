package dsd.mdhfer.drongo;


import android.content.Context;
import android.content.SharedPreferences;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dsd.mdhfer.drongo.dao.ContactDao;
import dsd.mdhfer.drongo.database.AppDatabase;
import dsd.mdhfer.drongo.models.Contact;
import dsd.mdhfer.drongo.ui.activities.LoginActivity;
import dsd.mdhfer.drongo.UITests.RegistrationBehaviorTest;
import dsd.mdhfer.drongo.ui.activities.RegisterActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static dsd.mdhfer.drongo.UITests.RegistrationBehaviorTest.CORRECT_USERNAME;
import static dsd.mdhfer.drongo.database.AppDatabase.getDatabase;



@RunWith(AndroidJUnit4.class)
@LargeTest
public class UserDataStorageTest {
    private AppDatabase database;
    private ContactDao contactDao;

    @Before
    public void createDb() {
        Context context = ApplicationProvider.getApplicationContext();
        database = AppDatabase.getDatabase(context);
        contactDao = database.contactDao();
    }

    @Test
    public void createUserAndAddIntoDatabase()
    {
        Contact contact = new Contact();
        contact.setContactUsername("Username123");
        contact.setGivenUsername("aaaaaaaaaa");
        contact.setContactUuid("123");

        contactDao.insertContact(contact);

        assert(contactDao.checkIfExists("123") != null);
        assert(contactDao.findByUsername("Username123"));
        Contact contactAdded = contactDao.findByName("Username123");
        assert(contactAdded.getContactUsername().equals(contact.getContactUsername()));
        assert(contactAdded.getContactUuid().equals(contact.getContactUuid()));


    }



}
