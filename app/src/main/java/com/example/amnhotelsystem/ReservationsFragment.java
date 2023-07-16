package com.example.amnhotelsystem;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amnhotelsystem.databinding.FragmentReservationsBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class ReservationsFragment extends Fragment implements ReservationsAdapter.OnReserveClickListener {
    private FragmentReservationsBinding binding;
    private RecyclerView reservationsRecyclerView;
    private ReservationsAdapter reservationsAdapter;
    private DatabaseReference reservationsRef;
    private TextView noReservationsTextView;

    private List<Hotel> activeHotels = new ArrayList<>();
    private List<Hotel> cancelledHotels = new ArrayList<>();

    private class SpaceItemDecoration extends RecyclerView.ItemDecoration {
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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentReservationsBinding.inflate(inflater, container, false);
        View view = binding.getRoot();
        requireActivity().setTitle("Reservations");
        noReservationsTextView = binding.noReservationsTextView;

        reservationsRecyclerView = binding.reservationsRecyclerView;
        reservationsAdapter = new ReservationsAdapter(activeHotels, this);
        reservationsRecyclerView.setAdapter(reservationsAdapter);
        reservationsRecyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        reservationsRecyclerView.addItemDecoration(new SpaceItemDecoration(spacing));

        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        reservationsRef = database.getReference("reservations/" + userId);

        reservationsRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                List<Hotel> updatedReserveHotels = new ArrayList<>();
                for (DataSnapshot childSnapshot : dataSnapshot.getChildren()) {
                    Hotel hotel = childSnapshot.getValue(Hotel.class);
                    if (hotel != null) {
                        updatedReserveHotels.add(hotel);
                    }
                }
                activeHotels.clear();
                activeHotels.addAll(updatedReserveHotels);
                reservationsAdapter.notifyDataSetChanged();
                if (activeHotels.isEmpty()) {
                    noReservationsTextView.setVisibility(View.VISIBLE);
                    reservationsRecyclerView.setVisibility(View.GONE);
                } else {
                    noReservationsTextView.setVisibility(View.GONE);
                    reservationsRecyclerView.setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });

        return view;
    }

    @Override
    public void onReserveClick(int position) {
        Hotel hotel = activeHotels.get(position);

        AlertDialog.Builder builder = new AlertDialog.Builder(requireContext());
        builder.setTitle("Cancel Reservation");
        builder.setMessage("Are you sure you'd like to cancel this reservation?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            // Remove the hotel from active reservations
            hotel.setReserved(false);
            activeHotels.remove(position);
            reservationsAdapter.updateData(activeHotels);

            // Remove the hotel from reservations in Firebase
            String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            reservationsRef = database.getReference("reservations/" + userId);
            reservationsRef.child(hotel.getId()).removeValue();

            // Add it to cancelled hotels in Firebase
            updateCancelledHotelsInDatabase(hotel);

            dialog.dismiss();
            Toast.makeText(requireContext(), "Reservation canceled successfully", Toast.LENGTH_SHORT).show();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    @Override
    public void onHotelClick(Hotel hotel) {
    }

    private void updateCancelledHotelsInDatabase(Hotel hotel) {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference cancelledHotelsRef = database.getReference("cancelledHotels/" + userId);
        // Set the hotel data under the generated key
        cancelledHotelsRef.setValue(hotel);
    }
}