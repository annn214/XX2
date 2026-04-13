package com.kelompok2.pencatatankeuangan.model;

public class Transaksi {
    private int id;
    private String judul;
    private double nominal;
    private String tanggal;
    private String tipe; // "Pemasukan" atau "Pengeluaran"

    public Transaksi() {}

    public Transaksi(int id, String judul, double nominal, String tanggal, String tipe) {
        this.id = id;
        this.judul = judul;
        this.nominal = nominal;
        this.tanggal = tanggal;
        this.tipe = tipe;
    }

    public Transaksi(String judul, double nominal, String tanggal, String tipe) {
        this.judul = judul;
        this.nominal = nominal;
        this.tanggal = tanggal;
        this.tipe = tipe;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getJudul() {
        return judul;
    }

    public void setJudul(String judul) {
        this.judul = judul;
    }

    public double getNominal() {
        return nominal;
    }

    public void setNominal(double nominal) {
        this.nominal = nominal;
    }

    public String getTanggal() {
        return tanggal;
    }

    public void setTanggal(String tanggal) {
        this.tanggal = tanggal;
    }

    public String getTipe() {
        return tipe;
    }

    public void setTipe(String tipe) {
        this.tipe = tipe;
    }
}
