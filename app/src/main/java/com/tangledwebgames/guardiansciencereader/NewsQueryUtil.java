package com.tangledwebgames.guardiansciencereader;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class NewsQueryUtil {

    private static final String LOG_TAG = NewsQueryUtil.class.getSimpleName();

    private static final int READ_TIMEOUT_TIME = 10000;
    private static final int CONNECT_TIMEOUT_RIME = 15000;

    private static final String HTTP_SCHEME = "http";
    private static final String GUARDIAN_BASE_URL = "content.guardianapis.com";
    private static final String GUARDIAN_CONTENT_ENDPOINT = "search";
    private static final String API_KEY_QUERY = "api-key";
    private static final String SECTION_QUERY = "section";
    private static final String PAGE_SIZE_QUERY = "page-size";
    private static final String SHOW_FIELDS_QUERY = "show-fields";
    private static final String TAGS_QUERY = "show-tags";
    private static final String SCIENCE_SECTION = "science";
    private static final String TECHNOLOGY_SECTION = "technology";
    private static final String HEADLINE_FIELD = "headline";
    private static final String TRAIL_TEXT_FIELD = "trailText";
    private static final String THUMBNAIL_FIELD = "thumbnail";
    private static final String CONTRIBUTOR_FIELD = "contributor";

    private static final String RESPONSE_KEY = "response";
    private static final String RESULTS_ARRAY_KEY = "results";
    private static final String ARTICLE_URL_KEY = "webUrl";
    private static final String PUBLICATION_DATE_KEY = "webPublicationDate";
    private static final String FIELDS_KEY = "fields";
    private static final String HEADLINE_KEY = "headline";
    private static final String TRAIL_TEXT_KEY = "trailText";
    private static final String THUMBNAIL_URL_KEY = "thumbnail";
    private static final String TAGS_ARRAY_KEY = "tags";
    private static final String AUTHOR_NAME_KEY = "webTitle";

    private static final int ARTICLES_PER_QUERY = 15;

    private static final String API_KEY_FILEPATH = "api_key.txt";

    private static SimpleDateFormat dateFormat;

    static String apiKey;

    static List<Article> getNewsData() {
        Uri.Builder builder = new Uri.Builder();
        builder.scheme(HTTP_SCHEME)
                .authority(GUARDIAN_BASE_URL)
                .appendPath(GUARDIAN_CONTENT_ENDPOINT)
                .appendQueryParameter(API_KEY_QUERY, apiKey)
                .appendQueryParameter(SECTION_QUERY, SCIENCE_SECTION + "|" + TECHNOLOGY_SECTION)
                .appendQueryParameter(SHOW_FIELDS_QUERY,
                        HEADLINE_FIELD + "," + TRAIL_TEXT_FIELD + "," + THUMBNAIL_FIELD
                ).appendQueryParameter(PAGE_SIZE_QUERY, ARTICLES_PER_QUERY + "")
                .appendQueryParameter(TAGS_QUERY, CONTRIBUTOR_FIELD);
        Log.v(LOG_TAG, "Accessing Guarding API at " + builder.build().toString());
        URL url;
        try {
            url = new URL(builder.build().toString());
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error forming URL, unable to send query.", e);
            return null;
        }

        String jsonResponse;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error making HTTP request.", e);
            return null;
        }

        try {
            return parseJsonResponse(jsonResponse);
        } catch (JSONException e) {
            Log.e(LOG_TAG, "Error parsing JSON response.", e);
            return null;
        }
    }

    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection connection = null;
        InputStream inputStream = null;
        try {
            connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(READ_TIMEOUT_TIME);
            connection.setConnectTimeout(CONNECT_TIMEOUT_RIME);
            connection.setRequestMethod("GET");
            connection.connect();

            if (connection.getResponseCode() == 200) {
                inputStream = connection.getInputStream();
                jsonResponse = readJsonResponse(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + connection.getResponseCode());
                connection.disconnect();
                return null;
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error attempting to make HTTP connections.", e);
            return null;
        } finally {
            if (connection != null) {
                connection.disconnect();
            }
            if (inputStream != null) {
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    private static String readJsonResponse(InputStream inputStream) throws IOException {
        if (inputStream == null) {
            return "";
        }

        StringBuilder output = new StringBuilder();
        BufferedReader reader = new BufferedReader(
                new InputStreamReader(inputStream, Charset.forName("UTF-8"))
        );
        String line = reader.readLine();
        while (line != null) {
            output.append(line);
            line = reader.readLine();
        }
        return output.toString();
    }

    private static List<Article> parseJsonResponse(String jsonResponse) throws JSONException {
        if (TextUtils.isEmpty(jsonResponse)) {
            Log.e(LOG_TAG, "Query received empty response.");
            return null;
        }

        List<Article> articles = new ArrayList<>();

        JSONArray resultArray = new JSONObject(jsonResponse)
                .getJSONObject(RESPONSE_KEY)
                .getJSONArray(RESULTS_ARRAY_KEY);
        for (int i = 0; i < resultArray.length(); i++) {
            Article article = new Article();
            JSONObject articleJson = resultArray.getJSONObject(i);
            if (articleJson.has(ARTICLE_URL_KEY))
                article.articleUrl = articleJson.getString(ARTICLE_URL_KEY);
            if (articleJson.has(PUBLICATION_DATE_KEY))
                article.publicationDate = parsePublicationDate(articleJson.getString(PUBLICATION_DATE_KEY));
            JSONObject fieldsJson = articleJson.getJSONObject(FIELDS_KEY);
            if (fieldsJson.has(HEADLINE_KEY))
                article.headline = fieldsJson.getString(HEADLINE_KEY);
            if (fieldsJson.has(TRAIL_TEXT_KEY))
                article.trailText = fieldsJson.getString(TRAIL_TEXT_KEY);
            if (fieldsJson.has(THUMBNAIL_URL_KEY))
                article.thumbnailUrl = fieldsJson.getString(THUMBNAIL_URL_KEY);
            JSONArray tagsArray = articleJson.getJSONArray(TAGS_ARRAY_KEY);
            String[] authors = new String[tagsArray.length()];
            for (int j = 0; j < authors.length; j++) {
                JSONObject tagObject = tagsArray.getJSONObject(j);
                if (tagObject.has(AUTHOR_NAME_KEY))
                    authors[j] = tagObject.getString(AUTHOR_NAME_KEY);
            }
            article.authors = Article.getAuthorList(authors);
            articles.add(article);
        }

        Log.v(LOG_TAG, "Finished parsing Guardian API JSON response.");
        return articles;
    }

    private static String parsePublicationDate(String dateString) {

        return dateString;
    }

    static void loadApiKey(Context context) throws IOException {
        InputStream apiKeyFile = context.getResources().openRawResource(R.raw.api_key);
        StringBuilder builder = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(apiKeyFile));
        String line;
        while ((line = bufferedReader.readLine()) != null) {
            builder.append(line);
        }
        apiKey = builder.toString();
    }

}
