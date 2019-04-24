package com.example.firetest;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

public class profile extends AppCompatActivity implements  View.OnClickListener {



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);





        findViewById(R.id.hiking).setOnClickListener(this);
        findViewById(R.id.smoking).setOnClickListener(this);
        findViewById(R.id.dancing).setOnClickListener(this);
        findViewById(R.id.fishing).setOnClickListener(this);
        findViewById(R.id.drinking).setOnClickListener(this);
        findViewById(R.id.boardgames).setOnClickListener(this);



    }


    @Override
    public void onClick(View view) {



        switch (view.getId()) {
            case R.id.hiking:


                String str="hiking";
                Intent intent = new Intent(getApplicationContext(), profile2.class);
                intent.putExtra("message", str);
                startActivity(intent);
                break;

            case R.id.smoking:

                
                String str2="smoking";
                Intent intent2 = new Intent(getApplicationContext(), profile2.class);
                intent2.putExtra("message", str2);
                startActivity(intent2);
                break;



            case R.id.dancing:


                str="dancing";
                intent = new Intent(getApplicationContext(), profile2.class);
                intent.putExtra("message", str);
                startActivity(intent);
                break;


            case R.id.fishing:


                str="fishing";
                intent = new Intent(getApplicationContext(), profile2.class);
                intent.putExtra("message", str);
                startActivity(intent);
                break;

            case R.id.boardgames:


                str="boardgames";
                intent = new Intent(getApplicationContext(), profile2.class);
                intent.putExtra("message", str);
                startActivity(intent);
                break;


            case R.id.drinking:


                str="drinking";
                intent = new Intent(getApplicationContext(), profile2.class);
                intent.putExtra("message", str);
                startActivity(intent);
                break;




        }
    }




}




