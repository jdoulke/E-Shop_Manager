package com.ihu.e_shopmanager;

import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.room.Room;

import android.content.Context;
import android.os.Bundle;
import android.os.Vibrator;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.material.navigation.NavigationView;


public class MainActivity extends AppCompatActivity {

    DrawerLayout drawerLayout;
    NavigationView navigationView;
    ActionBarDrawerToggle drawerToggle;

    public static EshopDatabase myAppDatabase;


    public static FragmentManager fragmentManager;
    ClientsFragment clientsFragment = new ClientsFragment();
    OrdersFragment ordersFragment = new OrdersFragment();
    ProductsFragment productsFragment = new ProductsFragment();
    HomeFragment homeFragment = new HomeFragment();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Init();

    }

    private void Init(){
        fragmentManager = getSupportFragmentManager();
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        drawerToggle = new ActionBarDrawerToggle(this, drawerLayout,R.string.open, R.string.close );
        drawerLayout.addDrawerListener(drawerToggle);
        drawerToggle.syncState();
        myAppDatabase = Room.databaseBuilder(getApplicationContext(),EshopDatabase.class,"eshopDB").allowMainThreadQueries().build();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("");
        setSupportActionBar(toolbar);
        getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
        TextView toolbarText = findViewById(R.id.toolbar_string);
        toolbarText.setText("Αρχική");
        registerListeners();
    }

    private void registerListeners() {
        ImageView menuIcon = findViewById(R.id.menu_icon);
        menuIcon.setOnClickListener(v -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            drawerLayout.openDrawer(GravityCompat.START);
        });
        navigationView.setNavigationItemSelectedListener(item -> {
            Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            vibrator.vibrate(40);
            TextView toolbarTitle = findViewById(R.id.toolbar_string);
            FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
            int itemId = item.getItemId();
            if (itemId == R.id.home) {
                transaction.replace(R.id.fragment_container, homeFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                toolbarTitle.setText("Αρχική");
                drawerLayout.closeDrawers();
            }
            if (itemId == R.id.orders) {
                transaction.replace(R.id.fragment_container, ordersFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                toolbarTitle.setText("Παραγγελίες");
                drawerLayout.closeDrawers();
            } else if (itemId == R.id.clients) {
                transaction.replace(R.id.fragment_container, clientsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                toolbarTitle.setText("Πελάτες");
                drawerLayout.closeDrawers();
            } else if (itemId == R.id.products) {
                transaction.replace(R.id.fragment_container, productsFragment);
                transaction.addToBackStack(null);
                transaction.commit();
                toolbarTitle.setText("Προϊόντα");
                drawerLayout.closeDrawers();
            }
            return false;
        });

    }
}