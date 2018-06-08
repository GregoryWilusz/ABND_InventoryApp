package com.example.android.inventoryapp;

import android.content.Context;
import android.database.Cursor;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by grzegorzwilusz on 6/8/18.
 */

public class ProductCursorAdapter extends CursorAdapter {

//    @BindView(R.id.product_name_text_view) TextView productNameTextView;
//    @BindView(R.id.product_price_text_view) TextView productPriceTextView;
//    @BindView(R.id.product_quantity_text_view) TextView productQuantityTextView;
//    @BindView(R.id.sell_button) Button sellButton;

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
    public void bindView(View view, Context context, Cursor cursor) {

        TextView productNameTextView = (TextView) view.findViewById(R.id.product_name_text_view);
        TextView productPriceTextView = (TextView) view.findViewById(R.id.product_price_text_view);
        TextView productQuantityTextView = (TextView) view.findViewById(R.id.product_quantity_text_view);

        String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
        String productPrice = String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("productPrice")));
        String productQuantity = String.valueOf(cursor.getInt(cursor.getColumnIndexOrThrow("productQuantity")));

        productNameTextView.setText(productName);
        productPriceTextView.setText(productPrice);
        productQuantityTextView.setText(productQuantity);
    }


}
