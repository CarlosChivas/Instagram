package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.parse.ParseUser;

public class MainActivity extends AppCompatActivity {
    ParseUser currentUser;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    private void logout(){
        ParseUser.logOut();
        //User will be null
        currentUser = ParseUser.getCurrentUser();
    }
}