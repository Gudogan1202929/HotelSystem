package com.example.amnhotelsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class SearchResultsAdapter extends RecyclerView.Adapter<SearchResultsAdapter.ViewHolder> {
    private final List<Hotel> searchResults;
    private OnHotelClickListener onHotelClickListener;
    private DatabaseReference favoritesRef;

    public SearchResultsAdapter() {
        searchResults = new ArrayList<>();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        FirebaseAuth firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() != null) {
            String userId = firebaseAuth.getCurrentUser().getUid();
            favoritesRef = database.getReference("favorites/" + userId);
        } else {
            // When the user is not authenticated
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public final ImageView hotelImageView;
        public final TextView hotelNameTextView;
        public final TextView priceInfoTextView;
        public final ImageButton favoriteButton;
        public final TextView hotelRegionNameTextView;
        public final TextView reviewScoreTextView;
        public final TextView totalReviewsTextView;
        public final TextView priceDay;

        public ViewHolder(View view) {
            super(view);
            hotelImageView = view.findViewById(R.id.hotelImageView);
            hotelNameTextView = view.findViewById(R.id.hotelNameTextView);
            priceInfoTextView = view.findViewById(R.id.priceInfoTextView);
            favoriteButton = view.findViewById(R.id.favoriteButton);
            hotelRegionNameTextView = view.findViewById(R.id.regionNameTextView);
            reviewScoreTextView = view.findViewById(R.id.reviewScoreTextView);
            totalReviewsTextView = view.findViewById(R.id.totalReviewsTextView);
            priceDay = view.findViewById(R.id.priceInfo);
        }
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_searchresult_hotel, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        Hotel hotel = searchResults.get(position);
        if (hotel.getImageUrl() != null) {
            Picasso.get()
                    .load(hotel.getImageUrl())
                    .into(holder.hotelImageView);
        }
        holder.hotelNameTextView.setText(hotel.getName());
        holder.priceInfoTextView.setText(hotel.getPriceInfo());
        holder.hotelRegionNameTextView.setText(hotel.getRegion());
        holder.priceDay.setText("Price for " + hotel.getNumberOfDays() + " " +
                (hotel.getNumberOfDays() > 1 ? "days" : "day") + " taxes and fees included");

        if (hotel.getTotalReviewCount().equals("0")) {
            holder.reviewScoreTextView.setText("");
            holder.totalReviewsTextView.setText("(No reviews yet)");
        } else {
            holder.reviewScoreTextView.setText(hotel.getReviewScore() + " / 10");
            holder.totalReviewsTextView.setText("(total " + hotel.getTotalReviewCount() + ")");
        }

        holder.itemView.setOnClickListener(view -> {
            if (onHotelClickListener != null) {
                onHotelClickListener.onHotelClick(hotel);
            }
        });

        holder.favoriteButton.setOnClickListener(view -> {
            hotel.setFavorite(!hotel.isFavorite());
            if (hotel.isFavorite()) {
                favoritesRef.child(hotel.getId()).setValue(hotel);
            } else {
                favoritesRef.child(hotel.getId()).removeValue();
            }
            notifyDataSetChanged();
        });

        if (hotel.isFavorite()) {
            holder.favoriteButton.setImageResource(R.drawable.icon_button_favorite);
        } else {
            holder.favoriteButton.setImageResource(R.drawable.icon_favorite_border);
        }
    }

    @Override
    public int getItemCount() {
        return searchResults.size();
    }

    public void setSearchResults(List<Hotel> results) {
        searchResults.clear();
        searchResults.addAll(results);
        notifyDataSetChanged();
    }

    public interface OnHotelClickListener {
        void onHotelClick(Hotel hotel);
    }

    public void setOnHotelClickListener(OnHotelClickListener listener) {
        onHotelClickListener = listener;
    }
}
