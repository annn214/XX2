package com.kelompok2.pencatatankeuangan;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import com.kelompok2.pencatatankeuangan.databinding.ActivityRegisterBinding;
import com.kelompok2.pencatatankeuangan.viewmodel.AuthViewModel;

public class RegisterActivity extends AppCompatActivity {
    private ActivityRegisterBinding binding;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegisterBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);

        binding.btnRegister.setOnClickListener(v -> {
            String username = binding.etUsername.getText().toString();
            String password = binding.etPassword.getText().toString();

            if (username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Harap isi semua kolom", Toast.LENGTH_SHORT).show();
            } else {
                authViewModel.register(username, password);
            }
        });

        authViewModel.getRegistrationStatus().observe(this, success -> {
            if (success) {
                Toast.makeText(this, "Registrasi Berhasil!", Toast.LENGTH_SHORT).show();
                finish();
            } else {
                Toast.makeText(this, "Registrasi Gagal, username mungkin sudah ada", Toast.LENGTH_SHORT).show();
            }
        });

        binding.tvLoginLink.setOnClickListener(v -> finish());
    }
}
