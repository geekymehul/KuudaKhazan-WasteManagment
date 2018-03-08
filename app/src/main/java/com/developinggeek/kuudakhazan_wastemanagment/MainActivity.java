package com.developinggeek.kuudakhazan_wastemanagment;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.developinggeek.kuudakhazan_wastemanagment.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();

       /* mAuthListener = new FirebaseAuth.AuthStateListener() {

            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user == null) {
                    // User is not signed in
                    sendToStart();
                }
            }
        };*/

       if(mAuth.getCurrentUser()==null)
       {
           sendToStart();
       }

    }

    public void sendToStart() {

        Log.i("login","login");

        Intent startIntent=new Intent(MainActivity.this,LoginActivity.class);
        startActivity(startIntent);
        finish();

    }
}
