package com.yetkinyurtsever.evobox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class PatientAddActivity extends AppCompatActivity{

    EditText name, surname, age;
    RadioGroup rg1;
    RadioButton r;
    Button addBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_add);

        name = findViewById(R.id.editText1);
        surname = findViewById(R.id.editText6);
        age = findViewById(R.id.editText3);
        rg1 = findViewById(R.id.radioGroup);
        addBtn = findViewById(R.id.addBtn);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg1.getCheckedRadioButtonId();
                r = findViewById(selectedId);

                String addPatientUrl = "http://192.168.1.47:8000/patient/";
                addPatient(addPatientUrl);
            }
        });
    }

    private void addPatient(String url){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());

        StringRequest sr = new StringRequest(Request.Method.POST, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PatientAddActivity.this, HomePageActivity.class);
                startActivity(intent);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
            }
        }) {
            @Override
            public byte[] getBody() throws AuthFailureError {
                HashMap<String, String> params2 = new HashMap<String, String>();
                params2.put("name", name.getText().toString());
                params2.put("surname", surname.getText().toString());
                params2.put("age", age.getText().toString());
                params2.put("gender", r.getText().toString());
                params2.put("user_id", Integer.toString(((EVOBOX) getApplication()).getUserID()));
                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(sr);
    }
}
