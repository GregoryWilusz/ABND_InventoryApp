package com.example.android.inventoryapp;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

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
    @BindView(R.id.order_button) Button orderButton;

    private static final int PET_LOADER = 0;
    private Uri mCurrentProductUri;
    private boolean mProductHasChanged = false;

    private View.OnTouchListener mTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            mProductHasChanged = true;
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_or_edit_product);
        ButterKnife.bind(this);

        Intent intent = getIntent();
        Uri currentUri = intent.getData();

        if (currentUri != null) {
            setTitle(getString(R.string.editor_activity_title_edit_product));
            mCurrentProductUri = currentUri;
            getLoaderManager().initLoader(PET_LOADER, null, this);
        } else {
            setTitle(getString(R.string.editor_activity_title_add_new_product));
            orderButton.setVisibility(View.GONE);
            // Invalidate the options menu, so the "Delete" menu option can be hidden.
            // (It doesn't make sense to delete a pet that hasn't been created yet.)
            invalidateOptionsMenu();
        }

        mProductName = (EditText) productNameTextView;
        mProductPrice = (EditText) productPriceTextView;
        mProductQuantity = (EditText) productQuantityTextView;
        mSupplierName = (EditText) supplierNameTextView;
        mSupplierPhone = (EditText) supplierPhoneTextView;

        mProductName.setOnTouchListener(mTouchListener);
        mProductPrice.setOnTouchListener(mTouchListener);
        mProductQuantity.setOnTouchListener(mTouchListener);
        mSupplierName.setOnTouchListener(mTouchListener);
        mSupplierPhone.setOnTouchListener(mTouchListener);
        mSupplierPhone.addTextChangedListener(new PhoneNumberFormattingTextWatcher());
    }

    @OnClick(R.id.order_button)
    public void onOrderButtonClick() {
        if (mCurrentProductUri != null) {
            Intent orderProductIntent = new Intent(Intent.ACTION_DIAL);
            orderProductIntent.setData(Uri.parse("tel:" + mSupplierPhone.getText().toString().trim()));
            startActivity(orderProductIntent);
        }
    }

    /**
     * Take care of popping the fragment back stack or finishing the activity
     * as appropriate.
     */
    @Override
    public void onBackPressed() {
        // If the pet hasn't changed, continue with handling back button press
        if (!mProductHasChanged) {
            super.onBackPressed();
            return;
        }

        // Otherwise if there are unsaved changes, setup a dialog to warn the user.
        // Create a click listener to handle the user confirming that changes should be discarded.
        DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // User clicked "Discard" button, close the current activity.
                finish();
            }
        };

        // Show dialog that there are unsaved changes
        showUnsavedChangesDialog(discardButtonClickListener);
    }

    private void showUnsavedChangesDialog(DialogInterface.OnClickListener discardButtonClickListener) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.unsaved_changes_dialog_message);
        builder.setPositiveButton(R.string.discard, discardButtonClickListener);
        builder.setNegativeButton(R.string.keep_editing, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        AlertDialog alertDialog = builder.create();
        alertDialog.show();
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

    private boolean areAllFieldsValid() {
        // Read from input fields
        // Use trim to eliminate leading or trailing white space
        String prodName = mProductName.getText().toString().trim();
        String prodPrice = mProductPrice.getText().toString().trim();
        String prodQuantity = mProductQuantity.getText().toString().trim();
        String supplName = mSupplierName.getText().toString().trim();
        String supplPhone = mSupplierPhone.getText().toString().trim();

        // Check if this is supposed to be a new product and all the fields in the editor are blank
        if (mCurrentProductUri == null && TextUtils.isEmpty(prodName) && TextUtils.isEmpty(prodPrice)
                && TextUtils.isEmpty(prodQuantity) && TextUtils.isEmpty(prodQuantity)
                && TextUtils.isEmpty(supplName) && TextUtils.isEmpty(supplPhone)) {
            Toast.makeText(this, R.string.editor_activity_insert_product_empty, Toast.LENGTH_SHORT).show();
            return false;
        }

        // Check if all the NOT NULL fields in the editor are blank. If yes, show the message to the user
        // and don't proceed.
        if ((mCurrentProductUri == null || mCurrentProductUri != null) && (TextUtils.isEmpty(prodName) || TextUtils.isEmpty(prodPrice)
                || TextUtils.isEmpty(supplName) || TextUtils.isEmpty(supplPhone))) {
            Toast.makeText(this, R.string.editor_activity_insert_not_null_fields, Toast.LENGTH_SHORT).show();
            return false;
        }

        saveProduct(prodName, prodPrice, prodQuantity, supplName, supplPhone);
        return true;
    }

    private void saveProduct(String productName, String productPrice, String productQuantity, String supplierName, String supplierPhone) {
        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, productName);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, productPrice);

        // If the quantity is not provided by the user, don't try to parse the string into an
        // integer value. Use 0 by default.
        int quantity = 0;
        if (!TextUtils.isEmpty(productQuantity)) {
            quantity = Integer.parseInt(productQuantity);
        }
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, quantity);

        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplierName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplierPhone);

        if (mCurrentProductUri == null) {
            Uri newUri = getContentResolver().insert(ProductEntry.CONTENT_URI, values);

            if (newUri == null) {
                Toast.makeText(this, R.string.editor_activity_saving_new_product_failed, Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(this, R.string.editor_activity_saving_new_product_successful, Toast.LENGTH_SHORT).show();
                finish();
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
                finish();
            }
        }
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(getString(R.string.delete_dialog_message));
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                deleteProduct();
                finish();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
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
                areAllFieldsValid();
                return true;
            case R.id.action_delete_product:
                showDeleteConfirmationDialog();
                return true;
            case android.R.id.home:
                if (!mProductHasChanged) {
                    NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    return true;
                }

                DialogInterface.OnClickListener discardButtonClickListener = new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NavUtils.navigateUpFromSameTask(ProductEditorActivity.this);
                    }
                };
                showUnsavedChangesDialog(discardButtonClickListener);
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
