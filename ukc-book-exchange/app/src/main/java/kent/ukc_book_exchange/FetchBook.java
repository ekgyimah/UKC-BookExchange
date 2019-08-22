package kent.ukc_book_exchange;

import android.os.AsyncTask;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONObject;

/**
 * Author: Emmanuel Gyimah
 * Class to fetch JSON and parse it from HTTPS request made in Network.getBookInfo()method
 * Runs as a background thread
 */

public class FetchBook extends AsyncTask<String, Void, String>{

    //TextViews to be set
    TextView bTitle;
    TextView bAuthor;
    TextView bEdition;

    FetchBook(TextView bAuthor, TextView bTitle, TextView bEdition)
    {
        this.bAuthor = bAuthor;
        this.bTitle = bTitle;
        this.bEdition = bEdition;
    }

    @Override
    protected String doInBackground(String... strings) {
        return Network.getBookInfo(strings[0]);
    }

    @Override
    protected void onPostExecute(String s) {
        super.onPostExecute(s);

        try {
            //Create new JSONObject
            JSONObject jsonObject = new JSONObject(s);

            //Gets JSON array in JSON "items" object from reply
            JSONArray items = jsonObject.getJSONArray("items");

            //For loop to iterate over "items" array
            for (int i = 0; i < items.length(); i++) {

                //Get JSONObject in "items"
                JSONObject book = items.getJSONObject(i);
                String title = null;
                String author = null;
                String edition = null;

                //Get JSONObject in "volumeInfo" from reply
                JSONObject bookInfo = book.getJSONObject("volumeInfo");

                try {

                    //Get title, author and edition from "bookInfo" object
                    //Assign them to corresponding variables
                    title = bookInfo.getString("title");
                    author = bookInfo.getString("authors");
                    edition = bookInfo.getString("publishedDate");
                } catch (Exception e) {
                    e.printStackTrace();
                }

                //Set textView
                if (title != null && author != null && edition != null) {
                    bAuthor.setText(author);
                    bTitle.setText(title);
                    bEdition.setText(edition);
                }

            }
        }catch (Exception e)
        {
            //If no results returned, set textViews to empty
            bAuthor.setText("");
            bTitle.setText("");
            bEdition.setText("");

            e.printStackTrace();
        }
    }
}
