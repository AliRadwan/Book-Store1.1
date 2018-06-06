package com.example.wekaradwan.bookstore20.data;

/**
 * Created by weka radwan on 4/20/2018.
 */

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Database helper for books store app and  Mange database creation and version management.
 */
public class BookDbHelper extends SQLiteOpenHelper {

    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    /**
     * Database version .
     * If you change the database schema ,you must increment the database version.
     */
    private static final String DATABASE_NAME = "Bookstore.db";

    /**
     * Constructs a new instance of {@link BookDbHelper}.
     *
     * @param context of the app
     */
    private static final int DATABASE_VERSION = 1;

    /**
     *  Constructs a new instance of {@link BookDbHelper}.
     * @param context of the app
     */
    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME,null, DATABASE_VERSION);
    }

    /**
     * This is called when the database is create for the first time .
     */
    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        // Create a string object that have the SQL statement  to create the books table
        String SQL_CREATE_BOOKS_TABLE = "CREATE TABLE "
                + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_BOOK_NAME + " TEXT NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_PRICE + " REAL NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_QUANTITY + " INTEGER NOT NULL, "
                + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_BOOK_SUPPLIER_PHONE + " INTEGER DEFAULT 0);";

         sqLiteDatabase.execSQL(SQL_CREATE_BOOKS_TABLE);
    }

    /**
     *  This is called when the Database need to upgraded.
     * @param sqLiteDatabase
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int oldVersion, int newVersion) {
        // The database is still at version 1, so there's nothing to be done here.
    }
}
