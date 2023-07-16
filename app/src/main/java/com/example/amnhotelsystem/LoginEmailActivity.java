package com.example.amnhotelsystem;

import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amnhotelsystem.databinding.ActivityLoginEmailBinding;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.FirebaseAuth;
import java.util.List;

public class LoginEmailActivity extends AppCompatActivity {
    private ActivityLoginEmailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginEmailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.continueEmail.setOnClickListener(v -> {
        String email = binding.emailEditText.getText().toString();

        // Validate email format
        if (!isEmailValid(email)) {
            binding.emailEditText.setError("Invalid email format");
            binding.emailEditText.requestFocus();
            return;
        }

        // Check whether user is registered
        isUserRegistered(email, isRegistered -> {
        if (isRegistered) {
            // If user is registered, go to password entry screen
            Intent intent = new Intent(this, EnterPasswordActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        } else {
            // If user isn't registered, go to registration screen
            Intent intent = new Intent(this, RegisterActivity.class);
            intent.putExtra("email", email);
            startActivity(intent);
        }
    });
    });
    }

    private void isUserRegistered(String email, final OnIsUserRegisteredCallback callback) {
        FirebaseAuth auth = FirebaseAuth.getInstance();

        auth.fetchSignInMethodsForEmail(email)
            .addOnCompleteListener(task -> {
        if (task.isSuccessful()) {
            List<String> signInMethods = task.getResult().getSignInMethods();
            boolean isUserRegistered = signInMethods != null && !signInMethods.isEmpty();
            callback.onIsUserRegistered(isUserRegistered);
        } else {
            // Handle error during execution
            callback.onIsUserRegistered(false);
        }
    });
    }

    private boolean isEmailValid(String email) {
        return Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    //
    private interface OnIsUserRegisteredCallback {
        void onIsUserRegistered(boolean isRegistered);
    }
}
