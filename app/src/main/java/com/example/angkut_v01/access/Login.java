package com.example.angkut_v01.access;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.driver.MainDriver;
import com.example.angkut_v01.user.MainUser;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.utils.Prefs;
import com.example.angkut_v01.R;
import com.example.angkut_v01.utils.Utils;
import com.google.android.material.textfield.TextInputEditText;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class Login extends AppCompatActivity {

    Button doLogin;
    TextInputEditText bUsername, bPassword;
    ProgressDialog progressDialog;
    LinearLayout bRegistUser, bRegistDriver;

    private RequestQueue mRequestQueue;
    ModelAccess profile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        bRegistUser = (LinearLayout) findViewById(R.id.regist_pengguna);
        bRegistDriver = (LinearLayout) findViewById(R.id.regist_driver);
        doLogin = (Button) findViewById(R.id.do_login);

        bUsername = (TextInputEditText) findViewById(R.id.username);
        bPassword = (TextInputEditText) findViewById(R.id.password);

        getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN);

        mRequestQueue = Volley.newRequestQueue(this);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        if(Utils.isLoggedIn()){
            int dRoll = Integer.parseInt(profile.getRole());
            if (dRoll == 1){
                Intent i = new Intent(this , MainUser.class);
                startActivity(i);
                finish();
            }else if(dRoll == 2){
                Intent i = new Intent(this , MainDriver.class);
                startActivity(i);
                finish();
            }
        }

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);

        bRegistUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterUser.class));
                Animatoo.animateSlideUp(Login.this);
            }
        });

        bRegistDriver.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Login.this, RegisterDriver.class));
                Animatoo.animateSlideUp(Login.this);
            }
        });

        doLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sUsername = bUsername.getText().toString();
                String sPassword = bPassword.getText().toString();

                if (sUsername.isEmpty()) {
                    StyleableToast.makeText(Login.this, "Username tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else if (sPassword.isEmpty()) {
                    StyleableToast.makeText(Login.this, "Password tidak boleh di kosongkan...", R.style.toastStyleWarning).show();
                } else {
                    loginAccess(sUsername, sPassword);
                }
            }
        });
    }

    public void loginAccess(String username, String password) {

        HashMap<String, String> params = new HashMap<String, String>();

        params.put("username", username);
        params.put("password", password);

        progressDialog.setTitle("Mohon tunggu sebentar...");
        showDialog();

        final JsonObjectRequest req = new JsonObjectRequest(BaseURL.login, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        hideDialog();
                        try {
                            String strMsg = response.getString("msg");
                            boolean statusMsg = response.getBoolean("error");
                            if (statusMsg == false) {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleSuccess).show();

                                JSONObject user = response.getJSONObject("data");
                                String tRole = user.getString("role");
                                App.getPref().put(Prefs.PREF_IS_LOGEDIN, true);
                                Utils.storeProfile(user.toString());

                                if (tRole.equals("1")) {
                                    startActivity(new Intent(Login.this, MainUser.class));
                                    Animatoo.animateSlideDown(Login.this);
                                } else {
                                    startActivity(new Intent(Login.this, MainDriver.class));
                                    Animatoo.animateSlideDown(Login.this);
                                }
                            } else {
                                StyleableToast.makeText(Login.this, strMsg, R.style.toastStyleWarning).show();
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
        startActivity(new Intent(Login.this, Login.class));
        Animatoo.animateZoom(Login.this);
    }
}
