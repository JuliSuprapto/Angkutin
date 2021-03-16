package com.example.angkut_v01.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
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
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class HomeFragmentDriver extends Fragment {

    RecyclerView recycleDriver;
    RecyclerView.Adapter recycleViewAdapter;
    List<ModelDriver> listDrivers;
    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    TextView nameUser, jumlahPesananD;
    ModelAccess modelAccess;
    String _idDriver, _idUser;
    LinearLayout pesananData;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_home_driver, container, false);

        mRequestQueue = Volley.newRequestQueue(getActivity());
        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);

        recycleDriver = (RecyclerView) v.findViewById(R.id.listDriver);
        jumlahPesananD = (TextView) v.findViewById(R.id.jumlahPesanan);
        pesananData = (LinearLayout) v.findViewById(R.id.pesananList);
        nameUser = (TextView) v.findViewById(R.id.namaUser);
        recycleDriver.setHasFixedSize(true);
        recycleDriver.setLayoutManager(new LinearLayoutManager(getActivity()));
        listDrivers = new ArrayList<>();
        recycleViewAdapter = new AdapterDriver(getActivity(), listDrivers);

        modelAccess = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        nameUser.setText(modelAccess.getFullname());
        _idDriver = modelAccess.get_id();

        getAllDriver();
        getPesanan(_idDriver);

        pesananData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), ListPesanan.class));
                Animatoo.animateSlideDown(getActivity());
            }
        });

        return v;
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

    private void getAllDriver() {
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.showUser, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            boolean status = response.getBoolean("error");
                            if (status == false) {
                                Log.d("data driver = ", response.toString());
                                String data = response.getString("data");
                                JSONArray jsonArray = new JSONArray(data);
                                for (int i = 0; i < jsonArray.length(); i++) {
                                    JSONObject jsonObject = jsonArray.getJSONObject(i);
                                    final ModelDriver driver = new ModelDriver();
                                    final String roles = jsonObject.getString("role");
                                    final String _id = jsonObject.getString("_id");
                                    final String fullname = jsonObject.getString("fullname");
                                    final String phone = jsonObject.getString("phone");
                                    final String plat = jsonObject.getString("plat");
                                    final String photoProfile = jsonObject.getString("profilephoto");
                                    if (roles.equals("2")) {
                                        driver.setFullname(fullname);
                                        driver.setPhone(phone);
                                        driver.setPlat(plat);
                                        driver.setProfilephoto(photoProfile);
                                        driver.set_id(_id);
                                        listDrivers.add(driver);
                                        recycleDriver.setAdapter(recycleViewAdapter);
                                    }
                                }
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
}
