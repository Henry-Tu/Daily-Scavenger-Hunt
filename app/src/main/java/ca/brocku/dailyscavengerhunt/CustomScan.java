package ca.brocku.dailyscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

public class CustomScan extends AppCompatActivity implements restRequest.OnRequestCompletedListener{
    ListView listView;
    DatabaseManager manager;
    /**
     * TODO
     *  Draw bounding boxes around found items
     *  If item has not previously been scanned and not on our list, give points
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
        listView = findViewById(R.id.listView);
        Engine.parseImage(this);

        manager = new DatabaseManager(this);    //initializes the DatabaseManager for this activity
    }

    @Override
    public void onRequestCompleted() {
        ArrayList<String> items = Engine.parseOutputAll();
        int newPoints = 0;
        //Adds items found into database
        for(String item: items)
        {
            newPoints += 5;
            manager.addScannedItem(item);   //adds item if the item does not already exist in the database
        }

        if (items.size() == 0)
        {
            items.add("No Items Found");
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1,items);
        listView.setAdapter(adapter);
        Toast.makeText(this,"+ " + newPoints + " points",Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onRequestFailed(String errorMessage) {

    }
}