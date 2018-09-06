package com.lawulu;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.net.URL;

public class SecureSocket {
    static {
        // System.setProperty("javax.net.debug", "all");
        System.setProperty("https.protocols", "TLSv1,TLSv1.1,TLSv1.2");
    }
    public static void main(String[] args) {
        String GhitHubSSLFile = "https://github.com/code4craft";
        try {
            String str = readCloudFileAsString(GhitHubSSLFile);
            // new String(Files.readAllBytes(Paths.get( "D:/Sample.file" )));

            System.out.println("Cloud File Data : "+ str);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static String readCloudFileAsString( String urlStr ) throws java.io.IOException {
        if( urlStr != null && urlStr != "" ) {
            java.io.InputStream s = null;
            String content = null;
            try {
                URL url = new URL( urlStr );
                s = (java.io.InputStream) url.getContent();
                content = IOUtils.toString(s, "UTF-8");
            } finally {
                if (s != null) s.close();
            }
            return content.toString();
        }
        return null;
    }
}