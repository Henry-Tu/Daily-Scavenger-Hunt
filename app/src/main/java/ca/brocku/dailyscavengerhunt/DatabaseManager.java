package ca.brocku.dailyscavengerhunt;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.Collections;

//This class handles interactions between the application and the database
public class DatabaseManager extends SQLiteOpenHelper {
    public static final String DB_NAME = "ScavengerHuntDatabase";
    public static final String DB_ITEM_TABLE = "items";
    public static final String DB_CAREER_TABLE = "career";
    public static final int DB_VERSION = 1;
    private static final String CREATE_ITEM_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_ITEM_TABLE +
            " (name TEXT PRIMARY KEY, scan_count INTEGER);";
    private static final String CREATE_CAREER_TABLE = "CREATE TABLE IF NOT EXISTS " + DB_CAREER_TABLE +
            " (player_id String PRIMARY KEY, points INTEGER, streak INTEGER);";

    //the alphabetized list of words for the scavenger hunt
    private final String[] alph_items = {
            "Apple",
            "Ball", "Battery", "Bed", "Bell", "Belt", "Bike", "Block", "Boat", "Bolt", "Bone", "Boot", "Book", "Bottle", "Bowl", "Box", "Brick", "Brush",
            "Cable", "Cake", "Can", "Candle", "Cap", "Cape", "Car", "Card", "Cart", "Case", "Chain", "Chair", "Chart", "Chip", "Clock", "Cloud", "Coat", "Coin", "Comb", "Cone", "Cork", "Corn", "Crayon", "Crown", "Cube", "Cup", "Curtain", "Cushion",
            "Desk", "Dice", "Disk", "Doll", "Door",
            "Ear", "Egg", "Eraser",
            "Fan", "Feather", "Fence", "File", "Flag", "Flashlight", "Flower", "Flute", "Fork", "Fungus",
            "Game", "Glass", "Grape", "Glasses",
            "Hammer", "Hand", "Hat", "Head", "Heart", "Hook",
            "Iron",
            "Jack", "Jar", "Jug",
            "Kettle", "Key", "Keyboard", "Kite", "Knee", "Knife", "Knot",
            "Lamp", "Leaf", "Lemon", "Lighter", "Lime", "Lock", "Log", "Loom", "Loop",
            "Mask", "Meat", "Medal", "Melon", "Mesh", "Mint", "Microwave", "Mop", "Mug",
            "Nail", "Napkin", "Neck", "Needle",
            "Pen", "Plate", "Pan",
            "Remote",
            "Shoe", "Socks", "Spatula", "Spoon", "Suitcase",
            "Table",  "Tie", "Towel",
            "Vase",
            "Watch", "Whisk"
    };

    //the list of words that will be randomized to choose from each day
    private ArrayList<String> wordBank;

    DatabaseManager(Context context) {
        super(context,DB_NAME,null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        wordBank = new ArrayList<>(); //initializes the list for choosing items

        db.execSQL(CREATE_ITEM_TABLE);   //creates the table for item records
        db.execSQL(CREATE_CAREER_TABLE); //creates the table for the stat records
        String query;
        Cursor cursor;
        int id = 1; //the id for each item

        /*THIS SECTION HANDLES THE SCENARIO WHERE THE ITEMS TABLE ALREADY EXISTED*/

        query = "SELECT COUNT(name) FROM items";
        cursor = db.rawQuery(query, null);   //gets the count of items in the database

        //if the career table is empty or missing items
        if(cursor == null || !cursor.moveToFirst() || cursor.getInt(0) != alph_items.length) {
            db.execSQL("DELETE FROM items");  //ensures that the items table is clear before loading items from the list

            //this loop adds records for each item that will be used in the scavenger hunt
            for (String name : alph_items) {
                wordBank.add(name); //adds the item name to the word bank list
                query = "INSERT INTO items (name, scan_count) VALUES (" + name + ", 0);";
                db.execSQL(query);
            }
        }

        /*THIS SECTION HANDLES THE SCENARIO WHERE THE CAREER TABLE ALREADY EXISTED*/

        query = "SELECT COUNT(player_id) FROM career";
        cursor = db.rawQuery(query, null);   //gets the count of players in the database

        //if the career table is empty
        if(cursor == null || !cursor.moveToFirst() || cursor.getInt(0) <= 0){
            query = "INSERT INTO career (player_id, points, streak) VALUES (001, 0, 0);";
            db.execSQL(query);
        }
    }
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //How to migrate or reconstruct data from old version to new on upgrade
    }

    //This method gets a list of 3 random items from the item table
    public ArrayList<String> getDailyItems(){
        Collections.shuffle(wordBank);  //shuffles the word bank

        return (ArrayList<String>) wordBank.subList(0,3);   //returns the first 3 entries in the shuffled list
    }

    //This method gets a list of values containing the name and scan count of all items from the item table (only items that have been scanned before)
    public ArrayList<ContentValues> getAllScannedItems(){
        ArrayList<ContentValues> itemListInfo = new ArrayList<>();   //the list that will store the item information
        SQLiteDatabase db = getReadableDatabase();     //gets a readable database
        Cursor cursor;  //cursor for traversing the items table

        cursor = db.rawQuery("SELECT * FROM items WHERE scan_count > 0;", null);   //gets the list of all items that have a scan count greater than zero

        //if the table is not empty
        if(cursor != null && cursor.moveToFirst()){
            ContentValues cv = new ContentValues(); //the content values object that will hold item information
            cv.put("name", cursor.getString(0)); //gets the name of the item
            cv.put("scan_count", cursor.getInt(1)); //gets the scan count for the item
            itemListInfo.add(cv); //adds the item information to the list

            //while the last record has not been reached yet
            while(cursor.moveToNext()){
                cv = new ContentValues();
                cv.put("name", cursor.getString(0)); //gets the name of the item
                cv.put("scan_count", cursor.getInt(1)); //gets the scan count for the item
                itemListInfo.add(cv); //adds the item information to the list
            }
        }

        if(cursor != null) cursor.close();  //closes the cursor if it is not already null

        return itemListInfo;    //returns the list of item information
    }

    //This method increases the count for the number of times the item with the given name has been scanned
    public void incrementScanCount(String itemName){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE items SET scan_count = scan_count + 1 WHERE name = " + itemName + ";";   //the query that will update the scan count

        db.execSQL(query, null);   //executes the update for the given item's scan count
    }

    //This method increases the streak counter for the player
    public void incrementStreak(){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE career SET streak = streak + 1;";   //the query that will update the streak

        db.execSQL(query, null);   //executes the update for the player's streak
    }

    //This method resets the player's streak counter
    public void resetStreak(){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE career SET streak = 0;";   //the query that will update the streak

        db.execSQL(query, null);   //executes the update for reseting the player's streak
    }

    //This method increases the player's points
    public void updatePoints(int points){
        SQLiteDatabase db = getWritableDatabase();     //gets a writable database
        String query = "UPDATE career SET points = points + " + points + ";";   //the query that will update the player's points

        db.execSQL(query, null);   //executes the update for the given item's scan count
    }
}

