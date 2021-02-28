package com.example.angkut_v01;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.squareup.picasso.Picasso;

import de.hdodenhof.circleimageview.CircleImageView;

public class Confirm extends AppCompatActivity {

    TextView namaUser, nameDriverD, phoneDriverD, platDriverD, jarakDriverD;
    CircleImageView fotoUser, fotoDriver;
    ModelAccess modelUser;
    Button chatDriver, pesanDriver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_confirm);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        modelUser = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

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

        if (dProfilePhoto == null) {
            fotoUser.setImageResource(R.drawable.ic_box);
        } else {
            Picasso.get().load(BaseURL.baseUrl + dProfilePhoto).into(fotoUser);
        }

        Bundle extra = getIntent().getExtras();
        if (extra != null){
            String _idDriver = extra.getString("idUser");
            String nameDriver = extra.getString("fullname");
            final String phoneDriver = extra.getString("phone");
            String platDriver = extra.getString("plat");
            float jarakDriver = extra.getFloat("jarak");
            String photoDriver = extra.getString("profilephoto");

            nameDriverD.setText(nameDriver);
            phoneDriverD.setText(phoneDriver);
            platDriverD.setText(platDriver);
            jarakDriverD.setText(String.valueOf(jarakDriver) + "Km");

            if (photoDriver.equals("default.png")){
                fotoDriver.setImageResource(R.drawable.icon_default);
            }else {
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


        }
    }
}
