package kent.ukc_book_exchange;


import android.content.Context;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Author: Emmanuel Gyimah
 * Class to scan ISBN barcode, set returned value to textviews and write it to database
 * It also set drop down lists
 */

public class Sell_Book extends AppCompatActivity {

    DatabaseReference bookRef;
    FirebaseDatabase mBookDatabase;

    TextView bTitle;
    TextView bAuthor;
    TextView bEdition;
    EditText phone;
    EditText email;

    //Dropdown list
    Spinner selectModule;
    Spinner selectCourse;
    Spinner selectYear;

    //Value of itemSelected from dropdownList
    String moduleItem;
    String courseItem;
    String yearItem;

    String title;
    String author;
    String edition;
    String phoneNumber;
    String emailAddress;
    String userName;

    //Arraylist
    ArrayList<String> courseList = new ArrayList<>();
    ArrayList<String> modulesList = new ArrayList<>();

    BookInfo bookInfo;
    Context context;


    public Sell_Book(FirebaseDatabase mBookDatabase, TextView bTitle, TextView bAuthor, TextView bEdition,
                     EditText phone, EditText email, BookInfo bookInfo, Context context)
    {
        this.mBookDatabase = mBookDatabase;
        this.bTitle = bTitle;
        this.bAuthor = bAuthor;
        this.bEdition = bEdition;
        this.phone = phone;
        this.email = email;
        this.bookInfo = bookInfo;
        this.context = context;
    }

    public Sell_Book()
    {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sell__book);

        //Get Firebase Database instance
        mBookDatabase = FirebaseDatabase.getInstance();

        bTitle = findViewById(R.id.BookTitle);
        bAuthor = findViewById(R.id.Author);
        bEdition = findViewById(R.id.Edition);

        selectModule = findViewById(R.id.sModule);
        selectCourse = findViewById(R.id.sSubject);
        selectYear = findViewById(R.id.year);

        email = findViewById(R.id.emailAddress);
        phone = findViewById(R.id.phoneNumber);

        //Set "selectYear" dropdown list
        //Get content from "Strings"
        ArrayAdapter<String> year = new ArrayAdapter<String>(Sell_Book.this, android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Year));
        year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectYear.setAdapter(year);

        //listener for items in "year" dropdownlist
        //Triggers when item is selected
        selectYear.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Gets value of item at this position
                yearItem = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });





        //Calls method "getJsonModules" to set "modules" and "subject" dropdown lists.
        getJsonModules();

    }

    /**
     * Button to scan ISBN. Initialise "CameraPreview" to scan ISBN.
     * @param view
     */
    public void scan(View view) {

        Intent intent = new Intent(this, CameraPreview.class);
        startActivityForResult(intent, 0);
    }

    /**
     * Method to initialise HTTP request after ISBN is scanned.
     * Run after "CameraPreview" returns a result.
     * @param requestCode
     * @param resultCode
     * @param data
     */
    protected void onActivityResult(int requestCode, int resultCode, Intent data)
    {
        if(requestCode == 0)
        {
            if (resultCode == CommonStatusCodes.SUCCESS)
            {
                if(data != null)
                {
                    //Gets "barcode" returned from "CameraPreview"
                    Barcode barcode = data.getParcelableExtra("barcode");

                    //Gets string value of returned result from "CameraPreview"
                    String bar = barcode.displayValue;

                    //Starts "FetchBook" with barcode returned
                    new FetchBook(bAuthor,bTitle,bEdition).execute(bar);
                }
            }
        }
    }


    /**
     * Read "Module_list.json" file containing module and Department list
     * Set their values to dropDown lists "selectCourse" and "selectModule"
     */
    public void getJsonModules()
    {
        final String modulesJson;

        try {

            //Read file
            InputStream is = context.getAssets().open("Module_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            modulesJson = new String (buffer, "UTF-8");

            JSONObject departments = new JSONObject(modulesJson);

            JSONArray departmentArray = departments.getJSONArray("Department");

            for(int i = 0; i < departmentArray.length(); i++)
            {
                final JSONObject courses = departmentArray.getJSONObject(i);

                Iterator<String> coursNames = courses.keys();

                while (coursNames.hasNext())
                {
                    courseList.add(coursNames.next());
                }

                ArrayAdapter<String> course = new ArrayAdapter<String>(Sell_Book.this, android.R.layout.simple_list_item_1,
                        courseList);
                course.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectCourse.setAdapter(course);


                selectCourse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView course, View view, int i, long l) {
                        String itemSelected = course.getItemAtPosition(i).toString();
                        modulesList.clear();
                        try {
                            JSONArray moduleArray = courses.getJSONArray(itemSelected);
                            for(int p = 0; p < moduleArray.length(); p++)
                            {
                                JSONObject module = moduleArray.getJSONObject(p);
                                modulesList.add(module.getString("sds_code"));
                                courseItem = itemSelected;
                            }

                            ArrayAdapter<String> modulesAdapter = new ArrayAdapter<String>(Sell_Book.this, android.R.layout.simple_list_item_1,
                                    modulesList);
                            modulesAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                            selectModule.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    moduleItem = parent.getItemAtPosition(position).toString();
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });

                            selectModule.setAdapter(modulesAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }

                    @Override
                    public void onNothingSelected(AdapterView adapterView) {

                    }
                });
            }

        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String setDataToView(FirebaseUser user) {
        return user.getDisplayName();

    }

    public void submit(View view) throws JSONException {


        userName = setDataToView(FirebaseAuth.getInstance().getCurrentUser());

        bookRef = mBookDatabase.getReference();
        title = bTitle.getText().toString();
        author = bAuthor.getText().toString();
        edition = bEdition.getText().toString();
        emailAddress = email.getText().toString();
        phoneNumber = phone.getText().toString();

        bookInfo = new BookInfo(title, author, edition, courseItem,
                moduleItem, emailAddress, phoneNumber, yearItem);


        //EditText phone = findViewById(R.id.phoneNumber);
        if (phone.getText().toString().length() == 0)
            phone.setError("Phone number is required!");

        //EditText email = findViewById(R.id.emailAddress);
        if (email.getText().toString().length() == 0)
            email.setError("Email address is required!");

        else {
            try {
                //Generate key to be used to store book in the DB
                String key = bookRef.push().getKey();

                bookRef.child("User").child(FirebaseAuth.getInstance().getCurrentUser()
                        .getUid()).child("Books").child(key).setValue(bookInfo);

                bookRef.child("Books").child(key).setValue(bookInfo);

                bTitle.setText("");
                bAuthor.setText("");
                bEdition.setText("");
                email.setText("");
                phone.setText("");

                //Toast.makeText(this, "Info submitted", Toast.LENGTH_SHORT).show();
            } catch (NullPointerException e) {

                e.printStackTrace();
            }


        }


    }


}
