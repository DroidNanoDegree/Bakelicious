package com.sriky.bakelicious;

import android.content.ContentValues;

import com.sriky.bakelicious.utils.BakeliciousUtils;

import org.junit.Test;

import java.util.Collection;

import static org.junit.Assert.*;

/**
 * Class to test {@link BakeliciousUtils} methods.
 */
public class BakeliciousUtilsTest {

    /* empty constructor required */
    public BakeliciousUtilsTest() {}

    /**
     * Test {@link BakeliciousUtils#getContentValues(Collection, int)} when collection is empty.
     * Method should return null as expected result.
     */
    @Test
    public void getContentValues_testInvalidInput() {
        ContentValues[] result = BakeliciousUtils.getContentValues(null, 0);

        assertTrue("Return invalid for null input!", null == result);
    }

    /*
    @Test
    public void getContentValues_testValidateContentValuesForRecipes() {
        assertEquals(4, 2 + 2);
    }
    */
}