package com.dualbrotech.tourmate.UI;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.dualbrotech.tourmate.Adapters.PlaceAdapter;
import com.dualbrotech.tourmate.MapActivity;
import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.WebResponses.PlaceResponse;
import com.dualbrotech.tourmate.helpers.LocationServiceHelper;
import com.dualbrotech.tourmate.others.URLs;
import com.google.firebase.auth.FirebaseAuth;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyPlacesActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        LocationServiceHelper.LocationResponseListener {

    public static String BASE_URL = "https://maps.googleapis.com/maps/api/place/";
    Context context;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;
    FirebaseAuth auth;
    GridView gridView;

    PlaceResponse responses;
    PlaceService service;
    PlaceAdapter adapter;

    Geocoder geocoder;
    List<Address> addresses;

    Spinner place_area, place_type;
    Button find;

    double lattitude = 23.750452, longitude = 90.393378;
    int area = 500;
    String type = "restaurant", api_key = "AIzaSyAXG0k0FK5sUbrIF6MHGGDCaop66Up1oDs";

    List<PlaceResponse.Result> places = new ArrayList<>();
    ProgressDialog ringProgressDialog;

    private LocationServiceHelper locationServiceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_places);
        othersInitiaLization();
        gridView = findViewById(R.id.nearby_places_gridview);
        place_area = findViewById(R.id.area_spinner);
        place_type = findViewById(R.id.types_spinner);
        find = findViewById(R.id.btn_find);

        // initializing LocationHelper
        locationServiceHelper = new LocationServiceHelper(this, this);

        checkLocationPermission();
        //get location latitude and longitude
        locationServiceHelper.getLatLon();

        final ArrayAdapter<CharSequence> area_adapter = ArrayAdapter.createFromResource(this,
                R.array.nearby_places_area, android.R.layout.simple_spinner_item);
        area_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        place_area.setAdapter(area_adapter);

        place_area.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long
                    l) {
                switch (position) {
                    case 0:
                        area = 500;
                        Log.e("selected:", String.valueOf(area));
                        break;
                    case 1:
                        area = 1000;
                        Log.e("selected:", String.valueOf(area));
                        break;
                    case 2:
                        area = 1500;
                        Log.e("selected:", String.valueOf(area));
                        break;
                    case 3:
                        area = 3000;
                        Log.e("selected:", String.valueOf(area));
                        break;
                    case 4:
                        area = 5000;
                        Log.e("selected:", String.valueOf(area));
                        break;
                    case 5:
                        area = 10000;
                        Log.e("selected:", String.valueOf(area));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                area = 500;
            }
        });

        ArrayAdapter<CharSequence> type_adapter = ArrayAdapter.createFromResource(this,
                R.array.nearby_places_categories, android.R.layout.simple_spinner_item);
        type_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        place_type.setAdapter(type_adapter);

        place_type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long
                    l) {
                switch (position) {
                    case 0:
                        type = "restaurant";
                        Log.e("selected:", String.valueOf(type));
                        break;
                    case 1:
                        type = "hospital";
                        Log.e("selected:", String.valueOf(type));
                        break;
                    case 2:
                        type = "atm";
                        Log.e("selected:", String.valueOf(type));
                        break;
                    case 3:
                        type = "bank";
                        Log.e("selected:", String.valueOf(type));
                        break;
                    case 4:
                        type = "bus_station";
                        Log.e("selected:", String.valueOf(type));
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {
                type = "restaurant";
            }
        });

        find.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ringProgressDialog = ProgressDialog.show(context, "Please wait ...", "Downloading" +
                        " Image ...", true);
                ringProgressDialog.setCancelable(false);
                updateUi(type, area, api_key);
            }
        });


        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(PlaceService.class);


    }

    private void updateUi(String placeType, int placeArea, String api) {
        try {
            places.clear();
            String urlString = String.format("nearbysearch/json?location=%f," +
                    "%f&radius=%d&types=%s&key=%s", lattitude, longitude, placeArea, placeType,
                    api);
            Call<PlaceResponse> responseCall = service.getplaceResponse(urlString);

            responseCall.enqueue(new Callback<PlaceResponse>() {
                @Override
                public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse> response) {
                    if (response.code() == 200) {

                        responses = response.body();
                        ringProgressDialog.dismiss();
                        for (int i = 0; i < responses.getResults().size(); i++) {
                            places.add(responses.getResults().get(i));
                        }

                        adapter = new PlaceAdapter(context, places);
                        gridView.setAdapter(adapter);


                        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int
                                    position, long l) {
                                String name = places.get(position).getName();
                                double lat = places.get(position).getGeometry().getLocation()
                                        .getLat();
                                double lon = places.get(position).getGeometry().getLocation()
                                        .getLng();
                                geocoder = new Geocoder(context, Locale.getDefault());

                                try {
                                    addresses = geocoder.getFromLocation(lat, lon, 1);
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }
                                String addr = addresses.get(0).getAddressLine(0); // If any
                                // additional address line present than only, check with max
                                // available address lines by getMaxAddressLineIndex()
                                String city = addresses.get(0).getLocality();
                                String country = addresses.get(0).getCountryName();
                                //String postalCode = addresses.get(0).getPostalCode();

                                String address = addr + ", " + city + ", " + country;
                                String phn = "none";
                                String img = "none";
                                try {
                                    img = places.get(position).getPhotos().get(0)
                                            .getPhotoReference();
                                    phn = addresses.get(0).getPhone();
                                } catch (Exception e) {

                                }


                                Intent intent = new Intent(context, NearbyMapActivity.class);
                                intent.putExtra("name", name);
                                intent.putExtra("address", address);
                                intent.putExtra("phone", phn);
                                intent.putExtra("photo", img);
                                intent.putExtra("lat", lat);
                                intent.putExtra("lon", lon);

                                startActivity(intent);

                            }
                        });

                        Toast.makeText(context, "Size: " + responses.getResults().size(), Toast
                                .LENGTH_LONG).show();
                    }
                }

                @Override
                public void onFailure(Call<PlaceResponse> call, Throwable t) {
                    Toast.makeText(context, t.toString(), Toast.LENGTH_LONG).show();

                }
            });


        } catch (Exception e) {
            Toast.makeText(context, "" + e.getMessage(), Toast.LENGTH_SHORT).show();
        }

