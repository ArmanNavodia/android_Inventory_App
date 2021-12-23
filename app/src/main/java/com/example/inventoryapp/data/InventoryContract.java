package com.example.inventoryapp.data;

import android.content.ContentResolver;
import android.content.ContentUris;
import android.media.Image;
import android.net.Uri;
import android.provider.BaseColumns;


/**
 * API Contract for the Inventory app.
 */
public final class InventoryContract {

    public static final String CONTENT_AUTHORITY = "com.example.inventoryapp";

    public static final Uri BASE_CONTENT_AUTHORITY = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH = "inventory";

    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private InventoryContract() {

    }

    /**
     * Inner class that defines constant values for the inventory database table.
     * Each entry in the table represents a single item.
     */
    public static final class InventoryEntry implements BaseColumns {

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_AUTHORITY, PATH);

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of items.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single item.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH;

        /**
         * Name of database table for inventory
         */
        public final static String TABLE_NAME = "Inventory";

        /**
         * Unique id number for all items
         * <p>
         * Type: INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name of the item
         * <p>
         * Type: TEXT
         */
        public final static String COLUMN_ITEM_NAME = "Item";

        /**
         * Price of the item
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_PRICE = "price";

        /**
         * Quantity of the item present
         * <p>
         * Type: INTEGER
         */
        public final static String COLUMN_QUANTITY = "quantity";

        /**
         * Image of the item present
         * <p>
         * Type: Blob
         */
        public static final String COLUMN_ITEM_IMAGE = "image";


    }
}
