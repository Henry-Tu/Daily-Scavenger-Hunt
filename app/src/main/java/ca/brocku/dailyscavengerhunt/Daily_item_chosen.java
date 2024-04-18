package ca.brocku.dailyscavengerhunt;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

public class Daily_item_chosen extends AppCompatActivity implements restRequest.OnRequestCompletedListener{

    ImageView iv;
    Button btnBack;
    String output;
    /**
     * TODO Check for streak bonus
     *      Achievements
     *      Google Play Points
     * @param savedInstanceState If the activity is being re-initialized after
     *     previously being shut down then this Bundle contains the data it most
     *     recently supplied in {@link #onSaveInstanceState}.  <b><i>Note: Otherwise it is null.</i></b>
     *
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_daily_item_chosen);
        TextView itemName = findViewById(R.id.ItemName);
        itemName.setText(Engine.items[Engine.currentItem].toUpperCase());
        iv = findViewById(R.id.ImageDisplay);
        iv.setImageURI(Engine.image);
        btnBack = findViewById(R.id.back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Daily_item_chosen.this,MainActivity.class);
                startActivity(intent);
            }
        });


         Engine.parseImage(this);

    }


    @Override
    public void onRequestCompleted() {
        System.out.println("Request complete");
        boolean match = Engine.parseOutputMatch();
        ImageView check;
        check = findViewById(R.id.match);
        check.setVisibility(View.VISIBLE);
        TextView result1 = findViewById(R.id.result1);
        result1.setVisibility(View.VISIBLE);
        if(match){
            Engine.setCompleted(Engine.currentItem);
            Engine.updatePoints(10);
            result1.setText("+10 Points");
            if(Engine.completed[0] && Engine.completed[1] && Engine.completed[2]){
                check.setImageResource(R.drawable.checkmark);
                Engine.updatePoints(10);
                Engine.dailiesCompleted();
                TextView result2 = findViewById(R.id.result2);
                result2.setText(" Dailies completed +10 Points");
            }
            int weeklyBonus = 0;
            for (boolean i:Engine.streak) {
                if(i){
                    weeklyBonus += 10;
                }
            }
            if(weeklyBonus !=0){
                Engine.updatePoints(weeklyBonus);
                TextView result3 = findViewById(R.id.result3);
                result3.setText("Streak bonus + " + weeklyBonus + " points");
            }
        }else{
            check.setImageResource(R.drawable.cross);
            result1.setText("Item not found in image");
        }
    }

    @Override
    public void onRequestFailed(String errorMessage) {

    }
}