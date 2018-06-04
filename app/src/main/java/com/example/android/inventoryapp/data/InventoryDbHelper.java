package com.example.android.inventoryapp.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

/**
 * Created by grzegorzwilusz on 6/4/18.
 */

public class InventoryDbHelper extends SQLiteOpenHelper{

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "inventory.db";

    private static final String SQL_CREATE_QUERIES =
            "CREATE TABLE " + ProductEntry.TABLE_NAME + " (" +
                    ProductEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    ProductEntry.COLUMN_PRODUCT_NAME + " TEXT NOT NULL, " +
                    ProductEntry.COLUMN_PRODUCT_PRICE + " INTEGER NOT NULL, " +
                    ProductEntry.COLUMN_PRODUCT_QUANTITY + " INTEGER DEFAULT 0, " +
                    ProductEntry.COLUMN_SUPPLIER_NAME + " TEXT NOT NULL, " +
                    ProductEntry.COLUMN_SUPPLIER_NAME + " INTEGER NOT NULL);";
    private static final String SQL_DELETE_QUERIES =
            "DROP TABLE IF EXISTS " + ProductEntry.TABLE_NAME;

    public InventoryDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_QUERIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_QUERIES);
        onCreate(db);
    }
}
