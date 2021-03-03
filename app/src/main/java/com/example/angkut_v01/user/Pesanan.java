package com.example.angkut_v01.user;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;

import com.android.volley.Request;
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
import com.google.gson.JsonObject;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class Pesanan extends AppCompatActivity {

    ModelAccess profile;
    String key, _idDriver, _idUser;
    private RequestQueue mRequestQueue;
    LinearLayout checka, checkb, checkc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pesanan);

        checka = (LinearLayout) findViewById(R.id.check_a);
        checkb = (LinearLayout) findViewById(R.id.check_b);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        _idUser = profile.get_id();

        mRequestQueue = Volley.newRequestQueue(this);

        getPesanan(_idUser);

    }

    private void getPesanan(final String _idUser) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.GET, BaseURL.getPesananUser + _idUser, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject jObj = new JSONObject(response.toString());
                            System.out.println("response = " + jObj);
                            String strMsg = jObj.getString("msg");
                            boolean statusMsg = jObj.getBoolean("error");
                            if (statusMsg == false) {
                                JSONObject data = jObj.getJSONObject("data");
                                String status = data.getString("status");
                                System.out.println("DATA JSON  = " + status);
                                if (status.equals("1")){
                                    checka.setVisibility(View.VISIBLE);
                                    checkb.setVisibility(View.GONE);
                                }else if (status.equals("2")){
                                    checka.setVisibility(View.GONE);
                                    checkb.setVisibility(View.VISIBLE);
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
}
