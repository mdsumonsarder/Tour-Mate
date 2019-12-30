package com.dualbrotech.tourmate.Adapters;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.dualbrotech.tourmate.R;
import com.dualbrotech.tourmate.UI.NearbyPlacesActivity;
import com.dualbrotech.tourmate.WebResponses.PlaceResponse;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Arif Rahman on 1/17/2018.
 */

public class PlaceAdapter extends BaseAdapter {
    private Context context;
    //private PlaceResponse placeResponse;
    private List<PlaceResponse.Result> placeResponses;

    public PlaceAdapter(Context context, List<PlaceResponse.Result> placeResponses) {
        this.context = context;
        this.placeResponses = placeResponses;
    }

    @Override
    public int getCount() {
        return placeResponses.size();
    }

    @Override
    public Object getItem(int i) {
        return placeResponses.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        view = inflater.inflate(R.layout.nearby_places,parent,false);
        ImageView imageView = view.findViewById(R.id.resturentIV);
        TextView textView = view.findViewById(R.id.returentTV);

        textView.setText(placeResponses.get(position).getName());

        try{
            String ref = placeResponses.get(position)
                    .getPhotos().get(0).getPhotoReference();
            //CnRtAAAATLZNl354RwP_9UKbQ_5Psy40texXePv4oAlgP4qNEkdIrkyse7rPXYGd9D_Uj1rVsQdWT4oRz4QrYAJNpFX7rzqqMlZw2h2E2y5IKMUZ7ouD_SlcHxYq1yL4KbKUv3qtWgTK0A6QbGh87GB3sscrHRIQiG2RrmU_jF4tENr9wGS_YxoUSSDrYjWmrNfeEHSGSc3FyhNLlBU
            String imageReference = NearbyPlacesActivity.BASE_URL+"photo?maxwidth=400&maxheight=400&photoreference="+ref+"&key=AIzaSyAXG0k0FK5sUbrIF6MHGGDCaop66Up1oDs";
            Uri uri = Uri.parse(imageReference);
            Picasso.with(context).load(uri).into(imageView);

        }catch (Exception e){

        }

        return view;
    }
}
