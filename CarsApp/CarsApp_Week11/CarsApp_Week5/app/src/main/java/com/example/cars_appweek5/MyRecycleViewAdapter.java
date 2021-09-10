package com.example.cars_appweek5;

import android.app.Application;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.cars_appweek5.provider.Car;
import com.example.cars_appweek5.provider.CarViewModel;

import java.util.ArrayList;
import java.util.List;

public class MyRecycleViewAdapter extends RecyclerView.Adapter<MyRecycleViewAdapter.ViewHolder>{

    List<Car> data = new ArrayList<>();

//    ArrayList<String> data;

    public MyRecycleViewAdapter(){

    }

    public void setData(List<Car> data) {
        this.data = data;
    }

    @NonNull
    @Override
    public MyRecycleViewAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_view, parent, false);
        ViewHolder viewHolder = new ViewHolder(v);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull MyRecycleViewAdapter.ViewHolder holder, int position) {
        holder.makerText.setText("Maker: " + data.get(position).getMaker()+"");
        holder.modelText.setText("Model: " + data.get(position).getModel()+"");
        holder.colorText.setText("Color: " + data.get(position).getColor()+"");
        holder.priceText.setText("Price: " + data.get(position).getPrice()+"");
        holder.yearText.setText("Year: " + data.get(position).getYear()+"");
        holder.seatsText.setText("Seats: " + data.get(position).getSeats()+"");

        final int fposition = position;

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String model = data.get(fposition).getModel();
                MainActivity2.mCarViewModel.deleteModel(model);
            }
        });
    }

    @Override
    public int getItemCount() {
        return data.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public View itemView;
        public TextView makerText;
        public TextView modelText;
        public TextView colorText;
        public TextView priceText;
        public TextView yearText;
        public TextView seatsText;


        public ViewHolder(View itemView){
            super(itemView);
            this.itemView = itemView;
            makerText = itemView.findViewById(R.id.makerTextView);
            modelText = itemView.findViewById(R.id.modelTextView);
            colorText = itemView.findViewById(R.id.colorTextView);
            priceText = itemView.findViewById(R.id.priceTextView);
            yearText = itemView.findViewById(R.id.yearTextView);
            seatsText = itemView.findViewById(R.id.seatsTextView);
        }
    }
}
