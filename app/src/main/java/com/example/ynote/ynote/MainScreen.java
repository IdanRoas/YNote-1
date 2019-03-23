package com.example.ynote.ynote;

import android.Manifest;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toolbar;
import com.facebook.login.LoginManager;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class MainScreen extends AppCompatActivity  {


    String photoUrl;
    Toolbar mainToolbar;
    FirebaseUser current_user;
    BottomNavigationView bottomNavigationView;
    ChatFragment chatFragment;
    HomeFragment homeFragment;
    LocationCallback mLocationCallback;
    LocationRequest mLocationRequest = new LocationRequest();
    FusedLocationProviderClient mFusedLocationClient;
    static LatLng userLocation;
    NotificationFragment notification;
    LocationManager locationManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainToolbar = findViewById(R.id.main_toolbar);
        current_user=FirebaseAuth.getInstance().getCurrentUser();
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("YNote");
        setContentView(R.layout.activity_main_screen);
        bottomNavigationView=findViewById(R.id.bottom_nav);
        homeFragment=new HomeFragment();
        chatFragment=new ChatFragment();
        notification= new NotificationFragment();
        ChangeFragment(homeFragment);
        bottomNavigationView.setSelectedItemId(R.id.home_btn);

        locationManager = (LocationManager) this.getSystemService(LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER )) {
            Intent callGPSSettingIntent = new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS);
            startActivity(callGPSSettingIntent);
        }
        // find user location
        findUserLocation();





        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {
                    case R.id.home_btn:
                        ChangeFragment(homeFragment);
                        return true;
                    case R.id.chat_btn:
                        ChangeFragment(chatFragment);
                        return true;
                    case R.id.notification_btn:
                        ChangeFragment(notification);
                        return true;
                }
                return false;
            }
        });
    }

    @Override
    public void onBackPressed() {
        startLocationUpdates();
    }
    @Override
    public void onResume() {
        super.onResume();
        boolean mRequestingLocationUpdates = true;
        if (mRequestingLocationUpdates) {
            startLocationUpdates();
        }
    }

    private void startLocationUpdates() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }

        if (LocationServices.getFusedLocationProviderClient(this)!=null){
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
            mFusedLocationClient.requestLocationUpdates(mLocationRequest,
                    mLocationCallback,
                    null);
        }else
            findUserLocation();

    }

    @Override
    protected void onStart() {
        super.onStart();
        if (current_user == null) {
            Intent intent = new Intent(MainScreen.this, MainActivity.class);
            startActivity(intent);
            finish();
        }

    }
//logOut


    private void setSupportActionBar(Toolbar mainToolbar) {
    }


    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    //setting menu
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case R.id.logout_button:
                FirebaseAuth.getInstance().signOut();
                logOut1();
                return true;

            case R.id.setting_button:

                Intent settingsIntent = new Intent(MainScreen.this, SetupActivity.class);
                settingsIntent.putExtra("Uri", photoUrl);
                startActivity(settingsIntent);

                return true;
            default:
                return false;
        }
    }

    private void ChangeFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction= getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container, fragment);
        fragmentTransaction.commit();
    }

    //logout method
    private void logOut1() {
        AuthUI.getInstance().signOut(this);
        LoginManager.getInstance().logOut();
        Intent intent = new Intent(MainScreen.this, MainActivity.class);
        startActivity(intent);
        finish();
    }

    public  void  findUserLocation(){
        mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
        }
        mFusedLocationClient.getLastLocation().addOnSuccessListener(this, new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if(location!= null)
                    userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        });
        mLocationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    startLocationUpdates();
                }else
                    for (Location location : locationResult.getLocations())
                        userLocation = new LatLng(location.getLatitude(), location.getLongitude());
            }
        };
    }
    static public LatLng getUserLocation(){
        return userLocation;
    }
}