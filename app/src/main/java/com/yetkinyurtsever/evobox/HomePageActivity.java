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

public class HomePageActivity extends AppCompatActivity {

    TextView username, patientDetail, patientNumber;
    CardView card;
    FloatingActionButton exitFab, infoFab;
    int user_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_page);

        card = findViewById(R.id.cardDetail1);
        exitFab = findViewById(R.id.exitFab1);
        infoFab = findViewById(R.id.infoFab1);
        username = findViewById(R.id.username);
        patientDetail = findViewById(R.id.patientDetail);
        patientNumber = findViewById(R.id.patientNumber);

        user_id = ((EVOBOX) this.getApplication()).getUserID();
        final String url = String.format("http://192.168.1.47:8000/user/?id=%d", user_id);
        String patientInfoUrl = String.format("http://192.168.1.47:8000/user/patient/?user_id=%d", user_id);


        userInfo(url);
        patientBriefInfo(patientInfoUrl);


        card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(patientDetail.getText().toString().equals("Click to add patient!")){
                    Intent intent = new Intent(HomePageActivity.this, PatientAddActivity.class);
                    intent.putExtra("user_id", user_id);
                    startActivity(intent);
                }else{
                    Intent intent = new Intent(HomePageActivity.this, PatientDetailActivity.class);
                    intent.putExtra("user_id", user_id);
                    intent.putExtra("patient_no", 1);
                    startActivity(intent);
                }
            }
        });

        exitFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);

                builder.setTitle("Are you sure to exit ?");

                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent intent = new Intent(HomePageActivity.this, MainActivity.class);
                        startActivity(intent);
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

        infoFab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(HomePageActivity.this);

                builder.setTitle("Information");

                builder.setMessage("Our whatsapp number: +90 245 842 65 54\n\nYasar University\nComputer Engineering Students");

                AlertDialog dialog = builder.create();
                dialog.show();
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        user_id = ((EVOBOX) this.getApplication()).getUserID();
        final String url = String.format("http://192.168.1.47:8000/user/?id=%d", user_id);
        String patientInfoUrl = String.format("http://192.168.1.47:8000/user/patient/?user_id=%d", user_id);


        userInfo(url);
        patientBriefInfo(patientInfoUrl);
    }

    private void patientBriefInfo(String url){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        ArrayList<JSONObject> patientListe = patientList(serverResponse);
                        if(patientListe.isEmpty()){
                            patientDetail.setText("Click to add patient!");
                            patientNumber.setText("0 patient");
                        }else {
                            JSONObject jsonObject = patientListe.get(0);
                            patientDetail.setText(jsonObject.getString("name") + " " + jsonObject.getString("surname") + "\nAge: "
                                    + jsonObject.getString("age") + "\nGender: " + jsonObject.getString("gender"));
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

    private ArrayList<JSONObject> patientList(JSONArray jsonArray) throws JSONException {

        ArrayList<JSONObject> patientList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++){
            patientList.add(jsonArray.getJSONObject(i));
        }
        return patientList;
    }

    private void userInfo(String url){

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        String name = serverResponse.getJSONObject(0).getString("name");
                        String surname = serverResponse.getJSONObject(0).getString("surname");
                        username.setText("Welcome,\n" + name + " " + surname);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_LONG).show();
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
