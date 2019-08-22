package kent.ukc_book_exchange;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Emmanuel Gyimah.
 *
 */

public class UserProfile {

    private ArrayList<String> module = new ArrayList<>();
    private String year;
    private String department;

    public UserProfile(ArrayList modules, String year, String department)
    {
        this.module = modules;
        this.year = year;
        this.department = department;
    }


    public String getDepartment() {
        return department;
    }

    public String getYear() {
        return year;
    }

    public ArrayList<String> getModules() {
        return module;
    }
}
