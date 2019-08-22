package kent.ukc_book_exchange;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.widget.ImageView;

/**
 * Author: Emmanuel Gyimah
 * Class to display waiting screen when app is launched
 */


public class Splash_Page extends AppCompatActivity {

    //Timer value
    private static int TIME_OUT = 2000;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash__page);

        //Set imageView to display logo
        ImageView imageView = findViewById(R.id.imageView);

        //Gets logo
        imageView.setImageResource(R.drawable.logo);

        //Method to display "splashScreen_Activity" for 2000 ms
        //After which, open MainActivity.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                MainActivity g = new MainActivity();
                if(g.user == null) {
                    Intent loginPage = new Intent(Splash_Page.this, MainActivity.class);
                    startActivity(loginPage);
                    finish();
                }
            }
        },TIME_OUT);
    }
}
