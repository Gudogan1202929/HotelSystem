package com.example.amnhotelsystem;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import com.example.amnhotelsystem.databinding.ActivityStartBinding;

public class StartActivity extends AppCompatActivity {
    private ActivityStartBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityStartBinding.inflate(getLayoutInflater());
        setContentView(R.layout.activity_start);
        setContentView(binding.getRoot());

        binding.continueEmail.setOnClickListener(view -> {
            Intent intent = new Intent(this, LoginEmailActivity.class);
            startActivity(intent);
        });
    }
}
