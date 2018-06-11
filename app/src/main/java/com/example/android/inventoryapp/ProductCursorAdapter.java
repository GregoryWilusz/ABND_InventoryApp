package com.example.android.inventoryapp;

import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.style.ForegroundColorSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.inventoryapp.data.InventoryContract.ProductEntry;

import butterknife.BindView;
import butterknife.ButterKnife;

/**
 * Created by grzegorzwilusz on 6/8/18.
 */

public class ProductCursorAdapter extends CursorAdapter {

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
        ViewHolder holder = new ViewHolder(view);

        String productName = cursor.getString(cursor.getColumnIndexOrThrow("productName"));
        Spannable spanProdName = new SpannableString(productName);
        spanProdName.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)), 0, productName.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        String productPrice = String.valueOf(cursor.getFloat(cursor.getColumnIndexOrThrow("productPrice")));
        Spannable spanProdPrice = new SpannableString(productPrice);
        spanProdPrice.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)), 0, productPrice.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final int productQuantity = cursor.getInt(cursor.getColumnIndexOrThrow("productQuantity"));
        String productQuantityString = String.valueOf(productQuantity);
        Spannable spanProdQuantity = new SpannableString(productQuantityString);
        spanProdQuantity.setSpan(new ForegroundColorSpan(context.getColor(R.color.colorAccent)), 0, productQuantityString.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);

        final int productId = cursor.getInt(cursor.getColumnIndexOrThrow(ProductEntry._ID));

        holder.productNameTextView.setText(new StringBuilder().append(context.getString(R.string.item_list_product_label)).append(" ").toString());
        holder.productNameTextView.append(spanProdName);

        holder.productPriceTextView.setText(new StringBuilder().append(context.getString(R.string.item_list_price_label_price)).append(" ").toString());
        holder.productPriceTextView.append(spanProdPrice);
        holder.productPriceTextView.append(context.getString(R.string.item_list_price_label_currency));

        holder.productQuantityTextView.setText(new StringBuilder().append(context.getString(R.string.list_item_quantity_label)).append(" ").toString());
        holder.productQuantityTextView.append(spanProdQuantity);

        holder.sellButton.setOnClickListener(new View.OnClickListener() {
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

    static class ViewHolder {
        @BindView(R.id.product_name_text_view) TextView productNameTextView;
        @BindView(R.id.product_price_text_view) TextView productPriceTextView;
        @BindView(R.id.product_quantity_text_view) TextView productQuantityTextView;
        @BindView(R.id.sell_button) Button sellButton;

        public ViewHolder(View view){
            ButterKnife.bind(this, view);
        }
    }
}
