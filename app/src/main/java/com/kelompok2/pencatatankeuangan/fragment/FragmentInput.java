package com.kelompok2.pencatatankeuangan.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import com.kelompok2.pencatatankeuangan.databinding.FragmentInputBinding;
import com.kelompok2.pencatatankeuangan.model.Transaksi;
import com.kelompok2.pencatatankeuangan.viewmodel.TransaksiViewModel;
import java.text.NumberFormat;
import java.util.Calendar;
import java.util.Locale;

public class FragmentInput extends Fragment {
    private FragmentInputBinding binding;
    private TransaksiViewModel viewModel;
    private String currentNominal = "";
    private String currentTanggal = "";
    private String ddmmyyyy = "DDMMYYYY";
    private Calendar cal = Calendar.getInstance();

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentInputBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        viewModel = new ViewModelProvider(requireActivity()).get(TransaksiViewModel.class);

        setupNominalFormatting();
        setupTanggalFormatting();

        binding.btnSimpan.setOnClickListener(v -> {
            String judul = binding.etJudul.getText().toString();
            String nominalStr = binding.etNominal.getText().toString().replaceAll("[^\\d]", "");
            String tanggal = binding.etTanggal.getText().toString();
            String tipe = binding.rbPemasukan.isChecked() ? "Pemasukan" : "Pengeluaran";

            if (TextUtils.isEmpty(judul) || TextUtils.isEmpty(nominalStr) || TextUtils.isEmpty(tanggal)) {
                Toast.makeText(getContext(), "Mohon isi semua data", Toast.LENGTH_SHORT).show();
                return;
            }

            if (tanggal.length() < 10 || tanggal.contains("D") || tanggal.contains("M") || tanggal.contains("Y")) {
                Toast.makeText(getContext(), "Format tanggal salah (DD-MM-YYYY)", Toast.LENGTH_SHORT).show();
                return;
            }

            double nominal = Double.parseDouble(nominalStr);
            Transaksi transaksi = new Transaksi(judul, nominal, tanggal, tipe);
            viewModel.addTransaksi(transaksi);

            binding.etJudul.setText("");
            binding.etNominal.setText("");
            binding.etTanggal.setText("");
            binding.rbPengeluaran.setChecked(true);
            Toast.makeText(getContext(), "Data berhasil disimpan", Toast.LENGTH_SHORT).show();
        });
    }

    private void setupNominalFormatting() {
        binding.etNominal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentNominal)) {
                    binding.etNominal.removeTextChangedListener(this);
                    String cleanString = s.toString().replaceAll("[^\\d]", "");
                    if (!cleanString.isEmpty()) {
                        double parsed = Double.parseDouble(cleanString);
                        String formatted = NumberFormat.getNumberInstance(Locale.GERMANY).format(parsed);
                        currentNominal = formatted;
                        binding.etNominal.setText(formatted);
                        binding.etNominal.setSelection(formatted.length());
                    } else {
                        currentNominal = "";
                        binding.etNominal.setText("");
                    }
                    binding.etNominal.addTextChangedListener(this);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }

    private void setupTanggalFormatting() {
        binding.etTanggal.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (!s.toString().equals(currentTanggal)) {
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
                    currentTanggal = clean;
                    binding.etTanggal.removeTextChangedListener(this);
                    binding.etTanggal.setText(currentTanggal);
                    binding.etTanggal.setSelection(currentTanggal.length() < 10 ? start + (count > 0 ? 1 : 0) : 10);
                    binding.etTanggal.addTextChangedListener(this);
                }
            }
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
