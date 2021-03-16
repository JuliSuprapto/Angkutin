package com.example.angkut_v01.driver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.angkut_v01.R;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelChanged;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.example.angkut_v01.utils.Utils;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class MainDriver extends AppCompatActivity implements OnMapReadyCallback, LocationListener {

    private static final String TAG = MainDriver.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    ModelAccess profile;
    boolean BackPress = false;

    private DatabaseReference reference;
    private LocationManager manager;
    private final int MIN_TIME = 1000; //1sec
    private final int MAX_DISTANCE = 1; //1meter
    private GoogleMap mMap;
    ModelChanged modelChanged;
    String _idDriver, statusDriver;
    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_driver);

        bottomNav = findViewById(R.id.bottom_nav);
        mRequestQueue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        _idDriver = profile.get_id();
        manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        reference = FirebaseDatabase.getInstance().getReference("location").child(_idDriver);

        if (!Utils.isLoggedIn()) {
            bottomNav.setItemSelected(R.id.account, true);
            fragmentManager = getSupportFragmentManager();
            AccountFragmentDriver accountFragment = new AccountFragmentDriver();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
        } else {
            if (savedInstanceState == null) {
                bottomNav.setItemSelected(R.id.home, true);
                fragmentManager = getSupportFragmentManager();
                HomeFragmentDriver homeFragment = new HomeFragmentDriver();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            }
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id) {
                    case R.id.home:
                        fragment = new HomeFragmentDriver();
                        break;
                    case R.id.account:
                        fragment = new AccountFragmentDriver();
                        break;
                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    Log.e(TAG, "Error creating fragment");
                }
            }
        });

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        MapView maps = (MapView) findViewById(R.id.maps_google_driver);
        maps.getMapAsync(this);

        getLocationUpdate();

        getAllDriver(_idDriver);
    }

    private void getLocationUpdate() {
        if (manager != null) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else if (manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                    manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, MIN_TIME, MAX_DISTANCE, this);
                } else {
                    StyleableToast.makeText(this, "Tidak ada provider...", R.style.toastStyleDefault).show();
                }
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 101);
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
                StyleableToast.makeText(this, "Membutuhkan hak akses...", R.style.toastStyleDefault).show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        if (BackPress) {
            super.onBackPressed();
            return;
        }
        this.BackPress = true;
        StyleableToast.makeText(this, "Tekan sekali lagi untuk keluar...", R.style.toastStyleDefault).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPress = false;
            }
        }, 2000);
    }

    private void getAllDriver(final String _idDriver) {

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.showUser + _idDriver, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            String strMsg = jObj.getString("msg");
                            boolean status = jObj.getBoolean("error");
                            if (status == false) {
                                JSONObject dataDriver = jObj.getJSONObject("data");
                                String statusD = dataDriver.getString("status");
                                statusDriver = statusD;
                                System.out.println("STATUS DRIVER NOW " + statusDriver);
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

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {

            String _id = profile.get_id();
            String fullname = profile.getFullname();
            String address = profile.getAddress();
            String nik = profile.getNik();
            String phone = profile.getPhone();
            String profilephoto = profile.getProfilephoto();
            String role = profile.getRole();
            String plat = profile.getPlat();
            double latitude = location.getLatitude();
            double longitude = location.getLongitude();
            String status = statusDriver;
            System.out.println("STATUS DRiVER LOCATION" + status);

            modelChanged = new ModelChanged(_id, fullname, address, nik, phone, plat, profilephoto, role, latitude, longitude, status);
            saveLocation(modelChanged);
        } else {
            StyleableToast.makeText(this, "Tidak ada lokasi...", R.style.toastStyleDefault).show();
        }
    }

    private void saveLocation(ModelChanged modelChanged) {
        if (Utils.isLoggedIn()) {
            reference.setValue(modelChanged);
            Log.d("DATA LOCATION", String.valueOf(modelChanged));
        }
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
