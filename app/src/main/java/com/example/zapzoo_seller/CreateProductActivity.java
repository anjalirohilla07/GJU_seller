package com.example.zapzoo_seller;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.UUID;

public class CreateProductActivity extends AppCompatActivity {

    private Spinner category, subCategory;
    private ArrayAdapter categoryAdapter, subCategoryAdapter;
    private String[] categories = {
            "Select Category",
            "Beverages",
            "Breakfast and Dairy",
            "Grocery and Staples",
            "Household Care",
            "Packaged Food",
            "Personal Care",
            "Snacks",
            "Baby Care"
    };
    private String[][] subCategories = {
            {"Select SubCategory"},
            {"Select SubCategory", "Tea and Coffee", "Energy and Soft Drinks", "Juices and Concentrates", "Milk Drinks", "Health Drinks and Supplements"},
            {
                "Select SubCategory",
                    "Butter Jam and Ketchup",
                    "Cheese Spreads and Dips",
                    "Breads and Buns",
                    "Milk Curd and Yogurt",
                    "Cereals and Ready to Eat"

            },
            {
                "Select SubCategory",
                    "Salt and Sugar",
                    "Atta and Other Flours",
                    "Dal and Pulses",
                    "Rice and Other Grains",
                    "Edible Oils"
            },
            {
                "Select SubCategory",
                    "Detergents Fabric Conditioner and Dishwash",
                    "Cleaning Tool",
                    "Fresheners and Repellents",
                    "Pooja Samgri",
                    "Foils Tissues and Disposables"
            },
            {
                "Select SubCategory",
                    "Noodles Vermicelli and Pasta",
                    "Sauces",
                    "Instant Food Mixes",
                    "Pickles and Chutneys",
                    "Frozen Food"
            },
            {
                "Select SubCategory",
                    "Bath and Handwash",
                    "Oral Care",
                    "Hair Care",
                    "Feminine Hygiene",
                    "Skin Care and Body Care"
            },
            {
                "Select SubCategory",
                    "Biscuit Cookies and Cakes",
                    "Namkeen",
                    "Chips Waffers and Popcorns",
                    "Chocolates Candies and Sweets",
                    "Baking and Dessert Mixes"
            },
            {
                "Select SubCategory",
                    "Baby Food and Formula",
                    "Baby Personal Care",
                    "Diapers and Wipes"
            }
    };
    private String[] choosenSubCategory = subCategories[0];
    private TextView productName, quantity, description, price;
    private ImageView chooseImage;
    private Uri imageUri;
    private String productImageUrl = "";
    private OkHttpClient client;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseStorage storage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_product);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Add Product");

        category = findViewById(R.id.category);
        subCategory = findViewById(R.id.subCategory);
        productName = findViewById(R.id.productName);
        quantity = findViewById(R.id.quantity);
        description = findViewById(R.id.description);
        price = findViewById(R.id.price);
        chooseImage = findViewById(R.id.choose_image);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = preferences.edit();

        client = new OkHttpClient();

        storage = FirebaseStorage.getInstance();

        categoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, categories);
        subCategoryAdapter = new ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, choosenSubCategory);

        category.setAdapter(categoryAdapter);
        subCategory.setAdapter(subCategoryAdapter);

        category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                choosenSubCategory = subCategories[i];
                subCategoryAdapter = new ArrayAdapter(CreateProductActivity.this, android.R.layout.simple_spinner_dropdown_item, choosenSubCategory);
                subCategory.setAdapter(subCategoryAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });
    }

    public void addProductBtnClick(View view) {
        String pname = productName.getText().toString();
        String q = quantity.getText().toString();
        String d = description.getText().toString();
        String p = price.getText().toString();
        String c = categories[category.getSelectedItemPosition()];
        String sc = subCategories[category.getSelectedItemPosition()][subCategory.getSelectedItemPosition()];
        
        if(pname.isEmpty() || q.isEmpty() || d.isEmpty() || p.isEmpty())
        {
            Toast.makeText(this, "Field is Empty!!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(category.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select Category", Toast.LENGTH_SHORT).show();
            return;
        }

        if(subCategory.getSelectedItemPosition() == 0)
        {
            Toast.makeText(this, "Select SubCategory", Toast.LENGTH_SHORT).show();
            return;
        }

        if(productImageUrl.isEmpty())
        {
            Toast.makeText(this, "Select Product Image", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody formBody = new FormEncodingBuilder()
                .add("name", pname)
                .add("quantity", q)
                .add("details", d)
                .add("price", p)
                .add("category", c)
                .add("sub-category", sc)
                .add("images", productImageUrl)
                .add("shop_id", preferences.getString("shop_id", ""))
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/addProduct")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(CreateProductActivity.this, "Network Error", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onResponse(final Response response) throws IOException {
                final String res = response.body().string();
                final int res_code = response.code();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if(res_code == 200)
                        {
                            Toast.makeText(CreateProductActivity.this, ""+res, Toast.LENGTH_SHORT).show();
                            finishActivity(55);
                        } else {
                            Toast.makeText(CreateProductActivity.this, ""+res, Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });


    }

    private void uploadImage(Uri filePath)
    {
        if(filePath != null) {
            ProgressDialog progressDialog = new ProgressDialog(this);
            progressDialog.setTitle("Uploading...");
            progressDialog.show();

            StorageReference ref = storage.getReference().child("" + UUID.randomUUID().toString());

            ref.putFile(filePath).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot)
                {
                    ref.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            productImageUrl = "https://firebasestorage.googleapis.com"+uri.getPath()+"?alt=media";
                            Toast.makeText(CreateProductActivity.this, "Image Uploaded!! "+productImageUrl, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(CreateProductActivity.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onProgress(UploadTask.TaskSnapshot taskSnapshot)
                {
                    double progress = (100.0 * taskSnapshot.getBytesTransferred() / taskSnapshot.getTotalByteCount());
                    progressDialog.setMessage("Uploaded " + (int)progress + "%");
                }
            });
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode ==101 && resultCode == RESULT_OK){
            if(data!=null){
                imageUri=data.getData();
                Glide.with(getApplicationContext()).load(imageUri).into(chooseImage);
                uploadImage(imageUri);
            }
        }
    }
}