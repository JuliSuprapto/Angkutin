package com.example.angkut_v01.adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.angkut_v01.Confirm;
import com.example.angkut_v01.R;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.server.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class RecycleViewAdapter extends RecyclerView.Adapter<RecycleViewAdapter.ViewHolder> {

    private Context context;
    private List<ModelDriver> driverList;

    public RecycleViewAdapter(Context context, List<ModelDriver> driverList) {
        this.context = context;
        this.driverList = driverList;
    }

    @NonNull
    @Override
    public RecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View v = inflater.inflate(R.layout.list_item, null);
        return new ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull final RecycleViewAdapter.ViewHolder holder, int position) {
        final ModelDriver driver = driverList.get(position);

        Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + driver.getProfilephoto()).into(holder.photoProfile);

        holder.fullname.setText(driver.getFullname());
        holder.phone.setText(driver.getPhone());
        holder.plat.setText(driver.getPlat());
        holder.jarak.setText(String.valueOf(driver.getJarak()) + " Km");
        holder._id = (driver.get_id());

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.startActivity(new Intent(context, Confirm.class).putExtra("idUser", driver.get_id()).putExtra("fullname", driver.getFullname()).putExtra("phone", driver.getPhone()).putExtra("plat", driver.getPlat()).putExtra("jarak", driver.getJarak()).putExtra("profilephoto", driver.getProfilephoto()));
            }
        });
    }

    @Override
    public int getItemCount() {
        return driverList.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        TextView fullname, phone, plat, jarak;
        CircleImageView photoProfile;
        String _id;

        public ViewHolder(View itemView) {
            super(itemView);

            fullname = itemView.findViewById(R.id.fullnameList);
            phone = itemView.findViewById(R.id.phoneDriverList);
            plat = itemView.findViewById(R.id.platDriverList);
            photoProfile = itemView.findViewById(R.id.profileDriverList);
            jarak = itemView.findViewById(R.id.jarakDriverList);

        }
    }

    public interface OnListItemClick {
        void onClick(View view, int position);
    }
}
