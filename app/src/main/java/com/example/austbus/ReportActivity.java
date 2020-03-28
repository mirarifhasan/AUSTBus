package com.example.austbus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.austbus.Remote.ServerAPI;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class ReportActivity extends AppCompatActivity {

    LinearLayout linearLayout;
    Button submit;
    EditText reportText;

    RequestQueue requestQueue;

    String addReport = new ServerAPI().baseUrl + "addReport.php";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        linearLayout = findViewById(R.id.backLinearLayout);
        reportText = findViewById(R.id.reportText);
        submit = findViewById(R.id.submitButton);

        // Back layout
        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ReportActivity.this, ViewBusActivity.class);
                startActivity(intent);
                finish();
            }
        });

        //Submit button
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!reportText.getText().toString().isEmpty()){
                    requestQueue = Volley.newRequestQueue(getApplicationContext());
                    StringRequest request = new StringRequest(Request.Method.POST, addReport, new Response.Listener<String>() {
                        @Override
                        public void onResponse(String response) {
                            Log.d("Response Data", response);
                            try {
                                JSONObject obj = new JSONObject(response);

                                if(obj.getBoolean("status")){
                                    Toast.makeText(getApplicationContext(), "Report placed successfully", Toast.LENGTH_SHORT).show();
                                    Intent intent = new Intent(getApplicationContext(), ViewBusActivity.class).putExtra("jsonResponse", response);
                                    startActivity(intent);
                                    finish();
                                }
                                else{
                                    Toast.makeText(getApplicationContext(), "Server Error", Toast.LENGTH_SHORT).show();
                                }
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                        }
                    }){
                        @Override
                        protected Map<String, String> getParams() throws AuthFailureError {
                            Map<String, String> parameters = new HashMap<String, String>();
                            parameters.put("report", reportText.getText().toString());
                            parameters.put("userID", "12");
                            Log.d("POST Data", parameters.toString());

                            return parameters;
                        }
                    };
                    requestQueue.add(request);
                }
                else{
                    Toast.makeText(getApplicationContext(), "Empty field", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
