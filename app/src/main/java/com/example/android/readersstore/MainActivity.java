package com.example.android.readersstore;

import android.content.ContentUris;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;

import com.example.android.readersstore.data.BookContract;
import com.example.android.readersstore.data.BookDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    private static final String LOG_TAG = MainActivity.class.getName();
    // Create global variable that holds the BookDbHelper
    BookDbHelper mDbHelper;
    BookCursorAdapter mCursorAdapter;
    private static final int BOOK_LOADER = 0;
    @BindView(R.id.list)
    ListView listView;
    @BindView(R.id.empty_view)
    TextView emptyView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        // Create instance of the BookDbHelper and store it in the variable
        mDbHelper = new BookDbHelper(this);
        listView.setEmptyView(emptyView);
        mCursorAdapter = new BookCursorAdapter(this, null);
        listView.setAdapter(mCursorAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(MainActivity.this, AddBookActivity.class);
                Uri currentBookUri = ContentUris.withAppendedId(BookContract.BookEntry.CONTENT_URI, id);
                intent.setData(currentBookUri);
                startActivity(intent);
            }
        });
        getLoaderManager().initLoader(BOOK_LOADER, null, this);
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // On selecting the menu add book option start an intent that takes to the AddBookActivity
            case R.id.add_book:
                Intent intent = new Intent(this, AddBookActivity.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {BookContract.BookEntry.COLUMN_PROD_NAME, BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QTY};
        return new CursorLoader(this, BookContract.BookEntry.CONTENT_URI, projection,
                null, null,null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mCursorAdapter.swapCursor(data);
    }
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mCursorAdapter.swapCursor(null);
    }
}