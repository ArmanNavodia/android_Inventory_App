package com.example.inventoryapp.data;

import static com.example.inventoryapp.data.InventoryContract.*;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


/**
 * Database helper for inventory app. Manages database creation and version management.
 */
public class InventorydbHelper extends SQLiteOpenHelper {

    /**
     * Name of the database file
     */
    private static final String DATABASE_NAME = "inventory.db";
    /**
     * Version of the database file
     */
    private static final int DATABASE_VERSION = 1;

    /**
     * Constructs a new instance of {@link InventorydbHelper}.
     *
     * @param context of the app
     */
    public InventorydbHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Create a String that contains the SQL statement to create the inventory table
        String CREATE_SQL_TABLE = "CREATE TABLE " + InventoryEntry.TABLE_NAME + '(' +
                InventoryEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                InventoryEntry.COLUMN_ITEM_NAME + " TEXT NOT NULL," +
                InventoryEntry.COLUMN_PRICE + " INTEGER NOT NULL," +
                InventoryEntry.COLUMN_QUANTITY + " INTEGER NOT NULL);";

        // Execute the SQL statement
        db.execSQL(CREATE_SQL_TABLE);

    }

    /**
     * This is called when the database needs to be upgraded.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to do be done here.
    }
}
