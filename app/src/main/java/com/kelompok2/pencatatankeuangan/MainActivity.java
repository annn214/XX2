package com.kelompok2.pencatatankeuangan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.kelompok2.pencatatankeuangan.databinding.ActivityMainBinding;
import com.kelompok2.pencatatankeuangan.viewmodel.AuthViewModel;

public class MainActivity extends AppCompatActivity {
    private ActivityMainBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnLogin.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi username dan password", Toast.LENGTH_SHORT).show();
            } else {
                authViewModel.login(username, password);
            }
        });

        authViewModel.getLoginStatus().observe(this, success -> {
            if (success) {
                String username = binding.etUsername.getText().toString();
                Toast.makeText(this, "Selamat Datang, " + username + "!", Toast.LENGTH_LONG).show();
                Intent intent = new Intent(MainActivity.this, HomeActivity.class);
                startActivity(intent);
                finish();
            } else {
                Toast.makeText(this, "Login Gagal! Periksa kembali username dan password", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvRegisterLink.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }
}
