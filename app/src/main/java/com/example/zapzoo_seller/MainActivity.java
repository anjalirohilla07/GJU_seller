package com.example.zapzoo_seller;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zapzoo_seller.adapter.FragmentAdapter;
import com.example.zapzoo_seller.drawer_classes.AboutUs;
import com.example.zapzoo_seller.drawer_classes.BankingInfo;
import com.example.zapzoo_seller.drawer_classes.UpdateStoreImage;
import com.example.zapzoo_seller.drawer_classes.ChangePassword;
import com.example.zapzoo_seller.drawer_classes.ContactUs;
import com.example.zapzoo_seller.drawer_classes.MinOrder;
import com.example.zapzoo_seller.drawer_classes.OnlinePayment;
import com.example.zapzoo_seller.drawer_classes.PreviousOrder;
import com.example.zapzoo_seller.drawer_classes.PrivacyPolicy;
import com.example.zapzoo_seller.drawer_classes.TermsofUse;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // variables
    DrawerLayout drawerLayout;
    NavigationView navigationView;
    Toolbar toolbar;
    TabLayout tabLayout;
    ViewPager viewPager;
    TabItem ti1,ti2,ti3,ti4,ti5;
    FragmentAdapter fragmentAdapter;
    private Button addProductBtn;
    private NotificationFunc func;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        func = new NotificationFunc(this);

        if (Build.VERSION.SDK_INT >= 21) {
            Window window = this.getWindow();
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS);
            window.clearFlags(WindowManager.LayoutParams.FLAG_TRANSLUCENT_STATUS);
            window.setStatusBarColor(this.getResources().getColor(R.color.colorPrimaryDark));
        }

        // hooks or init
        drawerLayout=findViewById(R.id.drawer_layout);
        navigationView=findViewById(R.id.nav_view);
        toolbar=findViewById(R.id.toolbar);
        tabLayout=findViewById(R.id.tablayout);
        viewPager=findViewById(R.id.placeholder);
        ti1=findViewById(R.id.ti1);
        ti2=findViewById(R.id.ti2);
        ti3=findViewById(R.id.ti3);
        ti4=findViewById(R.id.ti4);
        ti5=findViewById(R.id.ti5);

        addProductBtn = findViewById(R.id.addProductBtn);

        // setting toolbar as action bar
        setSupportActionBar(toolbar);

        // toggle drawer back and forth while open and close drawer
        ActionBarDrawerToggle toggle= new ActionBarDrawerToggle(this,drawerLayout,toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close);
        drawerLayout.addDrawerListener(toggle);
        toggle.syncState();

        // Drawer items
        navigationView.setNavigationItemSelectedListener(this);

        // setting home as default selected
        navigationView.setCheckedItem(R.id.nav_home);

        // setting fragments to adapter
        fragmentAdapter = new FragmentAdapter(getSupportFragmentManager(),FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT,tabLayout.getTabCount());
        viewPager.setAdapter(fragmentAdapter);

        // while clicking different tabs which layout have to show
        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        // while sliding viewpager getting tab layout item
        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        addProductBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //func.sendNotification("ZapZoo Owners", "Working");
                Intent intent = new Intent(MainActivity.this, AddProductActivity.class);
                startActivity(intent);
            }
        });
    }

    public void onBackPressed(){
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else{
            super.onBackPressed();
        }

    }


    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        int id = menuItem.getItemId();

        if(id==R.id.nav_logout){
            startActivity(new Intent(getApplicationContext(), login.class));
        }else if(id==R.id.nav_online_pay){
            startActivity(new Intent(getApplicationContext(), OnlinePayment.class));
        }else if(id==R.id.nav_banking){
            startActivity(new Intent(getApplicationContext(), BankingInfo.class));
        }else if(id==R.id.nav_address){
            startActivity(new Intent(getApplicationContext(), UpdateStoreImage.class));
        }else if (id==R.id.nav_change_password){
            startActivity(new Intent(getApplicationContext(), ChangePassword.class));
        }else if(id==R.id.nav_contact){
            startActivity(new Intent(getApplicationContext(), ContactUs.class));
        }else if(id==R.id.nav_about_us){
            startActivity(new Intent(getApplicationContext(), AboutUs.class));
        }else if(id==R.id.nav_terms_use){
            startActivity(new Intent(getApplicationContext(), TermsofUse.class));
        }else if(id==R.id.nav_privacy){
            startActivity(new Intent(getApplicationContext(), PrivacyPolicy.class));
        }
        else if(id==R.id.nav_share){

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "GJU STORE Owner");
                shareIntent.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id" + getApplicationContext().getPackageName());
                startActivity(Intent.createChooser(shareIntent, "Share With"));
            }catch (Exception e)
            {
                Toast.makeText(this, "Unable to share... ", Toast.LENGTH_SHORT).show();
            }
        }

        drawerLayout.closeDrawer(GravityCompat.START);
        return true;
    }
}