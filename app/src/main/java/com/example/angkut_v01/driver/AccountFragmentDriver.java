package com.example.angkut_v01.driver;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkResponse;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.R;
import com.example.angkut_v01.access.Login;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.server.VolleyMultipart;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.example.angkut_v01.utils.Utils;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.muddzdev.styleabletoastlibrary.StyleableToast;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccountFragmentDriver extends Fragment {

    TextView dFullname, dNik, dPhone, dAddress, dPlat, dLengkapi, dLihat;

    Switch switchUser;
    LinearLayout bLogout;
    LottieAnimationView defaultPhoto;
    CircleImageView profilePhotoUser;
    ImageView backgroundProfile;
    ModelAccess profile;
    LinearLayout customer;
    ProgressDialog progressDialog;

    FirebaseDatabase database;
    private DatabaseReference reference;
    private RequestQueue mRequestQueue;
    String statusOn, _idDriver;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_driver, container, false);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        System.out.println("DATA ACC = " + profile.getStatus());

        progressDialog = new ProgressDialog(getActivity());
        progressDialog.setCancelable(false);
        mRequestQueue = Volley.newRequestQueue(getActivity());

        defaultPhoto = (LottieAnimationView) v.findViewById(R.id.images);
        profilePhotoUser = (CircleImageView) v.findViewById(R.id.photoprofileuser);
        backgroundProfile = (ImageView) v.findViewById(R.id.view1);

        dFullname = (TextView) v.findViewById(R.id.dfullname);
        dNik = (TextView) v.findViewById(R.id.dnik);
        dPhone = (TextView) v.findViewById(R.id.phone);
        dAddress = (TextView) v.findViewById(R.id.address);
        dPlat = (TextView) v.findViewById(R.id.plat);
        switchUser = (Switch) v.findViewById(R.id.switch_btn);

        _idDriver = profile.get_id();
        database = FirebaseDatabase.getInstance();
        reference = database.getInstance().getReference("location").child(_idDriver);

        String statusDriver = profile.getStatus();

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

        final String phoneD = "082279058667";
        final String str1 = phoneD.replaceFirst("0", "+62");

        customer = (LinearLayout) v.findViewById(R.id.customerCare);
        customer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String url = "https://api.whatsapp.com/send?phone=" + str1;
                Intent i = new Intent(Intent.ACTION_VIEW);
                i.setPackage("com.whatsapp");
                i.setData(Uri.parse(url));
                startActivity(i);
            }
        });

        dLengkapi = (TextView) v.findViewById(R.id.lengkapiDriver);
        dLihat = (TextView) v.findViewById(R.id.showDataDriver);

        dFullname.setText(profile.getFullname());
        dNik.setText(profile.getNik());
        dPhone.setText(profile.getPhone());
        dAddress.setText(profile.getAddress());
        dPlat.setText(profile.getPlat());

        String dProfilePhoto = profile.getProfilephoto();
        String dAddress = profile.getAddress();

        if (dProfilePhoto == null || dAddress == null) {
            backgroundProfile.setVisibility(View.VISIBLE);
            profilePhotoUser.setVisibility(View.GONE);
            dLengkapi.setVisibility(View.VISIBLE);
            dLihat.setVisibility(View.GONE);
        } else {
            defaultPhoto.cancelAnimation();
            profilePhotoUser.setVisibility(View.VISIBLE);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(profilePhotoUser);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(backgroundProfile);
            dLengkapi.setVisibility(View.GONE);
            dLihat.setVisibility(View.VISIBLE);
        }

        bLogout = (LinearLayout) v.findViewById(R.id.logout);

        bLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                App.getPref().clear();
                startActivity(new Intent(getActivity(), Login.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        dLengkapi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), CompleteDriver.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        dLihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateDriver.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
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
                                System.out.println("DATA SEMUANYA = " + profile.getStatus());

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

}
