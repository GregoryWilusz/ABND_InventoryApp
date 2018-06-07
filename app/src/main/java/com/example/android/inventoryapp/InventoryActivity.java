package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InventoryActivity extends AppCompatActivity {

    @BindView(R.id.fab_add_inventory_item) FloatingActionButton fab;
    //@BindView(R.id.text_view_inventory) TextView dataBaseInfoTextView;

    private InventoryDbHelper mInventoryDbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.
        mInventoryDbHelper = new InventoryDbHelper(this);

        displayDatabaseInfo();
    }

    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = mInventoryDbHelper.getReadableDatabase();

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE,
        };

        Cursor cursor = db.query(ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null);

        try {
            // Create a table's number of records title and the header.
//            dataBaseInfoTextView.setText(String.format("%s %d %s", getString(R.string.table_contains), cursor.getCount(), getString(R.string.products)));
//            dataBaseInfoTextView.append(ProductEntry._ID + " - " +
//            ProductEntry.COLUMN_PRODUCT_NAME + " - " +
//            ProductEntry.COLUMN_PRODUCT_PRICE + " - " +
//            ProductEntry.COLUMN_PRODUCT_QUANTITY + " - " +
//            ProductEntry.COLUMN_SUPPLIER_NAME + " - " +
//            ProductEntry.COLUMN_SUPPLIER_PHONE + "\n");

            // Match the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_NAME);
            int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_SUPPLIER_PHONE);

            // Iterate through all the returned rows in the cursor, extract values and display them in the TextView
            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String currentProductName = cursor.getString(productNameColumnIndex);
                double currentProductPrice = cursor.getDouble(productPriceColumnIndex);
                int currentProductQuantity = cursor.getInt(productQuantityColumnIndex);
                String currentSupplierName = cursor.getString(supplierNameColumnIndex);
                String currentSupplierPhone = cursor.getString(supplierPhoneColumnIndex);

//                dataBaseInfoTextView.append("\n" + currentId + " - " +
//                        currentProductName + " - " +
//                        currentProductPrice + " - " +
//                        currentProductQuantity + " - " +
//                        currentSupplierName + " - " +
//                        currentSupplierPhone);
            }
        } finally {
            cursor.close();
        }
    }

    private void insertDummyProduct() {
        SQLiteDatabase db = mInventoryDbHelper.getWritableDatabase();

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "iPhone 5s");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 123.5);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 100);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Apple");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "123-456-7890");

        // Insert the new row, returning the primary key value of the new row
        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        Log.v("InventoryActivity", "New row ID: " + newRowId);
    }

    @Override
    protected void onStart() {
        super.onStart();
        displayDatabaseInfo();
    }

    @OnClick(R.id.fab_add_inventory_item)
    public void onFabClick(View view) {
        Intent addProductIntent = new Intent(InventoryActivity.this, AddProductActivity.class);
        startActivity(addProductIntent);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_insert_dummy_product:
                insertDummyProduct();
                displayDatabaseInfo();
        }
        return super.onOptionsItemSelected(item);
    }
}
