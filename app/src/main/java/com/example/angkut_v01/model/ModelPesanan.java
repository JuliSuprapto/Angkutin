package com.example.angkut_v01.model;

public class ModelPesanan {

    String _idPesanan, _idUser, _idDriver, fullnameUser, phoneUser, latitudeUser, longitudeUser, status;

    public ModelPesanan() {
        this._idUser = _idUser;
    }

    public ModelPesanan(String _idPesanan, String _idUser, String _idDriver, String fullnameUser, String phoneUser, String latitudeUser, String longitudeUser, String status) {
        this._idPesanan = _idPesanan;
        this._idUser = _idUser;
        this._idDriver = _idDriver;
        this.fullnameUser = fullnameUser;
        this.phoneUser = phoneUser;
        this.latitudeUser = latitudeUser;
        this.longitudeUser = longitudeUser;
        this.status = status;
    }

    public String get_idPesanan() {
        return _idPesanan;
    }

    public void set_idPesanan(String _idPesanan) {
        this._idPesanan = _idPesanan;
    }

    public String get_idUser() {
        return _idUser;
    }

    public void set_idUser(String _idUser) {
        this._idUser = _idUser;
    }

    public String get_idDriver() {
        return _idDriver;
    }

    public void set_idDriver(String _idDriver) {
        this._idDriver = _idDriver;
    }

    public String getFullnameUser() {
        return fullnameUser;
    }

    public void setFullnameUser(String fullnameUser) {
        this.fullnameUser = fullnameUser;
    }

    public String getPhoneUser() {
        return phoneUser;
    }

    public void setPhoneUser(String phoneUser) {
        this.phoneUser = phoneUser;
    }

    public String getLatitudeUser() {
        return latitudeUser;
    }

    public void setLatitudeUser(String latitudeUser) {
        this.latitudeUser = latitudeUser;
    }

    public String getLongitudeUser() {
        return longitudeUser;
    }

    public void setLongitudeUser(String longitudeUser) {
        this.longitudeUser = longitudeUser;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
