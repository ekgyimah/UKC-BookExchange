package kent.ukc_book_exchange;


import android.os.Bundle;

import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;

import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Authors: Khadija Ali
 * Class to display the searched books retrieved from Firebaseatabase
 */
public class SearchActivity extends AppCompatActivity {
    //The search bar
    EditText searchBar;

    //Firebase user variable
    FirebaseUser firebaseUser;

    //Arraylist to store books of type BookInfo
    ArrayList<BookInfo> bookNameList;

    //Search adapter
    SearchAdapter searchAdapter;

    //Database reference variable
    private DatabaseReference mFirebaseDatabaseReference;

    //Recycler view in order to display a scrolling list based on large data
    private RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_activity);

        searchBar = (EditText) findViewById(R.id.searchBar);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

        //Database "Books" path reference
        mFirebaseDatabaseReference = FirebaseDatabase.getInstance().getReference("Books/");

        recyclerView = (RecyclerView) findViewById(R.id.account_recycler_view1);
        //Layout view that the recyclerView will use
        recyclerView.setLayoutManager(new LinearLayoutManager(SearchActivity.this));
        //recyclerView size is not affected by the adapter contents
        recyclerView.setHasFixedSize(true);
        //The appearence of the children in the recyclerView
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));

        //List of books created
        bookNameList = new ArrayList<>();

        //Enables a TextWatcher to methods  called whenever the TextView's text changes
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }
            /*
            afterTextChanged alerts the system what to do once the text has been changed
             */
            @Override
            public void afterTextChanged(Editable s) {
                    //if string is empty, set adapter to new parameter entered
                if (!s.toString().isEmpty()) {
                    setAdapter(s.toString());

                } else {
                    //otherwise clear arraylist of books and execute searchAdapter on the data that has been changed
                    bookNameList.clear();
                    searchAdapter.notifyDataSetChanged();

                    onStart();
                }

            }

        });
    }

    /**
     * setAdapter searches for the data that has been entered in the search bar
     * @param search The text to be searched in the database
     */
    private void setAdapter(final String search) {

        //Executes onDataChange method instantly
        mFirebaseDatabaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //starts with an empty list and an empty recycler view to display books
                bookNameList.clear();
                recyclerView.removeAllViews();
                int counter = 0;

                //For all children find the value for title and author
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String uid = snapshot.getKey();
                    String bookName = snapshot.child("title").getValue(String.class);
                    String authorName = snapshot.child("author").getValue(String.class);

                    //Checks if book or author name is equal to the searched data
                    if (bookName.toLowerCase().contains(search.toLowerCase())
                            ||authorName.toLowerCase().contains(search.toLowerCase())) {

                        //If strings are equal, adds it to the list to be displayed
                        BookInfo bookSearched = snapshot.getValue(BookInfo.class);
                        bookNameList.add(bookSearched);
                        //increment count
                        counter++;
                    }
                    //limit search result to up to 20 books
                    if (counter == 20)
                        break;
                }

                searchAdapter = new SearchAdapter(SearchActivity.this, bookNameList);

                recyclerView.setAdapter((searchAdapter));

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }
}
