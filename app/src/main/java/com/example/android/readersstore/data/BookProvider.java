package com.example.android.readersstore.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.util.Log;

/**
 * {@link ContentProvider} for Store app.
 */

public class BookProvider extends ContentProvider {

    public static final String LOG_TAG = BookProvider.class.getSimpleName();
    private BookDbHelper mDbHelper;

    @Override
    public boolean onCreate() {
        mDbHelper = new BookDbHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {

        SQLiteDatabase database = mDbHelper.getReadableDatabase();

        Cursor cursor;

        int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            case PRODUCT_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                cursor = database.query(BookContract.BookEntry.TABLE_NAME, projection, selection, selectionArgs, null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI" + uri);
        }
        //set notification URI
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("URI error: " + uri);
        }
    }

    private Uri insertProduct(Uri uri, ContentValues values) {
        //check for valid entries
        String name = values.getAsString(BookContract.BookEntry.COLUMN_PROD_NAME);
        if (name == null) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        Long price = values.getAsLong(BookContract.BookEntry.COLUMN_PRICE);
        if (price == null || price <= 0) {
            throw new IllegalArgumentException("Price needs to be greater than 0");
        }
        Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_QTY);
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity needs to be greater 0");
        }
        String supname = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER);
        if (supname == null) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
        }
        Integer phone = values.getAsInteger(BookContract.BookEntry.COLUMN_PHONE);
        if (phone == null) {
            throw new IllegalArgumentException("Phone number cannot be empty");
        }
        //to get the writable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        //insert new product with with given values
        long id = database.insert(BookContract.BookEntry.TABLE_NAME, null, values);
        if (id == -1) {
            Log.e(LOG_TAG, "insert row failed for " + uri);
            return null;
        }
        //when data for product uri has changed notify the listeners
        getContext().getContentResolver().notifyChange(uri, null);
        //return the new uri with the id appended at the end
        return ContentUris.withAppendedId(uri, id);
    }

    //updating  the data at the given selection and selection argument with the new content values
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection, String[] selectionArgs) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update failed using " + uri);
        }
    }

    //update the products in the database with the given content values
    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (values.containsKey(BookContract.BookEntry.COLUMN_PROD_NAME)) {
            String name = values.getAsString(BookContract.BookEntry.COLUMN_PROD_NAME);
            if (name == null) {
                throw new IllegalArgumentException("Product name cannot be empty");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_PRICE)) {
            Long price = values.getAsLong(BookContract.BookEntry.COLUMN_PRICE);
            if (price == null || price <= 0) {
                throw new IllegalArgumentException("Price must be greater 0");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_QTY)) {
            Integer quantity = values.getAsInteger(BookContract.BookEntry.COLUMN_QTY);
            if (quantity == null || quantity <= 0) {
                throw new IllegalArgumentException("Quantity must be greater than 0");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_SUPPLIER)) {
            String supname = values.getAsString(BookContract.BookEntry.COLUMN_SUPPLIER);
            if (supname == null) {
                throw new IllegalArgumentException("Suppler name cannot be empty");
            }
        }
        if (values.containsKey(BookContract.BookEntry.COLUMN_PHONE)) {
            Integer phone = values.getAsInteger(BookContract.BookEntry.COLUMN_PHONE);
            if (phone == null) {
                throw new IllegalArgumentException("Phone number cannot be empty");
            }
        }
        // If there are no values, then don't update the database
        if (values.size() == 0) {
            throw new IllegalArgumentException("No values to update");
        } else {
            // Otherwise, get writeable database to update the data
            SQLiteDatabase database = mDbHelper.getWritableDatabase();
            // Perform the update on the database and get the number of rows affected
            int rowsUpdated = database.update(BookContract.BookEntry.TABLE_NAME, values, selection, selectionArgs);
            // If 1 or more rows were updated, then notify all listeners that the data at the given URI has changed
            if (rowsUpdated != 0) {
                getContext().getContentResolver().notifyChange(uri, null);
            }
            // Return the number of rows updated
            return rowsUpdated;
        }
    }

    //delete the data for the given URI
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = mDbHelper.getWritableDatabase();
        // Track the number of rows that were deleted
        int rowsDeleted;
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                // Delete all rows matching the selection and selection args
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = BookContract.BookEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                rowsDeleted = database.delete(BookContract.BookEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }
        // Return the number of rows deleted
        return rowsDeleted;
    }

    //returning MIME type of data for the contetn uri
    @Override
    public String getType(Uri uri) {
        final int match = sUriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return BookContract.BookEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return BookContract.BookEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }

    //product table uri matcher code for the content URI
    private static final int PRODUCTS = 100;
    //single product uri matcher code for the content Uri
    private static final int PRODUCT_ID = 101;
    //uri matcher object for matching a content URI to a corresponding code
    private static final UriMatcher sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    //static initializer is run the first time after something is called from this class
    static {
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_PRODUCTS, PRODUCTS);
        sUriMatcher.addURI(BookContract.CONTENT_AUTHORITY, BookContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }
}
