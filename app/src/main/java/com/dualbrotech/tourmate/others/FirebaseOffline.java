package com.dualbrotech.tourmate.others;

import android.app.Application;

import com.google.firebase.database.FirebaseDatabase;

/**
 * Created by Arif Rahman on 2/13/2018.
 */

public class FirebaseOffline extends Application{
    @Override
    public void onCreate() {
        super.onCreate();
        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
    }
}
