package com.yetkinyurtsever.evobox;

import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TimePicker;
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
import java.util.Calendar;
import java.util.HashMap;

public class PillEditActivity extends AppCompatActivity {

    EditText e1, e2, e3, e4, e5;
    FloatingActionButton dltBtn;
    Button addBtn;
    RadioButton r1, r2, r3;
    TimePickerDialog timePickerDialog;
    Calendar calendar;
    int currentHour;
    int currentMinute;
    String amPm;
    int patient_id, pill_id, pill_no;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pill_edit);

        e1 = findViewById(R.id.editText1);
        e2 = findViewById(R.id.editText6);
        e3 = findViewById(R.id.editText3);
        e4 = findViewById(R.id.editText4);
        e5 = findViewById(R.id.editText5);
        r1 = findViewById(R.id.radioButton1);
        r2 = findViewById(R.id.radioButton2);
        r3 = findViewById(R.id.radioButton3);
        dltBtn = findViewById(R.id.dltBtn1);
        addBtn = findViewById(R.id.addBtn);

        Intent intent = getIntent();
        patient_id = intent.getIntExtra("patient_id", 0);
        pill_no = intent.getIntExtra("pill_no", 0);

        String getPillUrl = String.format("http://192.168.1.47:8000/pill/?patient_id=%d", patient_id);
        getPill(pill_no, getPillUrl, new onResponseCallback() {
            @Override
            public void onSuccess(int id) {
                pill_id = id;
                String getPillUrl = String.format("http://192.168.1.47:8000/pill/detail/?pill_id=%d", pill_id);
                getPillDetail(getPillUrl);

                addBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String updatePillUrl = "http://192.168.1.47:8000/pill/";
                        updatePill(pill_id, updatePillUrl);
                    }
                });

                dltBtn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(PillEditActivity.this);

                        builder.setTitle("Are you sure to delete pill ?");

                        builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String deletePillUrl = String.format("http://192.168.1.47:8000/pill/?pill_id=%d", pill_id);
                                deletePill(deletePillUrl);
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
            }
        });

        e3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(PillEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        e3.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        e4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(PillEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        e4.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });

        e5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                calendar = Calendar.getInstance();
                currentHour = calendar.get(Calendar.HOUR_OF_DAY);
                currentMinute = calendar.get(Calendar.MINUTE);

                timePickerDialog = new TimePickerDialog(PillEditActivity.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker timePicker, int hourOfDay, int minutes) {
                        if (hourOfDay >= 12) {
                            amPm = "PM";
                        } else {
                            amPm = "AM";
                        }
                        e5.setText(String.format("%02d:%02d", hourOfDay, minutes) + amPm);
                    }
                }, currentHour, currentMinute, false);

                timePickerDialog.show();
            }
        });
    }

    private void getPill(final int pill_no, String url, final onResponseCallback callback){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        ArrayList<JSONObject> pillListe = pillList(serverResponse);
                        JSONObject jsonObject = pillListe.get(pill_no - 1);

                        e1.setText(jsonObject.getString("name"));
                        pill_id = jsonObject.getInt("pill_id");
                        Log.i("callbk", Integer.toString(pill_id));
                        callback.onSuccess(pill_id);
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

    private void getPillDetail(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject j = response;
                    try {
                        JSONArray serverResponse = j.getJSONArray("response");
                        JSONObject jsonObject = serverResponse.getJSONObject(0);

                        e2.setText(Integer.toString(jsonObject.getInt("quantity")));
                        e3.setText(jsonObject.getString("pill_time_1"));
                        e4.setText(jsonObject.getString("pill_time_2"));
                        e5.setText(jsonObject.getString("pill_time_3"));

                        if(jsonObject.getString("size").equals("S")){
                            r1.setChecked(true);
                            r2.setChecked(false);
                            r3.setChecked(false);
                        }else if(jsonObject.getString("size").equals("M")){
                            r1.setChecked(false);
                            r2.setChecked(true);
                            r3.setChecked(false);
                        }else if(jsonObject.getString("size").equals("L")){
                            r1.setChecked(false);
                            r2.setChecked(false);
                            r3.setChecked(true);
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

    private ArrayList<JSONObject> pillList(JSONArray jsonArray) throws JSONException {

        ArrayList<JSONObject> pillList = new ArrayList<JSONObject>();
        for (int i = 0; i < jsonArray.length(); i++){
            pillList.add(jsonArray.getJSONObject(i));
        }
        return pillList;
    }

    private void updatePill(final int pill_id, String url){
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
        String size = "S";

        if(r2.isChecked()) size = "M";
        else if(r3.isChecked()) size = "L";
        final String s = size;

        StringRequest sr = new StringRequest(Request.Method.PUT, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Toast.makeText(getApplicationContext(), "Success.", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(PillEditActivity.this, HomePageActivity.class);
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
                params2.put("pill_id", Integer.toString(pill_id));
                params2.put("name", e1.getText().toString());
                params2.put("quantity", e2.getText().toString());
                params2.put("pill_time_1", e3.getText().toString());
                params2.put("pill_time_2", e4.getText().toString());
                params2.put("pill_time_3", e5.getText().toString());
                params2.put("size", s);

                return new JSONObject(params2).toString().getBytes();
            }

            @Override
            public String getBodyContentType() {
                return "application/json";
            }
        };

        queue.add(sr);
    }

    private void deletePill(String url){
        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        try {
            JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(Request.Method.DELETE, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    Intent intent = new Intent(PillEditActivity.this, HomePageActivity.class);
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
