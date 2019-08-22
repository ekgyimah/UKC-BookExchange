package kent.ukc_book_exchange;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.w3c.dom.Text;

/**
 * Created by crutley on 01/02/2018.
 */

public class AccountActivity extends AppCompatActivity{

    private RecyclerView recyclerView;
    private FirebaseAuth.AuthStateListener authListener;
    private FirebaseDatabase firebaseDatabase;
    private DatabaseReference databaseReference;
    private DatabaseReference bookDBRefence;

    private String userID;

    private TextView name, userInfo;

    private FirebaseRecyclerAdapter<BookInfo, AccountActivityViewHolder> mFirebaseAdapter;

    public AccountActivity() {
        // Empty Constructor required
    }

    public AccountActivity(FirebaseRecyclerAdapter<BookInfo, AccountActivityViewHolder>
                           mFirebaseAdapter, TextView name)
    {
        this.mFirebaseAdapter = mFirebaseAdapter;
        this.name = name;
    }

    @Override
    public void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.account_view_layout);

        name = findViewById(R.id.textView7);
        userInfo = findViewById(R.id.textView10);

        authListener = new FirebaseAuth.AuthStateListener()
        {
            @SuppressLint("TextView7")
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                Log.d("AccountView", "onAuthStateChanged");
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    setDataToView(user);
                }
            }
        };

        firebaseDatabase = FirebaseDatabase.getInstance();
        userID = FirebaseAuth.getInstance().getCurrentUser().getUid();

        databaseReference = firebaseDatabase.getReference("User/"+userID+"/Books");
        bookDBRefence = firebaseDatabase.getReference("Books/");

        recyclerView = (RecyclerView)findViewById(R.id.account_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(AccountActivity.this));
        //Toast to show fetching list

    }


    @Override
    protected void onStart(){
        super.onStart();
        setmFirebaseAdapter();
    }

    public void setmFirebaseAdapter()
    {
        //setDataToView(FirebaseAuth.getInstance().getCurrentUser());

        mFirebaseAdapter = new FirebaseRecyclerAdapter<BookInfo, AccountActivityViewHolder>
                (BookInfo.class, R.layout.account_single_item,
                        AccountActivityViewHolder.class, databaseReference) {
            @Override
            protected void populateViewHolder(final AccountActivityViewHolder viewHolder, BookInfo bookInfo, final int position) {

                userInfo.setText("");
                viewHolder.text_BookInfo_Account(bookInfo.toString());

                viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(final View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(AccountActivity.this);
                        builder.setMessage("Do you want to delete this book?").setCancelable(false)
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        int selectedItem = position;
                                        String key = mFirebaseAdapter.getRef(selectedItem).getKey();

                                        mFirebaseAdapter.getRef(selectedItem).removeValue();

                                        //Delete value from book DB
                                        bookDBRefence.child(key).removeValue();

                                        mFirebaseAdapter.notifyItemRemoved(selectedItem);
                                        recyclerView.invalidate();
                                        onStart();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog dialog = builder.create();
                        dialog.setTitle("Confirm");
                        dialog.show();
                    }
                });

            }
        };
        recyclerView.setAdapter(mFirebaseAdapter);
    }

    @SuppressLint("TextView7")
    public void setDataToView(FirebaseUser user) {
        name.setText("User name: " + user.getDisplayName());
    }


    //View Holder for recycler View
    public static class AccountActivityViewHolder extends RecyclerView.ViewHolder {

        private final TextView accountBookInfo;
        private final TextView userViewBookInfo;

        public AccountActivityViewHolder(final View itemView) {
            super(itemView);
            accountBookInfo = itemView.findViewById(R.id.fetch_book_info_account);
            userViewBookInfo = itemView.findViewById(R.id.fetch_book_info_userView);

        }

        public void text_BookInfo_Account(String bookInfo) {
            accountBookInfo.setText(bookInfo);
        }

        public void text_BookInfo_userView(String bookInfo) {
            userViewBookInfo.setText(bookInfo);
        }

    }
}
