package com.kelompok2.pencatatankeuangan.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.kelompok2.pencatatankeuangan.model.Transaksi;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "pencatatan_keuangan.db";
    private static final int DATABASE_VERSION = 2;

    public static final String TABLE_NAME = "keuangan";
    public static final String COLUMN_ID = "id";
    public static final String COLUMN_JUDUL = "judul";
    public static final String COLUMN_NOMINAL = "nominal";
    public static final String COLUMN_TANGGAL = "tanggal";
    public static final String COLUMN_TIPE = "tipe";

    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_JUDUL + " TEXT, " +
                    COLUMN_NOMINAL + " REAL, " +
                    COLUMN_TANGGAL + " TEXT, " +
                    COLUMN_TIPE + " TEXT);";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(TABLE_CREATE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion < 2) {
            db.execSQL("ALTER TABLE " + TABLE_NAME + " ADD COLUMN " + COLUMN_TIPE + " TEXT DEFAULT 'Pengeluaran'");
        }
    }

    public void addTransaksi(Transaksi transaksi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JUDUL, transaksi.getJudul());
        values.put(COLUMN_NOMINAL, transaksi.getNominal());
        values.put(COLUMN_TANGGAL, transaksi.getTanggal());
        values.put(COLUMN_TIPE, transaksi.getTipe());
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public void updateTransaksi(Transaksi transaksi) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_JUDUL, transaksi.getJudul());
        values.put(COLUMN_NOMINAL, transaksi.getNominal());
        values.put(COLUMN_TANGGAL, transaksi.getTanggal());
        values.put(COLUMN_TIPE, transaksi.getTipe());
        db.update(TABLE_NAME, values, COLUMN_ID + " = ?", new String[]{String.valueOf(transaksi.getId())});
        db.close();
    }

    public void deleteTransaksi(int id) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, COLUMN_ID + " = ?", new String[]{String.valueOf(id)});
        db.close();
    }

    public List<Transaksi> getAllTransaksi() {
        List<Transaksi> transaksiList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_NAME + " ORDER BY " + COLUMN_ID + " DESC";
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Transaksi transaksi = new Transaksi();
                transaksi.setId(cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                transaksi.setJudul(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_JUDUL)));
                transaksi.setNominal(cursor.getDouble(cursor.getColumnIndexOrThrow(COLUMN_NOMINAL)));
                transaksi.setTanggal(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TANGGAL)));
                transaksi.setTipe(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TIPE)));
                transaksiList.add(transaksi);
            } while (cursor.moveToNext());
        }
        cursor.close();
        db.close();
        return transaksiList;
    }
}
