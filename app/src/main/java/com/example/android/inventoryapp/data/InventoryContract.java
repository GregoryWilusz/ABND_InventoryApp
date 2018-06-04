package com.example.android.inventoryapp.data;

import android.provider.BaseColumns;

/**
 * Created by grzegorzwilusz on 6/4/18.
 */

public final class InventoryContract {

    private InventoryContract() {}

    public static final class ProductEntry implements BaseColumns {

        public static final String TABLE_NAME = "products";

        public static final String _ID = BaseColumns._ID;
        public static final String COLUMN_PRODUCT_NAME = "productName";
        public static final String COLUMN_PRODUCT_PRICE = "productPrice";
        public static final String COLUMN_PRODUCT_QUANTITY = "productQuantity";
        public static final String COLUMN_SUPPLIER_NAME = "supplierName";
        public static final String COLUMN_SUPPLIER_PHONE = "supplierPhone";

    }
}
