package com.kelompok2.pencatatankeuangan.viewmodel;

import android.app.Application;
import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import com.kelompok2.pencatatankeuangan.database.AuthDatabaseHelper;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class AuthViewModel extends AndroidViewModel {
    private final AuthDatabaseHelper dbHelper;
    private final MutableLiveData<Boolean> loginStatus = new MutableLiveData<>();
    private final MutableLiveData<Boolean> registrationStatus = new MutableLiveData<>();
    private final ExecutorService executorService = Executors.newSingleThreadExecutor();

    public AuthViewModel(@NonNull Application application) {
        super(application);
        dbHelper = new AuthDatabaseHelper(application);
    }

    public LiveData<Boolean> getLoginStatus() {
        return loginStatus;
    }

    public LiveData<Boolean> getRegistrationStatus() {
        return registrationStatus;
    }

    public void login(String username, String password) {
        executorService.execute(() -> {
            boolean success = dbHelper.checkUser(username, password);
            loginStatus.postValue(success);
        });
    }

    public void register(String username, String password) {
        executorService.execute(() -> {
            long result = dbHelper.addUser(username, password);
            registrationStatus.postValue(result > 0);
        });
    }
}
