package com.example.angkut_v01.user;

import android.content.Intent;
import android.net.Uri;
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
public class AccountFragmentUser extends Fragment {

    TextView dFullname, dNik, dPhone, dEmail, dAddress, dLengkapi, dLihat;
    String dataAddress, dataPhoto;
    LinearLayout bLogout;
    LottieAnimationView defaultPhoto;
    CircleImageView profilePhotoUser;
    ImageView backgroundProfile;
    ModelAccess profile ;
    LinearLayout customer;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_account_user, container, false);

        defaultPhoto = (LottieAnimationView)v.findViewById(R.id.images);
        profilePhotoUser = (CircleImageView)v.findViewById(R.id.photoprofileuser);
        backgroundProfile = (ImageView)v.findViewById(R.id.view1);

        dFullname = (TextView)v.findViewById(R.id.dfullname);
        dNik = (TextView)v.findViewById(R.id.dnik);
        dPhone = (TextView)v.findViewById(R.id.phone);
        dAddress = (TextView)v.findViewById(R.id.address);
        dEmail = (TextView)v.findViewById(R.id.email);
        dLengkapi = (TextView)v.findViewById(R.id.lengkapiDataUser);
        dLihat = (TextView)v.findViewById(R.id.showData);

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

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        dFullname.setText(profile.getFullname());
        dNik.setText(profile.getNik());
        dPhone.setText(profile.getPhone());
        dAddress.setText(profile.getAddress());
        dEmail.setText(profile.getEmail());

        String dProfilePhoto = profile.getProfilephoto();
        String dAddress = profile.getAddress();

        if (dProfilePhoto == null || dAddress == null){
            backgroundProfile.setVisibility(View.VISIBLE);
            profilePhotoUser.setVisibility(View.GONE);
            dLengkapi.setVisibility(View.VISIBLE);
            dLihat.setVisibility(View.GONE);
        } else{
            dLengkapi.setVisibility(View.GONE);
            dLihat.setVisibility(View.VISIBLE);
            defaultPhoto.cancelAnimation();
            profilePhotoUser.setVisibility(View.VISIBLE);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(profilePhotoUser);
            Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + dProfilePhoto).into(backgroundProfile);
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
                startActivity(new Intent(getActivity(), CompleteUser.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        dLihat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getActivity(), UpdateUser.class));
                Animatoo.animateSlideUp(getActivity());
            }
        });

        return v;
    }
}
