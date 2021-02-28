package com.example.angkut_v01.driver;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airbnb.lottie.LottieAnimationView;
import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.example.angkut_v01.R;
import com.example.angkut_v01.access.Login;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

/**
 * A simple {@link Fragment} subclass.
 * create an instance of this fragment.
 */
public class AccountFragmentDriver extends Fragment {

    TextView dFullname, dNik, dPhone, dAddress, dPlat, dLengkapi, dLihat;

    LinearLayout bLogout;
    LottieAnimationView defaultPhoto;
    CircleImageView profilePhotoUser;
    ImageView backgroundProfile;
    ModelAccess profile ;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_driver, container, false);

        defaultPhoto = (LottieAnimationView)v.findViewById(R.id.images);
        profilePhotoUser = (CircleImageView)v.findViewById(R.id.photoprofileuser);
        backgroundProfile = (ImageView)v.findViewById(R.id.view1);

        dFullname = (TextView)v.findViewById(R.id.dfullname);
        dNik = (TextView)v.findViewById(R.id.dnik);
        dPhone = (TextView)v.findViewById(R.id.phone);
        dAddress = (TextView)v.findViewById(R.id.address);
        dPlat = (TextView)v.findViewById(R.id.plat);

        dLengkapi = (TextView)v.findViewById(R.id.lengkapiDriver);
        dLihat = (TextView)v.findViewById(R.id.showDataDriver);

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        dFullname.setText(profile.getFullname());
        dNik.setText(profile.getNik());
        dPhone.setText(profile.getPhone());
        dAddress.setText(profile.getAddress());
        dPlat.setText(profile.getPlat());


        String dProfilePhoto = profile.getProfilephoto();
        String dAddress = profile.getAddress();

        if (dProfilePhoto == null || dAddress == null){
            backgroundProfile.setVisibility(View.VISIBLE);
            profilePhotoUser.setVisibility(View.GONE);
            dLengkapi.setVisibility(View.VISIBLE);
            dLihat.setVisibility(View.GONE);
        } else{
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
}
