package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    @BindView(R.id.fab_add_inventory_item) FloatingActionButton fab;
    @BindView(R.id.list_view) ListView productListView;
    @BindView(R.id.empty_view) View emptyView;

    private static final int PET_LOADER = 0;
    ProductCursorAdapter mProductCursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);

        // To access our database, we instantiate our subclass of SQLiteOpenHelper
        // and pass the context, which is the current activity.

        productListView.setEmptyView(emptyView);
        mProductCursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(mProductCursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long id) {
                Intent editProductIntent = new Intent(InventoryActivity.this, AddProductActivity.class);

                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                editProductIntent.setData(currentProductUri);

                startActivity(editProductIntent);
            }
        });

        getLoaderManager().initLoader(PET_LOADER, null, this);
    }

    private void insertDummyProduct() {
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "iPhone 5s");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 123.5);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 100);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, "Apple");
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, "123-456-7890");

        Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

        Toast.makeText(this, "Dummy product saved.", Toast.LENGTH_SHORT).show();
    }

    private void deleteAllProducts() {
        int rowsDeleted = getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);

        if (rowsDeleted > 0) {
            Toast.makeText(this, R.string.action_delete_all_entries, Toast.LENGTH_SHORT).show();
        }

        Log.v("InventoryActivity", rowsDeleted + " rows deleted from inventory database.");
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
                return true;
            case R.id.action_delete_all_products:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Instantiate and return a new Loader for the given ID.
     *
     * @param id   The ID whose loader is to be created.
     * @param args Any arguments supplied by the caller.
     * @return Return a new Loader instance that is ready to start loading.
     */
    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null);
    }

    /**
     * Called when a previously created loader has finished its load.  Note
     * that normally an application is <em>not</em> allowed to commit fragment
     * transactions while in this call, since it can happen after an
     * activity's state is saved.
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        mProductCursorAdapter.swapCursor(data);
    }

    /**
     * Called when a previously created loader is being reset, and thus
     * making its data unavailable.  The application should at this point
     * remove any references it has to the Loader's data.
     *
     * @param loader The Loader that is being reset.
     */
    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mProductCursorAdapter.swapCursor(null);
    }
}
