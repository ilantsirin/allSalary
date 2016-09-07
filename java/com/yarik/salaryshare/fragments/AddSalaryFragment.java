package com.yarik.salaryshare.fragments;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.ParseObject;
import com.yarik.salaryshare.R;
import com.yarik.salaryshare.activities.LocationActivity;
import com.yarik.salaryshare.model.SalaryLocation;
import com.yarik.salaryshare.utils.CustomOnItemSelectedListener;

public class AddSalaryFragment extends Fragment{

    public Spinner fieldSpinner;
    private TextView posName;
    private TextView txtSalary;
    private TextView yrsExperience;
    private String field;
    private String position;
    private String salary;
    private String experience;
    public Button btnCreate;
    public Button btnBack;
    private final static int SELECT_LOCATION_REQUEST = 1;
    private TextView txtLocation;
    private ProgressDialog pDialogMap;
    private View rootView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        this.rootView = inflater.inflate(R.layout.fragment_add_salary, container, false);
        this.setHasOptionsMenu(true);
        //Define Spinner and populate with fields array
        this.fieldSpinner = (Spinner) this.rootView.findViewById(R.id.fieldSpinner);
        this.fieldSpinner.setOnItemSelectedListener(new CustomOnItemSelectedListener());

        ArrayAdapter<CharSequence> fieldAdapter = ArrayAdapter.createFromResource(getContext(),
                R.array.fields_array, android.R.layout.simple_spinner_item);
        fieldAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        this.fieldSpinner.setAdapter(fieldAdapter);


        this. posName = (TextView) this.rootView.findViewById(R.id.posName);
        this.txtSalary = (TextView) this.rootView.findViewById(R.id.txtSalary);
        this.yrsExperience = (TextView) this.rootView.findViewById(R.id.yrsExperience);
        this.txtLocation = (TextView) this.rootView.findViewById(R.id.addLocation);
        this.txtLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pDialogMap = ProgressDialog.show(getActivity(), "", "Opening map...", true);

                Intent intent = new Intent(getActivity().getApplicationContext(), LocationActivity.class);
                startActivityForResult(intent, SELECT_LOCATION_REQUEST);
            }
        });
        this.btnCreate = (Button) this.rootView.findViewById(R.id.btnCreate);
        this.btnCreate.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {
                checkInputAndSave();

            }
        });


        this.btnBack = (Button) this.rootView.findViewById(R.id.btnBack);
        this.btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                FragmentManager fm = getActivity()
                        .getSupportFragmentManager();
                fm.popBackStack("mainFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);

            }
        });

        return this.rootView;

}

    @Override
    public void onResume() {
        super.onResume();

        if (pDialogMap != null) {
            pDialogMap.dismiss();
        }
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater menuInflater) {
        menuInflater.inflate(R.menu.create_salary, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_create:
                this.checkInputAndSave();
            default:
                return super.onOptionsItemSelected(menuItem);
        }
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == SELECT_LOCATION_REQUEST && resultCode == Activity.RESULT_OK) {
            SalaryLocation salaryLocation = (SalaryLocation) data.getSerializableExtra(LocationActivity.EXTRA_LOCATION);
            txtLocation.setText(salaryLocation.strAddress);
            txtLocation.setTag(salaryLocation);
        }

    }
    private void checkInputAndSave() {
        field = this.fieldSpinner.getSelectedItem().toString();
        position = this.posName.getText().toString();
        salary = this.txtSalary.getText().toString();
        experience = this.yrsExperience.getText().toString();
        SalaryLocation salaryLocation = (SalaryLocation) this.txtLocation.getTag();


        //TODO: improve statement
        if (salary.equals("") && position.equals("") && experience.equals("") && field.equals("")) {
            Toast.makeText(getActivity(),
                    "Please fill up the whole form.",
                    Toast.LENGTH_LONG).show();

        } else {

            ParseObject salaryObject = new ParseObject("Salary");
            salaryObject.put("fieldName", field);
            salaryObject.put("posName", position);
            salaryObject.put("salaryNum", salary);
            salaryObject.put("expNum", experience);
            salaryObject.put("locationLatitude", salaryLocation.latitude);
            salaryObject.put("locationLongitude", salaryLocation.longitude);
            salaryObject.put("locationAddress", salaryLocation.strAddress);
            salaryObject.saveInBackground();

            FragmentManager fm = getActivity()
                    .getSupportFragmentManager();
            fm.popBackStack ("mainFragment", FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }

    }
}
