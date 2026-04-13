package com.kelompok2.pencatatankeuangan.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kelompok2.pencatatankeuangan.database.DatabaseHelper;
import com.kelompok2.pencatatankeuangan.model.Transaksi;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TransaksiViewModel extends AndroidViewModel {
    private final DatabaseHelper dbHelper;
    private final MutableLiveData<List<Transaksi>> allTransaksi = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public TransaksiViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new DatabaseHelper(application);
        refreshTransaksi();
    }

    public LiveData<List<Transaksi>> getAllTransaksi() {
        return allTransaksi;
    }

    public void refreshTransaksi() {
        executorService.execute(() -> {
            List<Transaksi> list = dbHelper.getAllTransaksi();
            allTransaksi.postValue(list);
        });
    }

    public void addTransaksi(Transaksi transaksi) {
        executorService.execute(() -> {
            dbHelper.addTransaksi(transaksi);
            refreshTransaksi();
        });
    }

    public void updateTransaksi(Transaksi transaksi) {
        executorService.execute(() -> {
            dbHelper.updateTransaksi(transaksi);
            refreshTransaksi();
        });
    }

    public void deleteTransaksi(int id) {
        executorService.execute(() -> {
            dbHelper.deleteTransaksi(id);
            refreshTransaksi();
        });
    }
}
