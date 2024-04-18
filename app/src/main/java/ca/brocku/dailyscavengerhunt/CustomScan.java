package ca.brocku.dailyscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;

public class CustomScan extends AppCompatActivity {
    /**
     * TODO Take parsed data and display items found and confidence
     *  Draw bounding boxes around found items
     *  If item has not previously been scanned and not on our list, give points
     *  Achievements
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom_scan);
        ImageView iw = findViewById(R.id.CustomDisplay);
        iw.setImageURI(Engine.image);

        Button btnBack = findViewById(R.id.customBack);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(CustomScan.this,MainActivity.class);
                startActivity(intent);
            }
        });
        ListView listView = findViewById(R.id.listView);


    }
}