package ca.brocku.dailyscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentValues;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.util.ArrayList;

public class History extends AppCompatActivity {

    LinearLayout historyLayout;
    LinearLayout.LayoutParams layoutParams; //the parameters for the linear layout
    LinearLayout.LayoutParams nameTextParams; //the parameters for the name text view
    LinearLayout.LayoutParams scanCountTextParams; //the parameters for the scan count text view

    DatabaseManager manager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        manager = new DatabaseManager(this);

        historyLayout = findViewById(R.id.scan_history);    //gets the layout that will contain the elements for the items scanned

        //the parameters for the linear layouts
        layoutParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );

        nameTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        nameTextParams.gravity = Gravity.START;  //fixes the view to the start of the container

        scanCountTextParams = new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.WRAP_CONTENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
        );
        scanCountTextParams.gravity = Gravity.END;  //fixes the view to the end of the container

        generateHistoryList();
    }

    /*
     * This method generates layouts that display the names and scan count
     * for each item that the user has successfully scanned for challenges
     */
    private void generateHistoryList(){
        historyLayout.removeAllViews(); //ensures that the history list layout is empty
        ArrayList<ContentValues> itemDetails = manager.getAllScannedItems();    //gets the details of the items that have been scanned by the user

        //if items have been scanned by the user
        if(itemDetails.size() > 0){
            //for each element in the list of item details
            for(ContentValues item: itemDetails){

                //creates the linear layout for the item element
                LinearLayout linearLayout = new LinearLayout(this); //the layout that will contain the text views for the item
                linearLayout.setLayoutParams(layoutParams); //sets the layout parameters
                linearLayout.setOrientation(LinearLayout.HORIZONTAL);   //sets the orientation of the layout to horizontal
                linearLayout.setBackgroundColor(Color.GRAY);    //sets the background for the element to gray

                //creates the views for the item details (name and times scanned)
                TextView nameText = new TextView(this); //the text view that will display the name of the item
                TextView scanCountText = new TextView(this); //the text view that will display the number of times the item has been correctly scanned
                View spacer = new View(this);   //the view that will allow the name and count information to be spaced out on opposite sides of the screen

                //sets the text for the text views and sets the their positioning
                nameText.setText((CharSequence) item.get("name"));  //sets the name text view to the name for the current item
                nameText.setLayoutParams(nameTextParams);   //sets the layout to be fixed to the start of the container
                spacer.setLayoutParams(new LinearLayout.LayoutParams(0, 0, 1.0f));  //sets the spacer to take up the space between the name and scan count details
                scanCountText.setText(String.valueOf(item.get("scan_count")));    //sets the scan count text view to the count for the current item
                scanCountText.setLayoutParams(scanCountTextParams);   //sets the layout to be fixed to the end of the container

                //sets the colour of the text in the elements to white
                nameText.setTextColor(Color.WHITE);
                scanCountText.setTextColor(Color.WHITE);

                //adds the sub components of the item element to the history layout
                linearLayout.addView(nameText);
                linearLayout.addView(spacer);
                linearLayout.addView(scanCountText);
                linearLayout.setPadding(10, 0, 10, 5);

                //adds the element to the start of the item history list
                historyLayout.addView(linearLayout);
            }
        }
        else{
            //displays text saying that there have not been any items scanned by the user
            TextView defaultText = new TextView(this);
            String d = "No items have been scanned yet";
            defaultText.setText(d);
            historyLayout.addView(defaultText);
        }
    }
}