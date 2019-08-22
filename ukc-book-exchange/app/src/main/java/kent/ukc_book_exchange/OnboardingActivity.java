package kent.ukc_book_exchange;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.view.LayoutInflater;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.messaging.FirebaseMessaging;
import com.ogaclejapan.smarttablayout.SmartTabLayout;

import java.util.ArrayList;

/**
 * Created by crutley on 14/11/2017.
 */

public class OnboardingActivity extends FragmentActivity {

    private FirebaseDatabase db;

    private ViewPager pager;
    private SmartTabLayout indicator;
    private Button next;

    private String yearSelected;
    private String departmentSelected;
    private ArrayList<String> modulesSelected= new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_onboarding);

        db = FirebaseDatabase.getInstance();

        pager = findViewById(R.id.pager);
        indicator = findViewById(R.id.indicator);
        next = findViewById(R.id.next);

        FragmentStatePagerAdapter adapter = new FragmentStatePagerAdapter(getSupportFragmentManager()) {
            @Override
            public Fragment getItem(int position) {
                switch (position) {
                    case 0 : return new OnboardingFragment1();
                    case 1 : return new OnboardingFragment2();
                    default: return null;
                }
            }

            @Override
            public int getCount() {
                return 2;
            }
        };

        pager.setAdapter(adapter);
        indicator.setViewPager(pager);


        next.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(pager.getCurrentItem() == 1) { // The last screen

                    UserProfile userProfile = new UserProfile(modulesSelected,
                            yearSelected, departmentSelected);

                    for (String module:modulesSelected) {
                        FirebaseMessaging.getInstance().subscribeToTopic(module);
                    }

                    db.getReference().child("UserProfile")
                            .child(FirebaseAuth.getInstance()
                                    .getCurrentUser().getUid()).setValue(userProfile);

                    finishOnboarding();

                } else {
                    pager.setCurrentItem(
                            pager.getCurrentItem() + 1,
                            true
                    );
                }
            }
        });
    }

    public void setYearSelected(String year)
    {
        yearSelected = year;
    }

    public void setDepartmentSelected(String department)
    {
        departmentSelected = department;
    }

    public void setModulesSelected(ArrayList<String> modules)
    {
        modulesSelected = modules;
    }

    private void finishOnboarding() {
//        //Get the shared preferences
        SharedPreferences preferences =
                getSharedPreferences("my_preferences", MODE_PRIVATE);

        // Set onboarding_complete to true
        preferences.edit()
                .putBoolean("onboarding_complete",true).apply();

//        db.getReference().child("AccountSet")
//                .child(FirebaseAuth.getInstance()
//                        .getCurrentUser().getUid()).child("completed").setValue(true);

        // Launch the main Activity, called MainActivity
        Intent user_view = new Intent(this, UserView.class);
        startActivity(user_view);

        // Close the OnboardingActivity
        finish();
    }
}
