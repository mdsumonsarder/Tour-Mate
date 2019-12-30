package com.dualbrotech.tourmate.UI;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.WebResponses.DirectionResponse;
import com.dualbrotech.tourmate.helpers.LocationServiceHelper;
import com.dualbrotech.tourmate.others.URLs;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.GoogleMapOptions;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.squareup.picasso.Picasso;

import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class NearbyMapActivity extends AppCompatActivity implements OnMapReadyCallback,
        LocationServiceHelper.LocationResponseListener {

    public static final String BASE_URL = "https://maps.googleapis.com/maps/api/directions/";
    ImageView iv_nearby_place_image;
    TextView tv_nearby_place_name, tv_nearby_place_address, tv_nearby_place_phone, tv_distance,
            tv_time;
    String address = "";
    String phn = "";
    String img = "";
    String name = "";
    double lat, lon;
    private Button btnDirection;
    private GoogleMap map;
    private GoogleMapOptions options;
    private DirectionService service;
    private String origin = "23.750452,90.393378";
    private String destination = "23.727924,90.413520";
    private String[] instructions;
    private int totalRoute = 0;
    private int routeIndex = 0;

    private LocationServiceHelper locationServiceHelper;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_nearby_map);

        iv_nearby_place_image = findViewById(R.id.iv_nearby_place_image);
        tv_nearby_place_name = findViewById(R.id.tv_nearby_place_name);
        tv_nearby_place_address = findViewById(R.id.tv_nearby_place_address);
        tv_nearby_place_phone = findViewById(R.id.tv_nearby_place_phone);
        tv_distance = findViewById(R.id.tv_distance);
        tv_time = findViewById(R.id.tv_time);
        btnDirection = findViewById(R.id.btnDirection);


        options = new GoogleMapOptions();
        options.zoomControlsEnabled(true);

        // initializing LocationHelper
        locationServiceHelper = new LocationServiceHelper(this, this);

        checkLocationPermission();
        //get location latitude and longitude
        locationServiceHelper.getLatLon();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        service = retrofit.create(DirectionService.class);

        SupportMapFragment mapFragment = SupportMapFragment.newInstance(options);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.nearby_map_container, mapFragment);
        ft.commit();
        mapFragment.getMapAsync(this);

        Intent intent = getIntent();
        address = intent.getStringExtra("address");
        phn = intent.getStringExtra("phone");
        img = intent.getStringExtra("photo");
        name = intent.getStringExtra("name");
        lat = intent.getDoubleExtra("lat", 0.0);
        lon = intent.getDoubleExtra("lon", 0.0);

        destination = String.valueOf(lat) + "," + String.valueOf(lon);

        try {
            if (img.equals("none")) {
                iv_nearby_place_image.setImageResource(R.drawable.noimg);
            } else {
                String imageReference = NearbyPlacesActivity.BASE_URL +
                        "photo?maxwidth=400&maxheight=400&photoreference=" + img +
                        "&key=AIzaSyAXG0k0FK5sUbrIF6MHGGDCaop66Up1oDs";
                Uri uri = Uri.parse(imageReference);
                Picasso.with(this).load(uri).into(iv_nearby_place_image);
            }

            if (phn.equals("none")) {
                tv_nearby_place_phone.setText("No phone found");
            } else {
                tv_nearby_place_phone.setText(phn);
            }

        } catch (Exception e) {

        }
        tv_nearby_place_name.setText(name);
        tv_nearby_place_address.setText(address);

        btnDirection.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getDirection();
            }
        });


    }

    private void getDirection() {
        String key = getString(R.string.google_direction_api);
        String urlString
                = String.format("json?origin=%s&destination=%s&key=%s",
                origin, destination, key);
        Call<DirectionResponse> directionResponseCall = service.getDirections(urlString);
        directionResponseCall.enqueue(new Callback<DirectionResponse>() {
            @Override
            public void onResponse(Call<DirectionResponse> call, Response<DirectionResponse>
                    response) {
                if (response.isSuccessful()) {
                    DirectionResponse directionResponse = response.body();

                    List<DirectionResponse.Step> steps =
                            directionResponse.getRoutes().get(0)
                                    .getLegs().get(0).getSteps();
                    //iterate through steps collection, define start and end latlng for each step
                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(23.750452,
                            90.393378), 13));
                    map.clear();
                    LatLng startLatLon = new LatLng(directionResponse.getRoutes().get(0).getLegs
                            ().get(0).getStartLocation().getLat(),
                            directionResponse.getRoutes().get(0).getLegs().get(0)
                                    .getStartLocation().getLng());
                    LatLng endLatlon = new LatLng(directionResponse.getRoutes().get(0).getLegs()
                            .get(0).getEndLocation().getLat(),
                            directionResponse.getRoutes().get(0).getLegs().get(0).getEndLocation
                                    ().getLng());
                    map.addMarker(new MarkerOptions().position(startLatLon).title("Starting " +
                            "Point"));
                    map.addMarker(new MarkerOptions().position(endLatlon).title("Ending Point"));
                    CameraPosition cameraPosition = new CameraPosition.Builder()
                            .target(startLatLon)      // Sets the center of the map to Mountain View
                            .zoom(17)                   // Sets the zoom
                            .bearing(90)                // Sets the orientation of the camera to
                            // east
                            .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                            .build();
                    map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));

                    tv_distance.setText("Distance: " + directionResponse.getRoutes().get(0)
                            .getLegs().get(0).getDistance().getText());
                    tv_time.setText("Time: " + directionResponse.getRoutes().get(0).getLegs().get
                            (0).getDuration().getText());
                    tv_time.setVisibility(View.VISIBLE);
                    tv_distance.setVisibility(View.VISIBLE);

                    for (int i = 0; i < steps.size(); i++) {
                        double startLat = steps.get(i).getStartLocation().getLat();
                        double startLng = steps.get(i).getStartLocation().getLng();
                        LatLng startPoint = new LatLng(startLat, startLng);

                        double endLat = steps.get(i).getEndLocation().getLat();
                        double endLng = steps.get(i).getEndLocation().getLng();
                        LatLng endPoint = new LatLng(endLat, endLng);

                        Polyline polyline = map.addPolyline(new PolylineOptions()
                                .add(startPoint)
                                .add(endPoint));
                        polyline.setColor(Color.BLUE);
                    }


                }


            }

            @Override
            public void onFailure(Call<DirectionResponse> call, Throwable t) {

            }
        });
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        map = googleMap;
        map.clear();
        LatLng pos = new LatLng(lat, lon);
        map.addMarker(new MarkerOptions().position(pos).title(name));
        LatLng coordinate = new LatLng(lat, lon);
//        CameraUpdate location = CameraUpdateFactory.newLatLngZoom(
//                coordinate, 18);
//        map.animateCamera(location);

        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(coordinate)      // Sets the center of the map to Mountain View
                .zoom(17)                   // Sets the zoom
                .bearing(90)                // Sets the orientation of the camera to east
                .tilt(30)                   // Sets the tilt of the camera to 30 degrees
                .build();
        map.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
    }

    @Override
    public void onLatLonReceived(double lat, double lon) {
        origin = String.valueOf(lat) + "," + String.valueOf(lon);
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
