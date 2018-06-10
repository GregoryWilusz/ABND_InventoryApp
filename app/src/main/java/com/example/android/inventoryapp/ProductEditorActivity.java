package com.example.android.inventoryapp;

import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ProductEditorActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private EditText mProductName;
    @BindView(R.id.add_product_name) TextView productNameTextView;
    private EditText mProductPrice;
    @BindView(R.id.add_product_price) TextView productPriceTextView;
    private EditText mProductQuantity;
    @BindView(R.id.add_product_quantity) TextView productQuantityTextView;
    private EditText mSupplierName;
    @BindView(R.id.add_supplier_name) TextView supplierNameTextView;
    private EditText mSupplierPhone;
    @BindView(R.id.add_supplier_phone) TextView supplierPhoneTextView;

    private static final int PET_LOADER = 0;
    private Uri mCurrentProductUri;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri currentUri = intent.getData();

        if (currentUri != null) {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            mCurrentProductUri = currentUri;
            getLoaderManager().initLoader(PET_LOADER, null, this);
        } else {
            setTitle(getString(R.string.editor_activity_title_add_new_product));

            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }

        mProductName = (EditText) productNameTextView;
        mProductPrice = (EditText) productPriceTextView;
        mProductQuantity = (EditText) productQuantityTextView;
        mSupplierName = (EditText) supplierNameTextView;
        mSupplierPhone = (EditText) supplierPhoneTextView;

    }

    /**
     * Prepare the Screen's standard options menu to be displayed.  This is
     * called right before the menu is shown, every time it is shown.  You can
     * use this method to efficiently enable/disable items or otherwise
     * dynamically modify the contents.
     * <p>
     * <p>The default implementation updates the system menu items based on the
     * activity's state.  Deriving classes should always call through to the
     * base class implementation.
     *
     * @param menu The options menu as last shown or first initialized by
     *             onCreateOptionsMenu().
     * @return You must return true for the menu to be displayed;
     * if you return false it will not be shown.
     * @see #onCreateOptionsMenu
     */
    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);

        if (mCurrentProductUri == null) {
            MenuItem menuItem = menu.findItem(R.id.action_delete_product);
            menuItem.setVisible(false);
        }
        return true;
    }

    private void saveProduct() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String prodName = mProductName.getText().toString().trim();
        String prodPrice = mProductPrice.getText().toString().trim();
        String prodQuantity = mProductQuantity.getText().toString().trim();
        String supplName = mSupplierName.getText().toString().trim();
        String supplPhone = mSupplierPhone.getText().toString().trim();

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, prodName);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, prodPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, prodQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplPhone);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.editor_activity_saving_new_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_activity_saving_new_product_successful, Toast.LENGTH_SHORT).show();
            }
        } else {
            // Otherwise this is an EXISTING pet, so update the pet with content URI: mCurrentPetUri
            // and pass in the new ContentValues. Pass in null for the selection and selection args
            // because mCurrentPetUri will already identify the correct row in the database that
            // we want to modify.
            // Update current pet into the provider, returning the content URI for the new pet.
            int rowsAffected = getContentResolver().update(mCurrentProductUri, values, null, null);

            if (rowsAffected == 0) {
                Toast.makeText(this, R.string.editor_activity_updating_current_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_activity_updating_current_product_successful, Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void deleteProduct() {
        if (mCurrentProductUri != null) {
            int rowsDeleted = getContentResolver().delete(mCurrentProductUri, null, null);

            if (rowsDeleted > 0) {
                Toast.makeText(this, R.string.editor_activity_deleting_current_product_successful, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_activity_deleting_current_product_failed, Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_add_product_activity, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.action_save_product:
                saveProduct();
                finish();
                return true;
            case R.id.action_delete_product:
                deleteProduct();
                finish();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
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
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_SUPPLIER_NAME,
                ProductEntry.COLUMN_SUPPLIER_PHONE
        };

        return new CursorLoader(this,
                mCurrentProductUri,
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
     * <p>
     * <p>This function is guaranteed to be called prior to the release of
     * the last data that was supplied for this Loader.  At this point
     * you should remove all use of the old data (since it will be released
     * soon), but should not do your own release of the data since its Loader
     * owns it and will take care of that.  The Loader will take care of
     * management of its data so you don't have to.
     *
     * @param loader The Loader that has finished.
     * @param data   The data generated by the Loader.
     */
    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data.moveToFirst()) {
            mProductName.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_NAME)));
            mProductPrice.setText(Float.toString(data.getFloat(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_PRICE))));
            mProductQuantity.setText(Integer.toString(data.getInt(data.getColumnIndexOrThrow(ProductEntry.COLUMN_PRODUCT_QUANTITY))));
            mSupplierName.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_NAME)));
            mSupplierPhone.setText(data.getString(data.getColumnIndexOrThrow(ProductEntry.COLUMN_SUPPLIER_PHONE)));
        }
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

    }
}
