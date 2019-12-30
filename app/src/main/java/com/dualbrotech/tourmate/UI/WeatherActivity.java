package com.dualbrotech.tourmate.UI;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import android.support.v7.app.AlertDialog;

import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;
import android.Manifest;
import android.app.SearchManager;
import android.content.DialogInterface;

import android.content.pm.PackageManager;

import android.provider.SearchRecentSuggestions;
import android.support.design.widget.TabLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;

import android.support.v7.widget.SearchView;

import com.dualbrotech.tourmate.MapActivity;
import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.fragments.CurrentWeatherFragment;
import com.dualbrotech.tourmate.fragments.ForecastWeatherFragment;
import com.dualbrotech.tourmate.helpers.LocationServiceHelper;
import com.dualbrotech.tourmate.helpers.WeatherServiceHelper;
import com.dualbrotech.tourmate.others.RecentSearchSuggestions;
import com.dualbrotech.tourmate.others.URLs;
import com.google.firebase.auth.FirebaseAuth;

public class WeatherActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationServiceHelper.LocationResponseListener {
    SharedPreferences preferences;

    FirebaseAuth auth;
    private Context context;

    public static double latitude;
    public static double longitude;
    public static String UNIT_SELECTED;
    public static int FORECAST_LIMIT_SELECTED;
    public static boolean getWeatherUsingLatLon;

    TabLayout tabLayout;
    ViewPager viewPager;
    TabPagerAdapter pagerAdapter;
    String searchQuery;
    private AlertDialog alertDialogUnits;
    private AlertDialog alertDialogForecastList;

    private SharedPreferences sharedPreferences;
    private SharedPreferences.Editor editor;

