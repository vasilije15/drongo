package dsd.mdhfer.drongo.UITests;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.ui.activities.HomeActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ContactDetailsTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> HomeActivityActivityScenarioRule
            = new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void showContactDetails () {
        onView(withId(R.id.singleContact)).perform(click());
        onView(withId(R.id.textview_contact_name)).perform(click());
    }

    @Test
    public void changeContactName () {
        onView(withId(R.id.singleContact)).perform(click());
        onView(withId(R.id.textview_contact_name)).perform(click());

        onView((withId(R.id.contact_details_given_username)))
                .perform(replaceText(""));
        onView((withId(R.id.contact_details_given_username)))
                .perform(typeText("Magarac"), closeSoftKeyboard());
        onView(withId(R.id.button_update_contact_info)).perform(click());
        onView(withId(R.id.chatActivityBackButton)).perform(click());


    }
}
