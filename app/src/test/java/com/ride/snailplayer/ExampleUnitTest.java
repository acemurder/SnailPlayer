package com.ride.snailplayer;

import org.junit.Test;

import java.util.regex.Pattern;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {

    private static final Pattern PATTERN = Pattern.compile("_m_((?:_?\\d+)+)");

    private static final String URL = "http://pic8.qiyipic.com/image/20160701/2f/bd/v_110609941_m_601_180_236.jpg";

    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);


    }
}