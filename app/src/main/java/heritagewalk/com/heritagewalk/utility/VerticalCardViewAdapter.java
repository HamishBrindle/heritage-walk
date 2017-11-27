package heritagewalk.com.heritagewalk.utility;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.main.SiteListActivity;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.models.Site;

public class VerticalCardViewAdapter extends RecyclerView.Adapter<VerticalCardViewAdapter.SiteViewHolder> {

    public TextView name;
    public TextView vicinity;
    public TextView rating;
    public TextView phoneNumber;

    public Activity mActivity;

    List<Site> mSites;

    public VerticalCardViewAdapter(List<Site> sites, Activity activity) {
        mSites = sites;
        mActivity = activity;
    }

    @Override
    public void onBindViewHolder(SiteViewHolder holder, final int position) {
        holder.siteName.setText(mSites.get(position).getName());
        holder.siteAddress.setText(mSites.get(position).getAddress());
        holder.cv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(mActivity, SitePageActivity.class);
                intent.putExtra("selectedSiteName", mSites.get(position).getName());
                intent.putExtra("selectedSiteDesc", mSites.get(position).getDescription());
                intent.putExtra("selectedSiteSummary", mSites.get(position).getSummary());
                intent.putExtra("selectedSiteLatLng", mSites.get(position).getLatLng().toString());
                mActivity.startActivity(intent);
                mActivity.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return mSites.size();
    }

    @Override
    public SiteViewHolder onCreateViewHolder(ViewGroup viewGroup, int i) {
        View v = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.layout_vertical_cardview, viewGroup, false);
        SiteViewHolder pvh = new SiteViewHolder(v);
        return pvh;
    }

    public static class SiteViewHolder extends RecyclerView.ViewHolder {
        CardView cv;
        TextView siteName;
        TextView siteAddress;

        SiteViewHolder(View itemView) {
            super(itemView);
            cv = (CardView) itemView.findViewById(R.id.cv);
            siteName = (TextView) itemView.findViewById(R.id.site_name);
            siteAddress = (TextView) itemView.findViewById(R.id.site_address);

        }
    }

}