//        if (responses.getNextPageToken() != null) {
//            try {
//                String url = String.format("nearbysearch/json?pagetoken=%s&key=%s", responses
// .getNextPageToken(), api_key);
//                Call<PlaceResponse> responseCall2 = service.getplaceResponse(url);
//                responseCall2.enqueue(new Callback<PlaceResponse>() {
//                    @Override
//                    public void onResponse(Call<PlaceResponse> call, Response<PlaceResponse>
// response) {
//                        responses = null;
//                        responses = response.body();
//                        //assert responses != null;
//                        for (int i = 0; i < responses.getResults().size(); i++) {
//                            places.add(responses.getResults().get(i));
//                        }
//                    }
//
//                    @Override
//                    public void onFailure(Call<PlaceResponse> call, Throwable t) {
//
//                    }
//                });
//
//            } catch (Exception e) {
//
//            }
//        }
    }

    private void othersInitiaLization() {
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar_nearby_places);
        setSupportActionBar(toolbar);

        context = this;
        preferences = getApplicationContext().getSharedPreferences("user", MODE_PRIVATE);
        editor = preferences.edit();
        auth = FirebaseAuth.getInstance();

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string
                .navigation_drawer_close);
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

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.events, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            return true;
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_map) {
            Intent intent = new Intent(context,MapActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_events) {
            Intent intent = new Intent(context, EventsActivity.class);
            finish();
            startActivity(intent);

        } else if (id == R.id.nav_weather) {
            Intent intent = new Intent(context,WeatherActivity.class);
            startActivity(intent);

        } else if (id == R.id.nav_nearby_places) {


        } else if (id == R.id.nav_share) {

        } else if (id == R.id.nav_sign_out) {

            auth.signOut();
            editor.clear();
            editor.putBoolean("signedIn", false);
            editor.commit();

            Toast.makeText(context, "Logged out", Toast.LENGTH_LONG).show();
            Intent intent = new Intent(context, LoginActivity.class);
            finish();
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onLatLonReceived(double lat, double lon) {
        this.lattitude = lat;
        this.longitude = lon;
        locationServiceHelper.stopLocationUpdates();
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

}
