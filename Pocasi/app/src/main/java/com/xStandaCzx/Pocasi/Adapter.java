package com.xStandaCzx.Pocasi;
import java.util.List;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class Adapter extends ArrayAdapter<Lokace>{

    Context context;
    int layoutResourceId;   
    List<Lokace> data = null;
    
   
    public Adapter(Context context, int layoutResourceId, List<Lokace> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.data = data;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        EntryHolder holder = null;
       
        if(row == null)
        {
        	LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );      	
            row = inflater.inflate(layoutResourceId, parent, false);

            holder = new EntryHolder();
            holder.cityname = (TextView)row.findViewById(R.id.textView);
            holder.temp = (TextView)row.findViewById(R.id.textView3);
            holder.weather = (TextView)row.findViewById(R.id.textView4);


            holder.pic = (ImageView) row.findViewById(R.id.imageView);
            
            row.setTag(holder);
        }
        else
        {
            holder = (EntryHolder)row.getTag();
        }


        Lokace entry = data.get(position);
        holder.cityname.setText(entry.cityname);
        holder.temp.setText(entry.temp+"°C");
        holder.weather.setText(entry.weather);

        String iconUrl = "http://openweathermap.org/img/w/" + entry.pic + ".png";
        Picasso.get().load(iconUrl).into(holder.pic);

        //String flags="http://openweathermap.org/img/w/";
       // entry.picx= context.getResources().getIdentifier(flags, "drawable", context.getPackageName());
        
        return row;
    }
   
    static class EntryHolder
    {
        TextView cityname;
        TextView time;
        TextView windspeed;
        TextView weather;
        TextView temp;
        TextView pressure;
        ImageView pic;
    }
}
