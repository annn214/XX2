package com.kelompok2.pencatatankeuangan.adapter;

import android.view.LayoutInflater;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.kelompok2.pencatatankeuangan.databinding.ItemTransaksiBinding;
import com.kelompok2.pencatatankeuangan.model.Transaksi;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class TransaksiAdapter extends RecyclerView.Adapter<TransaksiAdapter.ViewHolder> {
    private List<Transaksi> transaksiList = new ArrayList<>();
    private OnItemClickListener listener;

    public interface OnItemClickListener {
        void onItemClick(Transaksi transaksi);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        this.listener = listener;
    }

    public void setTransaksiList(List<Transaksi> list) {
        this.transaksiList = list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemTransaksiBinding binding = ItemTransaksiBinding.inflate(
                LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Transaksi transaksi = transaksiList.get(position);
        holder.binding.tvJudul.setText(transaksi.getJudul());
        
        String formattedNominal = NumberFormat.getNumberInstance(Locale.GERMANY).format(transaksi.getNominal());
        
        if ("Pemasukan".equals(transaksi.getTipe())) {
            holder.binding.tvNominal.setText("+ Rp " + formattedNominal);
            holder.binding.tvNominal.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_green_dark));
            holder.binding.ivIcon.setImageResource(android.R.drawable.ic_input_add);
        } else {
            holder.binding.tvNominal.setText("- Rp " + formattedNominal);
            holder.binding.tvNominal.setTextColor(holder.itemView.getContext().getResources().getColor(android.R.color.holo_red_dark));
            holder.binding.ivIcon.setImageResource(android.R.drawable.ic_delete);
        }
        
        holder.binding.tvTanggal.setText(transaksi.getTanggal());

        holder.itemView.setOnClickListener(v -> {
            if (listener != null) {
                listener.onItemClick(transaksi);
            }
        });
    }

    @Override
    public int getItemCount() {
        return transaksiList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        ItemTransaksiBinding binding;

        public ViewHolder(ItemTransaksiBinding binding) {
            super(binding.getRoot());
            this.binding = binding;
        }
    }
}
