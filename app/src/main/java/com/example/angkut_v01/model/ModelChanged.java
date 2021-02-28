package com.example.angkut_v01.model;

public class ModelChanged {

    String _id, fullname, address, nik, phone, plat, fotoprofile, role;
    double latitude, longitude;

    public ModelChanged() {

    }

    public ModelChanged(String _id, String fullname, String address, String nik, String phone, String plat, String fotoprofile, String role, double latitude, double longitude) {
        this._id = _id;
        this.fullname = fullname;
        this.address = address;
        this.nik = nik;
        this.phone = phone;
        this.plat = plat;
        this.fotoprofile = fotoprofile;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getFotoprofile() {
        return fotoprofile;
    }

    public void setFotoprofile(String fotoprofile) {
        this.fotoprofile = fotoprofile;
    }

    public String getRole() {
        return role;
    }

    public void setRole(String role) {
        this.role = role;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }
}
