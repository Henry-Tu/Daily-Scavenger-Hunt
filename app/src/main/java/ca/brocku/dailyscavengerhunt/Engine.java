package ca.brocku.dailyscavengerhunt;

import android.net.Uri;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Calendar;

public class Engine {

    public static Uri image ;
    public static String items[];
    public static int currentItem = -1;
    public static Calendar calendar;
    public static int points;
    public static boolean streak[];
    public static boolean completed[];
    public static ArrayList<String> itemsFound;
    public static ArrayList<Double> itemConfidence;
    public static DatabaseManager manager;  //manager for the database

    public static void chooseImage(Uri i){
        image = i;
    }

    /**
     *
     * @param context
     * @return
     */
    public static void parseImage(Context context) {
        System.out.println("Parsing image");
        byte imageByte[] = getBinaryImageData(context, image);


        // call rest request to compare imageByte to item
        restRequest r = new restRequest();
        r.requestWithSomeHttpHeaders(context,imageByte, (restRequest.OnRequestCompletedListener) context);

    }
    public static boolean parseOutputMatch(){
        String item = items[currentItem];
        boolean found = false;
        if(itemsFound== null){
            itemsFound = new ArrayList<>();
            System.out.println("Array was null?");
        }
        System.out.println("Arraylist size: " + itemsFound.size());
        for (int i = 0; i < itemsFound.size(); i++) {
            System.out.println("Comparing " + itemsFound.get(i) + " to " + item);
            if(itemsFound.get(i).equalsIgnoreCase(item))
            {
                found = true;
            }
        }
        return found;
    }
    static ArrayList<String> parseOutputAll(){
        return itemsFound;
    }

    public static byte[] getBinaryImageData(Context context, Uri imageUri) {
        InputStream inputStream = null;
        try {
            // Open an input stream from the URI
            inputStream = context.getContentResolver().openInputStream(imageUri);

            // Convert input stream to byte array
            ByteArrayOutputStream byteBuffer = new ByteArrayOutputStream();
            int bufferSize = 1024;
            byte[] buffer = new byte[bufferSize];

            int len;
            while ((len = inputStream.read(buffer)) != -1) {
                byteBuffer.write(buffer, 0, len);
            }

            return byteBuffer.toByteArray();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    //Thit method initializes the engine's variables
    public static void initialize(Context c){
        manager = new DatabaseManager(c);
        initItems();
        points = 10;
        calendar = Calendar.getInstance();
        initStreak();
        initCompleted();
    }

    //This method gets the completion status for each challenge item
    private static void initCompleted() {
        completed = manager.getChallengeStatuses(); //gets the completion status for each challenge
    }

    //This method gets the streak from the database
    private static void initStreak() {
        streak = manager.getWeeklyStreak();
    }


    //This method gets the items for the current day's challenges
    public static void initItems(){
        items = manager.getDailyItems();
    }
    
    //This method updates the user's points
    static void updatePoints(int p){
        points = manager.updatePoints(p);
    }

    //This method sets the status for the task represented by the given number to completed
    static void setCompleted(int task){
        manager.updateChallengeStatus(task);        //updates the challenge status for the task
        completed = manager.getChallengeStatuses(); //updates the list of statuses for the engine
    }

    //This method updates the streak counter
    static void dailiesCompleted(){
        manager.incrementStreak();          //increments the streak counter in the database
        streak = manager.getWeeklyStreak(); //gets the weekly representation of the streak counter
    }

}
