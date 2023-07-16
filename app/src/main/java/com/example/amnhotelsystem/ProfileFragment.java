package com.example.amnhotelsystem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.fragment.app.Fragment;
import com.example.amnhotelsystem.databinding.FragmentProfileBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class ProfileFragment extends Fragment {
    private FragmentProfileBinding binding;
    private boolean isLoading = true;
    private ProgressBar loadingProgressBar;

    private static final int REQUEST_IMAGE_SELECTION = 1001;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        binding = FragmentProfileBinding.inflate(inflater, container, false);
        View view = binding.getRoot();

        requireActivity().setTitle("Profile");
        isLoading = true;
        binding.profilePictureImageView.setVisibility(View.GONE);
        binding.nameTextView.setVisibility(View.GONE);
        loadingProgressBar = binding.loadingProgressBar;

        // Get the current user
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            // User is signed in
            String email = mAuth.getCurrentUser().getEmail();

            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String userId = mAuth.getCurrentUser().getUid();
            DatabaseReference profilePictureRef = database.getReference("users").child(userId).child("profilePicture");
            profilePictureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String base64Image = snapshot.getValue(String.class);
                    if (base64Image != null) {
                        Bitmap decodedImage = decodeBase64ToBitmap(base64Image);
                        binding.profilePictureImageView.setImageBitmap(decodedImage);
                    }
                    isLoading = false;
                    showProfileViews();
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle database read error
                    Log.e("DatabaseError", error.getMessage());
                    // Set the default picture as a fallback
                    binding.profilePictureImageView.setImageResource(R.drawable.defaultprofileimage);
                    isLoading = false;
                    showProfileViews();
                }
            });

            DatabaseReference nameRef = database.getReference("users").child(userId).child("name");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        binding.nameTextView.setText(name);
                    } else {
                        // Use the email address as a fallback if name is not available
                        binding.nameTextView.setText(email);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle database read error
                    Log.e("DatabaseError", error.getMessage());
                    // Use the email address as a fallback
                    binding.nameTextView.setText(email);
                }
            });

        }

        binding.profilePictureImageView.setOnClickListener(v -> {
        // Launch image selection intent
        Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, REQUEST_IMAGE_SELECTION);
    });

        binding.logoutButton.setOnClickListener(v -> showLogoutConfirmationDialog());

        binding.manageAccountButton.setOnClickListener(v -> {
        Intent intent = new Intent(requireContext(), ManageAccountActivity.class);
        startActivity(intent);
    });

        binding.settingsButton.setOnClickListener(v ->
        Toast.makeText(requireContext(), "To be implemented soon.", Toast.LENGTH_SHORT).show());

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();

        // Reload the user's data from the Firebase Realtime Database
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        if (mAuth.getCurrentUser() != null) {
            String email = mAuth.getCurrentUser().getEmail();
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            String userId = mAuth.getCurrentUser().getUid();

            DatabaseReference profilePictureRef = database.getReference("users").child(userId).child("profilePicture");
            profilePictureRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String base64Image = snapshot.getValue(String.class);
                    if (base64Image != null) {
                        Bitmap decodedImage = decodeBase64ToBitmap(base64Image);
                        binding.profilePictureImageView.setImageBitmap(decodedImage);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle database read error
                    Log.e("DatabaseError", error.getMessage());
                    // Set the default picture as a fallback
                    binding.profilePictureImageView.setImageResource(R.drawable.defaultprofileimage);
                }
            });

            DatabaseReference nameRef = database.getReference("users").child(userId).child("name");
            nameRef.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot snapshot) {
                    String name = snapshot.getValue(String.class);
                    if (name != null && !name.isEmpty()) {
                        binding.nameTextView.setText(name);
                    } else {
                        // Use the email address as a fallback if name is not available
                        binding.nameTextView.setText(email);
                    }
                }

                @Override
                public void onCancelled(DatabaseError error) {
                    // Handle database read error
                    Log.e("DatabaseError", error.getMessage());
                    // Use the email address as a fallback
                    binding.nameTextView.setText(email);
                }
            });
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_SELECTION && resultCode == Activity.RESULT_OK) {
            if (data != null) {
                Uri selectedImageUri = data.getData();
                // Process the selected image
                if (selectedImageUri != null) {
                    uploadProfilePicture(selectedImageUri);
                    try {
                        Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), selectedImageUri);
                        binding.profilePictureImageView.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    private void uploadProfilePicture(Uri imageUri) {
        try {
            Bitmap bitmap = MediaStore.Images.Media.getBitmap(requireActivity().getContentResolver(), imageUri);
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
            byte[] data = baos.toByteArray();

            String base64Image = Base64.encodeToString(data, Base64.DEFAULT);

            FirebaseAuth mAuth = FirebaseAuth.getInstance();
            if (mAuth.getCurrentUser() != null) {
                String userId = mAuth.getCurrentUser().getUid();

                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference profilePictureRef = database.getReference("users").child(userId).child("profilePicture");
                profilePictureRef.setValue(base64Image)
                    .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Profile pic uploaded successfully
                    } else {
                        // Upload failure
                    }
                });
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Bitmap decodeBase64ToBitmap(String base64Image) {
        byte[] decodedBytes = Base64.decode(base64Image, Base64.DEFAULT);
        return BitmapFactory.decodeByteArray(decodedBytes, 0, decodedBytes.length);
    }

    private void logout() {
        FirebaseAuth.getInstance().signOut();

        Intent intent = new Intent(requireContext(), StartActivity.class);
        startActivity(intent);

        requireActivity().finish();
    }

    private void showLogoutConfirmationDialog() {
        AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(requireContext());
        dialogBuilder.setMessage("Are you sure you'd like to logout?")
            .setPositiveButton("Confirm", (dialog, id) -> logout())
        .setNegativeButton("Cancel", (dialog, id) -> dialog.dismiss());

        AlertDialog alertDialog = dialogBuilder.create();
        alertDialog.show();
    }

    private void showProfileViews() {
        if (isLoading) {
            loadingProgressBar.setVisibility(View.VISIBLE);
            binding.profilePictureImageView.setVisibility(View.GONE);
            binding.nameTextView.setVisibility(View.GONE);
        } else {
            loadingProgressBar.setVisibility(View.GONE);
            binding.profilePictureImageView.setVisibility(View.VISIBLE);
            binding.nameTextView.setVisibility(View.VISIBLE);
        }
    }
}
