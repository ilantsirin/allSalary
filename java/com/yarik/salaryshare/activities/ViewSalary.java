package com.yarik.salaryshare.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.yarik.salaryshare.R;

public class ViewSalary extends AppCompatActivity {
    public static final String SALARY_ID = "salaryId";
    public static final String FIELD_NAME = "fieldName";
    public static final String POS_NAME = "posName";
    public static final String SALARY_NUM = "salaryNum";
    public static final String EXP_NUM = "expNum";

    private TextView posName;
    private TextView fieldName;
    private TextView salaryNum;
    private TextView expNum;
    private Intent intent;
    private String position;
    private String field;
    private String salary;
    private String exp;
    private String salaryid;

    private Button btnBack;

    //TODO: add queryData() to check internet conenction

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.view_salary);

        posName = (TextView) findViewById(R.id.posName);
        fieldName = (TextView) findViewById(R.id.fieldName);
        salaryNum = (TextView) findViewById(R.id.salaryNum);
        expNum = (TextView) findViewById(R.id.expNum);

        position = getIntent().getExtras().getString(POS_NAME);
        salaryid = getIntent().getExtras().getString(SALARY_ID);
        field = getIntent().getExtras().getString(FIELD_NAME);
        salary = getIntent().getExtras().getString(SALARY_NUM);
        exp = getIntent().getExtras().getString(EXP_NUM);


        posName.setText(position);
        fieldName.setText(field);
        salaryNum.setText(salary + "\u20AA");
        expNum.setText(exp);
        btnBack = (Button) findViewById(R.id.btnBack);
        btnBack.setOnClickListener(new View.OnClickListener() {

            public void onClick(View arg0) {

                Intent listActivity = new Intent(getApplicationContext(), NavigationDrawerActivity.class);
                startActivity(listActivity);

            }
        });


            }

}
