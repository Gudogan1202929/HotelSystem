package com.example.amnhotelsystem;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amnhotelsystem.databinding.ActivityManageAccountBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.Calendar;
import java.util.Locale;

public class ManageAccountActivity extends AppCompatActivity {
    private ActivityManageAccountBinding binding;
    private String userId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityManageAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setTitle("Edit Profile");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            binding.emailTextView.setText(email);
            userId = mAuth.getCurrentUser().getUid();

            // Retrieve the user's info from the Firebase Realtime Database
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference userRef = database.getReference("users").child(userId);
            userRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        UserInfo userInfo = snapshot.getValue(UserInfo.class);
                        if (userInfo != null) {
                            // Update the layout fields with the retrieved data
                            binding.nameEditLayout.getEditText().setText(userInfo.getName());
                            binding.surnameEditLayout.getEditText().setText(userInfo.getSurname());
                            binding.phoneEditText.setText(userInfo.getPhoneNumber());
                            binding.birthdateEditText.setText(userInfo.getBirthDate());
                            int genderPosition = getGenderPosition(userInfo.getGender());
                            binding.genderSpinner.setSelection(genderPosition);
                        }
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle database read error
                    Log.e("DatabaseError", error.getMessage());
                }
            });
        }

        binding.birthdateEditText.setOnClickListener(v -> showDatePickerDialog());

        binding.saveButton.setOnClickListener(v -> {
        // Retrieve the values from the input fields
        String name = binding.nameEditLayout.getEditText().getText().toString();
        String surname = binding.surnameEditLayout.getEditText().getText().toString();
        String phoneNumber = binding.phoneEditText.getText().toString();
        String birthDate = binding.birthdateEditText.getText().toString();
        String gender = binding.genderSpinner.getSelectedItem().toString();

        // Get a reference to the Firebase Realtime Database and the current user's node
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference userRef = database.getReference("users").child(userId);

        // Retrieve the profile picture value from the database
        userRef.child("profilePicture").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot snapshot) {
                String profilePicture = snapshot.getValue(String.class);

                // Create a new UserInfo object with the updated profile picture value
                UserInfo updatedUserInfo = new UserInfo(name, surname, phoneNumber, birthDate, gender, profilePicture != null ? profilePicture : "");

                // Save the user's information to the database
                userRef.setValue(updatedUserInfo)
                    .addOnSuccessListener(aVoid -> {
                Toast.makeText(ManageAccountActivity.this, "Information saved successfully", Toast.LENGTH_SHORT).show();
            })
                .addOnFailureListener(e -> {
                Toast.makeText(ManageAccountActivity.this, "Failed to save information", Toast.LENGTH_SHORT).show();
            });
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Handle database read error
                Log.e("DatabaseError", error.getMessage());
            }
        });

        finish();
    });
    }

    private void showDatePickerDialog() {
        Calendar currentDate = Calendar.getInstance();
        int year = currentDate.get(Calendar.YEAR);
        int month = currentDate.get(Calendar.MONTH);
        int day = currentDate.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(
            this,
            (view, selectedYear, selectedMonth, selectedDay) -> {
        String formattedDate = String.format(
                Locale.getDefault(),
        "%02d-%02d-%04d",
        selectedDay,
        selectedMonth + 1,
        selectedYear
        );
        binding.birthdateEditText.setText(formattedDate);
    },
        year,
        month,
        day
        );

        datePickerDialog.show();
    }

    private int getGenderPosition(String gender) {
        String[] genderOptions = getResources().getStringArray(R.array.gender_options);
        for (int i = 0; i < genderOptions.length; i++) {
        if (genderOptions[i].equals(gender)) {
            return i;
        }
    }
        return 0;
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }
}
