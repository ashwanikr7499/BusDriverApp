package com.example.busdriverapp;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;

import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.View;

import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    DatabaseReference databaseDrivers;
    private static final int REQUEST_LOCATION = 1;
    Button btnGetLocation;
    TextView showLocation;
    LocationManager locationManager;
    String latitude, longitude;
    String veh_no="NA",phone_no="NA";

    String uid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        SharedPreferences prefs = getSharedPreferences("SP_USER", MODE_PRIVATE);
        uid = prefs.getString("Current_USERID", "NA");
        databaseDrivers = FirebaseDatabase.getInstance().getReference("drivers");

        DatabaseReference rootRef = FirebaseDatabase.getInstance().getReference();
        rootRef.child("drivers").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                if (snapshot.hasChild(uid)) {
                            databaseDrivers.child(uid).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Driver driver = dataSnapshot.getValue(Driver.class);
                ((TextView)findViewById(R.id.phone_no)).setText(driver.getPhoneNo());
                ((TextView)findViewById(R.id.vehicle_no)).setText(driver.getBus_no());
                veh_no=driver.getBus_no();
                phone_no=driver.getPhoneNo();
            }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                handler.postDelayed(this, 1 * 60 * 1000); // every 60s
                updateLocation();
            }
        }, 2000); // first after 2s instantly
        findViewById(R.id.Submit).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                veh_no=((TextView)(findViewById(R.id.vehicle_no))).getText().toString();
                phone_no=((TextView)(findViewById(R.id.phone_no))).getText().toString();
                databaseDrivers = FirebaseDatabase.getInstance().getReference("drivers");
                Driver d = new Driver(uid, veh_no, latitude, longitude,phone_no);
                databaseDrivers.child(uid).setValue(d);
            }
        });

    }

    void updateLocation() {
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            OnGPS();
        } else {
            if (ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(
                    MainActivity.this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
            } else {
                Location locationGPS = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                if (locationGPS != null) {
                    double lat = locationGPS.getLatitude();
                    double longi = locationGPS.getLongitude();
                    latitude = String.valueOf(lat);
                    longitude = String.valueOf(longi);
//
//                    showLocation.setText("Your Location: " + "\n" + "Latitude: " + latitude + "\n" + "Longitude: " + longitude);
                    Toast.makeText(this, "Sending location...", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "Unable to find location.", Toast.LENGTH_SHORT).show();
                }
            }
        }

        String time = String.valueOf(System.currentTimeMillis());
        databaseDrivers = FirebaseDatabase.getInstance().getReference("drivers");
        Driver d = new Driver(uid, veh_no, latitude, longitude,phone_no);
        databaseDrivers.child(uid).setValue(d);
    }

    private void OnGPS() {
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setNegativeButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }


}

