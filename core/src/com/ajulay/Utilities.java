package com.ajulay;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

import java.io.BufferedReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by FlameXander on 13.05.2018.
 */

public class Utilities {
    public static List<String> readAllLinesFromFile(FileHandle fileHandle) {
        List<String> lines = new ArrayList<String>();
        BufferedReader br = null;
        try {
            br = fileHandle.reader(8192);
            String str;
            while ((str = br.readLine()) != null) {
                lines.add(str);
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return lines;
    }
}
