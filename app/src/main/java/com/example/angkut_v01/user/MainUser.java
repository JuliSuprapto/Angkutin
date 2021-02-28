package com.example.angkut_v01.user;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.example.angkut_v01.R;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.utils.App;
import com.example.angkut_v01.utils.GsonHelper;
import com.example.angkut_v01.utils.Prefs;
import com.example.angkut_v01.utils.Utils;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

public class MainUser extends AppCompatActivity {

    private static final String TAG = MainUser.class.getSimpleName();
    ChipNavigationBar bottomNav;
    FragmentManager fragmentManager;
    ModelAccess profile;
    boolean BackPress = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_user);

        bottomNav = findViewById(R.id.bottom_nav);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
            Window w = getWindow();
            w.setFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS, WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
        }

        profile = (ModelAccess) GsonHelper.parseGson(
                App.getPref().getString(Prefs.PREF_STORE_PROFILE, ""),
                new ModelAccess()
        );

        if (!Utils.isLoggedIn()) {
            bottomNav.setItemSelected(R.id.account, true);
            fragmentManager = getSupportFragmentManager();
            AccountFragmentUser accountFragment = new AccountFragmentUser();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, accountFragment).commit();
        } else {
            if (savedInstanceState == null) {
                bottomNav.setItemSelected(R.id.home, true);
                fragmentManager = getSupportFragmentManager();
                HomeFragmentUser homeFragment = new HomeFragmentUser();
                fragmentManager.beginTransaction().replace(R.id.fragment_container, homeFragment).commit();
            }
        }

        bottomNav.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int id) {
                Fragment fragment = null;
                switch (id) {
                    case R.id.home:
                        fragment = new HomeFragmentUser();
                        break;
                    case R.id.account:
                        fragment = new AccountFragmentUser();
                        break;
                }
                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.fragment_container, fragment).commit();
                } else {
                    Log.e(TAG, "Error creating fragment");
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        if (BackPress) {
            super.onBackPressed();
            return;
        }
        this.BackPress = true;
        StyleableToast.makeText(this, "Tekan sekali lagi untuk keluar...", R.style.toastStyleDefault).show();
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                BackPress = false;
            }
        }, 2000);
    }
}
