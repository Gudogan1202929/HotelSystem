package com.example.amnhotelsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class FavoritesAdapter extends RecyclerView.Adapter<FavoritesAdapter.FavoriteViewHolder> {
    private List<Hotel> favoriteHotels;
    private OnFavoriteClickListener listener;

    public FavoritesAdapter(List<Hotel> favoriteHotels, OnFavoriteClickListener listener) {
        this.favoriteHotels = favoriteHotels;
        this.listener = listener;
    }

    @Override
    public FavoriteViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_searchresult_hotel, parent, false);
        return new FavoriteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(FavoriteViewHolder holder, int position) {
        Hotel hotel = favoriteHotels.get(position);
        holder.bind(hotel);
    }

    @Override
    public int getItemCount() {
        return favoriteHotels.size();
    }

    public interface OnFavoriteClickListener {
        void onFavoriteClick(int position);

        void onHotelClick(Hotel hotel);
    }

    public class FavoriteViewHolder extends RecyclerView.ViewHolder {
        private ImageView hotelImageView;
        private TextView hotelNameTextView;
        private TextView priceInfoTextView;
        private ImageButton favoriteButton;
        private TextView hotelRegionNameTextView;
        private TextView reviewScoreTextView;
        private TextView totalReviewsTextView;
        private TextView priceDay;

        public FavoriteViewHolder(View itemView) {
            super(itemView);
            hotelImageView = itemView.findViewById(R.id.hotelImageView);
            hotelNameTextView = itemView.findViewById(R.id.hotelNameTextView);
            priceInfoTextView = itemView.findViewById(R.id.priceInfoTextView);
            favoriteButton = itemView.findViewById(R.id.favoriteButton);
            hotelRegionNameTextView = itemView.findViewById(R.id.regionNameTextView);
            reviewScoreTextView = itemView.findViewById(R.id.reviewScoreTextView);
            totalReviewsTextView = itemView.findViewById(R.id.totalReviewsTextView);
            priceDay = itemView.findViewById(R.id.priceInfo);
        }

        public void bind(Hotel hotel) {
            hotelNameTextView.setText(hotel.getName());
            priceInfoTextView.setText(hotel.getPriceInfo());
            hotelRegionNameTextView.setText(hotel.getRegion());
            if (hotel.getImageUrl() != null) {
                Picasso.get()
                        .load(hotel.getImageUrl())
                        .into(hotelImageView);
            }

            priceDay.setText("Price for " + hotel.getNumberOfDays() + " " +
                    ((hotel.getNumberOfDays() > 1) ? "days" : "day") +
                    " taxes and fees included");

            if (hotel.getTotalReviewCount().equals("0")) {
                reviewScoreTextView.setText("");
                totalReviewsTextView.setText("(No reviews yet)");
            } else {
                reviewScoreTextView.setText(hotel.getReviewScore() + " / 10");
                totalReviewsTextView.setText("(total " + hotel.getTotalReviewCount() + ")");
            }

            favoriteButton.setImageResource(R.drawable.icon_button_favorite);

            favoriteButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onFavoriteClick(position);
                }
            });

            itemView.setOnClickListener(v -> listener.onHotelClick(hotel));
        }
    }
}
