package com.tangledwebgames.guardiansciencereader;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

public class ThumbnailLoadTask extends AsyncTask<String, Void, Bitmap> {

    private static final int READ_TIMEOUT_TIME = 10000;
    private static final int CONNECT_TIMEOUT_RIME = 15000;

    protected ViewGroup thumbnailGroup;

    public ThumbnailLoadTask(ViewGroup thumbnailGroup) {
        super();
        this.thumbnailGroup = thumbnailGroup;
    }

    private static final String LOG_TAG = ThumbnailLoadTask.class.getSimpleName();

    @Override
    protected void onPreExecute() {
        thumbnailGroup.findViewById(R.id.thumbnail).setVisibility(View.GONE);
        thumbnailGroup.findViewById(R.id.no_thumbnail_textview).setVisibility(View.GONE);
        thumbnailGroup.findViewById(R.id.thumbnail_loading_indicator).setVisibility(View.VISIBLE);
    }

    @Override
    protected Bitmap doInBackground(String... strings) {
        if (strings.length == 0) {
            Log.d(LOG_TAG, "No URL provided for thumbnail.");
            return null;
        }
        URL url;
        try {
            url = new URL(strings[0]);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error forming thumbnail URL, unable to send query.", e);
            return null;
        }
        try {
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
                } else {
                    Log.e(LOG_TAG, "Error response code: " + connection.getResponseCode());
                    connection.disconnect();
                    return null;
                }
            } catch (IOException e) {
                Log.e(LOG_TAG, "Error attempting to make HTTP connection to fetch thumbnail.", e);
                return null;
            }
            Bitmap bitmap = null;
            if (inputStream != null) {
                bitmap = BitmapFactory.decodeStream(inputStream);
                inputStream.close();
            }
            connection.disconnect();
            return bitmap;
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error reading input stream for thumbnail.", e);
            return null;
        }
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        thumbnailGroup.findViewById((R.id.thumbnail_loading_indicator)).setVisibility(View.GONE);
        if (bitmap == null) {
            ((TextView) thumbnailGroup.findViewById(R.id.no_thumbnail_textview))
                    .setText(R.string.no_thumbnail);
        } else {
            ImageView thumbnail = thumbnailGroup.findViewById(R.id.thumbnail);
            thumbnail.setImageBitmap(bitmap);
            thumbnail.setVisibility(View.VISIBLE);
        }
    }
}
