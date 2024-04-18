package ca.brocku.dailyscavengerhunt;


import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.PickVisualMediaRequest;
import androidx.activity.result.contract.ActivityResultContracts;
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

    ActivityResultLauncher<Intent> launcher;

    String items[];
    int buttonPressed;

    /**
     *TODO
     *  Cleanup UI
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Context c = this;


        //r.requestWithSomeHttpHeaders(this);
        //r.getImageData();
        buttonPressed=-1;
        registerLauncher();
        ImageView upload1 = (ImageView) findViewById(R.id.upload1);
        upload1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(0);
            }
        });
        ImageView upload2 = (ImageView) findViewById(R.id.upload2);
        upload2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(1);
            }
        });
        ImageView upload3 = (ImageView) findViewById(R.id.upload3);
        upload3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(2);
            }
        });

        Button scan = findViewById(R.id.scan);
        scan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pickImage(-1);
            }
        });

        ImageView friends = findViewById(R.id.friends);
        friends.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        ImageView achievements = findViewById(R.id.achievements);
        achievements.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        initialize();
    }

    private void initialize() {
        Engine.initialize(this);

        TextView points = findViewById(R.id.Points);
        points.setText("Points: " + Engine.points);
        initStreak();

        //setup main screen items
        items = Engine.items;
        TextView item1 = findViewById(R.id.Item1);
        item1.setText(items[0].toUpperCase());
        TextView item2 = findViewById(R.id.Item2);
        item2.setText(items[1].toUpperCase());
        TextView item3 = findViewById(R.id.Item3);
        item3.setText(items[2].toUpperCase());

        ImageView iw;
        ImageView upload;
        if(Engine.completed[0]){
            iw = findViewById(R.id.check1);
            iw.setVisibility(View.VISIBLE);
            upload = findViewById(R.id.upload1);
            upload.setClickable(false);
        }
        if(Engine.completed[1]){
            iw = findViewById(R.id.check2);
            iw.setVisibility(View.VISIBLE);
            upload = findViewById(R.id.upload2);
            upload.setClickable(false);

        }
        if(Engine.completed[2]){
            iw = findViewById(R.id.check3);
            iw.setVisibility(View.VISIBLE);
            upload = findViewById(R.id.upload3);
            upload.setClickable(false);
        }
    }

    private void initStreak() {
        boolean streak[] = Engine.streak;
        ImageView iv;
        if(streak[0]){
            iv = findViewById(R.id.streak1);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[1]){
            iv = findViewById(R.id.streak2);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[2]){
            iv = findViewById(R.id.streak3);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[3]){
            iv = findViewById(R.id.streak4);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[4]){
            iv = findViewById(R.id.streak5);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[5]){
            iv = findViewById(R.id.streak6);
            iv.setVisibility(View.VISIBLE);
        }
        if(streak[6]){
            iv = findViewById(R.id.streak7);
            iv.setVisibility(View.VISIBLE);
        }
    }

    private void pickImage(int button){
        buttonPressed = button;
        Engine.currentItem=button;
        Intent intent = null;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R && android.os.ext.SdkExtensions.getExtensionVersion(android.os.Build.VERSION_CODES.R) >= 2) {
            intent = new Intent(MediaStore.ACTION_PICK_IMAGES);

            launcher.launch(intent);
        }else {
            Toast.makeText(MainActivity.this,"Can't pick image",Toast.LENGTH_SHORT).show();
        }

    }

    private void registerLauncher(){
        launcher  = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                new ActivityResultCallback<ActivityResult>() {
                    @Override
                    public void onActivityResult(ActivityResult result) {
                        try {
                            Uri imageUri = result.getData().getData();;
                            Engine.chooseImage(imageUri);
                            if(buttonPressed != -1){
                                Intent intent = new Intent(MainActivity.this,Daily_item_chosen.class);
                                startActivity(intent);
                            }else{
                                Intent intent = new Intent(MainActivity.this,CustomScan.class);
                                startActivity(intent);
                            }


                        }catch (Exception e){
                            Toast.makeText(MainActivity.this,"No image selected" , Toast.LENGTH_SHORT).show();
                        }
                    }
                }
        );
    }

    //This method starts an instance of the history activity
    public void showHistory(View view) {
        startActivity(new Intent(this, History.class));
    }
}