package com.example.amnhotelsystem;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import com.example.amnhotelsystem.databinding.ActivityEnterPasswordBinding;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class EnterPasswordActivity extends AppCompatActivity {
    private ActivityEnterPasswordBinding binding;
    private FirebaseAuth mAuth;
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    public static final String PASS = "PASS";
    public static final String FLAG = "FLAG";
    public static final String EMAIL = "EMAIL";
    private boolean flag = false;
    String email;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEnterPasswordBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

         email = getIntent().getStringExtra("email");

        String informationText = "Please enter your password for account " + email;
        binding.informationEmailText.setText(informationText);

        mAuth = FirebaseAuth.getInstance();

        setupSharedPrefs();
        checkPrefs();

        binding.loginButton.setOnClickListener(v -> {
        String password = binding.passwordEditText.getText().toString();

        if (email != null) {
            login(email, password);
        }
    });
    }

    private void checkPrefs() {
        boolean flagValue = prefs.getBoolean(FLAG, false);
        flag = flagValue;



        if(flag){
                String password = prefs.getString(PASS, "");
                binding.passwordEditText.setText(password);
                binding.chk.setChecked(true);
            }
    }

    private void setupSharedPrefs() {
        prefs= PreferenceManager.getDefaultSharedPreferences(this);
        editor = prefs.edit();
    }

    private void login(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password)
            .addOnCompleteListener(this, task -> {
        if (task.isSuccessful()) {
            // Login success (user is authenticated)
            FirebaseUser user = mAuth.getCurrentUser();

            if(binding.chk.isChecked()){
                    editor.putString(PASS, password);
                    editor.putBoolean(FLAG, true);
                    editor.putString(EMAIL,email);
                    editor.commit();
            }

            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);
            finish();
        } else {
            // Login failed
            Toast.makeText(
                this,
                "Login failed: " + task.getException().getMessage(),
                Toast.LENGTH_SHORT
            ).show();
        }
    });
    }


//    public void btnLoginOnClick(View view) {
//
//    }


    public void btnLoginOnClick(View view) {
        if (flag){
            flag=false;
            binding.chk.setChecked(true);
        }else{
            flag=true;
            binding.chk.setChecked(false);
        }
    }
}
