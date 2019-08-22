package kent.ukc_book_exchange;

import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.view.View;


/**
 * Created by crutley on 14/11/2017.
 */

public class OnboardingFragment1 extends Fragment {

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {

        return inflater.inflate(
                R.layout.onboarding_screen1,
                container,
                false);
    }
}