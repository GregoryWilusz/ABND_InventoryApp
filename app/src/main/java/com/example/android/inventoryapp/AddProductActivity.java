package com.example.android.inventoryapp;

import android.content.ContentValues;
import android.database.sqlite.SQLiteDatabase;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;
import com.example.android.inventoryapp.data.InventoryDbHelper;

import butterknife.BindView;
import butterknife.ButterKnife;

public class AddProductActivity extends AppCompatActivity {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);
        ButterKnife.bind(this);

        mProductName = (EditText) productNameTextView;
        mProductPrice = (EditText) productPriceTextView;
        mProductQuantity = (EditText) productQuantityTextView;
        mSupplierName = (EditText) supplierNameTextView;
        mSupplierPhone = (EditText) supplierPhoneTextView;
    }

    private void insertProduct() {
        String prodName = mProductName.getText().toString().trim();
        double prodPrice = Double.parseDouble(mProductPrice.getText().toString().trim());
        int prodQuantity = Integer.parseInt(mProductQuantity.getText().toString().trim());
        String supplName = mSupplierName.getText().toString().trim();
        String supplPhone = mSupplierPhone.getText().toString().trim();

        InventoryDbHelper inventoryDbHelper = new InventoryDbHelper(this);
        SQLiteDatabase db = inventoryDbHelper.getWritableDatabase();

        ContentValues values = new ContentValues();

        values.put(ProductEntry.COLUMN_PRODUCT_NAME, prodName);
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, prodPrice);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, prodQuantity);
        values.put(ProductEntry.COLUMN_SUPPLIER_NAME, supplName);
        values.put(ProductEntry.COLUMN_SUPPLIER_PHONE, supplPhone);

        long newRowId = db.insert(ProductEntry.TABLE_NAME, null, values);

        if (newRowId == -1) {
            Toast.makeText(this, "Error with saving product", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Product saved with id: " + newRowId, Toast.LENGTH_SHORT).show();
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
                insertProduct();
                finish();
                return true;
            case R.id.action_delete_product:
                Toast.makeText(getApplicationContext(), "Will delete product", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.home:
                NavUtils.navigateUpFromSameTask(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
