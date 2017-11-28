package heritagewalk.com.heritagewalk.utility;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;

import java.util.List;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.models.Place;

public class HorizontalCardViewAdapter extends RecyclerView.Adapter<HorizontalCardViewAdapter.MyViewHolder> {

    private static final String TAG = "HorizontalCardViewAdapter";
    private List<Place> horizontalList;
    private Context mContext;
    private GoogleMap mGoogleMap;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView vicinity;
        public TextView rating;
        public TextView phoneNumber;
        public CardView cv;

        public MyViewHolder(View view, Context context) {
            super(view);

            mContext = context;

            cv = (CardView)itemView.findViewById(R.id.card_view);

            name = (TextView) view.findViewById(R.id.name);
            vicinity = (TextView) view.findViewById(R.id.vicinity);
            rating = (TextView) view.findViewById(R.id.rating);

        }
    }

    public HorizontalCardViewAdapter(List<Place> horizontalList, GoogleMap mMap) {
        this.horizontalList = horizontalList;
        this.mGoogleMap = mMap;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.layout_cardview, parent, false);

        return new MyViewHolder(itemView, mContext);
    }

    @Override
    public void onBindViewHolder(final MyViewHolder holder, final int position) {
        holder.name.setText(horizontalList.get(position).getName());
        holder.vicinity.setText(horizontalList.get(position).getVicinity());
        holder.rating.setText(horizontalList.get(position).getRating());

        holder.name.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //OnClick move camera view
                SitePageActivity.moveToMarker(horizontalList.get(position).getLatLng(), mGoogleMap);
            }
        });
    }

    @Override
    public int getItemCount() {
        return horizontalList.size();
    }

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        super.onAttachedToRecyclerView(recyclerView);
    }
}