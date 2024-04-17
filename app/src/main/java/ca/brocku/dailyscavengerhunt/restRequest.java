package ca.brocku.dailyscavengerhunt;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

public class restRequest
{
    public void getImageData()
    {
        //byte[] imgSource = {0x32,0x43};
        //System.out.println(imgSource[0]);
    }
    public void requestWithSomeHttpHeaders(Context context)
    {
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://imageRecognition2024.cognitiveservices.azure.com/vision/v3.2/analyze?visualFeatures=Objects";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url,
                new Response.Listener<String>()
                {
                    @Override
                    public void onResponse(String response) {
                        // response
                        Log.d("Response", response);
                        System.out.println(response);
                    }
                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if (error == null || error.networkResponse == null) {
                            return;
                        }

                        String body = "";
                        //get status code here
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);
                        //get response body and parse with appropriate encoding
                        try
                        {
                            body = new String(error.networkResponse.data,"UTF-8");
                        } catch (UnsupportedEncodingException e) {
                            // exception
                        }
                        System.out.println(body);

                    }
                }
        ) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/json");
                params.put("Ocp-Apim-Subscription-Key", "6aededfb6bc943b3a1e0421ebf726175");
                params.put("Host", "imageRecognition2024.cognitiveservices.azure.com");

                return params;
            }
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                String total = "{\"url\": \"https://plantsource.org/lopple.png\"}";
                byte[] body = new byte[0];
                try
                {
                    body = total.getBytes("UTF-8");
                }
                catch (UnsupportedEncodingException e)
                {
                    System.out.println("UNRECOGNIZED ERROR");
                }
                return body;
            }



        };
        queue.add(postRequest);

    }
}