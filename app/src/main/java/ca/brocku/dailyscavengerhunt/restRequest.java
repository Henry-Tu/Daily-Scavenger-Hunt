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

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HexFormat;
import java.util.Map;

public class restRequest
{

    private OnRequestCompletedListener mListener;

    public void requestWithSomeHttpHeaders(Context context, byte[] imgdata, OnRequestCompletedListener listener)
    {
        mListener = listener;
        RequestQueue queue = Volley.newRequestQueue(context);
        String url = "https://imageRecognition2024.cognitiveservices.azure.com/vision/v3.2/analyze?visualFeatures=Objects";
        StringRequest postRequest = new StringRequest(Request.Method.POST, url, new Response.Listener<String>()
        {

            @Override
            public void onResponse(String response)
            {
                // response
                Log.d("Response", response);
                ArrayList<String> items = parseAPI(response);
                Engine.itemsFound = items;
                System.out.println("Sent to engine");
                mListener.onRequestCompleted();
            }
        },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error)
                    {

                        if (error == null || error.networkResponse == null)
                        {
                            System.out.println("Error ");
                            return;
                        }

                        String body = "";

                        //get status code here
                        final String statusCode = String.valueOf(error.networkResponse.statusCode);

                        //get response body and parse with appropriate encoding
                        try
                        {
                            body = new String(error.networkResponse.data,"UTF-8");

                        }
                        catch (UnsupportedEncodingException e)
                        {
                            System.out.println("Error " + e.toString());
                            // exception
                        }
                        mListener.onRequestFailed("Error message here");
                        System.out.println(body);

                    }
                }
        )
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError
            {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("Content-Type", "application/octet-stream");
                params.put("Ocp-Apim-Subscription-Key", "6aededfb6bc943b3a1e0421ebf726175");
                return params;
            }
            @Override
            public byte[] getBody() throws AuthFailureError
            {
                return imgdata;
            }

        };
        queue.add(postRequest);

    }

    ArrayList<String> parseAPI(String r)
    {
        System.out.println(r);
        boolean objectData = false;
        System.out.print("IDENTIFIED OBJECT: ");
        for (int i = 0; i < r.length(); i++)
        {
            if (r.charAt(i) == '[')
            {
                objectData = true;
            }
            else if (r.charAt(i) == ']')
            {
                objectData = false;
            }
            if (objectData)
            {
                System.out.print(r.charAt(i));
            }
        }
        System.out.print("]");
        System.out.println();
        ArrayList items = readObjects(r);
        System.out.println("Array list size " + items.size());
        for (int i = 0; i < items.size(); i++) {
            System.out.println(items.get(i));
        }
        return items;
    }

    private ArrayList<String> readObjects(String text) {
        int cursor = 0;
        boolean objectsPresent = false;
        boolean foundFirst = false;
        ArrayList objects = new ArrayList<>();
        while (cursor < text.length() - 1) {
            if (!objectsPresent) {
                if (text.charAt(cursor) == '[') {
                    //check if no objects
                    if (text.charAt(cursor+1) != ']') {
                        objectsPresent = true;
                    } else { //no objects
                        break;
                    }
                    cursor++;
                    continue;
                }
            } else {
                if (text.charAt(cursor) =='c' && text.charAt(cursor+1) =='t' &&text.charAt(cursor+2) == '"' && text.charAt(cursor+3) == ':' && text.charAt(cursor+4) == '"') {
                    String item = "";
                    cursor += 5;
                    //read the name of the object
                    while (text.charAt(cursor) != '"') {
                        item = item + text.charAt(cursor);
                        cursor++;
                    }
                    objects.add(item);
                    foundFirst = true;
                    cursor++;
                    continue;
                }else{
                    cursor++;
                    continue;
                }
            }

            cursor++;
        }
        return objects;
    }
    public interface OnRequestCompletedListener
    {
        void onRequestCompleted();
        void onRequestFailed(String errorMessage);
    }

}