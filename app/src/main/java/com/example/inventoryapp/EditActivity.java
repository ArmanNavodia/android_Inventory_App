package com.example.inventoryapp;

import static com.example.inventoryapp.data.InventoryContract.*;

import android.content.ContentValues;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NavUtils;
import androidx.loader.app.LoaderManager;
import androidx.loader.content.CursorLoader;
import androidx.loader.content.Loader;

import com.example.inventoryapp.data.InventoryContract;
import com.google.android.material.button.MaterialButton;

import java.net.URL;

/**
 * Allows user to create a new item or edit an existing one.
 */
public class EditActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /*
     *to recognize if there is change when the update screen is shown
     */
    private boolean mItemHasChanged = false;

    /**
     * Edit text field to enter item name
     */
    private EditText mItemNameEditText;

    /**
     * Edit text field to enter price of item
     */
    private EditText mPriceEditText;

    /**
     * Text view to display the quantity
     */
    private TextView mQuantityTextView;

    /**
     * To keep track of the quantity and display it in the textview
     * also to increment and decrement using button
     */
    Integer quantity = 0;

    /**
     * Uri to find to update the item or add new item
     * if  mUrl is null insert new item
     * otherwise edit the item
     */
    private Uri mUrl;

    // OnTouchListener that listens for any user touches on a View, implying that they are modifying
