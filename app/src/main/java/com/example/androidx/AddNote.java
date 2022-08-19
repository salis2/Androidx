package com.example.androidx;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

import static androidx.constraintlayout.motion.widget.Debug.getLocation;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.EditText;
import io.realm.Realm;

import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class AddNote extends AppCompatActivity {

    private static final int REQUEST_LOCATION = 1;
    String latitude, longitude;
    LocationManager locationManager;
    EditText locationInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_note);

        EditText titleInput = findViewById(R.id.titleinput);
        EditText descriptionInput = findViewById(R.id.descriptioninput);
        MaterialButton saveBtn = findViewById(R.id.savebtn);
        locationInput = findViewById(R.id.locationinput);
        ImageView getlocation = findViewById(R.id.getLocationinput);

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        getlocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
                if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)){
                    OnGPS();
                }else {
                    GetLocation();
                }
            }
        });


        Realm.init(getApplicationContext());
        Realm realm = Realm.getDefaultInstance();

        saveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String title = titleInput.getText().toString();
                String description = descriptionInput.getText().toString();
                long createdTime = System.currentTimeMillis();
                String inlocation = locationInput.getText().toString();

                realm.beginTransaction();
                Note note = realm.createObject(Note.class);
                note.setTitle(title);
                note.setDescription(description);
                note.setCreatedTime(createdTime);
                note.setLocation(inlocation);
                realm.commitTransaction();
                Toast.makeText(getApplicationContext(),"Note saved",Toast.LENGTH_SHORT).show();
                finish();


            }
        });
    }

    private void OnGPS(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Enable GPS").setCancelable(false).setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                startActivity(new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        }).setPositiveButton("No", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        final AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void GetLocation(){
        Geocoder geocoder = new Geocoder(this, Locale.getDefault());
        if(ActivityCompat.checkSelfPermission(AddNote.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(AddNote.this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
        {
            ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION},REQUEST_LOCATION);
        }else
        {
         Location LocationGps = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
         if(LocationGps !=null)
         {
             double lat = LocationGps.getLatitude();
             double longi = LocationGps.getLongitude();

             try{
                 List<Address> addresses = geocoder.getFromLocation(lat,longi,1);
                 String AddressLine = addresses.get(0).getAddressLine(0);
                 locationInput.setText(AddressLine);
                 locationInput.setVisibility(View.VISIBLE);
             }catch (IOException e){
                 e.printStackTrace();
             }
         }else{
             Toast.makeText(this,"Unable to find location", Toast.LENGTH_SHORT).show();
         }
        }
    }
}