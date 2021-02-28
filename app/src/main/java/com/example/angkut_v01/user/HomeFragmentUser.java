package com.example.angkut_v01.user;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.angkut_v01.AdapterDriver;
import com.example.angkut_v01.R;
import com.example.angkut_v01.access.Login;
import com.example.angkut_v01.adapter.RecycleViewAdapter;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executor;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragmentUser extends Fragment {

    private SupportMapFragment supportMapFragment;
    private FusedLocationProviderClient client;
     ModelAccess profile;
    FirebaseDatabase database;
    private DatabaseReference reference;
    private Button findData;
    private GoogleMap mMap;
    RecyclerView recycleView;
    RecyclerView.Adapter recycleViewAdapter;
    ProgressDialog progressDialog;
    private RequestQueue mRequestQueue;
    List<ModelDriver> listDataDriver;
    int markerImage;
    float jarakaDriverNow;
    String key, _idDriver;
    double latLast, lngLast;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_user, container, false);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        database = FirebaseDatabase.getInstance();
        reference = database.getInstance().getReference("location");

        mRequestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        supportMapFragment = (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.maps_google);
        client = LocationServices.getFusedLocationProviderClient(getActivity());

        LocationRequest locationRequest = LocationRequest.create();
        locationRequest.setInterval(1000);
        locationRequest.setFastestInterval(5000);
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);

        getCurrentLocation();

        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            getCurrentLocation();
        } else {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 44);
        }

        findData = (Button) v.findViewById(R.id.find);
        findData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(getActivity(), R.style.BottomSheetDialogTheme);
                View bottomSheetView = LayoutInflater.from(getActivity().getApplicationContext()).inflate(R.layout.layout_bottom_sheet, (LinearLayout) v.findViewById(R.id.bottomsheetcontainer));
                recycleView = (RecyclerView) bottomSheetView.findViewById(R.id.listDriver);
                recycleView.setHasFixedSize(true);
                recycleView.setLayoutManager(new LinearLayoutManager(getActivity()));
                listDataDriver = new ArrayList<>();
                recycleViewAdapter = new RecycleViewAdapter(getActivity(), listDataDriver);
                recycleView.setAdapter(recycleViewAdapter);

                bottomSheetDialog.setContentView(bottomSheetView);
                bottomSheetDialog.show();
                final FusedLocationProviderClient clients = LocationServices.getFusedLocationProviderClient(getActivity());

                reference.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {

                        for (DataSnapshot dataSnapshot : snapshot.getChildren()) {

                            ModelDriver modelDriver = new ModelDriver();
                            double latNew = dataSnapshot.child("latitude").getValue(Double.class);
                            double lngNew = dataSnapshot.child("longitude").getValue(Double.class);

                            clients.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                                @Override
                                public void onSuccess(Location location) {
                                    if (location != null){
                                        latLast = location.getLatitude();
                                        lngLast = location.getLongitude();
                                    }
                                }
                            });

                            String _id = dataSnapshot.child("_id").getValue(String.class);
                            String fullname = dataSnapshot.child("fullname").getValue(String.class);
                            String phone = dataSnapshot.child("phone").getValue(String.class);
                            String plat = dataSnapshot.child("plat").getValue(String.class);
                            String foto = dataSnapshot.child("fotoprofile").getValue(String.class);
                            if (foto == null){
                                foto = "default.png";
                            }
                            System.out.println("FOTO" + foto);

                            final float result[] = new float[10];
                            Location.distanceBetween(latLast, lngLast, latNew, lngNew, result);
                            float distanceLocation = result[0] / 1000;
                            float resultLocation = (float) (Math.round(distanceLocation * 100)) / 100;
                            jarakaDriverNow = resultLocation;

                            modelDriver.set_id(_id);
                            modelDriver.setFullname(fullname);
                            modelDriver.setPhone(phone);
                            modelDriver.setPlat(plat);
                            modelDriver.setProfilephoto(foto);
                            modelDriver.setJarak(jarakaDriverNow);

                            listDataDriver.add(modelDriver);

                        }
                        recycleViewAdapter.notifyDataSetChanged();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        });

        return v;
    }

    private void getCurrentLocation() {
        Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(final Location location) {
                if (location != null) {
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {
                        @Override
                        public void onMapReady(final GoogleMap googleMap) {

                            mMap = googleMap;

                            final Map<String, Marker> mNamedMarkers = new HashMap<String, Marker>();

                            reference.addChildEventListener(new ChildEventListener() {
                                @Override
                                public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if (!snapshot.equals(null)) {
                                        key = snapshot.getKey();
                                        Log.d("KEY", key);
                                        if (snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                                            double latNew = snapshot.child("latitude").getValue(Double.class);
                                            double lngNew = snapshot.child("longitude").getValue(Double.class);

                                            markerImage = R.drawable.ic_delivery_truck;

                                            LatLng newLocation = new LatLng(latNew, lngNew);
                                            Marker marker = mNamedMarkers.get(key);
                                            if (marker == null) {
                                                MarkerOptions options = getMarkerOption(key);
                                                marker = mMap.addMarker(options.position(newLocation));
                                                mNamedMarkers.put(key, marker);
                                            } else {
                                                marker.setPosition(newLocation);
                                            }
                                        } else {
                                            System.out.println("Long or Lat were null!!!");
                                        }
                                    }

                                }

                                @Override
                                public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    if (!snapshot.equals(null)) {
                                        key = snapshot.getKey();
                                        if (snapshot.hasChild("latitude") && snapshot.hasChild("longitude")) {
                                            double latNew = snapshot.child("latitude").getValue(Double.class);
                                            double lngNew = snapshot.child("longitude").getValue(Double.class);

                                            markerImage = R.drawable.ic_delivery_truck;

                                            LatLng newLocation = new LatLng(latNew, lngNew);
                                            Marker marker = mNamedMarkers.get(key);
                                            if (marker == null) {
                                                MarkerOptions options = getMarkerOption(key);
                                                marker = mMap.addMarker(options.position(newLocation));
                                                mNamedMarkers.put(key, marker);
                                            } else {
                                                marker.setPosition(newLocation);
                                            }
                                        } else {
                                            System.out.println("Long or Lat were null!!!");
                                        }
                                    }
                                }

                                @Override
                                public void onChildRemoved(@NonNull DataSnapshot snapshot) {
                                    String key = snapshot.getKey();
                                    Marker marker = mNamedMarkers.get(key);
                                    if (marker != null)
                                        marker.remove();
                                }

                                @Override
                                public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                    Log.d("PRIORITY FOR", snapshot.getKey());
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                            LatLng nowLocation = new LatLng(location.getLatitude(), location.getLongitude());
                            googleMap.setMinZoomPreference(15.0f);
                            googleMap.setMaxZoomPreference(20.0f);
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(nowLocation, 16.0f));
                            googleMap.getUiSettings().setZoomControlsEnabled(true);
                            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
                            googleMap.setMyLocationEnabled(true);
                            googleMap.setPadding(0, 100, 0, 150);
                        }
                    });
                }
            }
        });
    }

    private MarkerOptions getMarkerOption(String key) {
        return new MarkerOptions().icon(bitmapDescriptor(getActivity(), markerImage));
    }

    private BitmapDescriptor bitmapDescriptor(Context context, int vectorResId) {
        Drawable drawable = ContextCompat.getDrawable(context, vectorResId);
        drawable.setBounds(0, 0, drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight());
        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.draw(canvas);
        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == 44) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getCurrentLocation();
            }
        }
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
}
