package com.example.android.inventoryapp;

import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class InventoryActivity extends AppCompatActivity {

    @BindView(R.id.fab_add_inventory_item) FloatingActionButton fab;
    @BindView(R.id.text_view_inventory) TextView dataBase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);
        ButterKnife.bind(this);


    }

    @OnClick(R.id.fab_add_inventory_item)
    public void onFabClick(View view) {
        dataBase.setText("Temporary string");
        Toast.makeText(getApplicationContext(), "Add inventory item feature will be added soon", Toast.LENGTH_SHORT).show();
    }
}
