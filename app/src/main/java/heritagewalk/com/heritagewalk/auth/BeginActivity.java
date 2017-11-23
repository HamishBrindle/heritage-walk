package heritagewalk.com.heritagewalk.auth;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.MainActivity;
import heritagewalk.com.heritagewalk.maps.MapsActivity;

@SuppressWarnings("Convert2Lambda")
public class BeginActivity extends AppCompatActivity {
    private static final String TAG = "BeginActivity";
    private FirebaseDatabase database;
    private DatabaseReference myRef;
    private TextView name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_begin);

        setupFirebase();

        Button mBegin = (Button) findViewById(R.id.button_begin);

        final Intent mapsActivity = new Intent(this, MainActivity.class);
        name = (TextView) findViewById(R.id.user_name);
        TextView greeting = (TextView) findViewById(R.id.greeting);
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        Calendar calender = Calendar.getInstance();

        if (6 < calender.get(Calendar.HOUR_OF_DAY) && calender.get(Calendar.HOUR_OF_DAY) < 12) {
            greeting.setText(getString(R.string.good_morning_text));
        } else if (12 <= calender.get(Calendar.HOUR_OF_DAY) && calender.get(Calendar.HOUR_OF_DAY) < 17) {
            greeting.setText(getString(R.string.good_afternoon_text));
        } else {
            greeting.setText(getString(R.string.good_evening_text));
        }

        myRef.addValueEventListener(new ValueEventListener() {
            @SuppressWarnings("ConstantConditions")
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                String[] fullname = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Name").getValue().toString().split(" ");
//                String setName = fullname[0].substring(0, 1).toUpperCase() + fullname[0].substring(1);
//                name.setText(setName);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        mBegin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(mapsActivity);
                finish();
            }
        });
    }

    private void setupFirebase() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        FirebaseAuth.AuthStateListener mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    // User is signed in
                    Log.d(TAG, "onAuthStateChanged.Main:signed_in:" + user.getUid());
                    Intent homeIntent = new Intent(getBaseContext(), MainActivity.class);
                    startActivity(homeIntent);
                    finish();


                } else {
                    // User is signed out
                    Log.d(TAG, "onAuthStateChanged.Main:signed_out");

                }
            }
        };
    }
}



