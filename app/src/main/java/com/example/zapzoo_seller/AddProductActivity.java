package com.example.zapzoo_seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zapzoo_seller.adapter.AllOrdersAdapter;
import com.example.zapzoo_seller.adapter.ProductListAdapter;
import com.example.zapzoo_seller.models.Order;
import com.example.zapzoo_seller.models.SingleProduct;
import com.example.zapzoo_seller.util.AllOrdersItemOnClickListener;
import com.example.zapzoo_seller.util.SingleProductOnItemClickListener;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class AddProductActivity extends AppCompatActivity {

    private OkHttpClient client;
    private RecyclerView recyclerView;
    private ProgressBar progressBar;
    private List<SingleProduct> singleProducts = new ArrayList<>();
    private SharedPreferences preferences;
    private ProductListAdapter productListAdapter;
    private SingleProductOnItemClickListener listener;
    private FloatingActionButton fab;
    private TextView warning;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Products");

        client = new OkHttpClient();
        preferences = getSharedPreferences("data", Context.MODE_PRIVATE);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        fab = (FloatingActionButton) findViewById(R.id.fab);

        warning = (TextView) findViewById(R.id.warning);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());

        listener = new SingleProductOnItemClickListener() {
            @Override
            public void onItemClick(int position) {

            }

            @Override
            public void onAddToCartClick(int position) {
                //view Detail Btn Click
                Intent intent = new Intent(AddProductActivity.this, ProductDetailsActivity.class);
                intent.putExtra("item_id", singleProducts.get(position).getId());
                intent.putExtra("item_name", singleProducts.get(position).getName());
                intent.putExtra("item_price", singleProducts.get(position).getPrice());
                intent.putExtra("item_details", singleProducts.get(position).getDetails());
                intent.putExtra("image_url", singleProducts.get(position).getImage_url());
                startActivity(intent);
            }

            @Override
            public void onDeleteBtnClick(int position) {
                //Toast.makeText(AddProductActivity.this, "Delete Btn Click Working", Toast.LENGTH_SHORT).show();
                onDelete(position);
            }
        };

        productListAdapter = new ProductListAdapter(singleProducts, listener, this);
        recyclerView.setAdapter(productListAdapter);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(AddProductActivity.this, CreateProductActivity.class);
                startActivity(intent);
            }
        });
    }

    private void onDelete(int position) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("productId", ""+singleProducts.get(position).getId())
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/deleteProduct")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddProductActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();
                //productListAdapter.notifyDataSetChanged();
                //Log.d("#####", ""+productList.size());
                if(this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            singleProducts.remove(position);
                            if (singleProducts.size() == 0)
                            {
                                warning.setVisibility(View.VISIBLE);
                            } else {
                                warning.setVisibility(View.GONE);
                            }
                            if(productListAdapter != null) {
                                productListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                            //Toast.makeText(getActivity(), ""+res, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    void doRefresh()
    {
        singleProducts.clear();
        productListAdapter.notifyDataSetChanged();

        progressBar.setVisibility(View.VISIBLE);

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/getProduct")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(AddProductActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                try {
                    JSONArray jsonArray = new JSONArray(res);
                    //Log.d("#####", "Array Size : "+sub_category+" "+jsonArray.length());
                    for(int i=0; i<jsonArray.length(); i++)
                    {
                        JSONObject jsonobject = jsonArray.getJSONObject(i);
                        SingleProduct singleProduct = new SingleProduct();
                        singleProduct.setId(jsonobject.getString("_id"));
                        singleProduct.setName(jsonobject.getString("name"));
                        singleProduct.setDetails(jsonobject.getString("details"));
                        singleProduct.setCategory(jsonobject.getString("category"));
                        singleProduct.setSub_category(jsonobject.getString("sub-category"));
                        singleProduct.setImage_url(jsonobject.getString("images"));
                        singleProduct.setPrice(Integer.parseInt(jsonobject.getString("price")));
                        singleProduct.setQuantity(jsonobject.getString("quantity"));
                        //Log.d("#####", ""+sub_category+" "+singleProduct.getName());
                        singleProducts.add(singleProduct);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                //productListAdapter.notifyDataSetChanged();
                //Log.d("#####", ""+productList.size());
                if(this != null) {
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            if (singleProducts.size() == 0)
                            {
                                warning.setVisibility(View.VISIBLE);
                            } else {
                                warning.setVisibility(View.GONE);
                            }
                            if(productListAdapter != null) {
                                productListAdapter.notifyDataSetChanged();
                                progressBar.setVisibility(View.GONE);
                            }
                            //Toast.makeText(getActivity(), ""+res, Toast.LENGTH_SHORT).show();
                        }
                    });
                }

            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        doRefresh();
    }
}