package ro.danserboi.quotesformindandsoul;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkUtils {

    // Base URL for Books API.
    private static final String BOOK_BASE_URL = "https://quote-garden.herokuapp.com/api/v3/quotes";
    // Query parameter representing the limit number of quotes
    static final String QUERY_LIMIT_PARAM = "limit";
    // Query parameter representing the author
    static final String QUERY_AUTHOR_PARAM = "author";
    // Query parameter representing the genre
    static final String QUERY_GENRE_PARAM = "genre";

    static String getQuotes(Pair<String, String> query) {
        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;
        String bookJSONString = null;
        try {
            Uri builtURI = query.first.equals("random") ?
                    Uri.parse(BOOK_BASE_URL + "/random")
                    :
                    Uri.parse(BOOK_BASE_URL).buildUpon()
                            .appendQueryParameter(query.first, query.second)
                            .appendQueryParameter(QUERY_LIMIT_PARAM, "200")
                            .build();
            URL requestURL = new URL(builtURI.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // Get the InputStream.
            InputStream inputStream = urlConnection.getInputStream();

            // Create a buffered reader from that input stream.
            reader = new BufferedReader(new InputStreamReader(inputStream));

            // Use a StringBuilder to hold the incoming response.
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                builder.append(line);
            }
            if (builder.length() == 0) {
                // Stream was empty. No point in parsing.
                return null;
            }
            bookJSONString = builder.toString();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return bookJSONString;
    }

    static boolean isConnected(Context context) {
        ConnectivityManager connMgr = (ConnectivityManager)
                context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (connMgr != null) {
            networkInfo = connMgr.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnected();
    }
}