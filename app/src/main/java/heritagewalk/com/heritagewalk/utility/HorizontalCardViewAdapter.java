package heritagewalk.com.heritagewalk.utility;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.places.GeoDataClient;
import com.google.android.gms.location.places.PlacePhotoMetadata;
import com.google.android.gms.location.places.PlacePhotoMetadataBuffer;
import com.google.android.gms.location.places.PlacePhotoMetadataResponse;
import com.google.android.gms.location.places.PlacePhotoResponse;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.RuntimeExecutionException;
import com.google.android.gms.tasks.Task;

import java.util.List;

import heritagewalk.com.heritagewalk.R;
import heritagewalk.com.heritagewalk.maps.SitePageActivity;
import heritagewalk.com.heritagewalk.models.Place;

public class HorizontalCardViewAdapter extends RecyclerView.Adapter<HorizontalCardViewAdapter.MyViewHolder> {

    private static final String TAG = "HorizontalCardViewAdapter";
    private List<Place> horizontalList;
    private Context mContext;
    private GoogleMap mGoogleMap;
    private GeoDataClient mGeoDataClient;
    private GoogleApiClient googleApiClient;

    public class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView name;
        public TextView vicinity;
        public TextView rating;
        public TextView phoneNumber;
        public CardView cv;
        public ImageView image;

        public MyViewHolder(View view, Context context) {
            super(view);

            mContext = context;

            cv = (CardView) itemView.findViewById(R.id.card_view);

            name = (TextView) view.findViewById(R.id.name);
            vicinity = (TextView) view.findViewById(R.id.vicinity);
            rating = (TextView) view.findViewById(R.id.rating);

            image = (ImageView) view.findViewById(R.id.image);

        }
    }

    public HorizontalCardViewAdapter(List<Place> horizontalList, GoogleMap mMap) {
        this.horizontalList = horizontalList;
        this.mGoogleMap = mMap;
    }

    public HorizontalCardViewAdapter(List<Place> horizontalList, GeoDataClient geo, GoogleMap mMap) {
        this.horizontalList = horizontalList;
        mGeoDataClient = geo;
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

        final String placeId = horizontalList.get(position).getPlaceId();
        final Task<PlacePhotoMetadataResponse> photoMetadataResponse = mGeoDataClient.getPlacePhotos(placeId);
        photoMetadataResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoMetadataResponse>() {
            @Override
            public void onComplete(@NonNull Task<PlacePhotoMetadataResponse> task) {

                try {
                    // Get the list of photos.
                    PlacePhotoMetadataResponse photos = task.getResult();
                    // Get the PlacePhotoMetadataBuffer (metadata for all of the photos).
                    final PlacePhotoMetadataBuffer photoMetadataBuffer = photos.getPhotoMetadata();
                    // Get the first photo in the list.
                    PlacePhotoMetadata photoMetadata = photoMetadataBuffer.get(0);
                    // Get the attribution text.
                    CharSequence attribution = photoMetadata.getAttributions();
                    // Get a full-size bitmap for the photo.
                    Task<PlacePhotoResponse> photoResponse = mGeoDataClient.getPhoto(photoMetadata);
                    photoResponse.addOnCompleteListener(new OnCompleteListener<PlacePhotoResponse>() {
                        @Override
                        public void onComplete(@NonNull Task<PlacePhotoResponse> task) {
                            PlacePhotoResponse photo = task.getResult();
                            Bitmap bitmap = photo.getBitmap();
                            holder.image.setImageBitmap(bitmap);
                            photoMetadataBuffer.release();
                        }
                    });
                } catch (RuntimeExecutionException e) {
                    Log.e(TAG, "onComplete: Network error. Unable to pull photos.");
                }

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