package com.example.wekaradwan.bookstore20;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.example.wekaradwan.bookstore20.data.BookContract;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    /**
     * Identifier for the book data loader
     */
    private static final int BOOK_LOADER = 0;

    /**
     * Adapter for the Book data loader
     */
    BookCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // SetUp the FAB to open DetailsActivity
        FloatingActionButton floatingActionButton = (FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                startActivity(intent);
            }
        });
        // Find the listView which will populate the book data
        ListView bookListView = (ListView) findViewById(R.id.lvBookData);
        // Find set the empty vie on the ListView , so that it only shows the has no item.
        View emptyView = findViewById(R.id.emptyBook);
        bookListView.setEmptyView(emptyView);
        // Setup an adapter to create a list item for each row of book data in the cursor.
        // There is no book data yet (until the loader finishes) so pass in null for the cursor.
        cursorAdapter = new BookCursorAdapter(this, null);
        bookListView.setAdapter(cursorAdapter);
        // Set the OnItem  click listener
        bookListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int poastion, long id) {
                // Create new intent to go to DetailsActivity
                Intent intent = new Intent(MainActivity.this, DetailsActivity.class);
                // From te content URI that request the specific book that was click on,
                // by appending the "id" (passed as input to this method on to the {@link BookEntry#COUNTENT_URI}.
                // For example , the URI would be "content://com.example.android.BOOKS/BOOKS/2"
                // if the book with ID 2 was clicked on.
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                // Set the URI on the data filed of the intent
                intent.setData(currentBookUri);
                // Lunch the {@link DetailsActivity} to display the data for the current book.
                startActivity(intent);
            }
        });
        // Kick of the loader
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }

    /**
     * Helper method to insert fake data .
     */
    private void insertBook() {
        // Create a ContentValues object where column names are the keys,
        ContentValues values = new ContentValues();
        values.put(BookContract.BookEntry.COLUMN_BOOK_NAME, "Build your self");
        values.put(BookContract.BookEntry.COLUMN_BOOK_PRICE, "30");
        values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY, "20");
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME, "Ali Mohamed Radwan");
        values.put(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE, "01062767789");

        // Insert a new row for Build your self into the provider using the ContentResolver.
        // Use the {@link BookEntry#CONTENT_URI} to indicate that we want to insert
        // into the Book database table.
        // Receive the new content URI that will allow us to access Build your self's data in the future.
        Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);
    }

    /**
     * This method to delete all pets in the database
     */
    private void deleteAllBook() {
        int rowsDelete = getContentResolver().delete(BookContract.BookEntry.CONTENT_URI, null, null);
        Log.v("MainActivity", rowsDelete + " rows delete from database");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu from the res/menu/menu_main.xml file.
        // This add menu item to the app bar.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar
        switch (item.getItemId()) {
            case R.id.action_insert_fake_data:
                insertBook();
                return true;
            // on click on it show "Delete all Books "
            case R.id.deleteAllData:
                deleteAllBook();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        // define a projection that we specifies which column from the table we care about .
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_BOOK_NAME,
                BookContract.BookEntry.COLUMN_BOOK_PRICE,
                BookContract.BookEntry.COLUMN_BOOK_QUANTITY};

        // This loader will execute the ContentBookProvider query method on a background thread
        return new CursorLoader(this,   // Parent activity content
                BookContract.BookEntry.CONTENT_URI,      // Provider content URI to query
                projection,     // Columns to include int resulting Cursor
                null,           // No selection clause
                null,        // No selection arguments
                null);          // Default sort order
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        // Update {@link BookCurrentAdapter} with this new cursor updated book data
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Callback the loader when data need to be deleted
        cursorAdapter.swapCursor(null);
    }
}
