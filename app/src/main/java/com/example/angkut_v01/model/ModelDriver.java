package com.example.angkut_v01.model;

public class ModelDriver {

    String _id, fullname, phone, plat, profilephoto, status;
    float jarak;

    public ModelDriver(String _id, String fullname, String phone, String plat, String profilephoto, float jarak, String status) {
        this._id = _id;
        this.fullname = fullname;
        this.phone = phone;
        this.plat = plat;
        this.profilephoto = profilephoto;
        this.jarak = jarak;
        this.status = status;
    }

    public ModelDriver() {

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

    public String getProfilephoto() {
        return profilephoto;
    }

    public void setProfilephoto(String profilephoto) {
        this.profilephoto = profilephoto;
    }

    public float getJarak() { return jarak; }

    public void setJarak(float jarak) { this.jarak = jarak; }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
