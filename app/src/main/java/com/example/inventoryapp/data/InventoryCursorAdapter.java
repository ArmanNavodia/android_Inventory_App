package com.example.inventoryapp.data;

import static com.example.inventoryapp.data.InventoryContract.*;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.inventoryapp.R;
import com.google.android.material.button.MaterialButton;

public class InventoryCursorAdapter extends CursorAdapter {
    public InventoryCursorAdapter(Context context, Cursor c) {
        super(context, c);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        //if no view exists create new views from file drawable/layout/list_item
        return LayoutInflater.from(context).inflate(R.layout.list_item,parent,false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        //find the views
        TextView itemNameTextView=view.findViewById(R.id.item_text_view);
        TextView priceTextView=view.findViewById(R.id.price_text_view);
        TextView stockTextView=view.findViewById(R.id.stock_text_view);
        MaterialButton button=view.findViewById(R.id.button_sell);

    //get the data from the cursor
    String name=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME));
    String price=cursor.getString(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE));
    Integer stock=cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry.COLUMN_QUANTITY));

    //display the data in appropriate format
    itemNameTextView.setText(name);
    priceTextView.setText("Price: "+price+" $");
    if(stock==0){
        stockTextView.setText("Out of stock");
    }
    else {
        stockTextView.setText("In stock: " + stock);
    }
        // Get the current items ID
        int currentId = cursor.getInt(cursor.getColumnIndexOrThrow(InventoryEntry._ID));
        // Make the content uri for the current Id
        final Uri contentUri = ContentUris.withAppendedId(InventoryEntry.CONTENT_URI, currentId);

        // Change the quantity when you click the button
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int quantity = stock;

                if (quantity > 0) {
                    quantity = quantity - 1;
                }
                // Content Values to update quantity
                ContentValues values = new ContentValues();
                values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

                // update the database
                context.getContentResolver().update(contentUri, values, null, null);
            }
        });
    }
}
