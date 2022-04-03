package ro.danserboi.quotesformindandsoul

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkInfo
import android.net.Uri
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.StringBuilder
import java.net.HttpURLConnection
import java.net.URL

object NetworkUtils {
    // Base URL for Books API.
    private const val BOOK_BASE_URL = "https://quote-garden.herokuapp.com/api/v3/quotes"

    // Query parameter representing the limit number of quotes
    private const val QUERY_LIMIT_PARAM = "limit"

    // Query parameter representing the author
    const val QUERY_AUTHOR_PARAM = "author"

    // Query parameter representing the genre
    const val QUERY_GENRE_PARAM = "genre"

    @JvmStatic
    fun getQuotes(query: Pair<String?, String?>): String? {
        var urlConnection: HttpURLConnection? = null
        var reader: BufferedReader? = null
        var bookJSONString: String? = null
        try {
            val builtURI: Uri = if (query.first == "random") Uri.parse(BOOK_BASE_URL + "/random") else Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(query.first, query.second)
                    .appendQueryParameter(QUERY_LIMIT_PARAM, "200")
                    .build()
            val requestURL = URL(builtURI.toString())
            urlConnection = requestURL.openConnection() as HttpURLConnection
            urlConnection.requestMethod = "GET"
            urlConnection.connect()

            // Get the InputStream.
            val inputStream: InputStream = urlConnection.inputStream

            // Create a buffered reader from that input stream.
            reader = BufferedReader(InputStreamReader(inputStream))

            // Use a StringBuilder to hold the incoming response.
            val builder = StringBuilder()
            var line: String?
            while ((reader.readLine().also { line = it }) != null) {
                builder.append(line)
            }
            if (builder.isEmpty()) {
                // Stream was empty. No point in parsing.
                return null
            }
            bookJSONString = builder.toString()
        } catch (e: IOException) {
            e.printStackTrace()
        } finally {
            urlConnection?.disconnect()
            if (reader != null) {
                try {
                    reader.close()
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }
        }
        return bookJSONString
    }

    @JvmStatic
    fun isConnected(context: Context): Boolean {
        val connMgr: ConnectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkInfo: NetworkInfo? = connMgr.activeNetworkInfo
        return networkInfo != null && networkInfo.isConnected
    }
}