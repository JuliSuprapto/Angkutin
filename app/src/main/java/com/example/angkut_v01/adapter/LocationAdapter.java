package com.example.angkut_v01.adapter;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.example.angkut_v01.R;
import com.example.angkut_v01.model.ModelAccess;
import com.example.angkut_v01.model.ModelDriver;
import com.example.angkut_v01.server.BaseURL;
import com.squareup.picasso.Picasso;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class LocationAdapter extends BaseAdapter {

    private Activity activity;
    private LayoutInflater inflater;
    private List<ModelAccess> item;

    public LocationAdapter(Activity activity, List<ModelAccess> item) {
        this.activity = activity;
        this.item = item;
    }

    @Override
    public int getCount() {
        return item.size();
    }

    @Override
    public Object getItem(int position) {
        return item.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (inflater == null)
            inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        if (convertView == null)
            convertView = inflater.inflate(R.layout.list_item, null);

        TextView fullnameDriver = (TextView) convertView.findViewById(R.id.fullnameList);
        TextView phoneDriver = (TextView) convertView.findViewById(R.id.phoneDriverList);
        TextView platDriver = (TextView) convertView.findViewById(R.id.platDriverList);
        CircleImageView profilePhotoDriver = (CircleImageView) convertView.findViewById(R.id.profileDriverList);

        fullnameDriver.setText(item.get(position).getFullname());
        phoneDriver.setText(item.get(position).getPhone());
        platDriver.setText(item.get(position).getPlat());
        Picasso.get().load(BaseURL.baseUrl + "profilephoto/" + item.get(position).getProfilephoto())
                .resize(40, 40)
                .centerCrop()
                .into(profilePhotoDriver);
        return convertView;
    }
}
