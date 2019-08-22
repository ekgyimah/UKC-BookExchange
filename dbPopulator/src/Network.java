
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;

/**
 * Class to make google books API HTTP request
 */
public class Network {

    private static final String BOOK_BASE_URL = "https://www.googleapis.com/books/v1/volumes?q=isbn:";

    public static String getModuleBookList(String queryString)
    {
        HttpURLConnection urlConnection = null;
        String listJson = "";
        BufferedReader reader = null;

        try {
            URI uri = new URI(BOOK_BASE_URL.concat(queryString).concat("&").concat("maxResult=1"));

            URL requestURL = new URL(uri.toString());

            urlConnection = (HttpURLConnection) requestURL.openConnection();
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();
            InputStream inputStream = urlConnection.getInputStream();
            StringBuilder buffer = new StringBuilder();

            if (inputStream == null){
                return null;
            }

            reader = new BufferedReader(new InputStreamReader(inputStream));

            String line;

            //
            while((line = reader.readLine()) != null) {
                buffer.append(line + "\n");
            }

            if (buffer.length() == 0)
            {
                return null;
            }

            listJson = buffer.toString();

        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }

        finally {
            if (urlConnection != null){
                urlConnection.disconnect();
            }
            if(reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

        }
        System.out.println(listJson + "JSON file");

        return listJson;
    }

}
