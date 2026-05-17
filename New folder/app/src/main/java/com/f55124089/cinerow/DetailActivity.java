package com.f55124089.cinerow;

import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.f55124089.cinerow.R;
import com.f55124089.cinerow.databinding.ActivityDetailBinding;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: VIEW (Activity)
 *  Menampilkan detail lengkap satu film yang dipilih dari MainActivity.
 *
 *  Data diterima melalui Intent Extras — tidak perlu request API ulang
 *  karena datanya sudah ada sejak list diambil di MainActivity.
 * ─────────────────────────────────────────────────────────────
 */
public class DetailActivity extends AppCompatActivity {

    private ActivityDetailBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDetailBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setupToolbar();
        displayFilmData();
    }

    /**
     * Mengatur toolbar dengan tombol "Back" untuk kembali ke MainActivity.
     */
    private void setupToolbar() {
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            // Tampilkan tombol panah kembali di toolbar kiri
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setTitle("Detail Film");
        }
    }

    /**
     * Mengambil data dari Intent dan menampilkannya ke komponen UI.
     * getIntent().getStringExtra() mengambil data yang dikirim dari FilmAdapter.
     */
    private void displayFilmData() {
        // Ambil semua data film dari Intent Extras
        String judul    = getIntent().getStringExtra("film_judul");
        String genre    = getIntent().getStringExtra("film_genre");
        String poster   = getIntent().getStringExtra("film_poster");
        String deskripsi = getIntent().getStringExtra("film_deskripsi");

        // Pasang teks ke komponen UI
        binding.tvJudul.setText(judul);
        binding.tvGenre.setText(genre);
        binding.tvDeskripsi.setText(deskripsi != null && !deskripsi.isEmpty()
                ? deskripsi
                : "Tidak ada deskripsi tersedia.");

        // Set judul toolbar sama dengan judul film (tampilan lebih kontekstual)
        if (getSupportActionBar() != null && judul != null) {
            getSupportActionBar().setTitle(judul);
        }

        // ── Glide: muat poster berukuran besar untuk tampilan detail ──
        // fitCenter: memastikan seluruh poster terlihat tanpa dipotong
        // Berbeda dari centerCrop di list — detail page menampilkan poster penuh
        Glide.with(this)
                .load(poster)
                .placeholder(R.drawable.ic_poster_placeholder)
                .error(R.drawable.ic_poster_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(400))
                .fitCenter()
                .into(binding.ivPosterDetail);
    }

    /**
     * Menangani klik pada tombol "Back" di toolbar.
     * Dipanggil saat user menekan panah kembali di kiri atas.
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // android.R.id.home adalah ID tombol back bawaan Android toolbar
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // kembali ke Activity sebelumnya (MainActivity)
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}

