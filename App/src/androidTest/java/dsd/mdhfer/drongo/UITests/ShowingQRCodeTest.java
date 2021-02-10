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
import static androidx.test.espresso.matcher.ViewMatchers.withId;

@RunWith(AndroidJUnit4.class)
public class ShowingQRCodeTest {

    @Rule
    public ActivityScenarioRule<HomeActivity> HomeActivityActivityScenarioRule
            = new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void ShowQR_firstWay() {
        onView(withId(R.id.add_btn)).perform(click());
        onView(withId(R.id.show_qr_fab)).perform(click());
    }

    @Test
    public void ShowQR_secondWay() {
        onView(withId(R.id.add_btn)).perform(click());
        onView(withId(R.id.add_contacts_button)).perform(click());
        // permission
        onView(withId(R.id.show_qr)).perform(click());
    }
    
}
