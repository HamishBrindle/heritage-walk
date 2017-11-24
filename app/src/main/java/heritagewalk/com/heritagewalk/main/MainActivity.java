package heritagewalk.com.heritagewalk.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.view.View;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.game.AchievementsActivity;
import heritagewalk.com.heritagewalk.maps.MapsActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Select current activity on the BottomNavigationView
        selectCurrentIcon(R.id.action_home);

        // Setup some debug buttons
        setupDebugButtons();
    }

    /**
     * Setup all the debug UI elements for this activity.
     */
    private void setupDebugButtons() {
        findViewById(R.id.maps_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getBaseContext(), MapsActivity.class);
                startActivity(intent);
            }
        });
        findViewById(R.id.achievement_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
        findViewById(R.id.achievement_page_button).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getBaseContext(), AchievementsActivity.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Called by parent, BaseActivity. Used in order for BaseActivity to determine which
     * layout to use.
     * @return  this layout's ID
     */
    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }

    /**
     * Passes the ID of the BottomNavigationView item we want to be selected when on this
     * activity.
     * @param id    menu button id
     */
    @Override
    public void selectCurrentIcon(int id) {
        super.selectCurrentIcon(id);
    }
}