    private LocationServiceHelper locationServiceHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_weather);

        context = this;
        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();
        auth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        if (intent.ACTION_SEARCH.equals(intent.getAction())) {
            searchQuery = intent.getStringExtra(SearchManager.QUERY);
//            Toast.makeText(this, searchQuery, Toast.LENGTH_SHORT).show();
            SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                    RecentSearchSuggestions.AUTHORITY, RecentSearchSuggestions.MODE);
            suggestions.saveRecentQuery(searchQuery, null);
            getWeatherUsingLatLon = false;
        }

        initViews();

        // initializing shared preferences
        sharedPreferences = getSharedPreferences("WeatherSettings", MODE_PRIVATE);
        editor = sharedPreferences.edit();

        // initializing LocationHelper
        locationServiceHelper = new LocationServiceHelper(this, this);

        // getting data from sharedPreference
        getSettingsData();


        // setting up tabLayout and viewPager
        setupTabPager();




        othersInitiaLization();
    }


    private void getSettingsData() {
        UNIT_SELECTED = sharedPreferences.getString("unit", WeatherServiceHelper.UNIT_METRIC);
        FORECAST_LIMIT_SELECTED = sharedPreferences.getInt("limit", 7);
    }

    /**
     * This method binds the pagerAdapter with tabLayout and viewPager
     */
    private void setupTabPager() {
        tabLayout.addTab(tabLayout.newTab().setText("Current Weather"));
        tabLayout.addTab(tabLayout.newTab().setText("Forecast Weather"));

        pagerAdapter = new TabPagerAdapter(getSupportFragmentManager(), tabLayout.getTabCount());
        viewPager.setAdapter(pagerAdapter);

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

        viewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));
    }

    private void initViews() {
        tabLayout = findViewById(R.id.tabLayout);
        viewPager = findViewById(R.id.viewPager);
    }

    private void othersInitiaLization() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_weather);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);
        TextView navUsername = headerView.findViewById(R.id.tv_user_name);
        navUsername.setText("Hi "+auth.getCurrentUser().getDisplayName()+" !");
        navigationView.setNavigationItemSelectedListener(this);
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.weather_menu, menu);
        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        SearchView searchView = (SearchView) menu.findItem(R.id.item_search).getActionView();
        searchView.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        searchView.setSubmitButtonEnabled(true);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.item_unit:
                AlertDialogForUnitChange();
                return true;
            case R.id.item_forecast_limit:
                AlertDialogForForecastLimit();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    private void AlertDialogForForecastLimit() {
        CharSequence[] values = {" 7 Days ", " 14 Days "};


        int checkedItem = -1;
        if (FORECAST_LIMIT_SELECTED == 7) {
            checkedItem = 0;
        } else if (FORECAST_LIMIT_SELECTED == 14)
            checkedItem = 1;

        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);

        builder.setTitle("Select Your Choice");

        builder.setSingleChoiceItems(values, checkedItem, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        FORECAST_LIMIT_SELECTED = 7;
                        editor.putInt("limit", 7);
                        editor.commit();
                        Toast.makeText(WeatherActivity.this, "Forecast Limit changed to 7 days", Toast
                                .LENGTH_LONG).show();

                        restartActivity();
                        break;
                    case 1:
                        FORECAST_LIMIT_SELECTED = 14;
                        editor.putInt("limit", 14);
                        editor.commit();
                        Toast.makeText(WeatherActivity.this, "Forecast Limit changed to 14 days", Toast
                                .LENGTH_LONG).show();
                        restartActivity();
                        break;
                }
                alertDialogForecastList.dismiss();
            }
        });
        alertDialogForecastList = builder.create();
        alertDialogForecastList.show();
    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);
    }

    private void AlertDialogForUnitChange() {


        int checkedItem = -1;
        if (UNIT_SELECTED.equals(WeatherServiceHelper.UNIT_METRIC)) {
            checkedItem = 0;
        } else if (UNIT_SELECTED.equals(WeatherServiceHelper.UNIT_IMPERIAL))
            checkedItem = 1;

        CharSequence[] values = {" Metric ", " Imperial "};

        AlertDialog.Builder builder = new AlertDialog.Builder(WeatherActivity.this);

        builder.setTitle("Select Your Choice");

        builder.setSingleChoiceItems(values, checkedItem, new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int item) {

                switch (item) {
                    case 0:
                        UNIT_SELECTED = WeatherServiceHelper.UNIT_METRIC;
                        editor.putString("unit", WeatherServiceHelper.UNIT_METRIC);
                        editor.commit();
                        Toast.makeText(WeatherActivity.this, "Unit Changed To Metric", Toast
                                .LENGTH_LONG).show();
                        restartActivity();
                        break;
                    case 1:
                        UNIT_SELECTED = WeatherServiceHelper.UNIT_IMPERIAL;
                        editor.putString("unit", WeatherServiceHelper.UNIT_IMPERIAL);
                        editor.commit();
                        Toast.makeText(WeatherActivity.this, "Unit Changed To Imperial", Toast
                                .LENGTH_LONG).show();
                        restartActivity();
                        break;
                }
                alertDialogUnits.dismiss();
            }
        });
        alertDialogUnits = builder.create();
        alertDialogUnits.show();
    }

    public void activateLocation(View view) {
        getWeatherUsingLatLon = true;
        latitude = 0;
        longitude = 0;
        checkLocationPermission();
        //get location latitude and longitude
        locationServiceHelper.getLatLon();
    }

    private void checkLocationPermission() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission
                .ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_FINE_LOCATION,}, URLs
                            .PERMISSION_CODE_LOCATION);
            return;

        }
    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent intent = new Intent(WeatherActivity.this, MapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(this,EventsActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_weather) {
            //Intent intent = new Intent(this,WeatherActivity.class);

        } else if (id == R.id.nav_nearby_places) {
            Intent intent = new Intent(context,NearbyPlacesActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sign_out) {

            auth.signOut();
            editor.clear();
            editor.putBoolean("signedIn",false);
            editor.commit();
            Toast.makeText(context,"Logged out",Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context,LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }


    @Override
    public void onLatLonReceived(double lat, double lon) {
        // saving location to SharedPreference
        editor.putFloat("lat", (float) lat);
        editor.putFloat("lon", (float) lon);
        editor.commit();

        latitude = lat;
        longitude = lon;

        locationServiceHelper.stopLocationUpdates();
        Intent intent = getIntent();
        if (intent.ACTION_SEARCH.equals(intent.getAction()))
            intent.setAction(Intent.ACTION_MAIN);
        finish();
        startActivity(intent);
    }

    public class TabPagerAdapter extends FragmentPagerAdapter {

        int tabCount;

        TabPagerAdapter(FragmentManager fm, int tabCount) {
            super(fm);
            this.tabCount = tabCount;
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return CurrentWeatherFragment.getInstance(searchQuery);
                case 1:
                    return ForecastWeatherFragment.getInstance();
            }

            return null;
        }

        @Override
        public int getCount() {
            return tabCount;
        }
    }
}
