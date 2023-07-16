package com.example.amnhotelsystem;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amnhotelsystem.databinding.ActivityLocationDialogBinding;

public class LocationDialogActivity extends AppCompatActivity {
    private ActivityLocationDialogBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLocationDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        binding.saveButton.setOnClickListener(v -> {
        String location = binding.locationInputEditText.getText().toString();
        Intent resultIntent = new Intent();
        resultIntent.putExtra("location", location);
        setResult(Activity.RESULT_OK, resultIntent);
        finish();
    });
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
