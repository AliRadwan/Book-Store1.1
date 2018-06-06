package com.example.wekaradwan.bookstore20;

import android.content.ContentResolver;
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
import android.widget.Toast;

import com.example.wekaradwan.bookstore20.data.BookContract;

/**
 * Created by weka radwan on 4/22/2018.
 */

/**
 * {@link BookCursorAdapter} is an adapter for a list view
 * that uses a{@link Cursor} of book as its data source. This adapter knows
 * how to create list items for each row of book data in the {@link Cursor}.
 */
public class BookCursorAdapter extends CursorAdapter {

    // Button filed on click on saleButton the book item
    private Button saleButton;

    private final Context wContext;

    /**
     * Constructs a new {@link BookCursorAdapter}
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     */
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0/* flag */);
        wContext = context;
    }

    /**
     * Makes a new blank list item view. No data is set (or bound) to the views yet.
     *
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created list item view
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    /**
     * This method bind the book data (in the current row pointed to by the cursor ) to
     * the given list item view layout.
     * EX-
     * the name of the current book can be set to the name TextView in the list item layout.
     *
     * @param view    Existing view , returned ealier by newView() method
     * @param context app context
     * @param cursor  The cursor from which to get the data. The cursor is already moved to the correct row.
     */
    @Override
    public void bindView(final View view, final Context context, final Cursor cursor) {
        // Find the views that we want to modify inn the list item layout
        TextView nameTextView = (TextView) view.findViewById(R.id.tvName);
        TextView priceTextView = (TextView) view.findViewById(R.id.tvPrice);
        TextView quantityTextView = (TextView) view.findViewById(R.id.tvQuantity);

        saleButton = (Button) view.findViewById(R.id.btnSale);

        // Find the column of the boot table that we want
        final int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_BOOK_QUANTITY);


         int bookId = cursor.getInt(cursor.getColumnIndex(BookContract.BookEntry._ID));

        // Read the book attributes from the cursor for the current book.
        String bookNmae = cursor.getString(nameColumnIndex);
        String bookPrice = cursor.getString(priceColumnIndex);
        final int Quantity = cursor.getInt(quantityColumnIndex);

        String bookQuantity = String.valueOf(Quantity)+"";

     final  Uri  currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI,bookId);

        // Update the TextView with the attributes for the current book
        nameTextView.setText(bookNmae);
        priceTextView.setText(bookPrice);
        quantityTextView.setText(bookQuantity);

        /**
         *  Here when we click on sale button it decrease by 1.
         */
        saleButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ContentResolver contentResolver = view.getContext().getContentResolver();
                ContentValues values = new ContentValues();

                if (Quantity > 0){
                    int qq = Quantity;
                    values.put(BookContract.BookEntry.COLUMN_BOOK_QUANTITY,--qq);
                    contentResolver.update(
                            currentBookUri,
                            values,
                            null,
                            null
                    );
                    context.getContentResolver().notifyChange(currentBookUri,null);
                }else {
                    Toast.makeText(context," Quantity = 0 ",Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}