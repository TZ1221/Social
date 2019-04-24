package com.example.firetest;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;


public class map extends AppCompatActivity
        implements OnMapReadyCallback {


    String TAG="out";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_map);
        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }


    @Override
    public void onMapReady(final GoogleMap googleMap) {

        LatLng sydney = new LatLng(-33.852, 151.211);
        googleMap.addMarker(new MarkerOptions().position(sydney)
                .title("Marker in Sydney"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));



        Intent currentintent = getIntent();
        String uid = currentintent.getStringExtra("message");




        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(uid);
        DatabaseReference upvotesRef = myRef.child("myHost");
        Toast.makeText(getApplicationContext(), uid, Toast.LENGTH_LONG).show();




        upvotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String myhost = dataSnapshot.getValue(String.class);
                Log.d(TAG, "the host: " + myhost);





                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/hosts"+"/"+myhost);
                DatabaseReference upvotesRef = myRef.child("location") ;


                upvotesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String location = dataSnapshot.getValue(String.class);
                        Log.d(TAG, "host Value is: " + location);




                        LatLng adot = new LatLng(Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]));
                        googleMap.addMarker(new MarkerOptions().position(adot)
                                .icon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)));

                        googleMap.moveCamera(CameraUpdateFactory.newLatLng(adot));

                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });









                upvotesRef = myRef.child("currentUsers") ;


                // Read from the database
                upvotesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String userlist= dataSnapshot.getValue(String.class);

                        Log.d(TAG, "u list is: " + userlist);



                        for ( String eachuser: userlist.split(",")){



                            FirebaseDatabase database = FirebaseDatabase.getInstance();
                            DatabaseReference myRef = database.getReference(eachuser);
                            DatabaseReference upvotesRef = myRef.child("location");


                            // Read from the database
                            upvotesRef.addValueEventListener(new ValueEventListener() {


                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    // This method is called once with the initial value and again
                                    // whenever data at this location is updated.




                                    String location = dataSnapshot.getValue(String.class);
                                    Log.d(TAG, "location is: " + location);






                                    LatLng adot = new LatLng(Double.valueOf(location.split(",")[0]), Double.valueOf(location.split(",")[1]));
                                    googleMap.addMarker(new MarkerOptions().position(adot)
                                            );
                                    googleMap.moveCamera(CameraUpdateFactory.newLatLng(adot));














                                }

                                @Override
                                public void onCancelled(DatabaseError error) {
                                    // Failed to read value
                                    Log.w(TAG, "Failed to read value.", error.toException());
                                }
                            });









                        }












                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });

















            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w(TAG, "Failed to read value.", error.toException());
            }
        });


    }
}
