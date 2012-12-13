package com.pellcorp.jaxb.test;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;

import org.apache.commons.io.IOUtils;

public final class ResourceUtils {
    private static final String DEFAULT_CHARSET = "UTF-8";

    private ResourceUtils() {
    }

    public static Reader loadResourceAsReader(String path) throws IOException {
            InputStream is = loadResourceAsInputStream(path);
            return new InputStreamReader(is, DEFAULT_CHARSET);
    }

    public static String loadResourceAsString(String path) throws IOException {
        InputStream is = loadResourceAsInputStream(path);
        return IOUtils.toString(is, DEFAULT_CHARSET);
    }
    
    public static InputStream loadResourceAsInputStream(String path) throws IOException {
            InputStream is = ResourceUtils.class.getResourceAsStream(path);
            if (is != null) {
                    ByteArrayInputStream inputStream = new ByteArrayInputStream(IOUtils.toByteArray(is));
                    IOUtils.closeQuietly(is);
                    return inputStream;
            } else {
                    throw new IOException("Resource not found: " + path);
            }
    }
}