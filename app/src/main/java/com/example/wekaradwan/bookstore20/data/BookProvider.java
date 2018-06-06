package com.example.wekaradwan.bookstore20.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

/**
 * Created by weka radwan on 4/21/2018.
 */

public class BookProvider extends ContentProvider {
    /**
     * Tag for the log message
     */
    public static final String LOG_TAG = BookProvider.class.getSimpleName();

    /**
     * URI matcher code for the content URI for the books table
     */
    private static final int BOOKS = 404;
    /**
     * URI matcher code for the content URI for a single book in the books table .
     */
    private static final int BOOK_ID = 405;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     * The input passed into the constructor represents the code to return for the root URI.
     * It's common to use NO_MATCH as the input for this case.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // Static initializer. This is run the first time anything is called from this class.
    static {
        // The calls to addURI() go here , for all of the content URI parents that the provider
        // should recognize , All paths added to the UriMatcher have a corresponding code to return
        // when a match is found .
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS, BOOKS);
        // The content URI of the from "content://com.example.android.books/books/#" will map to the
        // integer code {@link #BOOK_ID}. This URI is used to provide access to ONE single row of the BOOkS table.

        // In this case, the "#" wildcard is used where "#" can be substituted for an integer.
        // For example, "content://com.example.android.BOOKS/BOOKS/4" matches , but
        // "content://com.example.android.BOOKS/BOOKS" (without a number at the end) doesn't match.
        uriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_BOOKS + "/#", BOOK_ID);
    }

    /**
     * Database helper object
     */
    private BookDbHelper bookDbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        bookDbHelper = new BookDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.Use the given projection, selection, selection arguments, and sort order.
     */
    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {

        // Get readable database
        SQLiteDatabase database = bookDbHelper.getReadableDatabase();
        // This cursor will hold the result of the query
        Cursor cursor;
        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // For the BOOKS code, query the books table directly with the given
                // Projection, selection, selection arguments, and sort order. The cursor
                // could contain multiple rows fo the Books table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case BOOK_ID:
                // For the BOOK_ID code ,extract out ID from the URI.
                // For an example URI such as "content://com.example.android.BOOKS/BOOKS/2",
                // the selection will be "_id=?" and the selection argument will be a
                // String array containing the actual ID of 2 in this case.

                // For every "?" in the selection, we need to have an element in the selection
                // arguments that will fill in the "?". Since we have 1 question mark in the
                // selection, we have 1 String in the selection arguments' String array.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // This will perform a query on the BOOKs table where the _id equals 2 to return a
                // Cursor containing that row of the table.
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException(" Can not query unknown URI " + uri);
        }
        // Set notification on the cursor ,
        // so we know what content URI the Cursor was created for.
        // If the data at this URI change ,then we know to update the cursor .
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        // Return the cursor
        return cursor;
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return insertBook(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not support for this" + uri);
        }
    }

    /**
     * Insert a book into the database with the given content values. Return the new content URI
     * for the specific row in the database .
     */
    private Uri insertBook(Uri uri, ContentValues values) {
        // Check that name is not null
        String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
        if (name == null) {
            throw new IllegalArgumentException(" Must Enter Book Name ");
        }
        // Check that the price is valid
        Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        if (price == null || (price != null && price < 0)) {
            throw new IllegalArgumentException(" Must Enter Book Price ");
        }
        // Check that the price is valid
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
        if (quantity == null || (quantity != null && quantity < 0)) {
            throw new IllegalArgumentException(" Must Enter Book Quantity ");
        }

        String supplierName = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME);
        if (supplierName == null) {
            throw new IllegalArgumentException(" Must Enter Supplier Name ");
        }


        // No need to check the Supplier name and phone , any value is valid (including null).

        // Writ in the database
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        // Insert the new book with the given values .
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        // If the id = -1 the the insertion failed. and log an error and return null.
        if (id == -1) {
            Log.e(LOG_TAG, "Can't insert row for  " + uri);
            return null;
        }
        // Notify all listener that the data has changed for the book content URI
        getContext().getContentResolver().notifyChange(uri, null);
        // Return the new URI with the ID (of the new insert row) append to the end.
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return updateBook(uri, contentValues, selection, selectionArgs);
            case BOOK_ID:
                // For the Book_ID code, get the id from the URI,
                // so we know which row is update.
                // Selection will be "_id=?" and selection arguments will be String array containing the actual ID.
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateBook(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException(" Update is not Supported for " + uri);
        }
    }

    /**
     * Update books in  the database with the given content values. Apply the changes to the rows
     * specified in the selection and selection arguments (which could be or 1 or more books).
     * Return the number of rows that were successfully updated.
     */
    public int updateBook(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        // If the {@link BookEntry#COLUMN_BOOKNAME} key is present,
        // check that the name values is not null.
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_BOOK_NAME);
            if (name == null) {
                throw new IllegalArgumentException("BOOKS Requires To Set Name");
            }
        }
        // If the {@link BookEntry#COLUMN_BOOK_GEDER} key is present,
        // check that the price value is valid.
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_PRICE)) {
            // Check that the price is greater than 0 or equal to 0 .
            Integer price = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_PRICE);
            if (price == null || (price != null && price < 0)) {
                throw new IllegalArgumentException("Must Enter Book Price");

            }
        }
        // If the {@link BookEntry#COLUMN_BOOK_GEDER} key is present,
        // check that the quantity value is valid.
        if (values.containsKey(BookContract.BookEntry.COLUMN_BOOK_QUANTITY)) {
            // Check that the quantity is greater than -1 or equal 0.
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);
            if (quantity == null || (quantity != null && quantity < 0)) {
                throw new IllegalArgumentException("Must Enter Book Quantity");
            }
        }
        // No need to check the Supplier name and phone , any value is valid (including null).

        // If need to check are no values to update , then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }
        // Writ database to update the database
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        // Perform the update on the data base and get the number of rows affected
        int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
        // If 1 or more rows were update the all listeners that the data at the given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of the rows update
        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {
        // Get write database
        SQLiteDatabase database = bookDbHelper.getWritableDatabase();
        // Track the number of rows that delete
        int rowsDelete;
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                // Delete all the rows that match the selection and the selection args.
                rowsDelete = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case BOOK_ID:
                // Delete single rows that match the selection and the selection args
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDelete = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Delete is not aviable for  " + uri);
        }
        // If 1 or more rows delete then tell all the listeners that the data at the
        // given URI has changed
        if (rowsDelete != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDelete;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case BOOKS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case BOOK_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri + " with match " + match);
        }
    }
}