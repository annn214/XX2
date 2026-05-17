package com.f55124089.cinerow;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.f55124089.cinerow.FilmCallback;
import com.f55124089.cinerow.FilmController;
import com.f55124089.cinerow.databinding.ActivityAddFilmBinding;
import com.f55124089.cinerow.Film;

import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: VIEW (Activity)
 *  Form untuk menambahkan film baru via HTTP POST ke MockAPI.
 *
 *  Alur:
 *   1. User isi form → klik tombol Simpan
 *   2. Activity validasi input (layer View boleh validasi UI)
 *   3. Delegasikan ke Controller untuk POST ke API
 *   4. Saat berhasil, kirim kembali data ke MainActivity via setResult()
 * ─────────────────────────────────────────────────────────────
 */
public class AddFilmActivity extends AppCompatActivity implements FilmCallback {

    private ActivityAddFilmBinding binding;
    private FilmController controller;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddFilmBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        controller = new FilmController();

        setupToolbar();
        setupClickListeners();
    }

    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Tambah Film Baru");
        }
    }

    private void setupClickListeners() {
        binding.btnSimpan.setOnClickListener(v -> {
            // Validasi input sebelum mengirim ke Controller
            if (validateInput()) {
                submitFilm();
            }
        });
    }

    /**
     * Validasi semua field form sebelum data dikirim ke server.
     * Validasi UI adalah tanggung jawab layer View — bukan Controller.
     * @return true jika semua input valid, false jika ada yang kosong
     */
    private boolean validateInput() {
        String judul    = binding.etJudul.getText().toString().trim();
        String genre    = binding.etGenre.getText().toString().trim();
        String poster   = binding.etPoster.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();

        // TextUtils.isEmpty() mengecek apakah string null atau kosong
        if (TextUtils.isEmpty(judul)) {
            // setError() menampilkan tanda error merah di bawah field input
            binding.tilJudul.setError("Judul film tidak boleh kosong");
            binding.etJudul.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(genre)) {
            binding.tilGenre.setError("Genre tidak boleh kosong");
            binding.etGenre.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(poster)) {
            binding.tilPoster.setError("URL poster tidak boleh kosong");
            binding.etPoster.requestFocus();
            return false;
        }

        // Validasi sederhana apakah URL dimulai dengan http:// atau https://
        if (!poster.startsWith("http://") && !poster.startsWith("https://")) {
            binding.tilPoster.setError("Masukkan URL yang valid (dimulai dengan http:// atau https://)");
            binding.etPoster.requestFocus();
            return false;
        }

        if (TextUtils.isEmpty(deskripsi)) {
            binding.tilDeskripsi.setError("Deskripsi tidak boleh kosong");
            binding.etDeskripsi.requestFocus();
            return false;
        }

        // Hapus semua error jika semua valid
        binding.tilJudul.setError(null);
        binding.tilGenre.setError(null);
        binding.tilPoster.setError(null);
        binding.tilDeskripsi.setError(null);

        return true;
    }

    /**
     * Membuat objek Film dari input form dan mendelegasikan POST ke Controller.
     */
    private void submitFilm() {
        // Ambil nilai dari setiap EditText
        String judul    = binding.etJudul.getText().toString().trim();
        String genre    = binding.etGenre.getText().toString().trim();
        String poster   = binding.etPoster.getText().toString().trim();
        String deskripsi = binding.etDeskripsi.getText().toString().trim();

        // Buat objek Film dari input user
        Film film = new Film(judul, genre, poster, deskripsi);

        // Tampilkan progress indicator — sembunyikan tombol Simpan saat loading
        setLoadingState(true);

        // Delegasikan ke Controller untuk POST ke API
        // 'this' = Activity sendiri sebagai callback
        controller.addFilm(film, this);
    }

    // ════════════════════════════════════════════════════════════════
    //  FilmCallback Implementation
    // ════════════════════════════════════════════════════════════════

    @Override
    public void onFilmsLoaded(List<Film> films) {
        // Method ini tidak dipakai di AddFilmActivity — hanya untuk GET
        // Kosongkan saja — wajib ada karena mengimplementasikan interface
    }

    @Override
    public void onFilmAdded(Film film) {
        setLoadingState(false);

        Toast.makeText(this, "Film berhasil ditambahkan!", Toast.LENGTH_SHORT).show();

        // Kirim data film baru kembali ke MainActivity via Intent
        // setResult(RESULT_OK) memberitahu MainActivity bahwa operasi berhasil
        Intent resultIntent = new Intent();
        resultIntent.putExtra("id", film.getId());
        resultIntent.putExtra("judul", film.getJudul());
        resultIntent.putExtra("genre", film.getGenre());
        resultIntent.putExtra("poster", film.getPoster());
        resultIntent.putExtra("deskripsi", film.getDeskripsi());
        setResult(RESULT_OK, resultIntent);

        // Tutup Activity ini dan kembali ke MainActivity
        finish();
    }

    @Override
    public void onError(String message) {
        setLoadingState(false);
        Toast.makeText(this, "Gagal menambahkan film: " + message, Toast.LENGTH_LONG).show();
    }

    // ════════════════════════════════════════════════════════════════
    //  Loading State
    // ════════════════════════════════════════════════════════════════

    /**
     * Mengubah tampilan saat proses POST sedang berjalan.
     * Mencegah user menekan tombol Simpan berkali-kali.
     */
    private void setLoadingState(boolean isLoading) {
        binding.btnSimpan.setEnabled(!isLoading);
        // Tampilkan/sembunyikan ProgressBar
        binding.progressBar.setVisibility(isLoading ? View.VISIBLE : View.GONE);
        binding.btnSimpan.setText(isLoading ? "Menyimpan..." : "Simpan Film");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        controller.shutdown();
    }
}
