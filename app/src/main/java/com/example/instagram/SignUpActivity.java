package com.example.instagram;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseUser;
import com.parse.SignUpCallback;
import com.parse.ParseException;

public class SignUpActivity extends AppCompatActivity {

    public static final String TAG = "SignUpActivity";
    private EditText usernameSignUp;
    private EditText passwordSignUp1;
    private EditText passwordSignUp2;
    private Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        usernameSignUp = findViewById(R.id.usernameSignUp);
        passwordSignUp1 = findViewById(R.id.passwordSignUp1);
        passwordSignUp2 = findViewById(R.id.passwordSignUp2);
        btnSignUp = findViewById(R.id.btnSignUp);

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(passwordSignUp1.getText().toString().equals(passwordSignUp2.getText().toString())){
                    signUp(usernameSignUp.getText().toString(), passwordSignUp1.getText().toString());
                }
                Toast.makeText(SignUpActivity.this, "Passwords needs be equals", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void signUp(String username, String password){
        // Create the ParseUser
        ParseUser user = new ParseUser();
        // Set core properties
        user.setUsername(username);
        user.setPassword(password);
        //user.setEmail("email@example.com");
        // Set custom properties
        //user.put("phone", "650-253-0000");
        // Invoke signUpInBackground
        user.signUpInBackground(new SignUpCallback() {
            public void done(ParseException e) {
                if (e == null) {
                    Intent i = new Intent(SignUpActivity.this, MainActivity.class);
                    startActivity(i);
                    finish();
                    // Hooray! Let them use the app now.
                } else {
                    Log.e(TAG, "Error with signUp");
                    Toast.makeText(SignUpActivity.this, "Error with signUp", Toast.LENGTH_SHORT).show();
                    // Sign up didn't succeed. Look at the ParseException
                    // to figure out what went wrong
                }
            }
        });
    }
}