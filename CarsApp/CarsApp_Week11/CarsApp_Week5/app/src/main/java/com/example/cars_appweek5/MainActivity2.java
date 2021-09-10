package com.example.cars_appweek5;

import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

import com.example.cars_appweek5.provider.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class MainActivity2 extends AppCompatActivity {
    //ArrayList<String> myList = new ArrayList<>();
    RecyclerView recyclerView;
    RecyclerView.LayoutManager layoutManager;
    MyRecycleViewAdapter adapter;

    EditText searchModel;

    static CarViewModel mCarViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);

//        SharedPreferences sP = getSharedPreferences("f1", 0);
//        String dbStr = sP.getString("KEY_LIST", "");
//        Type type = new TypeToken<ArrayList<String>>(){}.getType();
//        Gson gson = new Gson();
//        myList = gson.fromJson(dbStr, type);

        recyclerView = findViewById(R.id.MakerModelRecycler);

        layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        adapter = new MyRecycleViewAdapter();
        recyclerView.setAdapter(adapter);

        showAllCar();

        FloatingActionButton fab = findViewById(R.id.floatingActionButton2);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        FloatingActionButton fab2 = findViewById(R.id.floatingActionButton3);
        fab2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showAllCar();
            }
        });
    }

    public void showAllCar()
    {
        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        mCarViewModel.getAllCars().observe(this, newData -> {
            adapter.setData(newData);
            adapter.notifyDataSetChanged();
        });
    }

    public void showCar(View v){
        searchModel = findViewById(R.id.mySearchModel);
        String model = searchModel.getText().toString();

        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        mCarViewModel.selectModel(model).observe(this, newData -> {
            adapter.setData(newData);
            adapter.notifyDataSetChanged();
        });
    }
}