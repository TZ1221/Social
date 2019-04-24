package com.example.firetest;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.MutableData;
import com.google.firebase.database.Query;
import com.google.firebase.database.Transaction;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;
import java.io.IOException;
import android.app.ProgressDialog;
import static java.lang.String.valueOf;

public class userIn extends AppCompatActivity implements LocationListener,View.OnClickListener {




    String emailhost="enter your gmail account";
    final String password="enter your password";






    String userOrHost="user";
    private LocationManager locationManager;
    private String provider;
    private static final String TAG = MainActivity.class.getName();
    static String hostuid="";
    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
     String uid = user.getUid();
    final String userEmail=user.getEmail();
    private TextView  currentsize;
    private TextView  address;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_in);

        findViewById(R.id.map).setOnClickListener( this);

        currentsize = (TextView) findViewById(R.id.currentsize);
        address = (TextView) findViewById(R.id.address);
        locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        Criteria criteria = new Criteria();
        provider = locationManager.getBestProvider(criteria, false);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

            return;
        }
        Location location = locationManager.getLastKnownLocation(provider);




        //wrtite user info

        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(uid);

        final double lat = (location.getLatitude());
        final double lng = (location.getLongitude());



        myRef.child("latitude").setValue(lat);
        myRef.child("longitude").setValue(lng);
        myRef.child ("location").setValue(Double.valueOf(lat).toString() +","+ Double.valueOf(lng).toString() );
        myRef.child("userOrHost").setValue(userOrHost);


        Intent intent = getIntent();
        String activity = intent.getStringExtra("message");
        myRef.child("activity").setValue(activity);




        FirebaseInstanceId.getInstance().getInstanceId()
                .addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
                    @Override
                    public void onComplete(@NonNull Task<InstanceIdResult> task) {
                        if (!task.isSuccessful()) {
                            Log.w(TAG, "getInstanceId failed", task.getException());
                            return;
                        }

                        // Get new Instance ID token
                        String token = task.getResult().getToken();


                        Log.d(TAG, token);
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef = database.getReference(uid);
                        myRef.child("token").setValue(token);


                    }
                });












        //read and update the host information



        //get the clost host;  active, not full


        DatabaseReference dbRef = database.getReference("/hosts");
        Query query = dbRef.orderByChild("activity").equalTo(activity);


        query.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                float minD=1000000000;
                String email="";


                if (dataSnapshot.exists()) {
                    // dataSnapshot is the "issue" node with all children with id 0
                    for (DataSnapshot ahost : dataSnapshot.getChildren()) {
                        //Log.d("!!!!myTag!!!", ahost.child("latitude").getValue().toString());



                        Location loc1 = new Location("");
                        loc1.setLatitude(Double.parseDouble( valueOf (ahost.child("latitude").getValue())));
                        loc1.setLongitude(Double.parseDouble( valueOf (ahost.child("longitude").getValue())));

                        Location loc2 = new Location("");
                        loc2.setLatitude(lat);
                        loc2.setLongitude(lng);

                        float distanceInMeters = loc1.distanceTo(loc2);
                        Log.d("!!!!distance!!", String.valueOf(distanceInMeters));
                        Log.d("!!!!email!!", email=ahost.child("hostEmail").getValue().toString());
                        Log.d("!!!!key!! !",ahost.getKey());

                        if (distanceInMeters <minD)
                        {minD=distanceInMeters;
                        hostuid=ahost.getKey();
                        email=ahost.child("hostEmail").getValue().toString();
                        }


                    }




                    Log.d("!!!!min dis!!", String.valueOf(minD));
                    Log.d("!!!!min email !", email);
                    Log.d("!!!!min key!! !", hostuid);




                }





                // add my host to the user

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(uid);
                myRef.child("myHost").setValue(hostuid);





                //update the host current users

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference("/hosts"+"/"+hostuid);
                DatabaseReference upvotesRef = myRef.child("currentUsers");
                upvotesRef.runTransaction(new Transaction.Handler() {
                    @Override
                    public Transaction.Result doTransaction(MutableData mutableData) {



                        String uidlist = mutableData.getValue(String.class);
                        if (uidlist == "") {
                            mutableData.setValue(uid);
                        } else {

                                mutableData.setValue(uidlist  +uid+",");
                        }

                        return Transaction.success(mutableData);
                    }

                    @Override
                    public void onComplete(
                            DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                        System.out.println("Transaction completed");
                    }
                });




        // Read from host's threshold
                upvotesRef = myRef.child("threshold");
                upvotesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        final Integer threshold = dataSnapshot.getValue(Integer.class);
                        Log.d(TAG, "threshold Value is: " + threshold);






                        //add you to the email list
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference    myRef = database.getReference("/hosts"+"/"+hostuid);
                        DatabaseReference   upvotesRef = myRef.child("currentEmail");
                        upvotesRef.runTransaction(new Transaction.Handler() {
                            @Override
                            public Transaction.Result doTransaction(MutableData mutableData) {
                                String currentValue = mutableData.getValue(String.class);
                                if (currentValue == null) {
                                    mutableData.setValue(userEmail);
                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/hosts"+"/"+hostuid);
                                    DatabaseReference upvotesRef = myRef.child("currentSize");
                                    upvotesRef.setValue(   1 );
                                } else {

                                    mutableData.setValue(currentValue + "," + userEmail);



                                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                                    DatabaseReference myRef = database.getReference("/hosts"+"/"+hostuid);
                                    DatabaseReference upvotesRef = myRef.child("currentSize");
                                    upvotesRef.setValue(   currentValue.split(",").length+1 );







                                    if  (currentValue.split(",").length+1 >= Integer.valueOf(threshold)){





                                        //send emails


                                        database = FirebaseDatabase.getInstance();
                                        upvotesRef = database.getReference("/hosts"+"/"+hostuid).child("address");

                                        upvotesRef .addValueEventListener(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(DataSnapshot dataSnapshot) {
                                                // This method is called once with the initial value and again
                                                // whenever data at this location is updated.
                                                final String address = dataSnapshot.getValue(String.class);
                                                Log.d(TAG, "add is: " + address);



                                                FirebaseDatabase database = FirebaseDatabase.getInstance();
                                                DatabaseReference myRef = database.getReference("/hosts"+"/"+hostuid);
                                                DatabaseReference upvotesRef = myRef.child("currentEmail");
                                                upvotesRef.addValueEventListener(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                                        // This method is called once with the initial value and again
                                                        // whenever data at this location is updated.
                                                        String value = dataSnapshot.getValue(String.class);

                                                        Log.d(TAG, "email is: " + value);




                                                        try{
                                                            for (String each : value.split(",") ) {
                                                                sendMessage(each, address);
                                                            }}
                                                        catch (Exception e){
                                                            try {
                                                                sendMessage (value, address);
                                                            } catch (IOException e1) {
                                                                e1.printStackTrace();
                                                            }
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

                                return Transaction.success(mutableData);
                            }

                            @Override
                            public void onComplete(
                                    DatabaseError databaseError, boolean committed, DataSnapshot dataSnapshot) {
                                System.out.println("Transaction completed");
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
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });









    }







    protected void onStart() {

        super.onStart();


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference(uid);
        DatabaseReference upvotesRef = myRef.child("myHost");

        // Read from the database
        upvotesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
               String myhost = dataSnapshot.getValue(String.class);
               // Log.d(TAG, "Value is: " + value.toString());
                // Toast.makeText(getApplicationContext(),value, Toast.LENGTH_LONG).show();



                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference("/hosts"+"/"+myhost);
                DatabaseReference upvotesRef = myRef.child("currentSize");





                upvotesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        Integer value = dataSnapshot.getValue(Integer.class);
//                        Log.d(TAG, "Value is: " + value.toString());
                        //Toast.makeText(getApplicationContext(),value.toString(), Toast.LENGTH_LONG).show();

                        currentsize.setText(value+"    people have joined ");
                    }

                    @Override
                    public void onCancelled(DatabaseError error) {
                        // Failed to read value
                        Log.w(TAG, "Failed to read value.", error.toException());
                    }
                });





                upvotesRef = myRef.child("address");
                upvotesRef.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // This method is called once with the initial value and again
                        // whenever data at this location is updated.
                        String value = dataSnapshot.getValue(String.class);
//                        Log.d(TAG, "Value is: " + value.toString());
                        //Toast.makeText(getApplicationContext(),value.toString(), Toast.LENGTH_LONG).show();
                        address.setText("host location "+value);
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





    /* Request updates at startup */
    @Override
    protected void onResume() {
        super.onResume();
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    2);

            return;
        }
        locationManager.requestLocationUpdates(provider, 400, 1, (LocationListener) this);
    }

    /* Remove the locationlistener updates when Activity is paused */
    @Override
    protected void onPause() {
        super.onPause();
        locationManager.removeUpdates((LocationListener) this);
    }

    @Override
    public void onLocationChanged(Location arg0) {
        double lat =  (arg0.getLatitude());
        double lng =  (arg0.getLongitude());

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onProviderEnabled(String provider) {


    }

    @Override
    public void onProviderDisabled(String provider) {

    }













        private void sendMessage( final String etRecipient, String address) throws IOException {

            final String etContent= "the event has been activated; welcome to join the event " +
                    "the following is the address:" +
                    ""+address;



            final ProgressDialog dialog = new ProgressDialog(this);
            dialog.setTitle("Sending Email");
            dialog.setMessage("Please wait");
            dialog.show();
            Thread sender = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        GMailSender sender = new GMailSender(emailhost, password);
                        sender.sendMail("EmailSender App",
                                etContent,
                                emailhost,
                                etRecipient);
                        dialog.dismiss();
                    } catch (Exception e) {
                        Log.e("mylog", "Error: " + e.getMessage());
                    }
                }
            });
            sender.start();




        }






    public void onClick(View view) {


        switch (view.getId()) {
            case R.id.map:

                Intent intent = new Intent(getApplicationContext(), map.class);
                intent.putExtra("message",  uid);
                startActivity(intent);
                break;


        }
    }
}
