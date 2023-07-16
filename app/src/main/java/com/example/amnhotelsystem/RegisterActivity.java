package com.example.amnhotelsystem;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amnhotelsystem.databinding.ActivityRegisterBinding;
import com.google.firebase.auth.FirebaseAuth;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        Intent intent = getIntent();
        String email = intent.getStringExtra("email");

        mAuth = FirebaseAuth.getInstance();

        binding.createAndLoginButton.setOnClickListener(v -> {
        if (email != null) {
            createUser(email);
        }
    });
    }

    private void createUser(String email) {
        String password = binding.passwordEditText.getText().toString();
        String confirmPassword = binding.confirmPasswordEditText.getText().toString();

        if (TextUtils.isEmpty(password)) {
            binding.passwordEditText.setError("Password cannot be empty");
            binding.passwordEditText.requestFocus();
            return;
        } else if (TextUtils.isEmpty(confirmPassword)) {
            binding.confirmPasswordEditText.setError("Confirm password cannot be empty");
            binding.confirmPasswordEditText.requestFocus();
            return;
        } else if (password.length() < 8) {
            binding.passwordEditText.setError("Password must be at least 8 characters long");
            binding.passwordEditText.requestFocus();
            return;
        } else if (!password.matches(".*\\d.*")) {
            binding.passwordEditText.setError("Password must contain at least one number");
            binding.passwordEditText.requestFocus();
            return;
        } else if (!password.equals(confirmPassword)) {
            binding.confirmPasswordEditText.setError("Passwords do not match");
            binding.confirmPasswordEditText.requestFocus();
            return;
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                if (task.isSuccessful()) {
                    // Registration success, user is created
                    Intent intent = new Intent(this, MainActivity.class);
                    startActivity(intent);
                    finish();
                } else {
                    Toast.makeText(this, "Registration failed: " + task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
