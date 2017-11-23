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
        setupDebugButtons();
    }

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

    @Override
    public int getLayout() {
        return R.layout.activity_main;
    }
}
