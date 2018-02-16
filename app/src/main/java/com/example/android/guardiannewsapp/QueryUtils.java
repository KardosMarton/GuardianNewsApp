package com.example.android.guardiannewsapp;


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
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class QueryUtils {

    /** Tag for the log messages */
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    //Guardian API Keys
    private static final String RESPONSE = "response";
    private static final String RESULTS = "results";
    private static final String WEB_TITLE = "webTitle";
    private static final String SECTION_NAME = "sectionName";
    private static final String WEB_PUBLICATION_DTAE = "webPublicationDate";
    private static final String  WEB_URL = "webUrl";
    private static final String  TAGS ="tags";

    //In case something does not have a value use this string as value
    private static final String NOT_AVAILABLE = "Not available";

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }


    private static List<News> extractFeatureFromJson(String newsesJSON){

        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(newsesJSON)) {
            return null;
        }
        // Create an empty ArrayList that we can start adding newses to
        List<News> newses = new ArrayList<>();

        // Try to parse the JSON response string.
        try {
            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(newsesJSON);

            JSONObject jsonResults = baseJsonResponse.getJSONObject(RESPONSE);

            //JSONArray newsArray = baseJsonResponse.getJSONArray("response");
            JSONArray  newsArray = jsonResults.getJSONArray(RESULTS);

            //Loop through each results in the newsArray array
            for (int i = 0; i<newsArray.length(); i++){

                //Get a single news at position i within the list of news
                JSONObject currentNews = newsArray.getJSONObject(i);

                //String news title:
                String newsTitle = "";
                if (currentNews.has(WEB_TITLE)){
                    newsTitle = currentNews.getString(WEB_TITLE);
                } else{
                    newsTitle = NOT_AVAILABLE;
                }

                //String section name
                String sectionName = "";
                if (currentNews.has(SECTION_NAME)){
                    sectionName = currentNews.getString(SECTION_NAME);
                } else{
                    sectionName = NOT_AVAILABLE;
                }

                //String web publication date
                String webPublicDate = "";
                if (currentNews.has(WEB_PUBLICATION_DTAE)){
                    webPublicDate = formatDate(currentNews.getString(WEB_PUBLICATION_DTAE));
                } else{
                    webPublicDate = NOT_AVAILABLE;
                }

                //String web url
                String webUrl = "";
                if (currentNews.has(WEB_URL)){
                    webUrl = currentNews.getString(WEB_URL);
                } else{
                    webUrl = NOT_AVAILABLE;
                }

                //String author's name
                String authorName = "";
                JSONArray tagsPropertie = currentNews.getJSONArray(TAGS);
                if(tagsPropertie.length() > 0){
                    for (int j = 0; j < tagsPropertie.length(); j++) {
                        JSONObject tagsObject = tagsPropertie.getJSONObject(j);
                        authorName = tagsObject.getString(WEB_TITLE);
                    }
                } else{
                    authorName = NOT_AVAILABLE;
                }

                //public News(String sectionName, String webPublicationDate, String webTitle, String webUrl, String authorsName)
                News news = new News(sectionName, webPublicDate, newsTitle, webUrl, authorName);

                newses.add(news);


            }

        } catch (JSONException e){
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e("QueryUtils", "Problem parsing the news JSON results", e);
        }



        //Return the list of newses
        return newses;
    }


    /**
     * Query the GUARDIAN dataset and return a list of {@link News} objects.
     */
    public static List<News> fetchNewsData(String requestUrl){

        //Wait a little bit
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link News}s
        List<News> newses = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link News}s
        return newses;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }

    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the news JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    //Create a new dateformat date string
    private static String formatDate(String rawDate) {
        String jsonDatePattern = "yyyy-MM-dd'T'HH:mm:ss'Z'";
        SimpleDateFormat jsonFormatter = new SimpleDateFormat(jsonDatePattern, Locale.US);
        try {
            Date parsedJsonDate = jsonFormatter.parse(rawDate);
            String finalDatePattern = "d.MMM.yyy : HH:mm:ss";
            SimpleDateFormat finalDateFormatter = new SimpleDateFormat(finalDatePattern, Locale.US);
            return finalDateFormatter.format(parsedJsonDate);
        } catch (ParseException e) {
            Log.e(LOG_TAG, "Error parsing JSON Date: ", e);
            return "";
        }
    }


}