// the view, and we change the mItemHasChanged boolean to true.
    private View.OnTouchListener mTouchListner = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mItemHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.edit_activity);

        //get the intent from the  MainActivity when user taps on the view in the main screen
        mUrl = getIntent().getData();

        //check for the mUrl value
        //if mUrl is not null start the activity in edit mode
        // else start the activity to add new item
        if (mUrl != null) {
            setTitle(R.string.edit_item);
            LoaderManager.getInstance(this).initLoader(0, null, this);
        } else {
            setTitle(R.string.new_item);
            //order button is only visible in edit mode to place the order for the same item
            MaterialButton orderButton = findViewById(R.id.order_button);
            orderButton.setVisibility(View.GONE);
            invalidateOptionsMenu();
        }
        //find all the views
        mItemNameEditText = findViewById(R.id.Item_name_edit_text);
        mPriceEditText = findViewById(R.id.price_edit_text);
        mQuantityTextView = findViewById(R.id.quantity_text_view);

        //find the increment decrement button
        MaterialButton incButton = findViewById(R.id.increment_button);
        MaterialButton decButton = findViewById(R.id.decrement_button);

        //set the click listener on increment button to increase quantity
        incButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity += 1;
                mQuantityTextView.setText("" + quantity);
            }
        });

        //set the click listener on decrement button to decrease quantity
        //also check that quantity do not become negative
        decButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (quantity > 0) {
                    quantity -= 1;
                    mQuantityTextView.setText("" + quantity);
                } else {
                    Toast.makeText(getApplicationContext(), "Enter valid quantity", Toast.LENGTH_SHORT).show();
                }
            }
        });

        //set up the on touch listener on the fields to know if any of them is changed
        mPriceEditText.setOnTouchListener(mTouchListner);
        incButton.setOnTouchListener(mTouchListner);
        decButton.setOnTouchListener(mTouchListner);
        mItemNameEditText.setOnTouchListener(mTouchListner);

        //find the order button
        MaterialButton orderButton = findViewById(R.id.order_button);

        //when order button is clicked throw the intent to the email apps
        orderButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                String itemName = mItemNameEditText.getText().toString().trim();
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO);
                emailIntent.setData(Uri.parse("mailto:")); //sends the intent to only email apps
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "Order for the item " + itemName);
                startActivity(Intent.createChooser(emailIntent, "Choose an Email client :"));
            }
        });

    }


    //To insert the item
    void insertItem() {
        //get the values entered in the views
        String name = mItemNameEditText.getText().toString().trim();
        String price = mPriceEditText.getText().toString().trim();
        String quantity = mQuantityTextView.getText().toString().trim();

        //check if item name and price is not empty
        //if empty return as we cannot add null values
        if (mUrl == null && TextUtils.isEmpty(name) && TextUtils.isEmpty(price)) return;

        //create new content values object and put the values
        ContentValues values = new ContentValues();
        values.put(InventoryEntry.COLUMN_ITEM_NAME, name);
        values.put(InventoryEntry.COLUMN_PRICE, price);
        values.put(InventoryEntry.COLUMN_QUANTITY, quantity);

        //check if the activity is in edit mode or add new item mode
        //In add new item mode insert the new item in the database
        if (mUrl == null) {
            Uri newUri = getContentResolver().insert(InventoryEntry.CONTENT_URI, values);
            if (newUri == null) {
                // If the new content URI is null, then there was an error with insertion.
                Toast.makeText(this, "Error inserting Item",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the insertion was successful and we can display a toast.
                Toast.makeText(this, "Item inserted successfully",
                        Toast.LENGTH_SHORT).show();
            }
        }
        //In edit item mode Update the item in the database
        else {
            int rowsAffected = getContentResolver().update(mUrl, values, null, null);
            if (rowsAffected == 0) {
                //If the rowsAffected is zero that means no changes happened in database hence
                Toast.makeText(this, "Error updating Item",
                        Toast.LENGTH_SHORT).show();
            } else {
                // Otherwise, the updating was successful and we can display a toast.
                Toast.makeText(this, "Item updated successfully",
                        Toast.LENGTH_SHORT).show();


            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/edit_menu.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.edit_menu, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        //In new item mode no need to show delete option in menu
        if (mUrl == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_item);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Save" menu option
            case R.id.action_save:
                //insert the Item
                insertItem();
                finish();
                return true;

            // Respond to a click on the "Delete selected item" menu option
            case R.id.action_delete_item:
                showDeleteConfirmationDialog();
                return true;
            // Respond to a click on the "Up" arrow button in the app bar
            case R.id.home:
                if (!mItemHasChanged) {
                    NavUtils.navigateUpFromSameTask(EditActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListner = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // User clicked "Discard" button, navigate to parent activity.
                        NavUtils.navigateUpFromSameTask(EditActivity.this);
                    }
                };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedItemDialog(discardButtonClickListner);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener is the click listener for what to do when
     *                                   the user confirms they want to discard their changes
     */
    private void showUnsavedItemDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Discard changes and quit editing");
        builder.setPositiveButton("Discard", discardButtonClickListener);
        builder.setNegativeButton("Keep Editing", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user clicked keep editing, dismiss the dialog
                //continue in edit mode
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        //create and show alert dialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * This method is called when the back button is pressed.
     */
    @Override
    public void onBackPressed() {
        //check if any item is changed or not ,if not changed directly navigate to parent activity
        if (!mItemHasChanged) {
            super.onBackPressed();
            return;
        }
        //if discard button is pressed finish the activity
        DialogInterface.OnClickListener discardButton = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                finish();
            }
        };
        //show alert dialog box
        showUnsavedItemDialog(discardButton);

    }

    /**
     * This method is called when the the user selects delete button from the menu
     */

    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message, and click listeners
        // for the postivie and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Delete this Item?");
        builder.setPositiveButton("Delete", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user clicked delete button so delete item
                deleteItem();
            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                //user clicked cancel button,so dismiss the dialog
                //and continue in edit mode
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });
        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteItem() {

        int rowsDeleted = 0;
        if (mUrl != null) {
            // Call the ContentResolver to delete the pet at the given content URI.
            // Pass in null for the selection and selection args because the mCurrentPetUri
            // content URI already identifies the pet that we want.
            rowsDeleted = getContentResolver().delete(mUrl, null, null);
            if (rowsDeleted != 0) {
                //rows deleted show toast item deleted successfully
                Toast.makeText(this, "Item Deleted Successfully", Toast.LENGTH_SHORT).show();
            } else {
                //no rows deleted show toast error deleting item
                Toast.makeText(this, "Error deleting item", Toast.LENGTH_SHORT).show();
            }

        }
        finish();
    }


    @NonNull
    @Override
    public Loader<Cursor> onCreateLoader(int id, @Nullable Bundle args) {
        String[] projection = {
                InventoryEntry._ID,
                InventoryEntry.COLUMN_ITEM_NAME,
                InventoryEntry.COLUMN_PRICE,
                InventoryEntry.COLUMN_QUANTITY
        };
        //create a new cursor loader and return
        return new CursorLoader(this,
                mUrl,
                projection,
                null,
                null,
                null);

    }

    @Override
    public void onLoadFinished(@NonNull Loader<Cursor> loader, Cursor data) {
        //move the cursor to the first row
        if (data.moveToFirst()) {
            //get the data from the cursor
            int nameIndex = data.getColumnIndexOrThrow(InventoryEntry.COLUMN_ITEM_NAME);
            int priceIndex = data.getColumnIndexOrThrow(InventoryEntry.COLUMN_PRICE);
            int quantityIndex = data.getColumnIndexOrThrow(InventoryEntry.COLUMN_QUANTITY);

            //display the fetched data in the respective views
            mItemNameEditText.setText(data.getString(nameIndex));
            mPriceEditText.setText(Integer.toString(data.getInt(priceIndex)));
            mQuantityTextView.setText(Integer.toString(data.getInt(quantityIndex)));
            quantity = data.getInt(quantityIndex);
        }
    }

    @Override
    public void onLoaderReset(@NonNull Loader<Cursor> loader) {

    }
}
