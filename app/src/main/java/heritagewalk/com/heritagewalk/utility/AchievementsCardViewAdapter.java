package heritagewalk.com.heritagewalk.utility;

import android.app.Activity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.models.Achievement;

public class AchievementsCardViewAdapter extends RecyclerView.Adapter<AchievementsCardViewAdapter.AcitivtyViewHolder> {

    public TextView name;
    public TextView description;

    public Activity mActivity;

    List<Achievement> mAchievements;

    public AchievementsCardViewAdapter(List<Achievement> achievements, Activity activity) {
        mAchievements = achievements;
        mActivity = activity;
    }

    @Override
    public AcitivtyViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_achievements_card_view_adapter, viewGroup, false);
        AcitivtyViewHolder pvh = new AcitivtyViewHolder(v);
        return pvh;
    }

    @Override
    public void onBindViewHolder(AcitivtyViewHolder holder, final int position) {
        holder.achievementName.setText(mAchievements.get(position).getName());
    }

    @Override
    public int getItemCount() {
        return mAchievements.size();
    }


    public static class AcitivtyViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView achievementName;

        AcitivtyViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv_achievements);
            achievementName = (TextView) itemView.findViewById(R.id.achievement_name);
        }
    }
}