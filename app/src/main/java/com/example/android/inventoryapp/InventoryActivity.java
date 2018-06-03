package com.example.android.inventoryapp;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
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

//        fab.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                Intent addProductIntent = new Intent(InventoryActivity.this, InventoryActivity.class);
//                startActivity(addProductIntent);
//            }
//        });
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
                Toast.makeText(getApplicationContext(), "It will display new database", Toast.LENGTH_SHORT).show();
        }
        return super.onOptionsItemSelected(item);
    }
}
