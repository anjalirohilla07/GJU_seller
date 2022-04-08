package com.example.zapzoo_seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.text.InputType;
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
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.squareup.okhttp.Callback;
import com.squareup.okhttp.FormEncodingBuilder;
import com.squareup.okhttp.OkHttpClient;
import com.squareup.okhttp.Request;
import com.squareup.okhttp.RequestBody;
import com.squareup.okhttp.Response;

import java.io.IOException;

public class login extends AppCompatActivity {

    // Variables
    CheckBox checkBox;
    EditText mobile_number,password;
    TextView forget_password,become_owner;
    Button login;
    private OkHttpClient client;
    private SharedPreferences preferences;
    private SharedPreferences.Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("data", MODE_PRIVATE);
        editor = preferences.edit();

        client = new OkHttpClient();

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // hooks
        mobile_number=findViewById(R.id.mobile_number);
        password=findViewById(R.id.password);
        checkBox=findViewById(R.id.checkbox);
        forget_password=findViewById(R.id.forget_password);
        login=findViewById(R.id.login);
        become_owner=findViewById(R.id.become_owner);

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

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                doLogin();
                //startActivity(new Intent(getApplicationContext(),MainActivity.class));
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

    private void doLogin() {
        final String mn = mobile_number.getText().toString();
        final String pw = password.getText().toString();
        final String fcmToken = preferences.getString("fcmToken", "null");
        
        if(mn.isEmpty() || pw.isEmpty())
        {
            Toast.makeText(this, "Field Empty!!", Toast.LENGTH_SHORT).show();
            return;
        }

        RequestBody formBody = new FormEncodingBuilder()
                .add("mobileNumber", mn)
                .add("password", pw)
                .add("fcmToken", fcmToken)
                .build();

        Request request = new Request.Builder()
                .url("https://gjuserver.anjalirohilla.repl.co/seller/login")
                .post(formBody)
                .build();

        client.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Request request, IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(login.this, "Network Error", Toast.LENGTH_SHORT).show();
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
                        Toast.makeText(login.this, ""+res, Toast.LENGTH_SHORT).show();
                        if(res_code == 200)
                        {
                            editor.putBoolean("isLogined", true);
                            editor.putString("mobileNumber", mn);
                            editor.putString("shop_id", res);
                            editor.commit();
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                            finishAffinity();
                        }
                    }
                });
            }
        });

    }

    public void forgetPassword(View view)
    {
        startActivity(new Intent(getApplicationContext(),ForgetPassword.class));
    }
    public void becomeOwner(View view)
    {
        startActivity(new Intent(getApplicationContext(),signup.class));
    }

}