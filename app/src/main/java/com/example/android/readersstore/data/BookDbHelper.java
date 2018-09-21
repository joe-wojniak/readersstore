package com.example.android.readersstore.data;

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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class BookDbHelper extends SQLiteOpenHelper {
    public static final String LOG_TAG = BookDbHelper.class.getSimpleName();

    public static final String DATABASE_NAME = "readersstore.db";
    private static final int DATABASE_VERSION = 1;

    public BookDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String SQL_CREATE_STORE_TABLE = "CREATE TABLE " + BookContract.BookEntry.TABLE_NAME + " ("
                + BookContract.BookEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + BookContract.BookEntry.COLUMN_PROD_NAME + " TEXT, "
                + BookContract.BookEntry.COLUMN_PRICE + " REAL, "
                + BookContract.BookEntry.COLUMN_QTY + " INTEGER, "
                + BookContract.BookEntry.COLUMN_SUPPLIER + " TEXT, "
                + BookContract.BookEntry.COLUMN_PHONE + " NUMERIC);";

        Log.i( LOG_TAG, "SQL_CREATE_STORE_TABLE " + SQL_CREATE_STORE_TABLE);

        db.execSQL(SQL_CREATE_STORE_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // placeholder - database is at version 1
    }
}

