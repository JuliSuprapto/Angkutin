package com.example.angkut_v01.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.R;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class RegisterDriver extends AppCompatActivity {

    Button doRegist, bLogin;
    TextInputEditText bNik, bFullname, bUsername, bPassword, bPhone, bPlat;
    ProgressDialog progressDialog;

    private RequestQueue mRequestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register_driver);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mRequestQueue = Volley.newRequestQueue(this);

        bNik = (TextInputEditText) findViewById(R.id.nik);
        bFullname = (TextInputEditText) findViewById(R.id.fullname);
        bUsername = (TextInputEditText) findViewById(R.id.username);
        bPassword = (TextInputEditText) findViewById(R.id.password);
        bPhone = (TextInputEditText) findViewById(R.id.phone);
        bPlat = (TextInputEditText) findViewById(R.id.plat);
        bLogin = (Button) findViewById(R.id.back_login);
        doRegist = (Button) findViewById(R.id.do_regist);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        bLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(RegisterDriver.this, Login.class));
                Animatoo.animateSlideDown(RegisterDriver.this);
            }
        });
        doRegist.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sNik = bNik.getText().toString();
                String sFullname = bFullname.getText().toString();
                String sUsername = bUsername.getText().toString();
                String sPassword = bPassword.getText().toString();
                String sPhone = bPhone.getText().toString();
                String sEmail = null;
                String sAddress = null;
                String sProfilePhoto = null;
                String sStatus = "on";
                String sPlat = bPlat.getText().toString();

                if (sNik.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "NIK tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (sFullname.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "Nama lengkap tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sUsername.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "Username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPassword.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "Password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPhone.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "Nomor telepon tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                }else if (sPlat.isEmpty()) {
                    StyleableToast.makeText(RegisterDriver.this, "Email tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else {
                    registDriver(sNik, sFullname, sUsername, sPassword, sPhone, sEmail, sAddress, sProfilePhoto, sPlat, sStatus);
                }
            }
        });
    }

    public void registDriver(String nik, String fullname, String username, String password, String phone, String email, String address, String profilephoto, String plat, String status) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("nik", nik);
        params.put("fullname", fullname);
        params.put("username", username);
        params.put("password", password);
        params.put("phone", phone);
        params.put("email", email);
        params.put("address", address);
        params.put("profilephoto", profilephoto);
        params.put("role", "2");
        params.put("plat", plat);
        params.put("status", "on");
        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.registerDriver, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("msg");
                            boolean statusMsg = response.getBoolean("error");

                            if (statusMsg == false) {
                                StyleableToast.makeText(RegisterDriver.this, strMsg, R.style.toastStyleSuccess).show();
                                startActivity(new Intent(RegisterDriver.this, Login.class));
                                Animatoo.animateSlideDown(RegisterDriver.this);
                            } else {
                                StyleableToast.makeText(RegisterDriver.this, strMsg, R.style.toastStyleWarning).show();
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
    public void onBackPressed() {
        startActivity(new Intent(RegisterDriver.this, Login.class));
        Animatoo.animateSlideDown(RegisterDriver.this);
    }
}
