package com.tangledwebgames.guardiansciencereader;

import android.util.Log;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class QueryUtil {

    private static final String LOG_TAG = QueryUtil.class.getSimpleName();

    private static final int READ_TIMEOUT_TIME = 10000;
    private static final int CONNECT_TIMEOUT_RIME = 15000;

    static InputStream makeHttpRequest(URL url) throws IOException {
        HttpURLConnection connection;
        InputStream inputStream;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT_TIME);
            connection.setConnectTimeout(CONNECT_TIMEOUT_RIME);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                return inputStream;
            } else {
                Log.e(LOG_TAG, "Error response code: " + connection.getResponseCode());
                connection.disconnect();
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error attempting to make HTTP connections.", e);
            return null;
        }
    }
}
