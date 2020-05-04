package com.example.photogalleryapp;
import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.provider.MediaStore;
import android.view.KeyEvent;

import androidx.test.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import androidx.test.rule.ActivityTestRule;
import androidx.test.uiautomator.UiDevice;
import androidx.test.uiautomator.UiSelector;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressKey;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.Intents.intended;
import static androidx.test.espresso.intent.Intents.intending;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasAction;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.intent.matcher.IntentMatchers.toPackage;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static junit.framework.TestCase.assertEquals;
import static org.hamcrest.Matchers.equalTo;


@RunWith(AndroidJUnit4.class)
public class PhotoTaking {
    @Rule
    public ActivityTestRule<MainActivity> rule
            = new ActivityTestRule<>(MainActivity.class);
    @Rule
    public ActivityTestRule<GalleryActivity> rule1
            = new ActivityTestRule<>(GalleryActivity.class);
//    @Test
//    public void camera() {
////        // Now that we have the stub in place, click on the button in our app that launches into the Camera
//        onView(withId(R.id.item_camera)).perform(click());
//    }
//    @Test
//    public void changePhotoName() {
//        onView(withId(R.id.item_edit)).perform(click());
//        onView(withId(R.id.editText)).perform(replaceText("Test"));
//        onView(withId(R.id.button4)).perform(click());
//        onView(withId(R.id.textView)).check(matches(withText("Test")));
//    }
    @Test
    public void filter(){
        onView(withId(R.id.item_search)).perform(click());
        onView(withId(R.id.button2)).perform(click());
        onView(withId(R.id.button)).perform(click());
        onView(withId(R.id.text_keyword)).perform(click(),typeText("Ken"));
        onView(withId(R.id.button_confirm)).perform(click());
    }
}
