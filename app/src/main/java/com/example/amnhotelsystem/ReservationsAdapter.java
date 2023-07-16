package com.example.amnhotelsystem;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.squareup.picasso.Picasso;

import java.util.List;

public class ReservationsAdapter extends RecyclerView.Adapter<ReservationsAdapter.ReservationsViewHolder> {
    private List<Hotel> reserveHotels;
    private OnReserveClickListener listener;

    public ReservationsAdapter(List<Hotel> reserveHotels, OnReserveClickListener listener) {
        this.reserveHotels = reserveHotels;
        this.listener = listener;
    }

    @Override
    public ReservationsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_reservation, parent, false);
        return new ReservationsViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(ReservationsViewHolder holder, int position) {
        Hotel hotel = reserveHotels.get(position);
        holder.bind(hotel);
    }

    @Override
    public int getItemCount() {
        return reserveHotels.size();
    }

    public interface OnReserveClickListener {
        void onReserveClick(int position);
        void onHotelClick(Hotel hotel);
    }

    public class ReservationsViewHolder extends RecyclerView.ViewHolder {
        private ImageView hotelImageView;
        private TextView hotelNameTextView;
        private TextView priceInfoTextView;
        private Button cancelButton;
        private TextView hotelRegionNameTextView;
        private TextView reviewScoreTextView;
        private TextView totalReviewsTextView;
        private TextView priceDay;

        public ReservationsViewHolder(View itemView) {
            super(itemView);
            hotelImageView = itemView.findViewById(R.id.hotelImageView);
            hotelNameTextView = itemView.findViewById(R.id.hotelNameTextView);
            priceInfoTextView = itemView.findViewById(R.id.priceInfoTextView);
            cancelButton = itemView.findViewById(R.id.cancelReservationButton);
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
                    (hotel.getNumberOfDays() > 1 ? "days" : "day") + " taxes and fees included");

            if (hotel.getTotalReviewCount().equals("0")) {
                reviewScoreTextView.setText("");
                totalReviewsTextView.setText("(No reviews yet)");
            } else {
                reviewScoreTextView.setText(hotel.getReviewScore() + " / 10");
                totalReviewsTextView.setText("(total " + hotel.getTotalReviewCount() + ")");
            }

            cancelButton.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION) {
                    listener.onReserveClick(position);
                }
            });

            itemView.setOnClickListener(v -> listener.onHotelClick(hotel));
        }
    }

    public void updateData(List<Hotel> hotels) {
        reserveHotels.clear();
        reserveHotels.addAll(hotels);
        notifyDataSetChanged();
    }
}
