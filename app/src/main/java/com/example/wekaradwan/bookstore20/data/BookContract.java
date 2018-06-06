package com.example.wekaradwan.bookstore20.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by weka radwan on 4/20/2018.
 */

public class BookContract {
    // To prevent someone from accidentally instantiating the contract class,
    // give it an empty constructor.
    private BookContract() {}

    /**
     * The "Content authority" is a name for the entire content provider, similar to the
     * relationship between a domain name and its website.  A convenient string to use for the
     * content authority is the package name for the app, which is guaranteed to be unique on the device.
     */
    public static final String CONTENT_AUTHORITY = "com.example.android.books";

    /**
     * Use CONTENT_AUTHORITY to create the base of all URI's which apps will use to contact
     * the content provider.
     */
    public  static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /**
     * Possible path (appended to base content URI for possible URI's)
     * For instance, content://com.example.android.Books/Books/ is a valid path for
     * looking at book data . content://com.example.android.Books/lib  will fail,
     * as the ContentProvider hasn't been given any information on what to do with "staff".
     */
    public static final String PATH_BOOKS = "books";

    /**
     * Inner class that define constant values for the books database table
     * Each entry in the table represents a single book .
     */
    public static final class BookEntry implements BaseColumns {
        /** The Content URI to Access the BOOK data in the Provider*/
        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_BOOKS);

        /** The MIME type of the {@link #CONTENT_URI} for a list of books.*/
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /** The MIME type of the {@link #CONTENT_URI} for a single book.*/
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_BOOKS;

        /**
         * Name of database table for books
         */
        public final static String TABLE_NAME = "books";

        /**
         * Unique ID number for the book (only for use in the database table).
         * Type : INTEGER
         */
        public final static String _ID = BaseColumns._ID;

        /**
         * Name Of Book
         * Type : TEXT
         */
        public final static String COLUMN_BOOK_NAME = "name";

        /**
         * Price Of Book
         * Type : INTEGER
         */
        public final static String COLUMN_BOOK_PRICE = "price";

        /**
         * Quantity Of Book
         * Type : INTEGER
         */
        public final static String COLUMN_BOOK_QUANTITY = "quantity";

        /**
         * Name of the Supplier
         * Type : TEXT
         */
        public final static String COLUMN_BOOK_SUPPLIER_NAME = "supplierName";

        /**
         * Number of the Supplier
         * Type : INTEGER
         */
        public final static String COLUMN_BOOK_SUPPLIER_PHONE = "supplierPhone";
    }
}
