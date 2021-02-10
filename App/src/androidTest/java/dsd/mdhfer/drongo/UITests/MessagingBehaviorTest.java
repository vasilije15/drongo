package dsd.mdhfer.drongo.UITests;




import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeUp;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.ui.activities.HomeActivity;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class MessagingBehaviorTest {

    public static final String OGROMNA_PORUKA = "This will be veeeeeery loooooooong messagee ... in "+
            "this message will be a lotoflotoflotoflotlof text because we need to check what will happen " +
            "when the message is veeeeery biiiiig like this one, it could happen a lot of ugly things, " +
            "for example, our application could crash which is not goooood that's why I'm writing this " +
            "stupid text, that wouldn't happen later oooo f*** we didn't knoooooow .... this message is " +
            "too big and app cannot take it, for now ... ";

    public static final String[] ORDERED_MESSAGES = {"one", "two", "three", "four", "five",
        "six", "seven", "eight", "nine", "ten", "eleven", "twelve", "thirteen", "fourteen",
            "fifteen", "sixteen", "seventeen", "eighteen", "nineteen", "TWENTY"};

    @Rule
    public ActivityScenarioRule<HomeActivity> homeActivityActivityScenarioRule =
            new ActivityScenarioRule<>(HomeActivity.class);

    @Test
    public void EnterExitEnterContactTest() {
        onView(ViewMatchers.withId(R.id.homeActivity));
        onView(withId(R.id.singleContact)).perform(click());
        onView(withId(R.id.messageInputChatKit)).perform(click());
        onView(withId(R.id.chatActivityBackButton)).perform(click());
        onView(withId(R.id.singleContact)).perform(click());
    }

    @Test
    public void normalSizeMessagesTest() {
        onView(withId(R.id.homeActivity));
        onView(withId(R.id.singleContact)).perform(click());
        onView(withId(R.id.messageInput))
                .perform(typeText("first normal message"), closeSoftKeyboard());
        onView(withId(R.id.messageSendButton)).perform(click());
        onView(withId(R.id.messageInput))
                .perform(typeText("second normal message"), closeSoftKeyboard());
        onView(withId(R.id.messageSendButton)).perform(click());
        onView(withId(R.id.chatActivityBackButton)).perform(click());
        onView(withId(R.id.singleContact)).perform(click());
    }

    @Test
    public void lotOfMessagesSentTest() {
        onView(withId(R.id.homeActivity));
        onView(withId(R.id.singleContact)).perform(click());

        for(int i = 0; i < ORDERED_MESSAGES.length; i++) {
            onView(withId(R.id.messageInput)).perform(typeText(ORDERED_MESSAGES[i]), closeSoftKeyboard());
            onView(withId(R.id.messageSendButton)).perform(click());
        }

        onView(withId(R.id.messagesListChatKit)).perform(swipeUp());
        onView(withId(R.id.chatActivityBackButton)).perform(click());
        onView(withId(R.id.singleContact)).perform(click());
    }

    // ovi pada ..
    @Test
    public void LongMessageTest() {
        onView(withId(R.id.homeActivity));
        onView(withId(R.id.singleContact)).perform(click());
        onView(withId(R.id.messageInput))
                .perform(typeText(OGROMNA_PORUKA), closeSoftKeyboard());
        onView(withId(R.id.messageSendButton)).perform(click());
        onView(withId(R.id.chatActivityBackButton)).perform(click());
        onView(withId(R.id.singleContact)).perform(click());
    }

}
