package com.example.amnhotelsystem;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import com.example.amnhotelsystem.databinding.ActivityHotelDetailBinding;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HotelDetailActivity extends AppCompatActivity implements VolleyAccessRapidAPI.HotelImagesListener {
    private ActivityHotelDetailBinding binding;
    private List<ImageHotel> resultImages = new ArrayList<>();
    private List<ImageHotel> imageList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityHotelDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        String hotelId = getIntent().getStringExtra("hotelId");
        String hotelName = getIntent().getStringExtra("hotelName");
        String hotelRegion = getIntent().getStringExtra("hotelRegion");
        String hotelPrice = getIntent().getStringExtra("hotelPrice");
        int hotelNumDay = getIntent().getIntExtra("hotelnumday", 1);
        String hotelRev = getIntent().getStringExtra("hotelrev");
        String hotelTotalRev = getIntent().getStringExtra("hotelTotalReview");
        String imageurl = getIntent().getStringExtra("imageurl"); // todo delete

        ImageView imageView = findViewById(R.id.hotelImageView);
        if (imageurl != null) {
            Picasso.get()
                    .load(imageurl)
                    .into(imageView);
        }

        binding.hotelNameText.setText(hotelName);
        binding.priceInfoText.setText(hotelPrice);
        binding.regionNameTextView.setText(hotelRegion);

        binding.priceInfoDays.setText("Price for " + hotelNumDay + " " + (hotelNumDay > 1 ? "days" : "day") +
                " taxes and fees included");

        if (hotelTotalRev.equals("0")) {
            binding.reviewScoreTextView.setText("");
            binding.totalReviewsText.setText("(No reviews yet)");
        } else {
            binding.reviewScoreTextView.setText(hotelRev + " / 10");
            binding.totalReviewsText.setText("(total " + hotelTotalRev + ")");
        }


        binding.bookButton.setOnClickListener(v -> showConfirmationDialog());

        // Add a back button to the toolbar
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ViewPager viewPager = binding.imageViewPager;
        TabLayout tabLayout = binding.imageTabLayout;

        // Create an adapter for the ViewPager
        ImagePagerAdapter adapter = new ImagePagerAdapter(getSupportFragmentManager(), resultImages);



        // Set the adapter on the ViewPager
        viewPager.setAdapter(adapter);

        // Connect the TabLayout with the ViewPager
        tabLayout.setupWithViewPager(viewPager);

        ProgressBar imageProgressBar = binding.imageProgressBar;
        imageProgressBar.setVisibility(View.VISIBLE);
        VolleyAccessRapidAPI VolleyAccessRapidAPI = new VolleyAccessRapidAPI(this);
        if (hotelId != null) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    VolleyAccessRapidAPI.fetchHotelImages(hotelId, HotelDetailActivity.this);
                    resultImages.addAll(imageList);
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            adapter.updateImages(resultImages);
                            adapter.notifyDataSetChanged();
                            imageProgressBar.setVisibility(View.GONE);
                        }
                    });
                }
            }).start();
        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    private void showConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmation");
        builder.setMessage("Are you sure you want to book this offering?");
        builder.setPositiveButton("Confirm", (dialog, which) -> {
            bookHotel();
            Toast.makeText(this, "Successfully booked this offering", Toast.LENGTH_SHORT).show();
            dialog.dismiss();
        });
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());
        AlertDialog dialog = builder.create();
        dialog.show();
    }

    private void bookHotel() {
        String userId = FirebaseAuth.getInstance().getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        String hotelId = getIntent().getStringExtra("hotelId");
        String hotelName = getIntent().getStringExtra("hotelName");
        String hotelRegion = getIntent().getStringExtra("hotelRegion");
        String hotelPrice = getIntent().getStringExtra("hotelPrice");
        int hotelNumDay = getIntent().getIntExtra("hotelnumday", 1);
        String hotelRev = getIntent().getStringExtra("hotelrev");
        String hotelTotalRev = getIntent().getStringExtra("hotelTotalReview");
        String imageUrl = getIntent().getStringExtra("imageurl");

        // Create a new Hotel object
        Hotel hotel = new Hotel(hotelId, hotelName, hotelRegion, imageUrl, hotelPrice, false, true,
                hotelRev, hotelTotalRev, hotelNumDay);



        if (userId != null) {
            DatabaseReference reservationsRef = database.getReference("reservations/" + userId);
            reservationsRef.child(hotelId).setValue(hotel);
        }
    }

    @Override
    public void onHotelImagesSuccess(List<ImageHotel> imageList) {
        this.imageList = imageList;
    }

    @Override
    public void onHotelImagesError(String message) {

    }

    @Override
    public void onPointerCaptureChanged(boolean hasCapture) {
        super.onPointerCaptureChanged(hasCapture);
    }
}
