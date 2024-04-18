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
     * TODO
     * Do the rest request to analyze the image
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
        System.out.println("Arraylust size: " + itemsFound.size());
        for (int i = 0; i < itemsFound.size(); i++) {
            System.out.println("Comparing " + itemsFound.get(i) + " to");
            if(itemsFound.get(i).equals(item)){
                found = true;
            }
        }
        return found;
    }

    /**
     * TODO Do the Rest Request and get list of items and their confidence
     * @param context
     */
    public static void customScan(Context context){
        byte imageByte[] = getBinaryImageData(context, image);
        itemsFound = new ArrayList<>();
        itemConfidence = new ArrayList<>();

        //Do Rest request here
        //Parse output and store to ArrayLists

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

    public static void initialize(Context c){
        manager = new DatabaseManager(c);
        initItems();
        points = 10;
        calendar = Calendar.getInstance();
        initStreak();
        initCompleted();
    }

    /**
     * TODO
     * check database for which items today has been completed
     */
    private static void initCompleted() {
        completed = manager.getChallengeStatuses(); //gets the completion status for each challenge
    }

    /**
     * TODO
     * Check database for current week's streak
     */
    private static void initStreak() {
        streak = manager.getWeeklyStreak();
    }

    /**
     * TODO
     * Get today's objectives from database
     */
    public static void initItems(){
        items = manager.getDailyItems();
    }

    /**
     * TODO Update the database for the new number of points
     * @param p
     */
   


    /**
     * TODO Update the database for the new number of points
     * @param p
     */
    static void updatePoints(int p){
        points += p;

    }

    /**
     * TODO set a task is completed in database, update streak in database accordingly
     * @param task
     */
    static void setCompleted(int task){
        completed[task] = true;
    }

    /**
     * TODO update weekly streak in database if all tasks today have been completed
     *
     */
    static void dailiesCompleted(){

    }

}
