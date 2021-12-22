package com.example.inventoryapp.data;

import static com.example.inventoryapp.data.InventoryContract.*;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

public class InventoryProvider extends ContentProvider {
    private static final String LOG_TAG = InventoryProvider.class.getSimpleName();

    /**
     * To get the database
     */
    private InventorydbHelper mInventoryHelper;

    /**
     * URI matcher code for the content URI for the inventory table
     */
    private static final int inventory = 100;

    /**
     * URI matcher code for the content URI for a single item in the inventory table
     */
    private static final int inventory_id = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here, for all of the content URI patterns that the provider
        // should recognize. All paths added to the UriMatcher have a corresponding code to return
        // when a match is found.
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH, inventory);
        uriMatcher.addURI(CONTENT_AUTHORITY, PATH + "/#", inventory_id);
    }

    @Override
    public boolean onCreate() {
        mInventoryHelper = new InventorydbHelper(getContext());
        return false;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, @Nullable String[] projection, @Nullable String selection, @Nullable String[] selectionArgs, @Nullable String sortOrder) {
        //get readbale database as we need to just query
        SQLiteDatabase db = mInventoryHelper.getReadableDatabase();

        Cursor cursor;
        //match the uri with the uri matcher
        int match = uriMatcher.match(uri);
        switch (match) {
            case inventory:
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;

            case inventory_id:
                // For the Inventory_id code, extract out the ID from the URI.
                // For an example URI such as "content://com.example.inventoryapp/inventory/3",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 3 in this case.

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // This will perform a query on the inventory table where the _id equals 3 to return a
                // Cursor containing that row of the table.
                cursor = db.query(InventoryEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, null);
                break;
            default:
                throw new IllegalArgumentException("Cannot query the database");
        }
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case inventory:
                return InventoryEntry.CONTENT_LIST_TYPE;
            case inventory_id:
                return InventoryEntry.CONTENT_ITEM_TYPE;

            default:
                throw new IllegalArgumentException("Unkown uri" + uri);
        }

    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues values) {
        //match the uri with uriMatcher
        final int match = uriMatcher.match(uri);
        switch (match) {
            case inventory:
                return insertItem(uri, values);
            default:
                throw new IllegalArgumentException("Cannot insert pet ");
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        int rowsDeleted = 0;
        //get writable database
        SQLiteDatabase db = mInventoryHelper.getWritableDatabase();

        //match the uri with uri matcher
        int match = uriMatcher.match(uri);
        switch (match) {
            case inventory:
                // Delete all rows that match the selection and selection args
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;

            case inventory_id:
                // Delete a single row given by the ID in the URI
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = db.delete(InventoryEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Cannot delete the pet");
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        return rowsDeleted;
    }

    @Override
    public int update(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        int match = uriMatcher.match(uri);
        switch (match) {
            case inventory:
                //Update all the rows that match selection and selectionArgs
                return updateItem(uri, values, selection, selectionArgs);

            case inventory_id:
                //Update a single row with the given id in uri
                selection = InventoryEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateItem(uri, values, selection, selectionArgs);

            default:
                throw new IllegalArgumentException("Cannot update pet");
        }
    }

    /**
     * Update items in the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be 0 or 1 or more items).
     * Return the number of rows that were successfully updated.
     */
    private int updateItem(@NonNull Uri uri, @Nullable ContentValues values, @Nullable String selection, @Nullable String[] selectionArgs) {
        // If the {@link InventoryEntry#COLUMN_ITEM_NAME} key is present,
        // check that the name value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_ITEM_NAME)) {
            String name = values.getAsString(InventoryEntry.COLUMN_ITEM_NAME);
            if (name == null) throw new IllegalArgumentException("name cannot be null");
        }

        // If the {@link InventoryEntry#COLUMN_PRICE} key is present,
        // check that the price value is not null.
        if (values.containsKey(InventoryEntry.COLUMN_PRICE)) {
            Integer price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
            if (price != null && price < 0)
                throw new IllegalArgumentException("requires valid price");
        }
        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        //get writeable database
        SQLiteDatabase db = mInventoryHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = db.update(InventoryEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0)
            getContext().getContentResolver().notifyChange(uri, null);

        // Return the number of rows updated
        return rowsUpdated;
    }


    /**
     * Insert a item into the database with the given content values. Return the new content URI
     * for that specific row in the database.
     */
    private Uri insertItem(@NonNull Uri uri, @Nullable ContentValues values) {

        //get writable database
        SQLiteDatabase db = mInventoryHelper.getWritableDatabase();
        //get the price value and check if it is not negative
        int price = values.getAsInteger(InventoryEntry.COLUMN_PRICE);
        if (price < 0) {
            Toast.makeText(getContext(), "Price cannot be negative", Toast.LENGTH_SHORT);

        }
        //insert the item in the database
        long id = db.insert(InventoryEntry.TABLE_NAME, null, values);

        //if no rows inserted log it
        if (id == -1) {
            Log.v(LOG_TAG, "Error inserting the item");
            return null;
        }
        //row is inserted , notify the change to all listeners
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }
}
