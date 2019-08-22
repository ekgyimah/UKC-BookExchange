package kent.ukc_book_exchange;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


/**
 * Authors: Prince Yawlui, Emmanuel Gyimah
 * Class to display books retrieved from Firebase database
 * into Activity_User_View
 */
public class UserView extends AppCompatActivity {

    private FirebaseDatabase db;
    private DatabaseReference mFirebaseDatabaseReference;

    private DatabaseReference databaseReferenceUserPersonalised;

    private RecyclerView recyclerView;

    private GoogleApiClient mGoogleApiClient;

    private String userID;

    private FirebaseRecyclerAdapter<BookInfo, AccountActivity.AccountActivityViewHolder> mFirebaseAdapter;

    public UserView() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_view);
        recyclerView = findViewById(R.id.account_recycler_view);

        //Firebase Database instance
        db = FirebaseDatabase.getInstance();

        //Database "Books" path reference
        mFirebaseDatabaseReference = db.getReference("Books/");

        //RecyclerView properties
        recyclerView.setLayoutManager(new LinearLayoutManager(UserView.this));
        recyclerView.setHasFixedSize(true);
        recyclerView.addItemDecoration(new DividerItemDecoration(this, LinearLayoutManager.VERTICAL));
    }
    public void InApp_button (View view){
        Intent InApp_button = new Intent(this, InApp_Chat.class);
        startActivity(InApp_button);

    }

    @Override
    protected void onResume() {
        super.onResume();

        setmFirebaseAdapter();

        //Google sign in
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestEmail()
                .build();
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(Auth.GOOGLE_SIGN_IN_API, gso)
                .build();
        mGoogleApiClient.connect();

    }

    /**
     * Method to setAdapter for recyclerView
     */
    private void setmFirebaseAdapter()
    {
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReferenceUserPersonalised = db.getReference("UserPersonalisedFeed/"+
                userID);

        //Adapter to be used in recycler view
        mFirebaseAdapter = new FirebaseRecyclerAdapter<BookInfo, AccountActivity.AccountActivityViewHolder>
                (BookInfo.class, R.layout.user_single_item,
                        AccountActivity.AccountActivityViewHolder.class, databaseReferenceUserPersonalised) {

            //RecyclerView adaptor populator method
            @Override
            protected void populateViewHolder(final AccountActivity.AccountActivityViewHolder viewHolder,
                                              final BookInfo book, final int position) {

                //Populate viewHolder in RecyclerView with book.toString()
                viewHolder.text_BookInfo_userView(book.toString());

                //RecyclerView on CLick listener. Trigger anytime item in the view is clicked
                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        //Create new intent to open BuyView activity
                        Intent buyView = new Intent(UserView.this, BuyView.class);

                        //Create bundle to be passed to BuyView activity
                        Bundle b = new Bundle();

                        //Add element to be passed in the bundle
                        b.putString("title", book.getTitle());
                        b.putString("author", book.getAuthor());
                        b.putString("edition", book.getEdition());
                        b.putString("subject", book.getSubject());
                        b.putString("module", book.getModule());
                        b.putString("phone", book.getPhone());
                        b.putString("email", book.getEmail());
                        b.putString("username", book.getUsername());


                        //Add bundle to intent
                        buyView.putExtras(b);

                        //Start buyView activity
                        startActivity(buyView);
                    }
                });
            }
        };

        //Set recycleView with content in "mFirebaseAdapter"
        recyclerView.setAdapter(mFirebaseAdapter);

    }


    /**
     * Account view button
     * Start activity account view
     * @param view Current activity_view
     */
    public void accountViewButton(View view) {
        Intent account_view_button = new Intent(this, AccountActivity.class);
        startActivity(account_view_button);
    }

    /**
     * Sell book view button
     * Start activity sell book view
     * @param view Current activity_view
     */
    public void sellBookButton(View view) {
        Intent sell_book_button = new Intent(this, Sell_Book.class);
        startActivity(sell_book_button);
    }

    /**
     * Search bar  button
     * Start activity Search bar view
     * @param view Current activity_view
     */
    public void searchBarButton(View view) {
        Intent searchActivity = new Intent(this, SearchActivity.class);
        startActivity(searchActivity);
    }

    /**
     * Sign out button
     * Start activity Main view
     * @param view Current activity_view
     */
    public void signOut(View view) {

        //Sign out user from google
        Auth.GoogleSignInApi.signOut(mGoogleApiClient).setResultCallback(
                new ResultCallback<Status>() {
                    @Override
                    public void onResult(@NonNull Status status) {
                        //Once sign out successful, display toast
                        Toast.makeText(getApplicationContext(), "Successfully Logged Out", Toast.LENGTH_SHORT).show();

                        //Create new intent for MainActivity_activity
                        Intent mainActivity = new Intent(getApplicationContext(), MainActivity.class);

                        //Clears activity stacks, to avoid back button
                        //redirecting to another activity after logout
                        mainActivity.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);

                        //Start activity mainActivity
                        startActivity(mainActivity);
                    }
                });

    }
}





