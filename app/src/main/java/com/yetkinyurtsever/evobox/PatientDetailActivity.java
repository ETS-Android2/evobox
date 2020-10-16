package com.yetkinyurtsever.evobox;

import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class PatientDetailActivity extends AppCompatActivity  {

    FloatingActionButton deleteFab, editFab;
    CardView p1, p2, p3;
    TextView pill1_info1, pill1_info2, pill1_info3,
            pill2_info1, pill2_info2, pill2_info3,
            pill3_info1, pill3_info2, pill3_info3,
            patientName, patientText;

    int patient_id, pill_id, user_id, patient_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_patient_detail);

        deleteFab = findViewById(R.id.deleteFab1);
        editFab = findViewById(R.id.editFab1);
        p1 = findViewById(R.id.pill1);
        p2 = findViewById(R.id.cardPlus1);
        p3 = findViewById(R.id.cardPlus2);
        pill1_info1= findViewById(R.id.pill1_Info1);
        pill1_info2 = findViewById(R.id.pill1_Info2);
        pill1_info3 = findViewById(R.id.pill1_Info3);

        pill2_info1= findViewById(R.id.pill2_Info1);
        pill2_info2 = findViewById(R.id.pill2_Info2);
        pill2_info3 = findViewById(R.id.pill2_Info3);

        pill3_info1= findViewById(R.id.pill3_Info1);
        pill3_info2 = findViewById(R.id.pill3_Info2);
        pill3_info3 = findViewById(R.id.pill3_Info3);


        patientName = findViewById(R.id.username);
        patientText = findViewById(R.id.patientNo);

        Intent intent = getIntent();
        user_id = ((EVOBOX) this.getApplication()).getUserID();
        patient_no = intent.getIntExtra("patient_no", 0);

        String patientInfoUrl = String.format("http://192.168.1.47:8000/user/patient/?user_id=%d", user_id);
        patientInfo(patient_no, patientInfoUrl, new onResponseCallback() {
            @Override
            public void onSuccess(int id) {
                String pillInfoUrl = String.format("http://192.168.1.47:8000/pill/?patient_id=%d", id);
                pillInfo(1,pillInfoUrl);
                pillInfo(2,pillInfoUrl);
                pillInfo(3,pillInfoUrl);
            }
        });

        p1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pill1_info2.getText().toString().equals("Take hours: ---")){
                    Intent intent = new Intent(PatientDetailActivity.this, PillEditActivity.class);
                    intent.putExtra("pill_no", 1);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(PatientDetailActivity.this, PillAddActivity.class);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }
            }
        });

        p2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pill2_info2.getText().toString().equals("Take hours: ---")){
                    Intent intent = new Intent(PatientDetailActivity.this, PillEditActivity.class);
                    intent.putExtra("pill_no", 2);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(PatientDetailActivity.this, PillAddActivity.class);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }
            }
        });

        p3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!pill3_info2.getText().toString().equals("Take hours: ---")){
                    Intent intent = new Intent(PatientDetailActivity.this, PillEditActivity.class);
                    intent.putExtra("pill_no", 3);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(PatientDetailActivity.this, PillAddActivity.class);
                    intent.putExtra("patient_id", patient_id);
                    startActivity(intent);
                }
            }
        });

        deleteFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(PatientDetailActivity.this);

                builder.setTitle("Are you sure to delete patient ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String deleteUrl = String.format("http://192.168.1.47:8000/patient/?patient_id=%d", patient_id);
                        String deleteUrl2 = String.format("http://192.168.1.47:8000/patient/pill/?patient_id=%d", patient_id);
                        deletePatient(deleteUrl);
                        deletePatientPill(deleteUrl2);
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Nothing
                    }
                });

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });

        editFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(PatientDetailActivity.this, PatientEditActivity.class);
                intent.putExtra("patient_id", patient_id);
                startActivity(intent);
            }
        });
    }

    private void pillInfo(final int pill_no, String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        ArrayList<JSONObject> pillListe = pillList(serverResponse);

                        if(pill_no <= pillListe.size()){
                            if(pillListe.isEmpty()){
                                Toast.makeText(getApplicationContext(), "No pill.", Toast.LENGTH_LONG).show();
                            }else {
                                JSONObject jsonObject = pillListe.get(pill_no - 1);

                                pill_id = jsonObject.getInt("pill_id");
                                if(pill_no == 1){
                                    pill1_info1.setText(jsonObject.getString("name"));
                                }else if(pill_no == 2){
                                    pill2_info1.setText(jsonObject.getString("name"));
                                }else if(pill_no == 3){
                                    pill3_info1.setText(jsonObject.getString("name"));
                                }
                                String pillDetailInfoUrl = String.format("http://192.168.1.47:8000/pill/detail/?pill_id=%d", pill_id);
                                pillDetailInfo(pill_no, pillDetailInfoUrl);
                            }
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

    private void pillDetailInfo(final int pill_no, String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        j = serverResponse.getJSONObject(0);

                        if(pill_no == 1){
                            pill1_info2.setText("Take time: " + j.getString("pill_time_1")+ "-" + j.getString("pill_time_2") + "-" + j.getString("pill_time_3"));
                            pill1_info3.setText("Quantity: "+ j.getString("quantity"));
                        }else if(pill_no == 2){
                            pill2_info2.setText("Take time: " + j.getString("pill_time_1")+ "-" + j.getString("pill_time_2") + "-" + j.getString("pill_time_3"));
                            pill2_info3.setText("Quantity: "+ j.getString("quantity"));
                        }else if(pill_no == 3){
                            pill3_info2.setText("Take time: " + j.getString("pill_time_1")+ "-" + j.getString("pill_time_2") + "-" + j.getString("pill_time_3"));
                            pill3_info3.setText("Quantity: "+ j.getString("quantity"));
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

    // tamamlandı.
    private void patientInfo(final int patient_no, String url, final onResponseCallback callback){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        ArrayList<JSONObject> patientListe = patientList(serverResponse);
                        JSONObject jsonObject = patientListe.get(patient_no - 1);
                        patientName.setText(jsonObject.getString("name") + " " + jsonObject.getString("surname") + "'s Pill List");
                        patient_id = jsonObject.getInt("patient_id");
                        callback.onSuccess(patient_id);
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

    private ArrayList<JSONObject> patientList(JSONArray jsonArray) throws JSONException {

        ArrayList<JSONObject> patientList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++){
            patientList.add(jsonArray.getJSONObject(i));
        }
        return patientList;
    }

    private ArrayList<JSONObject> pillList(JSONArray jsonArray) throws JSONException {

        ArrayList<JSONObject> pillList = new ArrayList<JSONObject>();

        for (int i = 0; i < jsonArray.length(); i++){
            pillList.add(jsonArray.getJSONObject(i));
        }
        return pillList;
    }

    private void deletePatient(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
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

    private void deletePatientPill(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(PatientDetailActivity.this, HomePageActivity.class);
                    startActivity(intent);
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
}
