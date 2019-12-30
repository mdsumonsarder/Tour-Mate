package com.dualbrotech.tourmate.others;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * Created by Hard-won on 1/21/2018.
 */

public class Tools {
    public static String convertDate(long times) {
        Date date = new Date(times * 1000);
        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy - EEEE");
//        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String dateSt = sdf.format(date);

//        Log.d("TIME", "convertTime: "+time);
        return dateSt;
    }

    public static String convertTime(long times) {
        Date date = new Date(times * 1000);
//        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM, yyyy - EEEE");
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm aa");
        String time = sdf.format(date);

//        Log.d("TIME", "convertTime: "+time);
        return time;
    }
}
