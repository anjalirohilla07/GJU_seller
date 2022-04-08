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
import android.os.Build;
import android.os.Bundle;
import android.text.method.HideReturnsTransformationMethod;
import android.text.method.PasswordTransformationMethod;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlacePicker;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
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

public class signup extends AppCompatActivity {

    // Variables
    EditText firstName,lastName,mobileNumber,email,storeName,govtLicense,gst,category,password,upi;
    CheckBox checkBox;
    ImageView chooseImage;
    Uri imageUri;
    TextView chooseAdress;
    Button becomeOwner;
    private String storeImageUrl = "";
    private OkHttpClient client;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;
    private FirebaseStorage storage;
    private LatLng location;
    private String shopAddress = "";
    private TextView shopAddressTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = preferences.edit();

        client = new OkHttpClient();

        storage = FirebaseStorage.getInstance();

        // to change status bar color
        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // hooks
        firstName=findViewById(R.id.firstname);
        lastName=findViewById(R.id.lastname);
        mobileNumber=findViewById(R.id.mobile);
        email =findViewById(R.id.store_mail);
        storeName=findViewById(R.id.store_name);
        govtLicense=findViewById(R.id.govt);
        gst=findViewById(R.id.gstin);
        category=findViewById(R.id.category);
        password=findViewById(R.id.password);
        upi=findViewById(R.id.upi);
        checkBox=findViewById(R.id.checkbox);
        chooseImage=findViewById(R.id.choose_image);
        chooseAdress=findViewById(R.id.address);
        becomeOwner=findViewById(R.id.signup);
        shopAddressTV = (TextView) findViewById(R.id.shopAddressTV);

        // pick image
        chooseImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // code to pick image
                Intent intent=new Intent(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(intent,101);
            }
        });

        // show password
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(b){
                    password.setTransformationMethod(HideReturnsTransformationMethod.getInstance());
                }
                else{
                    password.setTransformationMethod(PasswordTransformationMethod.getInstance());
                }
            }
        });

        // become Owner button on click
        becomeOwner.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doSignup();
                //startActivity(new Intent(getApplicationContext(),login.class));
            }
        });

        chooseAdress.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                PlacePicker.IntentBuilder builder = new PlacePicker.IntentBuilder();
                try {
                    startActivityForResult(builder.build(signup.this), 333);
                } catch (GooglePlayServicesRepairableException e) {
                    e.printStackTrace();
                } catch (GooglePlayServicesNotAvailableException e) {
                    e.printStackTrace();
                }
            }
        });

        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.w("#####", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();
                        editor.putString("fcmToken", token).commit();

                        Log.d("#####", token);
                        //Toast.makeText(MainActivity.this, token, Toast.LENGTH_SHORT).show();
                    }
                });

    }

    private void doSignup() {
        final String fn = firstName.getText().toString();
        final String ln = lastName.getText().toString();
        final String mn = mobileNumber.getText().toString();
        final String ei = email.getText().toString();
        final String sn = storeName.getText().toString();
        final String gl = govtLicense.getText().toString();
        final String gi = gst.getText().toString();
        final String ct = category.getText().toString();
        final String pw = password.getText().toString();
        final String ui = upi.getText().toString();
        final String fcmToken = preferences.getString("fcmToken", "null");

        if(fn.isEmpty() || ln.isEmpty() || mn.isEmpty() || ui.isEmpty() || sn.isEmpty() || gl.isEmpty() || gi.isEmpty() || ct.isEmpty() || pw.isEmpty())
        {
            Toast.makeText(this, "Some Field is Empty!!", Toast.LENGTH_SHORT).show();
            return;
        }
        
        if(storeImageUrl.isEmpty())
        {
            Toast.makeText(this, "Select Store Image", Toast.LENGTH_SHORT).show();
            return;
        }

        if(location == null)
        {
            Toast.makeText(this, "Select Location on Map", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody formBody = new FormEncodingBuilder()
                .add("firstName", fn)
                .add("lastName", ln)
                .add("mobileNumber", mn)
                .add("email", ei)
                .add("storeName", sn)
                .add("govtLicense", gl)
                .add("gstin", gi)
                .add("category", ct)
                .add("password", pw)
                .add("fcmToken", fcmToken)
                .add("lat", String.valueOf(location.latitude))
                .add("lng", String.valueOf(location.longitude))
                .add("storeImageUrl", storeImageUrl)
                .add("upi", ui)
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/register")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(signup.this, "Network Error", Toast.LENGTH_SHORT).show();
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
                            Toast.makeText(signup.this, ""+res, Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(signup.this, login.class);
                            startActivity(intent);
                            finishAffinity();
                        } else {
                            Toast.makeText(signup.this, ""+res, Toast.LENGTH_SHORT).show();
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
                            storeImageUrl = "https://firebasestorage.googleapis.com"+uri.getPath();
                            Toast.makeText(signup.this, "Image Uploaded!! "+storeImageUrl, Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e)
                {
                    progressDialog.dismiss();
                    Toast.makeText(signup.this, "Failed " + e.getMessage(), Toast.LENGTH_SHORT).show();
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
        } else if(resultCode == RESULT_OK && requestCode == 333)
        {
            Place place = PlacePicker.getPlace(this, data);
            String placeName = String.format("Place: %s", place.getName());
            double latitude = place.getLatLng().latitude;
            double longitude = place.getLatLng().longitude;
            location = new LatLng(latitude, longitude);

            Geocoder geocoder;
            List<Address> addresses;
            geocoder = new Geocoder(this, Locale.getDefault());

            try {
                addresses = geocoder.getFromLocation(latitude, longitude, 1);
                String address = addresses.get(0).getAddressLine(0);
                String city = addresses.get(0).getLocality();
                shopAddress = "" + address + city;
                shopAddressTV.setText(shopAddress);
                shopAddressTV.setVisibility(View.VISIBLE);
                chooseAdress.setText("Change Location");
                editor.putString("myaddress", ""+address+" "+city).commit();
            } catch (IOException e) {
                e.printStackTrace();
            }
            Log.d("#####", "PlaceName : "+placeName+" "+latitude+" "+longitude);
        }
    }
}