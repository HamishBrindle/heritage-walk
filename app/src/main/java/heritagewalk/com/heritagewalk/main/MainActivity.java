package heritagewalk.com.heritagewalk.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.CardView;
import android.view.View;
import android.widget.Button;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.game.AchievementsActivity;
import heritagewalk.com.heritagewalk.maps.MapsActivity;

public class MainActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        View.OnClickListener {

    private CardView cvSearch;
    private CardView cvProgress;
    private Button mExplore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        cvProgress = (CardView) findViewById(R.id.progress_button);
        cvSearch = (CardView) findViewById(R.id.search_button);
        mExplore = (Button) findViewById(R.id.explore_button);

        cvProgress.setOnClickListener(this);
        cvSearch.setOnClickListener(this);
        mExplore.setOnClickListener(this);

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

    @Override
    public void onClick(View v) {
        int id = v.getId();
        Intent intent;
        if (id == cvProgress.getId()) {
            intent = new Intent(MainActivity.this, AchievementsActivity.class);
            startActivity(intent);
        } else if (id == cvSearch.getId()) {
            intent = new Intent(MainActivity.this, SiteListActivity.class);
            startActivity(intent);
        } else if (id == mExplore.getId()) {
            intent = new Intent(MainActivity.this, MapsActivity.class);
            startActivity(intent);
        }
    }
}
