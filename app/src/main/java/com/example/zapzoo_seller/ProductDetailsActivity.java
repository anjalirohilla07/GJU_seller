package com.example.zapzoo_seller;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.bumptech.glide.Glide;

public class ProductDetailsActivity extends AppCompatActivity {

    private Intent intent;
    private TextView item_price, item_details,item_name;
    private String name, item_id, image_url;
    private int price;
    private ImageView imv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        item_price = (TextView) findViewById(R.id.item_price);
        item_name=(TextView) findViewById(R.id.item_name);
        item_details = (TextView) findViewById(R.id.item_details);
        imv = (ImageView) findViewById(R.id.imv);

        intent = getIntent();
        name = intent.getStringExtra("item_name");
        price = intent.getIntExtra("item_price", 0);
        String details = intent.getStringExtra("item_details");
        item_id = intent.getStringExtra("item_id");
        image_url = intent.getStringExtra("image_url");

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(name);

        item_price.setText("₹ "+price);
        item_name.setText(name);
        item_details.setText(""+details.replace("\n", ""+System.getProperty("line.separator")));
        Glide.with(this).load(image_url).placeholder(R.drawable.bbsnacks).into(imv);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == android.R.id.home)
        {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void addToCart(View view) {

    }
}
