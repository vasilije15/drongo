package dsd.mdhfer.drongo.UITests;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.ui.activities.LoginActivity;

@RunWith(AndroidJUnit4.class)
public class LoginTest {

    public static final String CORRECT_PASSWORD = "Hehe123!";
    public static final String INCORRECT_PASSWORD = "Hehe123*";

    @Rule
    public ActivityScenarioRule<LoginActivity> LoginActivityActivityScenarioRule
            = new ActivityScenarioRule<>(LoginActivity.class);

    @Test
    public void correctLoginTest () {
        onView(withId(R.id.password_login))
                .perform(typeText(CORRECT_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.next_button_login)).perform(click());
    }

    @Test
    public void incorrectPassword_LoginTest () {
        onView(withId(R.id.password_login))
                .perform(typeText(INCORRECT_PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.next_button_login)).perform(click());
    }

    @Test
    public void noPassword_LoginTest () {
        onView(withId(R.id.next_button_login)).perform(click());
    }
}
