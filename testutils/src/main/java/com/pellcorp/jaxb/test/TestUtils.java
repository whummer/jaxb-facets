package com.pellcorp.jaxb.test;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;

public final class TestUtils {
    private TestUtils() {
    }

    public static String readFile(File file) throws IOException {
    	return readStream(new FileInputStream(file));
    }

    public static String readFile(String file) throws IOException {
    	return readStream(new FileInputStream(file));
    }

    public static String readURL(String url) throws IOException {
        return readStream(new URL(url).openStream());
    }
    
    public static String readStream(InputStream is) throws IOException {
        BufferedReader r = new BufferedReader(new InputStreamReader(is));
        String t = null;
        String c = "";
        while((t = r.readLine()) != null) {
                c += t + "\n";
        }
        return c;
    }
}
