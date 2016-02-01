package it.polimi.acclog;

import android.test.ActivityInstrumentationTestCase2;

/**
 * This is a simple framework for a test of an Application.  See
 * {@link android.test.ApplicationTestCase ApplicationTestCase} for more information on
 * how to write and extend Application tests.
 * <p/>
 * To run this test, you can type:
 * adb shell am instrument -w \
 * -e class it.polimi.acclog.AccLogActivityTest \
 * it.polimi.acclog.tests/android.test.InstrumentationTestRunner
 */
public class AccLogActivityTest extends ActivityInstrumentationTestCase2<AccLogActivity> {

    public AccLogActivityTest() {
        super("it.polimi.acclog", AccLogActivity.class);
    }

}
