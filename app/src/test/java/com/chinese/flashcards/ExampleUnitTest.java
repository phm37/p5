package com.chinese.flashcards;

import android.provider.MediaStore;

import com.chinese.flashcards.services.DataService;

import org.junit.Test;

import java.io.BufferedReader;
import java.io.FileReader;
import java.util.List;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() throws Exception {
        assertEquals(4, 2 + 2);
    }

    @Test
    public void dataServiceDownload() throws Exception {
       // DataService service = new DataService();
       // BufferedReader br = new BufferedReader(new FileReader("C:\\Users\\haneymansour\\Documents\\chidi.txt"));
       // String line = br.readLine();
       // String[] data = line.split(",");
    }
}