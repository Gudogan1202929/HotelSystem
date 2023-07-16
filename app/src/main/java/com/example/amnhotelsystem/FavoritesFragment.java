package com.example.amnhotelsystem;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amnhotelsystem.databinding.FragmentFavoritesBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.*;

import java.util.List;
import java.util.ArrayList;

import android.graphics.Rect;

public class FavoritesFragment extends Fragment implements FavoritesAdapter.OnFavoriteClickListener {

    private FragmentFavoritesBinding binding;

    private RecyclerView favoritesRecyclerView;
    private FavoritesAdapter favoritesAdapter;
    private List<Hotel> favoriteHotels = new ArrayList<>();

    private DatabaseReference favoritesRef;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentFavoritesBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        requireActivity().setTitle("Favorites");

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        favoritesRef = database.getReference("favorites/" + userId);

        favoritesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // Get the list of favorite hotels
                List<Hotel> updatedFavoriteHotels = new ArrayList<>();
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = snapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        updatedFavoriteHotels.add(hotel);
                    }
                }

                // Update the list of favorite hotels
                favoriteHotels.clear();
                favoriteHotels.addAll(updatedFavoriteHotels);

                // Notify the adapter about the updated data
                favoritesAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                // handle error
            }
        });

        favoritesRecyclerView = binding.favoritesRecyclerView;

        // Set up the adapter for the RecyclerView
        favoritesAdapter = new FavoritesAdapter(favoriteHotels, this);
        favoritesRecyclerView.setAdapter(favoritesAdapter);
        favoritesRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        favoritesRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));

        return view;
    }

    @Override
    public void onFavoriteClick(int position) {
        // Get the clicked hotel
        Hotel hotel = favoriteHotels.get(position);

        // Remove the hotel from favorites in Firebase
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference favoritesRef = database.getReference("favorites/" + userId);
        favoritesRef.child(hotel.getId()).removeValue();

        // Remove the hotel from the local list of favorite hotels
        favoriteHotels.remove(position);
        favoritesAdapter.notifyItemRemoved(position);
    }

    @Override
    public void onHotelClick(Hotel hotel) {
        // Open HotelDetailActivity to show the details of the hotel
        Intent intent = new Intent(requireContext(), HotelDetailActivity.class);
        intent.putExtra("hotelId", hotel.getId());
        intent.putExtra("hotelName", hotel.getName());
        intent.putExtra("hotelRegion", hotel.getRegion());
        intent.putExtra("hotelPrice", hotel.getPriceInfo());
        intent.putExtra("hotelnumday", hotel.getNumberOfDays());
        intent.putExtra("hotelrev", hotel.getReviewScore());
        intent.putExtra("hotelTotalReview", hotel.getTotalReviewCount());
        intent.putExtra("imageurl", hotel.getImageUrl());
        startActivity(intent);
    }
}

class SpaceItemDecoration extends RecyclerView.ItemDecoration {
    private int space;

    public SpaceItemDecoration(int space) {
        this.space = space;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        outRect.bottom = space;

        // Add top margin only for the first item to avoid double space between items
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.top = space;
        } else {
            outRect.top = 0;
        }
    }
}
