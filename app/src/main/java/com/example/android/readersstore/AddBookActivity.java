package com.example.android.readersstore;

/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Sections of example code ud845-pets were modified to complete
 * ABND Project Database App Stage 2: Sept-2018
 */

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
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.android.readersstore.data.BookContract;

import java.text.DecimalFormat;

/**
 * Allows user to create a new product or edit an existing one.
 */

public class AddBookActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int EXISTING_PRODUCTLOADER = 0;
    private Uri mCurrentProductUri;
    private EditText mProdNameEdit;
    private EditText mPriceEdit;
    private EditText mQtyEdit;
    private EditText mSuppNameEdit;
    private EditText mSuppPhoneEdit;

    private boolean mProductHasChanged = false;
    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent motionEvent) {
            mProductHasChanged = true;
            return false;
        }
    };
    /* OnKeyListener fix from github comments.
     * https://github.com/udacity/ud845-Pets/commit/bea7d9080f06d447892c634f6271cb83eef9762b
     * Author: trigal2012
     */
    private View.OnKeyListener mKeyListener = new View.OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_book);

        Intent intent = getIntent();
        mCurrentProductUri = intent.getData();
        if (mCurrentProductUri == null) {
            setTitle(getString(R.string.editor_add_book));
            invalidateOptionsMenu();
        } else {
            setTitle(getString(R.string.editor_edit_book));

            getLoaderManager().initLoader(EXISTING_PRODUCTLOADER, null, this);
        }

        mProdNameEdit = (EditText) findViewById(R.id.edit_product_name);
        mPriceEdit = (EditText) findViewById(R.id.edit_price);
        mQtyEdit = (EditText) findViewById(R.id.edit_qty);
        mSuppNameEdit = (EditText) findViewById(R.id.edit_supplier);
        mSuppPhoneEdit = (EditText) findViewById(R.id.edit_phone);

        ImageButton addButton = findViewById(R.id.qty_add_button);
        addButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyString = mQtyEdit.getText().toString().trim();
                if (TextUtils.isEmpty(qtyString)) {
                    return;
                } else {
                    int qty = Integer.parseInt(qtyString);
                    qty = qty + 1;
                    qtyString = Integer.toString(qty);
                    mQtyEdit.setText(qtyString);
                }
            }
        });

        ImageButton minusButton = findViewById(R.id.qty_remove_button);
        minusButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String qtyString = mQtyEdit.getText().toString().trim();
                if (TextUtils.isEmpty(qtyString)) {
                    return;
                } else {
                    int qty = Integer.parseInt(qtyString);
                    qty = qty - 1;
                    qtyString = Integer.toString(qty);
                    mQtyEdit.setText(qtyString);
                }

            }
        });
        /* Code in else statement to pass intent to Dialer is from stackoverflow.
         * https://stackoverflow.com/questions/4275678/how-to-make-a-phone-call-using-intent-in-android
         * Authors: Jonik, Ridcully
         */
        ImageButton callButton = findViewById(R.id.call_button);
        callButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String phoneString = mSuppPhoneEdit.getText().toString().trim();
                if (TextUtils.isEmpty(phoneString)) {
                    return;
                } else {
                    phoneString = phoneString.replaceAll("[-()]+", "");
                    String uri = "tel:"+phoneString;
                    Intent intent = new Intent(Intent.ACTION_DIAL);
                    intent.setData(Uri.parse(uri));
                    startActivity(intent);
                }

            }
        });

        ImageButton deleteButton = findViewById(R.id.delete_button);
        deleteButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDeleteConfirmationDialog();
            }
        });

        mProdNameEdit.setOnTouchListener(mTouchListener);
        mProdNameEdit.setOnKeyListener(mKeyListener);
        mPriceEdit.setOnTouchListener(mTouchListener);
        mPriceEdit.setOnKeyListener(mKeyListener);
        mQtyEdit.setOnTouchListener(mTouchListener);
        mQtyEdit.setOnKeyListener(mKeyListener);
        mSuppNameEdit.setOnTouchListener(mTouchListener);
        mSuppNameEdit.setOnKeyListener(mKeyListener);
        mSuppPhoneEdit.setOnTouchListener(mTouchListener);
        mSuppPhoneEdit.setOnKeyListener(mKeyListener);
    }

    private void saveProduct() {

        String nameString = mProdNameEdit.getText().toString().trim();
        String priceString = mPriceEdit.getText().toString().trim();
        String qtyString = mQtyEdit.getText().toString().trim();
        String supplierNameString = mSuppNameEdit.getText().toString().trim();
        String phoneString = mSuppPhoneEdit.getText().toString().trim();

        // Declare local variables for price, qty & phone number
        double price = 0;
        int qty = 0;
        int phone = 0;

        if (mCurrentProductUri == null &&
                TextUtils.isEmpty(nameString) || TextUtils.isEmpty(priceString) ||
                TextUtils.isEmpty(qtyString) || TextUtils.isEmpty(supplierNameString) ||
                TextUtils.isEmpty(phoneString)) {
            Toast.makeText(this, "Please enter all required information: " +
                    "Product name: " + nameString + " Price: " + priceString + " Quantity: " + qtyString +
                    " Supplier name: " + supplierNameString + " Phone number: " + phoneString, Toast.LENGTH_LONG).show();

        } else {
            /* remove characters . - ( ) from EditText inputs & use code snippet from stackoverflow
             * to handle EditText entry for price.
             * https://stackoverflow.com/questions/6866633/converting-string-to-double-in-android
             * code snippet author: bershadskiy
             * https://stackoverflow.com/users/1841970/bershadskiy
             */
            try {
                DecimalFormat dF = new DecimalFormat("0.00");
                Number num = dF.parse(priceString);
                price = num.doubleValue();
            } catch (Exception e) {
                price = Double.parseDouble(priceString);
            }

            phoneString = phoneString.replaceAll("[-()]+", "");

            qty = Integer.parseInt(qtyString);
            phone = Integer.parseInt(phoneString);

            ContentValues values = new ContentValues();
            values.put(BookContract.BookEntry.COLUMN_PROD_NAME, nameString);
            values.put(BookContract.BookEntry.COLUMN_PRICE, price);
            values.put(BookContract.BookEntry.COLUMN_QTY, qty);
            values.put(BookContract.BookEntry.COLUMN_SUPPLIER, supplierNameString);
            values.put(BookContract.BookEntry.COLUMN_PHONE, phone);

            if (mCurrentProductUri == null) {

                Uri newUri = getContentResolver().insert(BookContract.BookEntry.CONTENT_URI, values);

                if (newUri == null) {

                    Toast.makeText(this, getString(R.string.editor_insert_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            } else {

                int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

                if (rowsAffected == 0) {

                    Toast.makeText(this, getString(R.string.editor_update_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {

                    Toast.makeText(this, getString(R.string.editor_insert_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete);
            menuItem.setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.action_save:

                saveProduct();

                finish();
                return true;

            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;

            case android.R.id.home:

                NavUtils.navigateUpFromSameTask(this);

                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener =
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {

                                NavUtils.navigateUpFromSameTask(AddBookActivity.this);
                            }
                        };

                showUnsavedChangesDialog(discardButtonClickListener);
                return true;

        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {

        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }
        DialogInterface.OnClickListener discardButtonClickListener =
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {

                        finish();
                    }
                };
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int i, Bundle bundle) {
        String[] projection = {
                BookContract.BookEntry._ID,
                BookContract.BookEntry.COLUMN_PROD_NAME,
                BookContract.BookEntry.COLUMN_PRICE,
                BookContract.BookEntry.COLUMN_QTY,
                BookContract.BookEntry.COLUMN_SUPPLIER,
                BookContract.BookEntry.COLUMN_PHONE
        };

        return new CursorLoader(this, mCurrentProductUri, projection, null, null, null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        if (cursor == null || cursor.getCount() < 1) {
            return;
        }

        if (cursor.moveToFirst()) {

            int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PROD_NAME);
            int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
            int qtyColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QTY);
            int supplierColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_SUPPLIER);
            int suppphoneColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PHONE);

            String prodName = cursor.getString(nameColumnIndex);
            int prodPrice = cursor.getInt(priceColumnIndex);
            int prodqty = cursor.getInt(qtyColumnIndex);
            String prodSupname = cursor.getString(supplierColumnIndex);
            int prodsuppphone = cursor.getInt(suppphoneColumnIndex);

            mProdNameEdit.setText(prodName);
            mPriceEdit.setText(Double.toString(prodPrice));
            mQtyEdit.setText(Integer.toString(prodqty));
            mSuppNameEdit.setText(prodSupname);
            mSuppPhoneEdit.setText(Integer.toString(prodsuppphone));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {

        mProdNameEdit.setText("");
        mPriceEdit.setText("");
        mQtyEdit.setText("");
        mSuppNameEdit.setText("");
        mSuppPhoneEdit.setText("");
    }

    private void showUnsavedChangesDialog(
            DialogInterface.OnClickListener discardButtonClickListener) {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_msg);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void showDeleteConfirmationDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.dialog_delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void deleteProduct() {

        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);
            if (rowsDeleted == 0) {
                Toast.makeText(this, getString(R.string.editor_delete_product_failed),
                        Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, getString(R.string.editor_delete_product_successful),
                        Toast.LENGTH_SHORT).show();
            }
        }
        finish();
    }

}
