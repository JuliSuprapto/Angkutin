package com.example.angkut_v01.server;

public class BaseURL {

    //public static String baseUrl = "http://192.168.18.7:5050/";
    public static String baseUrl = "http://192.168.18.253:5050/";
    //public static String baseUrl = "http://192.168.43.81:5050/";
    public static String login = baseUrl + "access/login";
    public static String registerUser = baseUrl + "access/registrasiUser";
    public static String registerDriver = baseUrl + "access/registrasiDriver";

    public static String showUser = baseUrl + "access/getDataUser/";
    public static String completeUser = baseUrl + "access/completeUser/";
    public static String completeDriver = baseUrl + "access/completeDriver/";
    public static String updateUser = baseUrl + "access/updateUser/";
    public static String updateDriver = baseUrl + "access/updateDriver/";

    public static String addPesanan = baseUrl + "pesan/addPesanan";
    public static String getPesanan = baseUrl + "pesan/getdataPesananDriver/";
    public static String getPesananUser = baseUrl + "pesan/getdataPesananUser/";
    public static String deletePesanan = baseUrl + "pesan/deleteDataPesanan/";
    public static String updatePesanan = baseUrl + "pesan/updateDataPesanan/";

}
