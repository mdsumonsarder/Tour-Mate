package com.dualbrotech.tourmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.dualbrotech.tourmate.Models.EventImage;
import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.UI.GalleryActivity;
import com.squareup.picasso.Picasso;

import java.util.List;

/**
 * Created by Arif Rahman on 2/13/2018.
 */

public class ImageAdapter extends BaseAdapter {

    private Context context;
    //private PlaceResponse placeResponse;
    private List<EventImage> eventImages;

    public ImageAdapter(Context context, List<EventImage> eventImages) {
        this.context = context;
        this.eventImages = eventImages;
    }

    @Override
    public int getCount() {
        return eventImages.size();
    }

    @Override
    public Object getItem(int i) {
        return eventImages.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.single_image_item,viewGroup,false);
        ImageView imageView = view.findViewById(R.id.iv_event_img);

        String url = GalleryActivity.BASE_URL+"images%2F"+eventImages.get(i).getImageName()+"?alt=media&token=3cfc62e3-2016-4a0d-804d-59536eecb433";
        Uri uri = Uri.parse(url);
        Picasso.with(context).load(uri).into(imageView);

        return view;
    }
}
