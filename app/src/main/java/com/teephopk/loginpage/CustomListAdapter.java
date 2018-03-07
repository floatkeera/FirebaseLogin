package com.teephopk.loginpage;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;


import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Currency;
import java.util.List;
import java.util.Locale;

/**
 * Created by floatkeera on 6/21/17.
 */

public class CustomListAdapter extends ArrayAdapter<Product> {


    ArrayList<Product> products;
    Context context;
    int resource;



    public CustomListAdapter(Context context, int resource, ArrayList<Product> objects) {
        super(context, resource, objects);
        this.products = objects;
        this.context = context;
        this.resource = resource;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent){

        if(convertView == null){
            LayoutInflater layoutInflater = (LayoutInflater) getContext().getSystemService(Activity.LAYOUT_INFLATER_SERVICE);
            convertView =layoutInflater.inflate(R.layout.listview_layout, null, true);
        }

        Product product = getItem(position);
        ImageView imgview = (ImageView) convertView.findViewById(R.id.image);

        try {
            Picasso.with(context)
                    .load(product.description).centerInside()
                    .error(R.drawable.ic_action_name)
                    .placeholder(R.drawable.progress_animation)
                    .resize(300, 200)
                    .centerInside()
                    .into(imgview);
        } catch(Exception e){
            Picasso.with(context).load("https://vignette3.wikia.nocookie.net/lego/images/a/ac/No-Image-Basic.png/revision/latest?cb=20130819001030").placeholder(R.drawable.progress_animation).error(R.drawable.ic_action_name).resize(300,200).centerInside().into(imgview);
        }

        TextView txtname = (TextView) convertView.findViewById(R.id.name);
        txtname.setText(product.name);

        TextView txtprice = (TextView) convertView.findViewById(R.id.price);
        try {
            txtprice.setText(NumberFormat.getCurrencyInstance(new Locale("th", "TH")).format(Double.parseDouble(product.price)));
        } catch (Exception e){
            txtprice.setText("");
        }

        return convertView;

    }





}
