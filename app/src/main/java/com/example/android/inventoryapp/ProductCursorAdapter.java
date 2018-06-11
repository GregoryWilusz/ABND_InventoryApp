package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

/**
 * Created by grzegorzwilusz on 6/8/18.
 */

public class ProductCursorAdapter extends CursorAdapter {

//    @BindView(R.id.product_name_text_view) TextView productNameTextView;
//    @BindView(R.id.product_price_text_view) TextView productPriceTextView;
//    @BindView(R.id.product_quantity_text_view) TextView productQuantityTextView;

    /**
     * Recommended constructor.
     *
     * @param context The context
     * @param c       The cursor from which to get the data.
     *
     */
    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }

    /**
     * Makes a new view to hold the data pointed to by cursor.
     *
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     *                moved to the correct position.
     * @param parent  The parent to which the new view is attached to
     * @return the newly created view.
     */
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    /**
     * Bind an existing view to the data pointed to by cursor
     *
     * @param view    Existing view, returned earlier by newView
     * @param context Interface to application's global information
     * @param cursor  The cursor from which to get the data. The cursor is already
     */
    @Override
    public void bindView(View view, final Context context, final Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity_text_view);
        Button sellButton = (Button) view.findViewById(R.id.sell_button);

        String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
        String productPrice = String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("productPrice")));
        final int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("productQuantity"));
        final int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        productNameTextView.setText(String.format("%s %s", context.getString(R.string.item_list_product_label), productName));
        productPriceTextView.setText(String.format("%s %s %s", context.getString(R.string.item_list_price_label_price), productPrice, context.getString(R.string.item_list_price_label_currency)));
        productQuantityTextView.setText(String.format("%s %d", context.getString(R.string.list_item_quantity_label), productQuantity));

        sellButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (productQuantity > 0) {
                    ContentValues values = new ContentValues();
                    int quantityAfterSold = productQuantity;

                    values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, --quantityAfterSold);
                    Uri uri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productId);

                    int rowsUpdated = v.getContext().getContentResolver().update(uri, values, null, null);
                    if (rowsUpdated > 0) {
                        Toast.makeText(context, R.string.product_sold_successful, Toast.LENGTH_SHORT).show();
                        v.getContext().getContentResolver().notifyChange(uri, null);
                    } else {
                        Toast.makeText(context, R.string.product_sold_failed, Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(context, R.string.product_out_of_stock, Toast.LENGTH_SHORT).show();
                }

            }
        });
    }


}
