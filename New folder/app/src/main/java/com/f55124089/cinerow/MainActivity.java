package com.f55124089.cinerow;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.f55124089.cinerow.FilmCallback;
import com.f55124089.cinerow.FilmController;
import com.f55124089.cinerow.databinding.ActivityMainBinding;
import com.f55124089.cinerow.Film;

import java.util.ArrayList;
import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: VIEW (Activity)
 *  Tanggung jawab:
 *   1. Menampilkan tampilan ke pengguna
 *   2. Menerima aksi pengguna (klik, scroll)
 *   3. MENDELEGASIKAN logika ke Controller — tidak boleh ada logika bisnis di sini
 *   4. Menampilkan hasil yang diberikan Controller
 * ─────────────────────────────────────────────────────────────
 */
public class MainActivity extends AppCompatActivity implements FilmCallback {

    // ViewBinding: menggantikan findViewById() dengan objek yang type-safe
    // Tidak perlu casting dan tidak bisa NullPointerException karena semua sudah digenerate
    private ActivityMainBinding binding;

    // Controller: satu-satunya titik komunikasi dengan logika bisnis
    private FilmController controller;

    // Adapter: penghubung antara data film dan RecyclerView
    private FilmAdapter adapter;

    // List yang dipegang Adapter — diupdate saat data dari API tiba
    private List<Film> filmList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // ViewBinding: inflate layout XML dan ikat ke binding object
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot()); // set view utama dari binding

        // Inisialisasi Controller — Activity sebagai callback
        controller = new FilmController();

        // Inisialisasi list kosong dan adapter sebelum data datang
        filmList = new ArrayList<>();
        adapter = new FilmAdapter(this, filmList);

        setupUI();
        loadFilms();
    }

    /**
     * Mengatur semua komponen UI: RecyclerView, toolbar, FAB, dsb.
     * Dipisah ke method sendiri agar onCreate() tetap bersih dan mudah dibaca.
     */
    private void setupUI() {
        // Toolbar: set judul aplikasi
        setSupportActionBar(binding.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("CineVault");
        }

        // GridLayoutManager dengan 2 kolom: tampilan poster film lebih estetik
        // dibandingkan LinearLayoutManager (1 kolom) — mirip Netflix/Disney+
        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        binding.rvFilms.setLayoutManager(layoutManager);
        binding.rvFilms.setAdapter(adapter);

        // Animasi bawaan RecyclerView saat item ditambah/dihapus
        binding.rvFilms.setItemAnimator(new androidx.recyclerview.widget.DefaultItemAnimator());

        // FAB (Floating Action Button): tombol tambah film baru
        // setOnClickListener: mendaftarkan aksi klik
        binding.fabAddFilm.setOnClickListener(v -> {
            // Buka AddFilmActivity untuk form tambah film
            Intent intent = new Intent(this, AddFilmActivity.class);
            startActivityForResult(intent, 100); // 100 = request code untuk identifikasi
        });

        // Swipe-to-Refresh: user bisa tarik layar ke bawah untuk refresh data
        binding.swipeRefresh.setColorSchemeResources(
                android.R.color.holo_red_light,     // warna spinner refresh
                android.R.color.holo_orange_light
        );
        binding.swipeRefresh.setOnRefreshListener(this::loadFilms);
    }

    /**
     * Memulai proses pengambilan data film dari API.
     * Shimmer ditampilkan selama menunggu response dari server.
     */
    private void loadFilms() {
        // Tampilkan Shimmer, sembunyikan RecyclerView dan empty state
        showShimmer();

        // Delegasikan ke Controller — Activity sendiri sebagai callback
        // 'this' bisa dipakai karena MainActivity implements FilmCallback
        controller.getAllFilms(this);
    }

    // ════════════════════════════════════════════════════════════════
    //  FilmCallback Implementation
    //  Ketiga method ini dipanggil oleh Controller dari UI thread
    // ════════════════════════════════════════════════════════════════

    /**
     * Dipanggil Controller saat data film berhasil diambil dari API.
     * Ini PASTI berjalan di UI thread (sudah dijamin di Controller dengan mainHandler).
     */
    @Override
    public void onFilmsLoaded(List<Film> films) {
        hideShimmer();                          // matikan animasi shimmer
        binding.swipeRefresh.setRefreshing(false); // matikan indikator refresh

        if (films.isEmpty()) {
            // Tampilkan state kosong jika API tidak mengembalikan film apapun
            showEmptyState();
        } else {
            // Update adapter dengan data baru — RecyclerView otomatis refresh
            adapter.updateData(films);
            showRecyclerView();
        }
    }

    /**
     * Dipanggil Controller saat film baru berhasil di-POST ke server.
     */
    @Override
    public void onFilmAdded(Film film) {
        // Tambahkan film ke posisi paling atas tanpa reload seluruh list
        adapter.addFilmAtTop(film);
        showRecyclerView();

        // Scroll ke posisi 0 agar user melihat film yang baru ditambahkan
        binding.rvFilms.scrollToPosition(0);

        Toast.makeText(this, "Film \"" + film.getJudul() + "\" berhasil ditambahkan!", Toast.LENGTH_SHORT).show();
    }

    /**
     * Dipanggil Controller saat terjadi error (jaringan, parsing, server).
     */
    @Override
    public void onError(String message) {
        hideShimmer();
        binding.swipeRefresh.setRefreshing(false);

        // Tampilkan error dengan Toast
        Toast.makeText(this, message, Toast.LENGTH_LONG).show();

        // Jika list kosong, tampilkan empty state dengan pesan error
        if (filmList.isEmpty()) {
            showEmptyState();
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  onActivityResult: menerima hasil dari AddFilmActivity
    // ════════════════════════════════════════════════════════════════

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // requestCode 100 = dari AddFilmActivity (sesuai yang kita set di startActivityForResult)
        if (requestCode == 100 && resultCode == RESULT_OK && data != null) {
            // Ambil data film baru yang dikirim kembali dari AddFilmActivity
            Film newFilm = new Film(
                    data.getStringExtra("judul"),
                    data.getStringExtra("genre"),
                    data.getStringExtra("poster"),
                    data.getStringExtra("deskripsi")
            );
            newFilm.setId(data.getStringExtra("id"));

            // POST film ke server melalui Controller
            controller.addFilm(newFilm, this);
        }
    }

    // ════════════════════════════════════════════════════════════════
    //  State Management: mengatur tampilan berdasarkan kondisi
    // ════════════════════════════════════════════════════════════════

    /** Tampilkan animasi shimmer saat loading data */
    private void showShimmer() {
        binding.shimmerLayout.setVisibility(View.VISIBLE);
        binding.shimmerLayout.startShimmer(); // mulai animasi kilap
        binding.rvFilms.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.GONE);
    }

    /** Sembunyikan shimmer setelah data tiba atau error */
    private void hideShimmer() {
        binding.shimmerLayout.stopShimmer();   // hentikan animasi (hemat baterai)
        binding.shimmerLayout.setVisibility(View.GONE);
    }

    /** Tampilkan RecyclerView berisi film */
    private void showRecyclerView() {
        binding.rvFilms.setVisibility(View.VISIBLE);
        binding.layoutEmptyState.setVisibility(View.GONE);
    }

    /** Tampilkan empty state saat tidak ada film atau terjadi error */
    private void showEmptyState() {
        binding.rvFilms.setVisibility(View.GONE);
        binding.layoutEmptyState.setVisibility(View.VISIBLE);
    }

    // ════════════════════════════════════════════════════════════════
    //  Lifecycle
    // ════════════════════════════════════════════════════════════════

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Bersihkan thread pool Controller agar tidak memory leak
        // Thread yang masih berjalan akan dihentikan dengan graceful shutdown
        controller.shutdown();
    }
}
