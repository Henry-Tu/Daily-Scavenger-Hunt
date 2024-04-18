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

    public static void chooseImage(Uri i){
        image = i;
    }

    /**
     * TODO
     * Do the rest request to analyze the image
     * @param context
     * @return
     */
    public static boolean parseImage(Context context) {
        byte imageByte[] = getBinaryImageData(context, image);
        String item = items[currentItem];

        // call rest request to compare imageByte to item
        return false;
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

    public static void initialize(){
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
        completed = new boolean[3];

        for (int i = 0; i < 3; i++) {
            completed[i] = false;
        }
        completed[0]=true;

    }

    /**
     * TODO
     * Check database for current week's streak
     */
    private static void initStreak() {
        streak= new boolean[7];
        for (int i = 0; i < 7; i++) {
            streak[i]=false;
        }
        streak[0]=true;
    }

    /**
     * TODO
     * Get todays objectives from database
     */
    public static void initItems(){
        items = new String[3];
        items[0] = "fork";
        items[1] = "book";
        items[2] = "cup";
    }

}
