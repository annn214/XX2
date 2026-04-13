package com.kelompok2.pencatatankeuangan.fragment;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import com.kelompok2.pencatatankeuangan.adapter.TransaksiAdapter;
import com.kelompok2.pencatatankeuangan.databinding.DialogEditTransaksiBinding;
import com.kelompok2.pencatatankeuangan.databinding.FragmentBerandaBinding;
import com.kelompok2.pencatatankeuangan.model.Transaksi;
import com.kelompok2.pencatatankeuangan.viewmodel.TransaksiViewModel;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class FragmentBeranda extends Fragment {
    private FragmentBerandaBinding binding;
    private TransaksiViewModel viewModel;
    private TransaksiAdapter adapter;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentBerandaBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        adapter = new TransaksiAdapter();
        binding.rvTransaksi.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvTransaksi.setAdapter(adapter);

        viewModel = new ViewModelProvider(requireActivity()).get(TransaksiViewModel.class);
        viewModel.getAllTransaksi().observe(getViewLifecycleOwner(), transaksis -> {
            adapter.setTransaksiList(transaksis);
            calculateTotal(transaksis);
        });

        adapter.setOnItemClickListener(this::showOptionsDialog);
    }

    private void calculateTotal(List<Transaksi> list) {
        double total = 0;
        for (Transaksi t : list) {
            if ("Pemasukan".equals(t.getTipe())) {
                total += t.getNominal();
            } else {
                total -= t.getNominal();
            }
        }
        String formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(total);
        binding.tvTotalSaldo.setText("Rp " + formatted);
        
        if (total < 0) {
            binding.tvTotalSaldo.setTextColor(getResources().getColor(android.R.color.holo_red_light));
        } else {
            binding.tvTotalSaldo.setTextColor(getResources().getColor(android.R.color.white));
        }
    }

    private void showOptionsDialog(Transaksi transaksi) {
        String[] options = {"Edit", "Hapus"};
        new AlertDialog.Builder(getContext())
                .setTitle("Pilih Aksi")
                .setItems(options, (dialog, which) -> {
                    if (which == 0) {
                        showEditDialog(transaksi);
                    } else {
                        showDeleteConfirmation(transaksi);
                    }
                })
                .show();
    }

    private void showDeleteConfirmation(Transaksi transaksi) {
        new AlertDialog.Builder(getContext())
                .setTitle("Hapus Transaksi")
                .setMessage("Apakah Anda yakin ingin menghapus \"" + transaksi.getJudul() + "\"?")
                .setPositiveButton("Hapus", (dialog, which) -> {
                    viewModel.deleteTransaksi(transaksi.getId());
                    Toast.makeText(getContext(), "Data berhasil dihapus", Toast.LENGTH_SHORT).show();
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void showEditDialog(Transaksi transaksi) {
        DialogEditTransaksiBinding editBinding = DialogEditTransaksiBinding.inflate(getLayoutInflater());
        
        editBinding.etJudul.setText(transaksi.getJudul());
        editBinding.etNominal.setText(String.format("%.0f", transaksi.getNominal()));
        editBinding.etTanggal.setText(transaksi.getTanggal());

        setupFormatting(editBinding.etNominal, editBinding.etTanggal);

        new AlertDialog.Builder(getContext())
                .setTitle("Edit Transaksi")
                .setView(editBinding.getRoot())
                .setPositiveButton("Simpan", (dialog, which) -> {
                    String judul = editBinding.etJudul.getText().toString();
                    String nominalStr = editBinding.etNominal.getText().toString().replaceAll("[^\\d]", "");
                    String tanggal = editBinding.etTanggal.getText().toString();

                    if (!TextUtils.isEmpty(judul) && !TextUtils.isEmpty(nominalStr) && tanggal.length() >= 10) {
                        transaksi.setJudul(judul);
                        transaksi.setNominal(Double.parseDouble(nominalStr));
                        transaksi.setTanggal(tanggal);
                        viewModel.updateTransaksi(transaksi);
                        Toast.makeText(getContext(), "Data berhasil diperbarui", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getContext(), "Mohon lengkapi data", Toast.LENGTH_SHORT).show();
                    }
                })
                .setNegativeButton("Batal", null)
                .show();
    }

    private void setupFormatting(EditText etNominal, EditText etTanggal) {
        etNominal.addTextChangedListener(new TextWatcher() {
            private String current = "";
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    etNominal.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(parsed);
                        current = formatted;
                        etNominal.setText(formatted);
                        etNominal.setSelection(formatted.length());
                    }
                    etNominal.addTextChangedListener(this);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });

        etTanggal.addTextChangedListener(new TextWatcher() {
            private String current = "";
            private String ddmmyyyy = "DDMMYYYY";
            private Calendar cal = Calendar.getInstance();
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(current)) {
                    String clean = s.toString().replaceAll("[^\\d]", "");
                    if (clean.length() < 8) {
                        clean = clean + ddmmyyyy.substring(clean.length());
                    } else {
                        int day = Integer.parseInt(clean.substring(0, 2));
                        int mon = Integer.parseInt(clean.substring(2, 4));
                        int year = Integer.parseInt(clean.substring(4, 8));
                        mon = mon < 1 ? 1 : mon > 12 ? 12 : mon;
                        cal.set(Calendar.MONTH, mon - 1);
                        year = (year < 1900) ? 1900 : (year > 2100) ? 2100 : year;
                        cal.set(Calendar.YEAR, year);
                        day = (day > cal.getActualMaximum(Calendar.DATE)) ? cal.getActualMaximum(Calendar.DATE) : day;
                        clean = String.format("%02d%02d%04d", day, mon, year);
                    }
                    clean = String.format("%s-%s-%s", clean.substring(0, 2), clean.substring(2, 4), clean.substring(4, 8));
                    current = clean;
                    etTanggal.removeTextChangedListener(this);
                    etTanggal.setText(current);
                    etTanggal.setSelection(current.length() < 10 ? start + (count > 0 ? 1 : 0) : 10);
                    etTanggal.addTextChangedListener(this);
                }
            }
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}
