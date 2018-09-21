package com.example.android.readersstore;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.readersstore.data.BookContract;

import butterknife.BindView;
import butterknife.ButterKnife;

public class BookCursorAdapter extends CursorAdapter {
    Integer quantity;
    String productName;
    String price;
    @BindView(R.id.product_name)TextView nameTv;
    @BindView(R.id.product_price) TextView priceTv;
    @BindView(R.id.quantity) TextView quantityTv;
    @BindView(R.id.sale_btn)
    Button saleBtn;
    public BookCursorAdapter(Context context, Cursor c) {
        super(context, c, 0);
    }
    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }
    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        ButterKnife.bind(this, view);
        int nameColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PROD_NAME);
        int priceColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(BookContract.BookEntry.COLUMN_QTY);
        // Read the pet attributes from the Cursor for the current pet
        productName = cursor.getString(nameColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);
        price = cursor.getString(priceColumnIndex);
        nameTv.setText(productName);
        priceTv.setText(price);
        if(quantity == 0) {
            quantityTv.setText("Out of Stock!");
        } else {
            quantityTv.setText(quantity.toString());
        }
        saleBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                quantity -= 1;
            }
        });
    }
}