package ca.brocku.dailyscavengerhunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

/*[METHODS]
* getDailyItems(): gets the current day's challenge items {return: void}
* getAllScannedItems(): gets all items that have been scanned by the user for completing challenges {return: ArrayList<ContentValues>}
* incrementStreak(): increments the streak counter for the user {return: void}
* resetStreak(): sets the streak counter for the user to zero {return: void}
* getWeeklyStreak(): gets an array representing the number of days in the week the streak has continued to {return: boolean[]}
* updatePoints(int): adds the given integer to the user's overall point counter and returns the result {return: int}
* updateChallengeStatus(int): updates the status for the challenges (0 = challenge_1, 1 = challenge_2, 2 = challenge_3, any other number = set all to incomplete) {return: void}
* getChallengeStatuses(): gets an array representing the statuses for the daily challenges {return: boolean[]}
* */


//This class handles interactions between the application and the database
public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DB_NAME = "ScavengerHuntDatabase";
    public static final String DB_ITEM_TABLE = "items";
    public static final String DB_CAREER_TABLE = "career";
    public static final String DB_CHALLENGE_STATUS_TABLE = "challenge";
    public static final int DB_VERSION = 1;
    private static final String CREATE_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_ITEM_TABLE +
            " (name TEXT PRIMARY KEY, scan_count INTEGER);";
    private static final String CREATE_CAREER_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_CAREER_TABLE +
            " (player_id TEXT PRIMARY KEY, points INTEGER, streak INTEGER);";
    private static final String CREATE_CHALLENGE_STATUS_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_CHALLENGE_STATUS_TABLE +
            " (status_1 INTEGER, status_2 INTEGER, status_3 INTEGER, item_1 TEXT, item_2 TEXT, item_3 TEXT, date TEXT);";

    //the alphabetized list of words for the scavenger hunt
    /**
     * TODO
     *  Go through list and remove some questionable ones and replace them (Complete...I think)
     */
    private final String[] alph_items = {
            "Acorn", "Apple", "Alarm",
            "Ball", "Banana", "Battery", "Bed", "Bell", "Belt", "Bike", "Bolt", "Boot", "Book", "Bottle", "Bowl", "Box", "Brick", "Brush", "Bucket",
            "Cable", "Cake", "Can", "Candle", "Cap", "Car", "Card", "Cart", "Chain", "Chair", "Clock", "Coat", "Coin", "Comb", "Cone", "Cork", "Corn", "Crayon", "Crown", "Cube", "Cup",
            "Desk", "Dice", "Dime", "Disk", "Doll", "Donut", "Door",
            "Ear", "Egg", "Eraser",
            "Fan", "Feather", "File", "Flashlight", "Flower", "Flute", "Fork",
            "Glass", "Glasses", "Grape", "Gum",
            "Hammer", "Hand", "Hat", "Head", "Heart", "Helmet",
            "Iron",
            "Jar", "Jug",
            "Kettle", "Key", "Keyboard", "Kite", "Knife",
            "Lamp", "Leaf", "Lemon", "Lighter", "Lime", "Lock", "Loom",
            "Marble", "Mask", "Match", "Medal", "Melon", "Mesh", "Microwave", "Mop", "Muffin", "Mug", "Mushroom",
            "Nail", "Napkin", "Necklace", "Needle",
            "Olive", "Onion", "Orange", "Outlet",
            "Pen", "Plate", "Pan",
            "Remote",
            "Shoe", "Socks", "Spatula", "Spoon", "Suitcase",
            "Table", "Tie", "Towel",
            "Vase",
            "Watch", "Whisk"
    };

    //the list of words that will be randomized to choose from each day
    private ArrayList<String> wordBank;

    //constructor
    DatabaseManager(Context context) {
        super(context,DB_NAME,null, DB_VERSION);

        wordBank = new ArrayList<>(); //initializes the list for choosing items

        //adds all the item names to the word bank list
        wordBank.addAll(Arrays.asList(alph_items));

        Collections.shuffle(wordBank);  //shuffles the word bank
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_ITEM_TABLE);   //creates the table for item records
        db.execSQL(CREATE_CAREER_TABLE); //creates the table for the stat records
        db.execSQL(CREATE_CHALLENGE_STATUS_TABLE);
        String query;
        Cursor cursor;

        /*THIS SECTION HANDLES THE SCENARIO WHERE THE ITEMS TABLE ALREADY EXISTED*/
        query = "SELECT COUNT(name) FROM items";
        cursor = db.rawQuery(query, null);   //gets the count of items in the database

        //if the career table is empty or missing items
        if(cursor == null || !cursor.moveToFirst() || cursor.getInt(0) != alph_items.length) {
            db.execSQL("DELETE FROM items");  //ensures that the items table is clear before loading items from the list

            //this loop adds records for each item that will be used in the scavenger hunt
            for (String name : alph_items) {
                //Log.d("onCreate", "[" + name + ", 0]"); //test code
                query = "INSERT INTO items (name, scan_count) VALUES ('" + name + "', 0);";
                db.execSQL(query);
            }
        }
        if(cursor != null) cursor.close();  //closes the cursor


        /*THIS SECTION HANDLES THE SCENARIO WHERE THE CAREER TABLE ALREADY EXISTED*/
        query = "SELECT COUNT(player_id) FROM career";  //the table should only have one record
        cursor = db.rawQuery(query, null);   //gets the count of players in the database

        //if the career table is empty
        if(cursor == null || !cursor.moveToFirst() || cursor.getInt(0) <= 0 || cursor.getInt(0) > 1){
            query = "INSERT INTO career (player_id, points, streak) VALUES ('001', 0, 0);";
            db.execSQL(query);
        }
        if(cursor != null) cursor.close();  //closes the cursor


        /*THIS SECTION HANDLES THE SCENARIO WHERE THE CHALLENGE TABLE ALREADY EXISTED*/
        query = "SELECT COUNT(status_1) FROM challenge"; //the table should only have one record
        cursor = db.rawQuery(query, null);   //gets the count of players in the database

        String[] initItems = new String[3];
        for(int i=0; i<3; i++) initItems[i] = wordBank.get(i);  //adds the first three items to the daily items list

        LocalDate today = LocalDate.now();  //gets the current date
        DateTimeFormatter format = DateTimeFormatter.ofPattern("yyy-MM-dd");    //creates a format for the String representation of the date
        String date = today.format(format); //gets the String representation of today's date

        //if the challenge table is empty
        if(cursor == null || !cursor.moveToFirst() || cursor.getInt(0) <= 0 || cursor.getInt(0) != 1){
            db.execSQL("DELETE FROM challenge;");
            query = "INSERT INTO challenge (status_1, status_2, status_3, item_1, item_2, item_3, date) VALUES (0, 0, 0, '" + initItems[0] +"', '" + initItems[1] + "', '" + initItems[2] + "', '" + date + "');";
            db.execSQL(query);
        }
        if(cursor != null) cursor.close();  //closes the cursor
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //How to migrate or reconstruct data from old version to new on upgrade
    }

    //This method gets a list of 3 items from the item table (items for the day will be swapped if called on a new day)
    public String[] getDailyItems(){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        Cursor cursor;  //cursor for accessing the challenge table
        String[] items = new String[3]; //the list that will hold the items for today's challenges

        cursor = db.rawQuery("SELECT * FROM challenge;", null);

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            LocalDate today = LocalDate.now();  //gets the current date
            DateTimeFormatter format = DateTimeFormatter.ofPattern("yyy-MM-dd");    //creates a format for the String representation of the date
            String date = today.format(format); //gets the String representation of today's date

            //if today's date does not match the last recorded challenge date
            if(!cursor.getString(6).equals(date)){
                Collections.shuffle(wordBank);  //shuffles the word bank

                for(int i=0; i<3; i++) items[i] = wordBank.get(i);  //adds the first three items to the daily items list

                //the query for resetting the challenge record for the new day
                String query = "UPDATE challenge SET status_1 = 0, status_2 = 0, status_3 = 0, item_1 = '" +
                        items[0] + "', item_2 = '" + items[1] + "', item_3 = '" + items[2] + "', date = '" +
                        date + "';";

                db.execSQL(query);    //executes the query
            }
            else{
                items[0] = cursor.getString(3); //gets the name of the first item
                items[1] = cursor.getString(4); //gets the name of the second item
                items[2] = cursor.getString(5); //gets the name of the third item
            }
        }

        if(cursor != null) cursor.close();  //closes the cursor

        return items;    //test code
    }

    //This method gets a list of values containing the name and scan count of all items from the item table (only items that have been scanned before)
    public ArrayList<ContentValues> getAllScannedItems(){
        ArrayList<ContentValues> itemListInfo = new ArrayList<>();   //the list that will store the item information
        SQLiteDatabase db = getReadableDatabase();     //gets a readable database
        Cursor cursor;  //cursor for traversing the items table
        ContentValues cv;

        cursor = db.rawQuery("SELECT * FROM items WHERE scan_count > 0;", null);   //gets the list of all items that have a scan count greater than zero

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            Log.d("getAllScannedItems", "[" + cursor.getString(0) + ", 0]");    //test code
            cv = new ContentValues(); //the content values object that will hold item information
            cv.put("name", cursor.getString(0)); //records the name of the item
            cv.put("scan_count", cursor.getInt(1)); //records the scan count for the item
            itemListInfo.add(cv); //adds the item information to the list

            //while the last record has not been reached yet
            while(cursor.moveToNext()){
                cv = new ContentValues(); //the content values object that will hold item information
                cv.put("name", cursor.getString(0)); //records the name of the item
                cv.put("scan_count", cursor.getInt(1)); //records the scan count for the item
                itemListInfo.add(cv); //adds the item information to the list
            }
        }

        if(cursor != null) cursor.close();  //closes the cursor if it is not already null

        return itemListInfo;    //returns the list of item information
    }

    //This method increases the count for the number of times the item with the given name has been scanned
    private void incrementScanCount(String itemName){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE items SET scan_count = scan_count + 1 WHERE name = '" + itemName + "';";   //the query that will update the scan count

        db.execSQL(query);   //executes the update for the given item's scan count
    }

    //This method increases the streak counter for the player
    public void incrementStreak(){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE career SET streak = streak + 1;";   //the query that will update the streak

        db.execSQL(query);   //executes the update for the player's streak
    }

    //This method resets the player's streak counter
    public void resetStreak(){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE career SET streak = 0;";   //the query that will update the streak

        db.execSQL(query);   //executes the update for resetting the player's streak
    }

    //This method gets the weekly array
    public boolean[] getWeeklyStreak(){
        SQLiteDatabase db = getReadableDatabase();      //gets a writable database
        Cursor cursor;                                  //cursor for traversing the items table
        boolean[] weeklyStreak = new boolean[7];        //the streak kept for the current week
        int streakCount = 0;                            //the value for the number of days the streak has been kept

        cursor = db.rawQuery("SELECT * FROM career;", null); //gets the record for the challenge table

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            streakCount = cursor.getInt(2); //gets the daily streak information
        }

        if(cursor != null) cursor.close();  //closes the cursor

        streakCount %= 7;   //performs mod7 on the streak count to find the weekly streak value

        for(int i=0; i<streakCount; i++) weeklyStreak[i] = true;    //sets all values up until the weekly streak counter value to true

        return weeklyStreak;
    }

    //This method increases the user's points
    public int updatePoints(int p){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        Cursor cursor;                                  //cursor for accessing the career table
        String query = "SELECT * FROM career;";      //gets the career record
        int points = 0;

        cursor = db.rawQuery(query, null);  //executes the query

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            points = cursor.getInt(1);
        }

        //if the change in points is greater than zero
        if(p > 0){
            points += p;    //calculates the updated points score

            //query = "UPDATE career SET points = points" + p + ";";   //the query that will update the player's points
            query = "UPDATE career SET points = " + points + ";";   //the query that will update the player's points
            db.execSQL(query);   //executes the update for the given item's scan count
        }

        if(cursor != null) cursor.close();  //closes the cursor

        return points;
    }

    //This method updates the status for the challenges (0 = challenge_1, 1 = challenge_2, 2 = challenge_3, any other number = set all to incomplete)
    public void updateChallengeStatus(int cNum){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        Cursor cursor;
        String itemName;
        String query;

        boolean[] statuses = getChallengeStatuses();    //gets the current challenge statuses

        //if the challenge was already completed, do nothing
        if(0 <= cNum && cNum <= 2 && statuses[cNum]) return;

        //if one of the challenges is selected, set it to true
        if(cNum == 0) query = "UPDATE challenge SET status_1 = 1;";   //the query that will set challenge_1 to being completed
        else if(cNum == 1) query = "UPDATE challenge SET status_2 = 1;";   //the query that will set challenge_2 to being completed
        else if(cNum == 2) query = "UPDATE challenge SET status_3 = 1;";   //the query that will set challenge_3 to being completed
        else{
            query = "UPDATE challenge SET status_1 = 0, status_2 = 0, status_3 = 0;";   //the query that will set the status to be incomplete for all three challenges
            db.execSQL(query);    //executes the query
            return;
        }

        db.execSQL(query);    //executes the query

        cursor = db.rawQuery("SELECT * FROM challenge", null);  //gets the challenge information

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            incrementScanCount(cursor.getString(cNum+3));   //gets the name of the item for the challenge that was completed
        }

        if(cursor != null) cursor.close();  //closes the cursor
    }

    //This method gets the statuses for each challenge
    public boolean[] getChallengeStatuses(){
        SQLiteDatabase db = getReadableDatabase();      //gets a writable database
        Cursor cursor;                                  //cursor for traversing the items table
        boolean[] status = new boolean[3];              //the array for story the statuses for the challenges

        cursor = db.rawQuery("SELECT * FROM challenge;", null); //gets the record for the challenge table

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            //gets the boolean value for each challenge status
            status[0] = cursor.getInt(0) != 0;
            status[1] = cursor.getInt(1) != 0;
            status[2] = cursor.getInt(2) != 0;
        }

        if(cursor != null) cursor.close();  //closes the cursor

        return status;
    }
}

