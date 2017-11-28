package heritagewalk.com.heritagewalk.game;

import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.util.ArrayList;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.BaseActivity;
import heritagewalk.com.heritagewalk.models.Achievement;
import heritagewalk.com.heritagewalk.utility.AchievementsCardViewAdapter;

public class AchievementsActivity extends BaseActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView mRecyclerView;
    private ArrayList <Achievement> mAchievements;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mRecyclerView = (RecyclerView)findViewById(R.id.achievements_rv);

        setupAcheivementsView();
    }

    private void setupAcheivementsView() {
        String[] achievementNames = getResources().getStringArray(R.array.achievement_array);
        mAchievements = new ArrayList(achievementNames.length);
        for( int i = 0; i < achievementNames.length; i++ ) {
            mAchievements.add(new Achievement(achievementNames[i]));
        }
        AchievementsCardViewAdapter cardViewAdapter = new AchievementsCardViewAdapter(mAchievements, this);
        LinearLayoutManager llm = new LinearLayoutManager(getApplicationContext());
        mRecyclerView.setLayoutManager(llm);
        mRecyclerView.setAdapter(cardViewAdapter);
    }

    @Override
    public int getLayout() {
        return R.layout.activity_achievements;
    }
}
