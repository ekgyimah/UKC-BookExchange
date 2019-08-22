package kent.ukc_book_exchange;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by crutley on 14/11/2017.
 */

public class OnboardingFragment2 extends Fragment {

    //Dropdown list
    Spinner selectModuleF;
    Spinner selectCourseF;
    Spinner selectYearF;

    //Value of itemSelected from dropdownList
    String courseItemF;
    String yearItemF;

    ListView modlist;
    //Arraylist
    ArrayList<String> courseListF = new ArrayList<>();
    ArrayList<String> modulesListF = new ArrayList<>();
    ArrayList<String> selectedItems = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle s) {

        View view = inflater.inflate(R.layout.onboarding_screen2, container, false);

        modlist = view.findViewById(R.id.mod);

        modlist.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

        selectYearF = view.findViewById(R.id.yearOfStudy);

        selectCourseF = view.findViewById(R.id.department);

        //Set "selectYear" dropdown list
        //Get content from "Strings"
        ArrayAdapter<String> year = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                getResources().getStringArray(R.array.Year));
        year.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        selectYearF.setAdapter(year);

        //listener for items in "year" dropdownlist
        //Triggers when item is selected
        selectYearF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                //Gets value of item at this position
                yearItemF = parent.getItemAtPosition(position).toString();

                ((OnboardingActivity)getActivity()).setYearSelected(yearItemF);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        getJsonModules();

        return view;
    }

    /**
     * Read "Module_list.json" file containing module and Department list
     * Set their values to dropDown lists "selectCourse" and "selectModule"
     */
    public void getJsonModules() {
        final String modulesJson;

        try {

            //Read file
            InputStream is = getActivity().getAssets().open("Module_list.json");
            int size = is.available();
            byte[] buffer = new byte[size];

            is.read(buffer);
            is.close();

            modulesJson = new String(buffer, "UTF-8");


            JSONObject departments = new JSONObject(modulesJson);

            JSONArray departmentArray = departments.getJSONArray("Department");

            for (int i = 0; i < departmentArray.length(); i++) {
                final JSONObject courses = departmentArray.getJSONObject(i);

                Iterator<String> coursNames = courses.keys();

                while (coursNames.hasNext()) {
                    courseListF.add(coursNames.next());
                }

                ArrayAdapter<String> course = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1,
                        courseListF);
                course.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                selectCourseF.setAdapter(course);


                selectCourseF.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                    @Override
                    public void onItemSelected(AdapterView course, View view, int i, long l) {
                        String itemSelected = course.getItemAtPosition(i).toString();
                        modulesListF.clear();
                        try {
                            JSONArray moduleArray = courses.getJSONArray(itemSelected);
                            for (int p = 0; p < moduleArray.length(); p++) {
                                JSONObject module = moduleArray.getJSONObject(p);
                                modulesListF.add(module.getString("sds_code"));
                                courseItemF = itemSelected;
                                ((OnboardingActivity)getActivity()).setDepartmentSelected(courseItemF);
                            }

                            ArrayAdapter<String> modulesAdapter = new ArrayAdapter<String>(getActivity(), R.layout.checkablelist,
                                    R.id.moduleC, modulesListF);
                            modlist.setAdapter(modulesAdapter);

                            modlist.setOnItemClickListener(new AdapterView.OnItemClickListener(){
                                @Override
                                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                                    String selected= ((TextView)view).getText().toString();

                                    if(selectedItems.contains(selected))
                                    {
                                        selectedItems.remove(selected);
                                    }

                                    else
                                    {
                                        selectedItems.add(selected);
                                    }

                                    ((OnboardingActivity)getActivity()).setModulesSelected(selectedItems);
                                }
                            });

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
}
