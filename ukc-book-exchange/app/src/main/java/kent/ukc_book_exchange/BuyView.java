package kent.ukc_book_exchange;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class BuyView extends AppCompatActivity {

    TextView bookTitle;
    TextView bookAuthor;
    TextView bookPublication;
    TextView bookSubject;
    TextView moduleCode;
    TextView phoneNumber;
    TextView emailAddress;
    TextView bookSeller;
   // String userID;
    Button InApp;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buy_view1);
      //  userID = FirebaseAuth.getInstance().getCurrentUser().getProviderId();

        bookTitle = findViewById(R.id.bookTitle);
        bookAuthor = findViewById(R.id.bookAuthor);
        bookPublication = findViewById(R.id.bookPublication);
        bookSubject = findViewById(R.id.bookSubject);
        moduleCode = findViewById(R.id.moduleCode);
        phoneNumber = findViewById(R.id.phoneNumber);
        emailAddress = findViewById(R.id.emailAddress);
        InApp = findViewById(R.id.Chat);
        bookSeller = findViewById(R.id.bookSeller);

        Bundle b = getIntent().getExtras();

        bookTitle.setText(b.getCharSequence("title"));
        bookAuthor.setText(b.getCharSequence("author"));
        bookPublication.setText(b.getCharSequence("edition"));
        bookSubject.setText(b.getCharSequence("subject"));
        moduleCode.setText(b.getCharSequence("module"));
        phoneNumber.setText(b.getCharSequence("phone"));
        emailAddress.setText(b.getCharSequence("email"));
        bookSeller.setText(b.getCharSequence("username"));



        phoneNumber.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                String phone_no= phoneNumber.getText().toString().replaceAll("-", "");
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:"+phone_no));
                startActivity(callIntent);



            }


        });


        emailAddress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String i= emailAddress.getText().toString().replaceAll("-", "");
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setType("plain/text");
                emailIntent.setData(Uri.parse("mailto:"+i));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Interested in your book");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");

                startActivity(Intent.createChooser(emailIntent, "Send Email"));
                startActivity(emailIntent);
            }
        });


        }

        public void InApp_button (View view){
            Intent InApp_button = new Intent(this, InApp_Chat.class);
            startActivity(InApp_button);

        }

    }
