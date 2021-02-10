/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dsd.mdhfer.drongo.UITests;

import android.app.Activity;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.matcher.ViewMatchers;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;

import dsd.mdhfer.drongo.R;
import dsd.mdhfer.drongo.ui.activities.RegisterActivity;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.swipeLeft;
import static androidx.test.espresso.action.ViewActions.swipeRight;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;


/**
 * Basic tests showcasing simple view matchers and actions like {@link ViewMatchers#withId},
 * {@link ViewActions#click} and {@link ViewActions#typeText}.
 * <p>
 * Note that there is no need to tell Espresso that a view is in a different {@link Activity}.
 */
@RunWith(AndroidJUnit4.class)
@LargeTest
public class RegistrationBehaviorTest {

    public static final String CORRECT_USERNAME = "Maradona";
    public static final String INCORRECT_USERNAME_1 = "maradona";
    public static final String INCORRECT_USERNAME_2 = "ma";

    public static final String PASSWORD = "Hehe123!";
    public static final String NOT_MATCHING_PASS = "Hehe123/";

    public static final String WEAK_PASSWORD = "slabalozinka";

    // TODO write test cases here to test UI of RegisterActivity
    // TODO test carousel, test password fields, etc...

    @Rule public ActivityScenarioRule<RegisterActivity> registerActivityActivityScenarioRule
            = new ActivityScenarioRule<>(RegisterActivity.class);

    // example
    @Test
    public void correctRegistrationTest() {
        // Type username and then press the button
        onView(ViewMatchers.withId(R.id.username_register))
                .perform(typeText(CORRECT_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.next_button_register_username)).perform(click());

        // Choose avatar
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.choose_avatar_button)).perform(click());

        // Type password and then press the button
        onView(withId(R.id.password_register))
                .perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.confirmation_register))
                .perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.button_create_account)).perform(click());
    }

    @Test
    public void incorrectUsername1_RegistrationTest() {
        // Type username and then press the button
        onView(withId(R.id.username_register))
                .perform(typeText(INCORRECT_USERNAME_1), closeSoftKeyboard());
        onView(withId(R.id.next_button_register_username)).perform(click());

    }

    @Test
    public void incorrectUsername2_RegistrationTest() {
        // Type username and then press the button
        onView(withId(R.id.username_register))
                .perform(typeText(INCORRECT_USERNAME_2), closeSoftKeyboard());
        onView(withId(R.id.next_button_register_username)).perform(click());

    }

    @Test
    public void weakPassword_RegistrationTest() {
        // Type username and then press the button
        onView(withId(R.id.username_register))
                .perform(typeText(CORRECT_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.next_button_register_username)).perform(click());

        // Choose avatar
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.choose_avatar_button)).perform(click());

        // Type password and then press the button
        onView(withId(R.id.password_register))
                .perform(typeText(WEAK_PASSWORD), closeSoftKeyboard());

    }

    @Test
    public void notMatchingPassword_RegistrationTest() {
        // Type username and then press the button
        onView(withId(R.id.username_register))
                .perform(typeText(CORRECT_USERNAME), closeSoftKeyboard());
        onView(withId(R.id.next_button_register_username)).perform(click());

        // Choose avatar
        onView(withId(R.id.avatar_picker)).perform(swipeLeft());
        onView(withId(R.id.choose_avatar_button)).perform(click());

        // Type password and then press the button
        onView(withId(R.id.password_register))
                .perform(typeText(PASSWORD), closeSoftKeyboard());
        onView(withId(R.id.confirmation_register))
                .perform(typeText(NOT_MATCHING_PASS), closeSoftKeyboard());
        onView(withId(R.id.button_create_account)).perform(click());
    }


}