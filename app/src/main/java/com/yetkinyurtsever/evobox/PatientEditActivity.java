package com.yetkinyurtsever.evobox;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class PatientEditActivity extends AppCompatActivity {

    EditText name, surname, age;
    RadioGroup rg1;
    RadioButton r, r1, r2;
    Button addBtn;
    int patient_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_edit);

        name = findViewById(R.id.editText1);
        surname = findViewById(R.id.editText6);
        age = findViewById(R.id.editText3);
        rg1 = findViewById(R.id.radioGroup);
        addBtn = findViewById(R.id.addBtn);
        r1 = findViewById(R.id.radioButton1);
        r2 = findViewById(R.id.radioButton2);

        Intent intent = getIntent();
        patient_id = intent.getIntExtra("patient_id", 0);

        String patientInfo = String.format("http://192.168.1.47:8000/patient/?patient_id=%d", patient_id);
        getPatient(patientInfo);

        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int selectedId = rg1.getCheckedRadioButtonId();
                r = findViewById(selectedId);
                String patientUpdateUrl = "http://192.168.1.47:8000/patient/";
                updatePatient(patient_id, patientUpdateUrl);
            }
        });
    }

    private void getPatient(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        JSONObject jsonObject = serverResponse.getJSONObject(0);

                        name.setText(jsonObject.getString("name"));
                        surname.setText(jsonObject.getString("surname"));
                        age.setText(Integer.toString(jsonObject.getInt("age")));

                        if(jsonObject.getString("gender").equals("M")){
                            r1.setChecked(true);
                            r2.setChecked(false);
                        }else{
                            r1.setChecked(false);
                            r2.setChecked(true);
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error.", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updatePatient(int id, String url){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        final int a_id = id;
        String g = "M";

        if(r2.isChecked()) g = "F";

        StringRequest sr = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PatientEditActivity.this, HomePageActivity.class);
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
                params2.put("patient_id", Integer.toString(a_id));
                params2.put("name", name.getText().toString());
                params2.put("surname", surname.getText().toString());
                params2.put("age", age.getText().toString());
                params2.put("gender", r.getText().toString());
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
