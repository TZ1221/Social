package com.example.firetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class profile2 extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile2);



        findViewById(R.id.user).setOnClickListener(this);
        findViewById(R.id.host).setOnClickListener(this);


    }


    @Override
    public void onClick(View view) {



        switch (view.getId()) {
            case R.id.user:

                Intent currentintent = getIntent();

                String activity = currentintent.getStringExtra("message");


                Intent intent = new Intent(getApplicationContext(), userIn.class);
                intent.putExtra("message",  activity);

                startActivity(intent);


                break;

            case R.id.host:

                Intent currentintent2 = getIntent();

                String activity2 = currentintent2.getStringExtra("message");


                Intent intent2 = new Intent(getApplicationContext(), hostIn.class);
                intent2.putExtra("message", activity2);

                startActivity(intent2);




                break;
        }
    }

}
