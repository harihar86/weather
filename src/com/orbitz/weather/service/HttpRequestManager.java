package com.orbitz.weather.service;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class HttpRequestManager {

    public String getResponse(String requestUrl) throws IOException {
        InputStream is = null;
        try {

            HttpURLConnection connection = getConnection(requestUrl);
            is = connection.getInputStream();

            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }

            return builder.toString();

        } catch (Exception e) {
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    public Bitmap getBitmapResponse(String requestUrl) throws IOException {
        InputStream is = null;
        try {
            HttpURLConnection connection = getConnection(requestUrl);
            is = connection.getInputStream();

            Bitmap bitmap = BitmapFactory.decodeStream(is);

            return bitmap;

        } catch (Exception e) {
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private HttpURLConnection getConnection(String requestUrl) throws Exception {
        URL url = new URL(requestUrl);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setReadTimeout(1000);
        connection.setConnectTimeout(15000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept-Encoding", "");
        connection.setDoInput(true);
        connection.connect();

        return connection;
    }
}

