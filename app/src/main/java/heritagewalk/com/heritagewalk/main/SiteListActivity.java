package heritagewalk.com.heritagewalk.main;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.util.ArrayList;
import java.util.Arrays;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.MapsActivity;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.maps.tasks.JSONParseAsyncTask;
import heritagewalk.com.heritagewalk.maps.tasks.OnJSONParseCompleted;
import heritagewalk.com.heritagewalk.models.Site;
import heritagewalk.com.heritagewalk.utility.HorizontalCardViewAdapter;
import heritagewalk.com.heritagewalk.utility.VerticalCardViewAdapter;

public class SiteListActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        OnJSONParseCompleted {

    private ArrayList<Site> mSites;
    private RecyclerView mRecyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = (RecyclerView)findViewById(R.id.rv);

        // Obtain data from JSON (later calls AddMarkersAsyncTask)
        // This is basically what kicks everything off.
        new JSONParseAsyncTask(this).execute();
    }

    @Override
    public int getLayout() {
        return R.layout.activity_site_list;
    }

    /*
    * Serializing the site object is giving me issues, so I'm going to reload the site using Google places
    * in the SitePageActivity
    */
    private void startNewSiteActivity(Site newSite) {
        Intent intent = new Intent(SiteListActivity.this, SitePageActivity.class);
        intent.putExtra("selectedSiteName", newSite.getName());
        intent.putExtra("selectedSiteDesc", newSite.getDescription());
        intent.putExtra("selectedSiteSummary", newSite.getSummary());

        intent.putExtra("selectedSiteLatLng", newSite.getLatLng().toString());
        startActivity(intent);
    }

    @Override
    public void selectCurrentIcon(int id) {
        // super.selectCurrentIcon(id);
    }

    @Override
    public void onJSONParseCompleted(Site[] sites) {
        mSites = new ArrayList<>(Arrays.asList(sites));
        setupSiteList();
    }

    private void setupSiteList() {
        VerticalCardViewAdapter cardViewAdapter = new VerticalCardViewAdapter(mSites, this);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(cardViewAdapter);
    }
}
