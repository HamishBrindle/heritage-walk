package heritagewalk.com.heritagewalk.auth;

import android.app.LoaderManager.LoaderCallbacks;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.MainActivity;

/**
 * A register screen that offers login via email/password.
 */
@SuppressWarnings({"Convert2Lambda", "ConstantConditions"})
public class ProfileActivity extends AppCompatActivity implements LoaderCallbacks<Cursor>, AdapterView.OnItemSelectedListener {

    private static final String TAG = "ProfileActivity";
    // UI references.
    private TextView mAgeView;
    private TextView mPostCodeView;
    private TextView mWeightView;
    private Boolean ulcersDBCheck;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private Intent registerIntent;
    private DatabaseReference myRef;
    private CheckBox mUlcersCheck;
    private boolean ulcers;
    private Boolean alreadySetUp;
    private Spinner spinner;

    public ProfileActivity() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        //Firebase stuff
        mAuth = FirebaseAuth.getInstance();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference();

        //Views
        Button continueButton = (Button) findViewById(R.id.form_button_next);
        mAgeView = (TextView) findViewById(R.id.form_age);
        mWeightView = (TextView) findViewById(R.id.form_weight);
        mPostCodeView = (TextView) findViewById(R.id.form_postal_code);

        //Setting up Gender Spinner
        setupGenderSpinner();

        //Intents
        registerIntent = new Intent(this, LoginRegisterActivity.class);
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if (user != null) {
                    Log.d(TAG, "onAuthStateChanged: user is in profile and sifned in");
                    //User is signed in
                    myRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @SuppressWarnings("ConstantConditions")
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            if (dataSnapshot.child("Users").hasChild(FirebaseAuth.getInstance().getCurrentUser().getUid())) {
                                alreadySetUp = (Boolean) dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("SetUp").getValue();
                                if (alreadySetUp) {
                                    ulcersDBCheck = (Boolean) dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Ulcers").getValue();
                                    if (ulcersDBCheck) {
                                        mUlcersCheck.setChecked(true);
                                    } else {
                                        mUlcersCheck.setChecked(false);
                                    }
                                    mAgeView.setText(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Age").getValue().toString());
                                    mPostCodeView.setText(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("PostCode").getValue().toString());
                                    mWeightView.setText(dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Weight").getValue().toString());
                                    checkForGender();

                                }
                            } else {
                                myRef.child("Users").child(mAuth.getCurrentUser().getUid()).child("Email").setValue(mAuth.getCurrentUser().getEmail());

                            }
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                } else {
                    //User is NOT signed in;
                    startActivity(registerIntent);

                    finish();
                }
            }
        };

        continueButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d(TAG, "onClick: Verify Ran");
                verify();
            }
        });
    }

    private void verify() {

        Log.d(TAG, "verify: in verify");
        // Reset errors.
        mAgeView.setError(null);
        mPostCodeView.setError(null);
        mWeightView.setError(null);


        // Store values at the time of the login attempt.
        String age = mAgeView.getText().toString();
        String postCode = mPostCodeView.getText().toString();
        String weight = mWeightView.getText().toString();

        String regex = "^(?!.*[DFIOQUdfioqu])[A-VXYa-vxy][0-9][A-Za-z] ?[0-9][A-Za-z][0-9]$";
        @SuppressWarnings("unused") Pattern pattern = Pattern.compile(regex);

        boolean cancel = false;
        View focusView = null;


        // Check for a valid email address.S
        if (TextUtils.isEmpty(age)) {
            mAgeView.setError(getString(R.string.error_field_required));
            focusView = mAgeView;
            cancel = true;
        }
        if (TextUtils.isEmpty(postCode)) {
            mPostCodeView.setError(getString(R.string.error_field_required));
            focusView = mPostCodeView;
            cancel = true;
        }
        if (TextUtils.isEmpty(weight)) {
            mWeightView.setError(getString(R.string.error_field_required));
            focusView = mWeightView;
            cancel = true;
        }
        if (!(postCode.matches(regex))) {
            mPostCodeView.setError("Invalid Post Code");
            focusView = mPostCodeView;
            cancel = true;
        }
        if (cancel) {
            // There was an error; don't attempt login and focus the first
            // form field with an error.
            focusView.requestFocus();
        } else {
            // perform the user update attempt.
            Log.d(TAG, "verify: UPDATE RAN");
            update();
        }
    }


    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return null;
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
    }

    /**
     *
     */
    private void update() {
        Log.d(TAG, "update: RUNNING UPDATE");
        FirebaseUser user = mAuth.getCurrentUser();
        Intent beginIntent;
        if (alreadySetUp) {
            beginIntent = new Intent(this, MainActivity.class);
        } else {
            beginIntent = new Intent(this, BeginActivity.class);

        }
        myRef.child("Users").child(user.getUid()).child("SetUp").setValue(true);
        myRef.child("Users").child(user.getUid()).child("Weight").setValue(mWeightView.getText().toString());
        myRef.child("Users").child(user.getUid()).child("Age").setValue(mAgeView.getText().toString());
        myRef.child("Users").child(user.getUid()).child("PostCode").setValue(mPostCodeView.getText().toString());
        myRef.child("Users").child(user.getUid()).child("Gender").setValue(spinner.getSelectedItem().toString());
        if (ulcers) {
            myRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Ulcers").setValue(true);
        } else {
            myRef.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Ulcers").setValue(false);
        }
        Log.d(TAG, "update: ABOUT TO REDIRECT");

        startActivity(beginIntent);
        finish();
    }

    private void setupGenderSpinner() {

        // Spinner element
        spinner = (Spinner) findViewById(R.id.form_gender_spinner);

        // Spinner click listener
        spinner.setOnItemSelectedListener(this);

        // Spinner Drop down elements
        List<String> categories = new ArrayList<>();
        categories.add("Male");
        categories.add("Female");
        categories.add("Other");
        categories.add("Undisclosed");

        // Creating adapter for spinner
        ArrayAdapter<String> dataAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, categories);

        // Drop down layout style - list view with radio button
        dataAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

        // attaching data adapter to spinner
        spinner.setAdapter(dataAdapter);

    }

    private void checkForGender() {

        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d(TAG, "onDataChange: SPINNER");
                String gender = dataSnapshot.child("Users").child(FirebaseAuth.getInstance().getCurrentUser().getUid()).child("Gender").getValue().toString();
                Log.d(TAG, "onDataChange: " + gender);
                if (gender.equalsIgnoreCase("male")) {
                    Log.d(TAG, "onDataChange: male");
                    spinner.setSelection(0, false);
                } else if (gender.equalsIgnoreCase("female")) {
                    Log.d(TAG, "onDataChange: female");
                    spinner.setSelection(1, false);
                } else if (gender.equalsIgnoreCase("other")) {
                    Log.d(TAG, "onDataChange: other");
                    spinner.setSelection(2, false);
                } else if (gender.equalsIgnoreCase("undisclosed")) {
                    Log.d(TAG, "onDataChange: undsclosed");
                    spinner.setSelection(3, false);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        String gender = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}