package com.developinggeek.kuudakhazan_wastemanagment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.developinggeek.kuudakhazan_wastemanagment.Activity.PostWasteProduct;
import com.developinggeek.kuudakhazan_wastemanagment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();


       if(mAuth.getCurrentUser()==null)
       {
           sendToStart();
       }
       floatingActionButton=(FloatingActionButton) findViewById(R.id.fab);
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this, PostWasteProduct.class);
                startActivity(intent);
            }
        });


    }

    public void sendToStart() {

        Log.i("login","login");

        Intent startIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();

    }
}
