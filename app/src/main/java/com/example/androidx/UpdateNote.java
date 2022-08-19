package com.example.androidx;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

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
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.button.MaterialButton;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;

public class UpdateNote extends AppCompatActivity {

    Realm realm;

    private static final int REQUEST_LOCATION = 1;
    String latitude, longitude;
    LocationManager locationManager;

    EditText titleUpdate;
    EditText descriptionUpdate;
    MaterialButton updatebtn;
    EditText locationUpdate;
    ImageView getLocation;
    Note note;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_note);



        realm = Realm.getDefaultInstance();
        titleUpdate = findViewById(R.id.titleupdate);
        descriptionUpdate = findViewById(R.id.descriptionupdate);
        updatebtn = findViewById(R.id.updatebtn);
        locationUpdate = findViewById(R.id.locationupdate);
        getLocation = findViewById(R.id.getLocationupdate);


        String title = getIntent().getExtras().getString("data_title");
        note=realm.where(Note.class).equalTo("title", title).findFirst();


        titleUpdate.setText(note.getTitle());
        descriptionUpdate.setText(note.getDescription());
        locationUpdate.setText(note.getLocation());

        ActivityCompat.requestPermissions(this, new String[]{ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        getLocation.setOnClickListener(new View.OnClickListener() {
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


        updatebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateData(title);
            }
        });
    }

    private void updateData(String title){

        realm.executeTransactionAsync(
                realm1 -> {
                    Note notes = realm1.where(Note.class).equalTo("title", title).findFirst();

                    notes.setTitle(titleUpdate.getText().toString());
                    notes.setDescription(descriptionUpdate.getText().toString());
                    notes.setLocation(locationUpdate.getText().toString());

                    finish();
                },
                () -> Toast.makeText(UpdateNote.this, "Berhasil Update", Toast.LENGTH_LONG).show(),
                error -> Toast.makeText(UpdateNote.this, "Gagal", Toast.LENGTH_LONG).show()
        );
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
        if(ActivityCompat.checkSelfPermission(UpdateNote.this, ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(UpdateNote.this,ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)
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
                    locationUpdate.setText(AddressLine);
                    locationUpdate.setVisibility(View.VISIBLE);
                }catch (IOException e){
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(this,"Unable to find location", Toast.LENGTH_SHORT).show();
            }
        }
    }
}