package com.example.angkut_v01.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.VolleyLog;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.angkut_v01.R;
import com.example.angkut_v01.driver.CompleteDriver;
import com.example.angkut_v01.driver.MainDriver;
import com.example.angkut_v01.model.ModelPesanan;
import com.example.angkut_v01.server.BaseURL;
import com.example.angkut_v01.user.Confirm;
import com.example.angkut_v01.user.MainUser;
import com.muddzdev.styleabletoastlibrary.StyleableToast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class AdapterPesanan extends RecyclerView.Adapter<AdapterPesanan.ViewHolder> {

    private Context context;
    private List<ModelPesanan> pesananList;
    private RequestQueue mRequestQueue;

    public AdapterPesanan(Context context, List<ModelPesanan> pesananList) {
        this.context = context;
        this.pesananList = pesananList;
    }


    @NonNull
    @Override
    public AdapterPesanan.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.item_pesanan, null);
        mRequestQueue = Volley.newRequestQueue(context);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final AdapterPesanan.ViewHolder holder, int position) {
        ModelPesanan pesanan = pesananList.get(position);

        holder.fullname.setText(pesanan.getFullnameUser());
        holder.phone.setText(pesanan.getPhoneUser());

        holder._idPesanan = pesanan.get_idPesanan();
        holder.statusUser = pesanan.getStatus();

        if (holder.statusUser.equals("1")){
            holder.tolakUser.setVisibility(View.VISIBLE);
            holder.konfirmasiUser.setVisibility(View.VISIBLE);
        }else if(holder.statusUser.equals("2")){
            holder.konfirmasiUser.setVisibility(View.GONE);
            holder.tolakUser.setText("Pesanan Selesai");
            holder.tolakUser.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, 125));
        }

        holder.konfirmasiUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                updateDataPesanan(holder._idPesanan);
            }
        });

        holder.tolakUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                hapusDataPesanan(holder._idPesanan);
            }
        });

    }

    @Override
    public int getItemCount() {
        return pesananList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView fullname, phone;
        Button konfirmasiUser, tolakUser;
        String _idPesanan, statusUser;

        public ViewHolder(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullnameUsers);
            phone = itemView.findViewById(R.id.phoneUsers);
            konfirmasiUser = itemView.findViewById(R.id.terima);
            tolakUser = itemView.findViewById(R.id.tolak);
        }
    }

    private void hapusDataPesanan(final String _idPesanan) {
        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.DELETE, BaseURL.deletePesanan + _idPesanan, null,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            boolean statusMsg = response.getBoolean("error");
                            if (statusMsg == false) {
                                context.startActivity(new Intent(context, MainDriver.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
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

    private void updateDataPesanan(final String idPesanan) {
        HashMap<String, String> params = new HashMap<String, String>();
        params.put("status", "2");

        final JsonObjectRequest req = new JsonObjectRequest(Request.Method.PUT, BaseURL.updatePesanan + idPesanan, new JSONObject(params),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println("response = " + response);
                        try {
                            JSONObject jsonObject = new JSONObject(response.toString());
                            System.out.println("res = " + jsonObject.toString());
                            String strMsg = jsonObject.getString("msg");
                            boolean status = jsonObject.getBoolean("error");
                            if (status == false) {
                                context.startActivity(new Intent(context, MainDriver.class).setFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        VolleyLog.e("Error: ", error.getMessage());
//                        StyleableToast.makeText(context, error.getMessage(), R.style.toastStyleWarning).show();
                    }
                });
        mRequestQueue.add(req);
    }

}
