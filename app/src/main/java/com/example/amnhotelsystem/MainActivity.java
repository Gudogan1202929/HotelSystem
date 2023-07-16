package com.example.amnhotelsystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import com.example.amnhotelsystem.databinding.ActivityMainBinding;
import com.google.firebase.auth.FirebaseAuth;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Fragment profileStartFragment = new ProfileFragment();
        replaceFragment(profileStartFragment);

        mAuth = FirebaseAuth.getInstance();

        binding.bottomNavigationView.setOnItemSelectedListener(item -> {
        switch (item.getItemId()) {
            case R.id.profile:
            Fragment profileFragment = new ProfileFragment();
            replaceFragment(profileFragment);
            break;
            case R.id.favorites:
            Fragment favoritesFragment = new FavoritesFragment();
            replaceFragment(favoritesFragment);
            break;
            case R.id.search:
            Fragment searchFragment = new SearchFragment();
            replaceFragment(searchFragment);
            break;
            case R.id.reservations:
            Fragment reservationsFragment = new ReservationsFragment();
            replaceFragment(reservationsFragment);
            break;
        }
        return true;
    });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser() == null) {
            startActivity(new Intent(this, StartActivity.class));
            finish();
        }
    }

    private void replaceFragment(Fragment fragment) {
        getSupportFragmentManager().beginTransaction()
            .replace(R.id.frameLayout, fragment)
            .commit();
    }
}
