package com.example.wekaradwan.bookstore20;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.wekaradwan.bookstore20.data.BookContract;

/**
 * Allow user to create new book or edit one .
 */
public class DetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the Book data loader
     */
    private static final int EXISTING_BOOK_LOADER = 0;

    /**
     * Content URI for the existing book(null if it's a new book)
     */
    private Uri currentBookUri;

    /**
     * EditText field to enter the book name
     */
    private EditText nameEditText;

    /**
     * EditText filed to enter the book price
     */
    private EditText priceEditText;

    /**
     * EditText filed to enter the book quantity
     */
    private EditText quantityEditText;

    /**
     * EditText filed to enter the supplier name
     */
    private EditText supplierNameEditText;

    /**
     * EditText filed to enter the supplier phone
     */
    private EditText supplierPhoneEditText;

    /**
     * Button filed on click on increase the book quantity
     */
    private Button increaseButton;

    /**
     * Button filed on click on decrease the book quantity
     */
    private Button decreaseButton;

    /**
     * Button filed on click on delete the book item
     */
    private Button deleteButton;

    /**
     * Boolean flag that track that keeps track if re book has been edit (true) or (false)
     */
    private boolean bookHasChanged = false;

    private ImageView imageViewCall;

    /**
     * OnTouchListener that listeners for any user touches on a view , implying that they modifying
     * the view, and we change the bookHasChanged boolean to true.
     */
    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            bookHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.details);
        // Examine the intent that was used to lunch activity ,
        // in order to figure out if were creating a new book or edit an existing one.
        final Intent intent = getIntent();
        currentBookUri = intent.getData();
        // If the intent DOSE NOT contain a book content URI, then we know that we are
        // Creating a new Book.
        if (currentBookUri == null) {
            // This is a new Book so change the app bar to "Add Book"
            setTitle(R.string.add_new_book);
        } else {
            // Otherwise this is an existing Book , so change app bar to say "Edit Book"
            setTitle(R.string.edit_book);

            // Initialize a loader to read the book data from the database
            // and display the current value in the editor
            getLoaderManager().initLoader(EXISTING_BOOK_LOADER, null, this);
        }
        // Find all relevant views that we will need to read user input from
        nameEditText = (EditText) findViewById(R.id.etBookName);
        priceEditText = (EditText) findViewById(R.id.etBookPrice);
        quantityEditText = (EditText) findViewById(R.id.etBookQuantity);
        supplierNameEditText = (EditText) findViewById(R.id.etSupplierName);
        supplierPhoneEditText = (EditText) findViewById(R.id.etSupplierPhone);

        increaseButton = (Button) findViewById(R.id.btnIncreaseQuantity);
        decreaseButton = (Button) findViewById(R.id.btnDecreaseQuantity);
        deleteButton = (Button) findViewById(R.id.btnDelete);

        imageViewCall = (ImageView) findViewById(R.id.imvCall);

        // Setup OnTouchListeners on all the input fields, so we can determine if the user
        // has touched or modified them. This will let us know if there are unsaved changes
        // or not, if the user tries to leave the editor without saving.
        nameEditText.setOnTouchListener(touchListener);
        priceEditText.setOnTouchListener(touchListener);
        quantityEditText.setOnTouchListener(touchListener);
        supplierNameEditText.setOnTouchListener(touchListener);
        supplierPhoneEditText.setOnTouchListener(touchListener);

        /**
         *  When the user click here it will be increase the quantity by 1 every time.
         */
        increaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String increaseQuantity = quantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(increaseQuantity)) {
                    Integer itemQuantity = Integer.parseInt(increaseQuantity);
                    itemQuantity += 1;
                    quantityEditText.setText(String.valueOf(itemQuantity));
                }
            }
        });

        /**
         *  When the user click here it will be decrease the quantity by 1 every time.
         */
        decreaseButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String decreaseQuantity = quantityEditText.getText().toString().trim();
                if (!TextUtils.isEmpty(decreaseQuantity)) {
                    Integer itemQuantity = Integer.parseInt(decreaseQuantity);
                    if (itemQuantity > 0) {
                        itemQuantity = itemQuantity - 1;
                    } else {
                        itemQuantity = 0;
                    }
                    quantityEditText.setText(String.valueOf(itemQuantity));
                }
            }
        });

        /**
         *  When the user click here it will be delete the item .
         */
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        /**
         *  When the user click here it will be make call by the supplier.
         */
        imageViewCall.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent callIntent = new Intent(Intent.ACTION_DIAL);
                callIntent.setData(Uri.parse("tel:" + supplierPhoneEditText.getText().toString().trim()));
                startActivity(callIntent);
            }
        });
    }

    /**
     * GEt user input from the edit text and save into database.
     */
    private void saveBook() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String nameString = nameEditText.getText().toString().trim();
        String priceString = priceEditText.getText().toString().trim();
        String quantityString = quantityEditText.getText().toString().trim();
        String supplierName = supplierNameEditText.getText().toString().trim();
        String supplierPhone = supplierPhoneEditText.getText().toString().trim();

        // Check if this is a new book
        // check if all the fields in the editor are blank
        if (currentBookUri == null &&
                TextUtils.isEmpty(nameString) && TextUtils.isEmpty(priceString) &&
                TextUtils.isEmpty(quantityString) && TextUtils.isEmpty(supplierName) &&
                TextUtils.isEmpty(supplierPhone)) {
            // Since no filed were modified , we can return early without creating a new book.
            // No need to create ContentValues and no need to do any ContentProvider operations.
            return;
        }

        // Prevent the user from insert empty information .
        if (nameString.isEmpty()) {
            Toast.makeText(this, "Please Insert Book Name", Toast.LENGTH_LONG).show();
            return;
        }

        if (priceString.isEmpty()) {
            Toast.makeText(this, "Please Insert Book Price", Toast.LENGTH_LONG).show();
            return;
        }
        if (quantityString.isEmpty()) {
            Toast.makeText(this, "Please Insert Book Quantity", Toast.LENGTH_LONG).show();
            return;
        }
        if (supplierName.isEmpty()) {
            Toast.makeText(this, "Please Insert Supplier Name", Toast.LENGTH_LONG).show();
            return;
        }
        if (supplierPhone.isEmpty()) {
            Toast.makeText(this, "Please Insert Supplier Phone", Toast.LENGTH_LONG).show();
            return;
        }


        // Create a ContentValues  object column names are the keys ,
        // and book attributes from the editor are the values.
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, nameString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, priceString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, quantityString);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, supplierName);
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, supplierPhone);

        // Tf the the supplier phone is not provider by the user , don't try to parse the string into an
        // integer values .use 0 by default.

        int price = 0;
        if (!TextUtils.isEmpty(priceString)) {
            price = Integer.parseInt(priceString);
        }
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, price);

        int phone = 0;
        if (!TextUtils.isEmpty(supplierPhone)) {
            phone = Integer.parseInt(supplierPhone);
        }
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, phone);

        // Determine if the this is a new or existing book by checking if currentBookUri is null or not
        if (currentBookUri == null) {
            // This is a new book, so insert a new book into the provider,
            // returning the content URI fro the new book.
            Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

            // Show a toast message depending on whether or not the insertion was successful.
            if (newUri == null) {
                // If the new content URI is null , then there was an error with insertion.
                Toast.makeText(this, R.string.error_save_book, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise the insertion was successful we show toast.
                Toast.makeText(this, R.string.save_book, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an Existing book, so update the book with content URI currentBookUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because currentBookUri will already identify the correct row in the database that we want to modify.
            int rowAffected = getContentResolver().update(currentBookUri, values, null, null);

            // Show a toast message depending on whether or not the update was successful.
            if (rowAffected == 0) {
                // If no rows were affected , the update was successful and we can display a toast.
                Toast.makeText(this, R.string.error_update_book, Toast.LENGTH_LONG).show();
            } else {
                //Otherwise, then was an error with the update .
                Toast.makeText(this, R.string.update_book, Toast.LENGTH_LONG).show();
            }
        }
        // Exit activity
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu option from the res/menu/menu_details.xml file.
        // This add the menu item to the app bar
        getMenuInflater().inflate(R.menu.menu_details, menu);
        return true;
    }

    /**
     * This method is called after invalidateOptionsMenu(), so that the
     * menu can be updated (some menu items can be hidden or made visible).
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
        // If the is a new book, hide the "Delete" item
        if (currentBookUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User click on the menu option in the app overflow menu
        switch (item.getItemId()) {
            // Respond to click on the "save" option
            case R.id.action_save:
                // save book to database.
                saveBook();
                // Exit activity
                return true;
            // Respond ot click on the "delete" option
            case R.id.action_delete:
                // Pop up conformation dialog for deletion
                showDeleteConfirmationDialog();
                return true;
            // Respond to click on the "up" button in the app bar
            case android.R.id.home:
                // If the book hasn't change , continue with navigation up to parent activity
                // which will be the MainActivity
                if (!bookHasChanged) {
                    NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                    return true;
                }
                // Otherwise if there are unsaved changes, setup a dialog to warn the user.
                // Create a click listener to handle the user confirming that
                // changes should be discarded.
                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // User click "Discard" button , navigate to parent activity.
                                NavUtils.navigateUpFromSameTask(DetailsActivity.this);
                            }
                        };
                // Show a dialog that notifies the user they have unsaved changes
                showUnsavedChangesDialog(discardButtonClickListener);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * This method is called when the back is pressed.
     */
    @Override
    public void onBackPressed() {
        // If the book has not changed , continue  with handling back button press
        if (!bookHasChanged) {
            super.onBackPressed();
            return;
        }
        // Otherwise if there are unsaved changed, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that change should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        // User clicked"Discard" button, close the current activity .
                        finish();
                    }
                };
        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // Science the editor shows all book attributes , define a projection that contains
        // all columns from the book table
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME,
                BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE};
        // This loader will execute the ContentProvider's query method on background thread
        return new CursorLoader(this,       // parent activity content
                currentBookUri,         // Query the content URI for the current book
                projection,             // Columns to include in the resulting cursor
                null,           // No selection clause
                null,        // No selection arguments
                null);         // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {
        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor )
        if (cursor.moveToFirst()) {
            // Find the columns of the book attributes that we need
            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE);

            // Extract out the value from the cursor for the given column index
            String name = cursor.getString(nameColumnIndex);
            int price = cursor.getInt(priceColumnIndex);
            int quantity = cursor.getInt(quantityColumnIndex);
            String supplierName = cursor.getString(supplierNameColumnIndex);
            int supplierPhone = cursor.getInt(supplierPhoneColumnIndex);

            // Update the views on the screen with the values from the database
            nameEditText.setText(name);
            priceEditText.setText(Integer.toString(price));
            quantityEditText.setText(Integer.toString(quantity));
            supplierNameEditText.setText(supplierName);
            supplierPhoneEditText.setText(Integer.toString(supplierPhone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // If the loader is invalidate, clear out all the data from the input fields.
        nameEditText.setText("");
        priceEditText.setText("");
        quantityEditText.setText("");
        supplierNameEditText.setText("");
        supplierPhoneEditText.setText("");
    }

    /**
     * Show a dialog that warns the user there are unsaved changes that will be lost
     * if they continue leaving the editor.
     *
     * @param discardButtonClickListener
     */
    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {
        // Create an AlertDialog.Builder and set the message,and click listeners
        // for the positive and negative buttons on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.discard_changes_and_quite);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_edit, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                // User clicked the "keep editing" button , so dismiss the dialog
                // and continue editing the book.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Prompt the user to confirm that they want to delete this Book
     */
    private void showDeleteConfirmationDialog() {
        // Create an AlertDialog.Builder and set the message , and click listeners
        // for the positive and negative button on the dialog.
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_book);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // User clicked the Delete button, so delete the book.
                deleteBook();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int id) {
                // User click the cancel button so dismiss the dialog
                // and continue editing the book.
                if (dialogInterface != null) {
                    dialogInterface.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Perform the deletion of the Book in the database.
     */
    private void deleteBook() {
        // Only perform the delete if this is an existing book.
        if (currentBookUri != null) {
            // Call the contentResolver to delete the book at the given  content URI.
            // Pass in null for the selection and selection args because the currentBookUri.
            // content URI already identifier the book that we want
            int rowsDelete = getContentResolver().delete(currentBookUri, null, null);

            // Show a Toast message depending on whether or not the deletion was successful.
            if (rowsDelete == 0) {
                // If no rows were delete , then there was an error with the deletion.
                Toast.makeText(this, R.string.error_with_delete, Toast.LENGTH_LONG).show();
            } else {
                // Otherwise , the delete was successful and we can display a toast.
                Toast.makeText(this, R.string.book_delete, Toast.LENGTH_LONG).show();
            }
        }
        // close the activity
        finish();
    }
}