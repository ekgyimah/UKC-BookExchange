import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Class to automatically populate the firebase database with books based on their module code.
 */
public class Main {

    private static FirebaseDatabase booksfirebase;
    private static DatabaseReference firebaseDatabaseReference ;

    private String jsonFile = "";
    private String bookListHandle = "";

    private String JSONbookListpointer = "http://purl.org/vocab/resourcelist/schema#contains";
    private ArrayList<String> books = new ArrayList<>();
    private ArrayList<String> tempKeys = new ArrayList<>();
    private ArrayList<String> isbnHolder = new ArrayList<>();

    private static final String BASE_URL = "http://resourcelists.kent.ac.uk/search.json?q=";
    private String LINK_URL = "";


    /**
     * Constructor to connect to firebase database
     */
    public Main(){

        FileInputStream serviceAccount = null;
        try {
            serviceAccount = new FileInputStream("C:\\Users\\Emmy\\Desktop\\Uni stuff\\dbPopulator\\src\\ukc-book-exchange-firebase-adminsdk-ykyie-f2a1f68fe2.json");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        FirebaseOptions options = null;
        try {
            options = new FirebaseOptions.Builder()
                    .setCredentials(GoogleCredentials.fromStream(serviceAccount))
                    .setDatabaseUrl("https://ukc-book-exchange.firebaseio.com/")
                    .build();
        } catch (IOException e) {
            e.printStackTrace();
        }

        FirebaseApp.initializeApp(options);

        booksfirebase = FirebaseDatabase.getInstance();
        firebaseDatabaseReference = booksfirebase.getReference();
    }

    /**
     * Method to make HTTP request using the url.
     * Searches for reading list available for specific module.
     * @param url: url for HTTP request
     * @param module: Module to be searched
     * @return response to the request in JSON format
     */
    private String getModuleBookList(String url, String module){

        HttpURLConnection urlConnection = null;
        String listJson = "";
        BufferedReader reader = null;

        try {
            URI uri = new URI(url.concat(module));

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

        jsonFile = listJson;

        return listJson;
    }

    /**
     * Parse JSON file returned and gets link for the 2017/2018 academic year
     * reading list.
     * Uses link to make another HTTP request using "getModuleBookList" method.
     */
    public void getList()
    {
        JSONObject result = new JSONObject(jsonFile);

        JSONObject lists = result.getJSONObject("lists");
        JSONArray res = lists.getJSONArray("results");

        for(int i = 0; i < res.length();i++)
        {
            JSONObject object = res.getJSONObject(i);

            if(object.getString("timePeriodTitle").equals("2017-2018"))
            {
                String linkUrl = object.getString("link");

                bookListHandle = object.getString("link").substring(object.getString("link").indexOf("s/")+2);

                LINK_URL = linkUrl;
            }

            else
            {}
        }

        getModuleBookList(LINK_URL,".json");
    }


    /**
     * Parse JSON file returned by getList() method.
     * Gets book in the reading list and for each book in the reading list
     * it gets its details by using "getBookDetails()" method.
     * The books are in form of a link.
     */
    public void getBookList()
    {
        int iterations = 0;
        JSONObject result = new JSONObject(jsonFile);

        JSONObject lists = result.getJSONObject("http://resourcelists.kent.ac.uk/lists/" + bookListHandle);
        JSONArray res = lists.getJSONArray(JSONbookListpointer);

        if(res.length()>5)
        {
            iterations = 5;
        }

        else
        {
        }

        for(int i = 0; i < iterations;i++)
        {
            JSONObject object = res.getJSONObject(i);

            if(object.getString("value").contains("sections"))
            {

            }
            else
            {
                books.add(object.getString("value"));
            }
        }

        for(String handle: books)
        {
            getBookDetails(handle );
        }
    }


    /**
     * Makes HTTP request with books link using getModuleBookList.
     * Parse the JSON file returned to get the isbn code of the book.
     * Add ISBN code found into Arraylist
     * @param handle: book link
     */
    public void getBookDetails(String handle)
    {
        JSONObject result = new JSONObject(getModuleBookList(handle, ".json"));
        String endPoint = "";

        Iterator<String> resources = result.keys();

        while (resources.hasNext())
        {
            String temp = resources.next();
            if(!(temp.contains("authors")||temp.contains("editors")))
            {
                tempKeys.add(temp);
            }
            else
            {

            }
        }

        //System.out.println(tempKeys.size());

        for(String link : tempKeys)
        {
            if(link.contains("http://resourcelists.kent.ac.uk/resources/"))
            {
                endPoint = link;
            }
        }

        JSONObject bookdetails = result.getJSONObject(endPoint);
        JSONArray isbnHandle;

        try {
            isbnHandle = bookdetails.getJSONArray("http://purl.org/ontology/bibo/isbn13");
        }
        catch (JSONException e)
        {
           isbnHandle = bookdetails.getJSONArray("http://purl.org/ontology/bibo/isbn10");
        }

        for(int i=0; i < isbnHandle.length(); i++)
        {
            JSONObject isbn = isbnHandle.getJSONObject(i);
            isbnHolder.add(isbn.getString("value"));

            //System.out.println(isbn.getString("value"));

        }

        //System.out.println(isbnHolder.size());
        tempKeys.clear();
    }

    /**
     * Make google books API HTTP request for each element in the "isbnHolder" Arraylist.
     * Parse the json returned to get Book: Author, Title and published date.
     * It then push the information onto the firebase database
     */
    public void getISBN() {
        for (String pos : isbnHolder) {

            Network.getModuleBookList(pos);

            try {
                JSONObject jsonObject = new JSONObject(Network.getModuleBookList(pos));
                JSONArray itemsArray = jsonObject.getJSONArray("items");

                for (int i = 0; i < itemsArray.length(); i++) {

                    JSONObject book = itemsArray.getJSONObject(i);

                    String title = null;
                    String authors = null;
                    String edition = null;

                    JSONObject bookInfo = book.getJSONObject("volumeInfo");
                    JSONArray author = bookInfo.getJSONArray("authors");

//                    try {
//
//                        authors = author.toString();
//                        title = bookInfo.getString("title");
//                        edition = bookInfo.getString("publishedDate");
//
//                        String key = firebaseDatabaseReference.push().getKey();
//
//                        BookInfo bookDetails = new BookInfo(title,authors,edition,"History",
//                                "HI391","ukcbookexchange@gmail.com","000000");
//
//                        firebaseDatabaseReference.child("User").child("ZTv6feWECNO8paAY4NpZGycULDX2")
//                                .child("Books").child(key).setValueAsync(bookDetails);
//                        firebaseDatabaseReference.child("Books").child(key).setValueAsync(bookDetails);
//
//                    } catch (Exception e) {
//                        e.printStackTrace();
//                    }

                    System.out.println(bookInfo);

                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args){
        Main m = new Main();

        m.getModuleBookList(BASE_URL,"HI391");
        m.getList();
        m.getBookList();
        m.getISBN();
    }

}



