package com.skylan.allinweather;

import android.net.Uri;

import org.junit.Test;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest  {
    @Test
    public void addition_isCorrect() {
        Uri uri = Uri.parse("content://com.skylan.allinweather.provider/Province");
        List<String> list = uri.getPathSegments();
        for(String s :list) {
            System.out.println(s);
        }
        System.out.println("----------------------");
        System.out.println(uri.getPath());
    }
}