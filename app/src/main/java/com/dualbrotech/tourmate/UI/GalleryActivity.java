package com.dualbrotech.tourmate.UI;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.GridView;
import android.widget.Toast;

import com.dualbrotech.tourmate.Adapters.ImageAdapter;
import com.dualbrotech.tourmate.Models.EventImage;
import com.dualbrotech.tourmate.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class GalleryActivity extends AppCompatActivity {
    GridView gridView;
    public static String BASE_URL = "https://firebasestorage.googleapis.com/v0/b/tour-mate-3bd52.appspot.com/o/";
    ArrayList<EventImage> eventImages = new ArrayList<>();
    ImageAdapter adapter;

    FirebaseDatabase firebaseDatabase;
    DatabaseReference myref;
    ProgressDialog ringProgressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gallery);

        ringProgressDialog = ProgressDialog.show(this, "Please wait ...", "Downloading Images...", true);
        ringProgressDialog.setCancelable(false);
        Intent intent = getIntent();
        String eventId = intent.getStringExtra("eventId");


        gridView = findViewById(R.id.image_gridView);
        firebaseDatabase = FirebaseDatabase.getInstance();
        myref = firebaseDatabase.getReference();

        Query query = myref.child("event_images").orderByChild("eventId").equalTo(eventId);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //eventImages.clear();
                for (DataSnapshot items : dataSnapshot.getChildren()){
                    EventImage image = items.getValue(EventImage.class);
                    eventImages.add(image);
                }
                Toast.makeText(GalleryActivity.this, "Size:"+eventImages.size(), Toast.LENGTH_SHORT).show();

                if (eventImages.size()>0){
                    adapter = new ImageAdapter(GalleryActivity.this,eventImages);
                    gridView.setAdapter(adapter);
                    ringProgressDialog.dismiss();
                }else {
                    ringProgressDialog.dismiss();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
}
