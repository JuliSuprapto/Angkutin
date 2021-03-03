package com.example.angkut_v01.user;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.R;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnSuccessListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Confirm extends AppCompatActivity {

    TextView namaUser, nameDriverD, phoneDriverD, platDriverD, jarakDriverD;
    CircleImageView fotoUser, fotoDriver;
    ModelAccess modelUser;
    Button chatDriver, pesanDriver;
    ProgressDialog progressDialog;

    private RequestQueue mRequestQueue;
    double latitudeUser, longitudeUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        mRequestQueue = Volley.newRequestQueue(this);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        modelUser = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        namaUser = (TextView) findViewById(R.id.namaUser);
        fotoUser = (CircleImageView) findViewById(R.id.photoprofileuser);
        fotoDriver = (CircleImageView) findViewById(R.id.profileDriverList);
        nameDriverD = (TextView) findViewById(R.id.fullnameList);
        phoneDriverD = (TextView) findViewById(R.id.phoneDriverList);
        platDriverD = (TextView) findViewById(R.id.platDriverList);
        jarakDriverD = (TextView) findViewById(R.id.jarakDriverList);

        chatDriver = (Button) findViewById(R.id.chat);
        pesanDriver = (Button) findViewById(R.id.pesan);

        namaUser.setText(modelUser.getFullname());

        String dProfilePhoto = modelUser.getProfilephoto();
        final String _idUser = modelUser.get_id();
        final String fullnameUser = modelUser.getFullname();
        final String phoneUser = modelUser.getPhone();

        if (dProfilePhoto == null) {
            fotoUser.setImageResource(R.drawable.ic_box);
        } else {
            Picasso.get().load(BaseURL.baseUrl + dProfilePhoto).into(fotoUser);
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null) {
            final String _idDriver = extra.getString("idUser");
            String nameDriver = extra.getString("fullname");
            final String phoneDriver = extra.getString("phone");
            String platDriver = extra.getString("plat");
            float jarakDriver = extra.getFloat("jarak");
            String photoDriver = extra.getString("profilephoto");

            nameDriverD.setText(nameDriver);
            phoneDriverD.setText(phoneDriver);
            platDriverD.setText(platDriver);
            jarakDriverD.setText(String.valueOf(jarakDriver) + "Km");

            if (photoDriver.equals("default.png")) {
                fotoDriver.setImageResource(R.drawable.icon_default);
            } else {
                Picasso.get().load(BaseURL.baseUrl + photoDriver).into(fotoDriver);
            }

            final String phoneD = phoneDriver;
            final String str1 = phoneD.replaceFirst("0", "+62");
            System.out.println(photoDriver);

            chatDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    String url = "https://api.whatsapp.com/send?phone=" + str1;
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setPackage("com.whatsapp");
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
            });

            pesanDriver.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    FusedLocationProviderClient clients = LocationServices.getFusedLocationProviderClient(getApplicationContext());
                    clients.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
                        @Override
                        public void onSuccess(Location location) {
                            if (location != null) {
                                latitudeUser = location.getLatitude();
                                longitudeUser = location.getLongitude();

                                final String latUser = String.valueOf(latitudeUser);
                                final String longUser = String.valueOf(longitudeUser);
                                functionPesan(_idUser, _idDriver, fullnameUser, phoneUser, latUser, longUser);

                                Log.d("LAT", latUser);
                            }
                        }
                    });


                }
            });
        }
    }

    private void functionPesan(String _idUser, String _idDriver, String fullnameUser, String phoneUser, String latUser, String longUser) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("idUser", _idUser);
        params.put("idDriver", _idDriver);
        params.put("fullname", fullnameUser);
        params.put("phone", phoneUser);
        params.put("latitudeUser", latUser);
        params.put("longitudeUser", longUser);
        params.put("status", "1");

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.addPesanan, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("msg");
                            boolean statusMsg = response.getBoolean("error");

                            if (statusMsg == false) {
                                StyleableToast.makeText(Confirm.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(Confirm.this, Pesanan.class));
                                Animatoo.animateSlideDown(Confirm.this);
                            } else {
                                StyleableToast.makeText(Confirm.this, strMsg, R.style.toastStyleWarning).show();
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
