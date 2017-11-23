package heritagewalk.com.heritagewalk.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.MainActivity;

import static android.content.Intent.FLAG_ACTIVITY_CLEAR_TOP;

public class LoginRegisterActivity extends AppCompatActivity {
    private static final String TAG = "LoginRegisterActivity";
    Button mRegisterButton;
    Button mSignInButton;
    Intent registerIntent;
    Intent signInIntent;
    Intent homeIntent;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_register);

        //Initializing all items
        mRegisterButton = (Button) findViewById(R.id.register_button);
        mSignInButton = (Button) findViewById(R.id.sign_in_button);
        signInIntent = new Intent(this, LoginActivity.class);
        registerIntent = new Intent(this, RegisterActivity.class);
        homeIntent = new Intent(this, MainActivity.class);

        setupFirebase();

        //Setting the font of the description text;
        mRegisterButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(registerIntent);
            }
        });

        mSignInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(signInIntent);
            }
        });
    }

    public void setupFirebase() {

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                final Intent register_login_intent = new Intent(getApplicationContext(), LoginRegisterActivity.class);
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged: User is signed in");
                    startActivity(homeIntent);
                    finish();
                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged.Main:signed_out");

                    // Create the logout activity intent.
                    startActivity(register_login_intent);
                    register_login_intent.setFlags(FLAG_ACTIVITY_CLEAR_TOP);
                    finish();
                }
            }
        };

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        setupFirebase();

    }
}
