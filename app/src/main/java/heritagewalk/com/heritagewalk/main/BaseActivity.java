package heritagewalk.com.heritagewalk.main;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.auth.LoginActivity;
import heritagewalk.com.heritagewalk.exception.ResourceNotFoundException;
import heritagewalk.com.heritagewalk.game.AchievementsActivity;
import heritagewalk.com.heritagewalk.maps.MapsActivity;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.utility.BottomNavigationViewHelper;

public class BaseActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        BottomNavigationView.OnNavigationItemSelectedListener {

    // TODO: Enable this to allow Firebase to get authentication of user.
    private final boolean FIREBASE_ENABLED = false;

    private FirebaseAuth mAuth;
    private ProgressDialog mProgressDialog;
    private BottomNavigationView mBottomNavigationView;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // We try to get the child activity's view so the BaseActivity
        // can interact with it's UI elements.
        try {
            setContentView(getLayout());
        } catch (Resources.NotFoundException re) {
            throw new ResourceNotFoundException();
        }

        // Check Firebase authentication; is user logged in?
        // If not logged in, redirect to LoginActivity
        if (FIREBASE_ENABLED) {
            mAuth = FirebaseAuth.getInstance();
            mAuth.addAuthStateListener(new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    updateUI(currentUser);
                }
            });
        }

        // Toolbar
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        // Drawer
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        // Drawer Navigation
        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        // Bottom Navigation
        mBottomNavigationView = (BottomNavigationView) findViewById(R.id.navigation);
        // This disables the shifting of icons in the bottom navigation.
        BottomNavigationViewHelper.disableShiftMode(mBottomNavigationView);

        // Bottom Navigation listener. TODO: Try implementing the listener and defining below
        mBottomNavigationView.setOnNavigationItemSelectedListener(this);
    }

    public void selectCurrentIcon(int id) {
        if (mBottomNavigationView != null) {
            mBottomNavigationView.setSelectedItemId(id);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (FIREBASE_ENABLED) {
            // Check if user is signed in (non-null) and update UI accordingly.
            FirebaseUser currentUser = mAuth.getCurrentUser();
            updateUI(currentUser);
        }
    }

    /*
    User is not logged in. Redirect user to the LoginActivity.
     */
    public void updateUI(FirebaseUser currentUser) {
        if (currentUser == null) {
            Intent intent = new Intent(this, LoginActivity.class);
            startActivity(intent);
        }
    }

    /**
     * Shows a progress dialog with a given message.
     */
    public void showProgressDialog(String message) {
        if (mProgressDialog == null) {
            mProgressDialog = new ProgressDialog(this);
            mProgressDialog.setCancelable(false);
            mProgressDialog.setMessage(message);
        }
        mProgressDialog.show();
    }

    /**
     * Hide a currently-showing progress dialog.
     */
    public void hideProgressDialog() {
        if (mProgressDialog != null && mProgressDialog.isShowing()) {
            mProgressDialog.dismiss();
        }
    }

    /**
     * Get the current Firebase user's UUID.
     * @return UUID
     */
    public String getUid() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    /**
     * Build a dialog box with header and content.
     * @param header    Top of dialog box.
     * @param content   Body of dialog box.
     */
    public void buildDialog(String header, String content) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(header);
        builder.setMessage(content);
        builder.setNegativeButton("OK", null);
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.navigation, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_logout) {
            if (FIREBASE_ENABLED) {
                mAuth.signOut();
            } else {
                Toast.makeText(this, "Firebase not enabled.", Toast.LENGTH_SHORT).show();
            }
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onNavigationItemSelected(MenuItem item) {

        Intent intent;

        // Handle navigation view item clicks here.
        int id = item.getItemId();

        // Navigation Drawer Menu Options
        if (id == R.id.nav_home) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_explore) {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_sites) {
            intent = new Intent(getApplicationContext(), SiteListActivity.class);
            Log.d("hello", "intent u there?");

            startActivity(intent);
        } else if (id == R.id.nav_achievements) {
            intent = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(intent);
        } else if (id == R.id.nav_share) {
            // TODO: social media sharing
        } else if (id == R.id.nav_send) {
            // TODO: sending email / contact?

        // Bottom Navigation Menu Options
        } else if (id == R.id.action_home) {
            intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_explore) {
            intent = new Intent(getApplicationContext(), MapsActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_sites) {
            intent = new Intent(getApplicationContext(), SiteListActivity.class);
            startActivity(intent);
        } else if (id == R.id.action_achievements) {
            intent = new Intent(getApplicationContext(), AchievementsActivity.class);
            startActivity(intent);
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    /**
     * Get the layout we are referring our child activity to.
     * Note: This method is to be overwritten in the child activity.
     *
     * @return layout ID
     */
    public int getLayout() {
        return 0;
    }


}