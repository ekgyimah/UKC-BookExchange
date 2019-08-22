package kent.ukc_book_exchange;

import android.net.Uri;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Author: Emmanuel Gyimah
 * Class to connect to google API
 * Performs book query based on the ISBN value scanned by camera
 */

public class Network {

    //API URL
    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?";

    //Parameter to add to baseURL to perform query
    private static final String PARAMETER = "q";

    //Parameter to add to baseURL to set amount of result to be returned
    private static final String MAX_RES = "maxResults";

    //Parameter to add to baseURL to make query based on isbn
    private static final String ISBN = "isbn:";
    //private static final String API_KEY = "key";

    //Parameter to add to baseURL to set type of result
    private static final String TYPE = "printType";


    public static String getBookInfo(String queryString)
    {
        HttpURLConnection urlConnection = null;
        String bookJSONString;
        BufferedReader reader = null;

        //built up query with parameters
        try {

            //Add parameters value to the baseURL to perform query
            Uri uri = Uri.parse(BOOK_BASE_URL).buildUpon()
                    .appendQueryParameter(PARAMETER,ISBN + queryString )
                    .appendQueryParameter(MAX_RES, "1")
                    .appendQueryParameter(TYPE,"books")
                    .build();

            //Create new URL and pass it the uri
            URL requestURl = new URL(uri.toString());


            //Perform HTTP request using "GET" method with URL address
            urlConnection = (HttpURLConnection) requestURl.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            //Result of the query
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            //If no result returned, return null
            if (inputStream == null){
                return null;
            }

            //Read result of query
            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            //Restructure result of query
            //Append each line to StringBuilder "buffer"
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                return null;
            }

            //Convert result into string
            bookJSONString = buffer.toString();
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        finally {
            //Close connection
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    //Close "reader"
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }

        //Return result from query converted to string
        return bookJSONString;
    }

    }

