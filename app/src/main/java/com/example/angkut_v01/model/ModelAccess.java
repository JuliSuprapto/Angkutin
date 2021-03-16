package com.example.angkut_v01.model;

public class ModelAccess {

    String _id, nik, fullname, username, password, phone, address, profilephoto, email, plat, role, status;
    double latitude, longitude;

    public ModelAccess() {

    }

    public ModelAccess(String _id, String nik, String fullname, String username, String password, String phone, String address, String profilephoto, String email, String plat, String role, double latitude, double longitude, String status) {
        this._id = _id;
        this.nik = nik;
        this.fullname = fullname;
        this.username = username;
        this.password = password;
        this.phone = phone;
        this.address = address;
        this.profilephoto = profilephoto;
        this.email = email;
        this.plat = plat;
        this.role = role;
        this.latitude = latitude;
        this.longitude = longitude;
        this.status = status;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getNik() {
        return nik;
    }

    public void setNik(String nik) {
        this.nik = nik;
    }

    public String getFullname() {
        return fullname;
    }

    public void setFullname(String fullname) {
        this.fullname = fullname;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPlat() {
        return plat;
    }

    public void setPlat(String plat) {
        this.plat = plat;
    }

    public String getRole() { return role; }

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

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
