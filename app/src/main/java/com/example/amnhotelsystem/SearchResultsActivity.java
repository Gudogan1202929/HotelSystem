package com.example.amnhotelsystem;

import android.content.Intent;
import android.graphics.Rect;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.amnhotelsystem.databinding.ActivitySearchResultsBinding;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class SearchResultsActivity extends AppCompatActivity implements SearchResultsAdapter.OnHotelClickListener, VolleyAccessRapidAPI.LocationSearchListener, VolleyAccessRapidAPI.HotelSearchListener {
    private String geolocationId;
    private List<Hotel> hotelList = new ArrayList<>();

    @Override
    public void onLocationSearchSuccess(String geolocationId) {
        this.geolocationId = geolocationId;
    }

    @Override
    public void onLocationSearchError(String message) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }

    @Override
    public void onHotelSearchSuccess(List<Hotel> hotelList) {
        this.hotelList = hotelList;
    }

    @Override
    public void onHotelSearchError(String message) {

    }

    public class SpaceItemDecoration extends RecyclerView.ItemDecoration {
        private final int space;

        public SpaceItemDecoration(int space) {
            this.space = space;
        }

        @Override
        public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
            outRect.bottom = space;

            // Adding top margin only for the first item
            if (parent.getChildAdapterPosition(view) == 0) {
                outRect.top = space;
            } else {
                outRect.top = 0;
            }
        }
    }

    private RecyclerView recyclerView;
    private SearchResultsAdapter adapter;
    private ActivitySearchResultsBinding binding;
    private final List<Hotel> resultHotels = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySearchResultsBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        String location = "USA";
        int numOfAdults = 1;

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());

        // Set the date range to the last 10 days of June
        calendar.set(Calendar.MONTH, Calendar.JUNE);
        calendar.set(Calendar.DAY_OF_MONTH, 21); // Set the check-in day
        int checkInDay = calendar.get(Calendar.DAY_OF_MONTH);
        int checkInMonth = calendar.get(Calendar.MONTH);
        int checkInYear = calendar.get(Calendar.YEAR);

        calendar.add(Calendar.DAY_OF_MONTH, 10); // Add 10 days for the check-out date
        int checkOutDay = calendar.get(Calendar.DAY_OF_MONTH);
        int checkOutMonth = calendar.get(Calendar.MONTH);
        int checkOutYear = calendar.get(Calendar.YEAR);

        SimpleDateFormat displayFormat = new SimpleDateFormat("d MMMM", Locale.getDefault());
        String checkInFormatted = displayFormat.format(calendar.getTime());
        calendar.add(Calendar.DAY_OF_MONTH, 1); // Increment the calendar by 1 day for the check-out formatted date
        String checkOutFormatted = displayFormat.format(calendar.getTime());
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        recyclerView = binding.searchResultsRecyclerView;
        adapter = new SearchResultsAdapter();
        adapter.setOnHotelClickListener(this);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        int spacing = getResources().getDimensionPixelSize(R.dimen.item_spacing);
        recyclerView.addItemDecoration(new SpaceItemDecoration(spacing));

        if (location != null) {
            VolleyAccessRapidAPI VolleyAccessRapidAPI = new VolleyAccessRapidAPI(this);
            VolleyAccessRapidAPI.performLocationSearch(
                    location,
                    new VolleyAccessRapidAPI.LocationSearchListener() {
                        @Override
                        public void onLocationSearchSuccess(String geolocationId) {
                            VolleyAccessRapidAPI.performHotelSearch(
                                    geolocationId,
                                    numOfAdults,
                                    checkInDay,
                                    checkInMonth,
                                    checkInYear,
                                    checkOutDay,
                                    checkOutMonth,
                                    checkOutYear,
                                    new VolleyAccessRapidAPI.HotelSearchListener() {
                                        @Override
                                        public void onHotelSearchSuccess(List<Hotel> hotelList) {
                                            resultHotels.addAll(hotelList);
                                            runOnUiThread(() -> {
                                                binding.progressBar.setVisibility(View.GONE);
                                                adapter.setSearchResults(resultHotels);
                                            });
                                        }

                                        @Override
                                        public void onHotelSearchError(String message) {
                                            // Handle hotel search error
                                        }
                                    });
                        }

                        @Override
                        public void onLocationSearchError(String message) {

                        }

                    });

        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public void onHotelClick(Hotel hotel) {
        Intent intent = new Intent(this, HotelDetailActivity.class);
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
