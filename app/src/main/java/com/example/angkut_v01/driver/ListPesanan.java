package com.example.angkut_v01.driver;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.angkut_v01.R;
import com.example.angkut_v01.adapter.AdapterPesanan;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelPesanan;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class ListPesanan extends AppCompatActivity {

    RecyclerView recyclePesanan;
    RecyclerView.Adapter recycleViewAdapter;
    List<ModelPesanan> listPesanan;
    private RequestQueue mRequestQueue;
    ProgressDialog progressDialog;
    TextView nameUser, jumlahPesananD;
    ModelAccess modelAccess;
    String _idDriver, _idUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_pesanan);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        mRequestQueue = Volley.newRequestQueue(getApplicationContext());
        progressDialog = new ProgressDialog(getApplicationContext());
        progressDialog.setCancelable(false);

        recyclePesanan = (RecyclerView) findViewById(R.id.listPesanan);
        nameUser = (TextView) findViewById(R.id.namaUser);
        recyclePesanan.setHasFixedSize(true);
        recyclePesanan.setLayoutManager(new LinearLayoutManager(this));
        listPesanan = new ArrayList<>();
        recycleViewAdapter = new AdapterPesanan(getApplicationContext(), listPesanan);

        modelAccess = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        nameUser.setText(modelAccess.getFullname());
        _idDriver = modelAccess.get_id();

        getAllPesanan(_idDriver);

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
