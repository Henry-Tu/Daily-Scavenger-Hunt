package ca.brocku.dailyscavengerhunt;


import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;


public class MainActivity extends AppCompatActivity
{

    private static final String TAG = MainActivity.class.getName();
    private Button btnRequest;
    restRequest r = new restRequest();


    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context c = this;
        btnRequest = (Button) findViewById(R.id.displayData);

        btnRequest.setOnClickListener((View.OnClickListener)(new View.OnClickListener()
        {
            public final void onClick(View it) {
                try
                {
                    r.requestWithSomeHttpHeaders(c, r.getImageData());
                }
                catch (IOException e)
                {
                    throw new RuntimeException(e);
                }
            }
        }));

    }


}