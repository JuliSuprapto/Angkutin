package com.example.angkut_v01.driver;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.adapter.AdapterDriver;
import com.example.angkut_v01.R;
import com.example.angkut_v01.adapter.AdapterPesanan;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelChanged;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.model.ModelPesanan;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.example.angkut_v01.utils.Utils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static android.content.Context.LOCATION_SERVICE;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragmentDriver extends Fragment implements OnMapReadyCallback, LocationListener {

    private DatabaseReference reference;
    private LocationManager manager;
    private final int MIN_TIME = 1000; //1sec
    private final int MAX_DISTANCE = 1; //1meter
    private GoogleMap mMap;
    ModelChanged modelChanged;
    String _idDriver, statusDriver;
    String dataStatus;

    Switch switchUser;
    RecyclerView recyclePesanan;
    RecyclerView.Adapter recycleViewAdapter;
    List<ModelPesanan> listPesanan;

    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    TextView nameUser, jumlahPesananD;
    ModelAccess modelAccess;
    String _idUser, statusOn;
    LinearLayout pesananData, noDataItem, availableDataItem;
    SwipeRefreshLayout swipeRefreshLayout;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_driver, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        jumlahPesananD = (TextView) v.findViewById(R.id.jumlahPesanan);
        pesananData = (LinearLayout) v.findViewById(R.id.pesananList);
        nameUser = (TextView) v.findViewById(R.id.namaUser);
        noDataItem = (LinearLayout) v.findViewById(R.id.no_item);
        availableDataItem = (LinearLayout) v.findViewById(R.id.available_item);
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swipe_refresh);

        recyclePesanan = (RecyclerView) v.findViewById(R.id.listPesanan);
        nameUser = (TextView) v.findViewById(R.id.namaUser);
        recyclePesanan.setHasFixedSize(true);
        recyclePesanan.setLayoutManager(new LinearLayoutManager(getActivity()));
        listPesanan = new ArrayList<>();
        recycleViewAdapter = new AdapterPesanan(getActivity(), listPesanan);

        modelAccess = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        switchUser = (Switch) v.findViewById(R.id.switch_btn);

        String statusDriver = modelAccess.getStatus();

        if (statusDriver.equals("on")) {
            switchUser.setChecked(true);
        } else {
            switchUser.setChecked(false);
        }

        switchUser.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    statusOn = "on";
                    updateStstusDriver(statusOn);
                } else {
                    statusOn = "off";
                    updateStstusDriver(statusOn);
                }
            }
        });
        System.out.println("DATA HOME = " + modelAccess.getStatus());

        nameUser.setText(modelAccess.getFullname());
        _idDriver = modelAccess.get_id();

        manager = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        reference = FirebaseDatabase.getInstance().getReference("location").child(_idDriver);

        getPesanan(_idDriver);
        getAllPesanan(_idDriver);

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        MapView maps = (MapView) v.findViewById(R.id.maps_google_driver);
        maps.getMapAsync(this);

        getLocationUpdate();

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                getAllPesanan(_idDriver);
                listPesanan.clear();
                recycleViewAdapter.notifyDataSetChanged();
                swipeRefreshLayout.setRefreshing(false);
            }
        });

        return v;
    }

    private void getLocationUpdate() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else {
                    StyleableToast.makeText(getActivity(), "Tidak ada provider...", R.style.toastStyleDefault).show();
                }
            } else {
                ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 101) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getLocationUpdate();
            } else {
                StyleableToast.makeText(getActivity(), "Membutuhkan hak akses...", R.style.toastStyleDefault).show();
            }
        }
    }

    private void getPesanan(final String _idDriver) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getPesanan + _idDriver, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("error");
                            if (statusMsg == false) {
                                String data = response.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                int lengthData = jsonArray.length();
                                jumlahPesananD.setText(String.valueOf(lengthData));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
    }

    private void getAllPesanan(final String _idDriver) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getPesanan + _idDriver, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            boolean status = response.getBoolean("error");
                            if (status == false) {
                                Log.d("data driver = ", response.toString());
                                String data = response.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelPesanan pesanan = new ModelPesanan();
                                    final String _idPesanan = jsonObject.getString("_id");
                                    final String _idUser = jsonObject.getString("idUser");
                                    final String _idDriver = jsonObject.getString("idDriver");
                                    final String fullname = jsonObject.getString("fullname");
                                    final String phone = jsonObject.getString("phone");
                                    String statusUser = jsonObject.getString("status");
                                    pesanan.setStatus(statusUser);
                                    pesanan.set_idPesanan(_idPesanan);
                                    pesanan.setFullnameUser(fullname);
                                    pesanan.setPhoneUser(phone);
                                    pesanan.set_idUser(_idUser);
                                    pesanan.set_idDriver(_idDriver);
                                    listPesanan.add(pesanan);
                                    recyclePesanan.setAdapter(recycleViewAdapter);

                                }
                                noDataItem.setVisibility(View.GONE);
                                availableDataItem.setVisibility(View.VISIBLE);
                            } else {
                                noDataItem.setVisibility(View.VISIBLE);
                                availableDataItem.setVisibility(View.GONE);
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
            }
        });
        mRequestQueue.add(req);
    }

    private void updateStstusDriver(String statusOn) {

        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", statusOn);

        System.out.println("DATA STATUS = " + statusOn);

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.updateStatus + _idDriver, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = response.getString("msg");
                            boolean status = response.getBoolean("error");
                            if (status == false) {
                                JSONObject user = jsonObject.getJSONObject("result");
                                Utils.storeProfile(user.toString());
                                App.getPref().put(Prefs.PREF_STORE_PROFILE, user.toString());
                                System.out.println("DATA SEMUANYA = " + modelAccess.getStatus());

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                AccountFragmentDriver accountFragment = new AccountFragmentDriver();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();

                            } else {

                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                HomeFragmentDriver homeFragment = new HomeFragmentDriver();
                                fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();

                                StyleableToast.makeText(getActivity().getApplicationContext(), strMsg, R.style.toastStyleWarning).show();
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                VolleyLog.e("Error: ", error.getMessage());
                hideDialog();
            }
        });
        mRequestQueue.add(req);
    }

    private void showDialog() {
        if (!progressDialog.isShowing()) {
            progressDialog.show();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    private void hideDialog() {
        if (progressDialog.isShowing()) {
            progressDialog.dismiss();
            progressDialog.setContentView(R.layout.dialog_loading);
            progressDialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            String _id = modelAccess.get_id();
            String fullname = modelAccess.getFullname();
            String address = modelAccess.getAddress();
            String nik = modelAccess.getNik();
            String phone = modelAccess.getPhone();
            String profilephoto = modelAccess.getProfilephoto();
            String role = modelAccess.getRole();
            String plat = modelAccess.getPlat();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String dataStatus = modelAccess.getStatus();

            System.out.println("STATUS LOCATION = " + dataStatus);

            modelChanged = new ModelChanged(_id, fullname, address, nik, phone, plat, profilephoto, role, latitude, longitude, dataStatus);
            saveLocation(modelChanged);
        } else {
            StyleableToast.makeText(getActivity(), "Tidak ada lokasi...", R.style.toastStyleDefault).show();
        }
    }

    private void saveLocation(ModelChanged modelChanged) {
        reference.setValue(modelChanged);
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng sydney = new LatLng(31, 74);
    }
}
