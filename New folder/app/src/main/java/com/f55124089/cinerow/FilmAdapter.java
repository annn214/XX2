package com.f55124089.cinerow;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.f55124089.cinerow.R;
import com.f55124089.cinerow.Film;

import java.util.List;

/**
 * ─────────────────────────────────────────────────────────────
 *  LAYER: VIEW (Adapter)
 *  Tanggung jawab: Mengambil data dari List<Film> dan "memasangnya"
 *  ke setiap item view di RecyclerView.
 *
 *  Adapter adalah penghubung antara data (Model) dan tampilan list (View).
 *  Dalam konteks MVC murni, Adapter termasuk bagian dari layer View.
 * ─────────────────────────────────────────────────────────────
 */
public class FilmAdapter extends RecyclerView.Adapter<FilmAdapter.FilmViewHolder> {

    // Context diperlukan Glide untuk memuat gambar sesuai lifecycle Activity
    private final Context context;

    // List film yang akan ditampilkan — diubah saat data dari API datang
    private List<Film> filmList;

    public FilmAdapter(Context context, List<Film> filmList) {
        this.context = context;
        this.filmList = filmList;
    }

    // ── onCreateViewHolder: dipanggil SEKALI untuk membuat ViewHolder baru ──
    // LayoutInflater mengubah file XML layout menjadi View object di memori
    @NonNull
    @Override
    public FilmViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // inflate() membaca item_film.xml dan mengubahnya menjadi View
        View view = LayoutInflater.from(context).inflate(R.layout.item_film, parent, false);
        return new FilmViewHolder(view);
    }

    // ── onBindViewHolder: dipanggil setiap kali item akan ditampilkan ──
    // RecyclerView mendaur ulang ViewHolder yang sudah ada — sangat hemat memori
    @Override
    public void onBindViewHolder(@NonNull FilmViewHolder holder, int position) {
        // Ambil film yang sesuai dengan posisi item saat ini
        Film film = filmList.get(position);

        // Pasang judul dan genre ke TextView
        holder.tvJudul.setText(film.getJudul());
        holder.tvGenre.setText(film.getGenre());

        // ── Glide: memuat gambar poster dari URL secara asynchronous ──
        // with(context): terikat dengan lifecycle Activity (auto-cancel saat Activity mati)
        // load(url): URL gambar yang akan diunduh
        // placeholder: gambar sementara sebelum poster selesai diunduh
        // error: gambar yang ditampilkan jika URL tidak valid atau gagal
        // transition: animasi fade-in agar perpindahan placeholder → poster terlihat smooth
        // into(imageView): target ImageView yang akan diisi gambar
        Glide.with(context)
                .load(film.getPoster())
                .placeholder(R.drawable.ic_poster_placeholder)
                .error(R.drawable.ic_poster_placeholder)
                .transition(DrawableTransitionOptions.withCrossFade(300)) // crossfade 300ms
                .centerCrop() // crop gambar agar mengisi penuh tanpa distorsi
                .into(holder.ivPoster);

        // ── Click listener: buka DetailActivity saat item film diklik ──
        holder.itemView.setOnClickListener(v -> {
            Intent intent = new Intent(context, DetailActivity.class);

            // Kirim data film ke DetailActivity melalui Intent Extras
            // Lebih sederhana daripada Serializable/Parcelable untuk kasus ini
            intent.putExtra("film_id", film.getId());
            intent.putExtra("film_judul", film.getJudul());
            intent.putExtra("film_genre", film.getGenre());
            intent.putExtra("film_poster", film.getPoster());
            intent.putExtra("film_deskripsi", film.getDeskripsi());

            context.startActivity(intent);
        });
    }

    // ── getItemCount: memberitahu RecyclerView total jumlah item ──
    @Override
    public int getItemCount() {
        return filmList != null ? filmList.size() : 0;
    }

    /**
     * Memperbarui seluruh list setelah data baru datang dari API.
     * notifyDataSetChanged() memberitahu RecyclerView untuk menggambar ulang semua item.
     */
    public void updateData(List<Film> newFilms) {
        this.filmList = newFilms;
        notifyDataSetChanged(); // refresh tampilan RecyclerView
    }

    /**
     * Menambahkan satu film baru ke posisi paling atas list.
     * notifyItemInserted(0) lebih efisien dari notifyDataSetChanged()
     * karena hanya menggambar item baru, bukan seluruh list.
     */
    public void addFilmAtTop(Film film) {
        filmList.add(0, film);           // tambahkan ke index 0 (posisi teratas)
        notifyItemInserted(0);          // animasi insert item baru
    }

    // ════════════════════════════════════════════════════════════════
    //  ViewHolder: menyimpan referensi ke komponen UI setiap item
    // ════════════════════════════════════════════════════════════════

    /**
     * ViewHolder adalah "tempat simpan" referensi View agar tidak perlu
     * memanggil findViewById() berulang kali (sangat mahal secara performa).
     * RecyclerView mendaur ulang ViewHolder — sehingga disebut "Recycler".
     */
    static class FilmViewHolder extends RecyclerView.ViewHolder {
        ImageView ivPoster;
        TextView tvJudul;
        TextView tvGenre;

        public FilmViewHolder(@NonNull View itemView) {
            super(itemView);
            // Temukan komponen UI berdasarkan ID yang didefinisikan di item_film.xml
            ivPoster = itemView.findViewById(R.id.iv_poster);
            tvJudul  = itemView.findViewById(R.id.tv_judul);
            tvGenre  = itemView.findViewById(R.id.tv_genre);
        }
    }
}
