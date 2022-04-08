package com.example.zapzoo_seller;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.zapzoo_seller.adapter.OrderProductListAdapter;
import com.example.zapzoo_seller.models.Product;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class OrderDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    private List<Product> productList;
    private Toolbar toolbar;
    private String o_id;
    private RecyclerView recyclerView;
    private OrderProductListAdapter adapter;
    private ImageView imageView;
    private TextView orderid, date, status, name, mobileNumber, email;
    private LinearLayout acceptreject;
    private RelativeLayout threeSeekBar;
    private SeekBar seekBar;
    private OkHttpClient client;
    private SharedPreferences preferences;
    private CardView actions;
    private String username;
    private GoogleMap googleMap;
    private LocationManager locationManager;
    private String provider;
    private Location location;
    private TextView currentAddress;
    private MarkerOptions markerOptions;
    private Marker marker;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        client = new OkHttpClient();
        preferences = getSharedPreferences("data", MODE_PRIVATE);

        Intent intent = getIntent();
        o_id = intent.getStringExtra("orderid");
        username = intent.getStringExtra("username");
        productList = (List<Product>) intent.getExtras().getSerializable("products");

        getSupportActionBar().setTitle("Respond to orders");

        imageView = findViewById(R.id.imageView);
        orderid = findViewById(R.id.orderid);
        date = findViewById(R.id.date);
        status = findViewById(R.id.status);
        name = findViewById(R.id.name);
        mobileNumber = findViewById(R.id.mobileNumber);
        email = findViewById(R.id.email);
        acceptreject = findViewById(R.id.acceptreject);
        threeSeekBar = findViewById(R.id.threeSeekBar);
        seekBar = findViewById(R.id.seekBar);
        actions = findViewById(R.id.actionsCV);

        DateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
        //Date d = new Date(o_id);
        Timestamp timeStampDate = new Timestamp(Long.parseLong(o_id));

        orderid.setText("Order ID:"+o_id);
        date.setText("Date: "+timeStampDate.toString());
        status.setText("⚫ "+intent.getStringExtra("status"));

        adapter = new OrderProductListAdapter(productList, this);

        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        //locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        //Criteria criteria = new Criteria();
        //provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            //return;
        }
        //Log.d("#####", provider);
        //location = locationManager.getLastKnownLocation(LocationManager.PASSIVE_PROVIDER);
        markerOptions = new MarkerOptions()
                .position(new LatLng(20.5937, 78.9629))
                .title("You have to deliver here");
        markerOptions.draggable(true);

        seekBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int i, boolean b) {
                //Toast.makeText(OrderDetailActivity.this, "Position "+i+" bool "+b, Toast.LENGTH_SHORT).show();
                if(b && i == 0)
                {
                    changeStatus("Accepted");
                } else if(b && i == 1)
                {
                    changeStatus("In Transit");
                } else if(b && i == 2)
                {
                    changeStatus("Delivered");
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        if(status.getText().toString().contains("Accepted") || status.getText().toString().contains("Paid"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
        } else if(status.getText().toString().contains("Rejected"))
        {
            actions.setVisibility(View.GONE);
        } else if(status.getText().toString().contains("In Transit"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(1);
        } else if(status.getText().toString().contains("Delivered"))
        {
            acceptreject.setVisibility(View.GONE);
            threeSeekBar.setVisibility(View.VISIBLE);
            seekBar.setProgress(2);
        }

        fetchCustomerInfo();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        this.googleMap = googleMap;

        LatLng myloc = new LatLng(20.5937, 78.9629);
        marker = googleMap.addMarker(markerOptions);
        googleMap.animateCamera(CameraUpdateFactory.newLatLng(myloc));
    }

    private void changeStatus(String s) {

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", String.valueOf(Long.parseLong(o_id)))
                .add("username", username)
                .add("status", ""+s)
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            acceptreject.setVisibility(View.GONE);
                            threeSeekBar.setVisibility(View.VISIBLE);
                            status.setText("⚫ "+s);
                        }
                    }
                });
            }
        });

    }

    private void fetchCustomerInfo() {
        //https://dummyimage.com/600x400/000/fff&text=AB
        Glide.with(this)
                .load("https://dummyimage.com/130x130/26c4c1/fff&text=AB")
                .into(imageView);

        RequestBody formBody = new FormEncodingBuilder()
                .add("username", username)
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/getProfile")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            try {
                                JSONObject object = new JSONObject(res);
                                name.setText("Name: "+object.getString("fullname"));
                                mobileNumber.setText("Customer Mobile: "+object.getString("phoneNumber"));
                                email.setText("Customer Email: "+object.getString("email"));
                                LatLng myloc = new LatLng(Double.parseDouble(object.getString("lat")), Double.parseDouble(object.getString("lng")));
                                marker.setPosition(myloc);
                                CameraPosition cameraPosition = new CameraPosition.Builder()
                                        .target(myloc)
                                        .zoom(17)
                                        .bearing(90)
                                        .tilt(30)
                                        .build();
                                googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
                                Glide.with(OrderDetailActivity.this)
                                        .load("https://dummyimage.com/130x130/26c4c1/fff&text="+object.getString("fullname").toString().substring(0, 2))
                                        .into(imageView);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                });
            }
        });
    }

    public void onAccepted(View view) {

        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", String.valueOf(Long.parseLong(o_id)))
                .add("username", username)
                .add("status", "Accepted")
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            acceptreject.setVisibility(View.GONE);
                            threeSeekBar.setVisibility(View.VISIBLE);
                            status.setText("⚫ Accepted");
                        }
                    }
                });
            }
        });
    }

    public void onRejected(View view) {
        RequestBody formBody = new FormEncodingBuilder()
                .add("shop_id", preferences.getString("shop_id", ""))
                .add("timestamp", o_id)
                .add("username", username)
                .add("status", "Rejected")
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/changeStatus")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(OrderDetailActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();

                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            actions.setVisibility(View.GONE);
                            status.setText("⚫ Rejected");
                            //acceptreject.setVisibility(View.GONE);
                            //threeSeekBar.setVisibility(View.VISIBLE);
                        }
                    }
                });
            }
        });
    }
}