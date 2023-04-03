package com.example.weatherapp;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class CityAdapter extends RecyclerView.Adapter<CityAdapter.CityHolder> {

    private List<City> cities = new ArrayList<>();
    CardView card;
    onCityListener onCityListener;


    public CityAdapter(onCityListener onCityListener){this.onCityListener=onCityListener;}


    @NonNull
    @Override
    public CityHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.city_item, parent, false);
        return new CityHolder(itemView, onCityListener);
    }

    @Override
    public void onBindViewHolder(@NonNull CityHolder holder, int position) {
        City currentCity = cities.get(position);

        card=holder.itemView.findViewById(R.id.card);
        if(currentCity.getTemperature()<0.0){
            card.setCardBackgroundColor(Color.parseColor("#68BFED"));
        }else if(currentCity.getTemperature()>=0.0 && currentCity.getTemperature()<=15.0){
            card.setCardBackgroundColor(Color.parseColor("#8BF786"));
        }else if(currentCity.getTemperature()>15.0 && currentCity.getTemperature()<=27.0) {
            card.setCardBackgroundColor(Color.parseColor("#DCE685"));
        }else if(currentCity.getTemperature()>27.0 && currentCity.getTemperature()<=35.0){
            card.setCardBackgroundColor(Color.parseColor("#E69640"));
        }else if(currentCity.getTemperature()>35.0 && currentCity.getTemperature()<=46.0){
            card.setCardBackgroundColor(Color.RED);
        }else{
            card.setCardBackgroundColor(Color.parseColor("#800000"));
        }
        holder.text1.setText(currentCity.getCityName());
        holder.text2.setText(currentCity.getCountry());
        holder.text3.setText(currentCity.getTemperature()+"°C");
    }

    @Override
    public int getItemCount() {
        return cities.size();
    }

    public void setCities(List<City> cities){
        this.cities=cities;
        notifyDataSetChanged();
    }

    public City getCity(int position){
        return cities.get(position);
    }

    class CityHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private TextView text1;
        private TextView text2;
        private TextView text3;
        onCityListener onCityListener;


        public CityHolder(@NonNull View itemView, onCityListener onCityListener) {
            super(itemView);
            text1 = itemView.findViewById(R.id.ctName);
            text2 = itemView.findViewById(R.id.coName);
            text3 = itemView.findViewById(R.id.tvtemp);
            this.onCityListener=onCityListener;

            itemView.setOnClickListener(this);
        }


        @Override
        public void onClick(View v) {
            onCityListener.onCityClick(getAdapterPosition());

        }
    }

    public interface onCityListener{
        void onCityClick(int position);
    }
}
