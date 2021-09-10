package com.example.cars_appweek5;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cars_appweek5.provider.Car;
import com.example.cars_appweek5.provider.CarViewModel;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.gson.Gson;

import java.util.ArrayList;
import java.util.StringTokenizer;

import static java.lang.Integer.parseInt;

public class MainActivity extends AppCompatActivity {

    Toolbar toolbar;
    Context self;

    ArrayList<String> myList = new ArrayList<>();
    ArrayAdapter myAdapter;

    DrawerLayout drawer;

    EditText maker, model, year, color, seats, price;
    View myConstraint;
    Gson gson = new Gson();

    private CarViewModel mCarViewModel;
    MyRecycleViewAdapter adapter;

    DatabaseReference myRef;

    int x_down;
    int y_down;

    GestureDetector gestureDetector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.drawer_layout);

        maker = findViewById(R.id.inputMaker);
        model = findViewById(R.id.inputModel);
        year = findViewById(R.id.inputYear);
        color = findViewById(R.id.inputColor);
        seats = findViewById(R.id.inputSeats);
        price = findViewById(R.id.inputPrice);
        myConstraint = findViewById(R.id.constraint_id);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        self = this;

        adapter = new MyRecycleViewAdapter();

        mCarViewModel = new ViewModelProvider(this).get(CarViewModel.class);
        mCarViewModel.getAllCars().observe(this, newData -> {
            adapter.setData(newData);
            adapter.notifyDataSetChanged();
        });

        ListView listView = findViewById(R.id.my_list_layout);
        if(savedInstanceState!=null)
        {
            myList = savedInstanceState.getStringArrayList("LIST");
        }
        myAdapter = new ArrayAdapter(this, android.R.layout.simple_list_item_1,myList);
        listView.setAdapter(myAdapter);

        drawer = findViewById(R.id.drawerLayout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this,drawer,toolbar,
                R.string.nav_open,R.string.nav_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = findViewById(R.id.navigationView);
        navigationView.setNavigationItemSelectedListener(new myNavListener());

        FloatingActionButton fab = findViewById(R.id.floatingActionButton);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Car car = new Car(maker.getText().toString(), model.getText().toString(), year.getText().toString(), color.getText().toString(), seats.getText().toString(), price.getText().toString());
                mCarViewModel.insert(car);
                myRef.push().setValue(car);
            }
        });


        FirebaseDatabase database = FirebaseDatabase.getInstance();
        myRef = database.getReference("car");


        /* Request permissions to access SMS */
        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.SEND_SMS, Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS}, 0);
        /* Create and instantiate the local broadcast receiver
           This class listens to messages come from class SMSReceiver
         */
        MyBroadCastReceiver myBroadCastReceiver = new MyBroadCastReceiver();

        /*
         * Register the broadcast handler with the intent filter that is declared in
         * class SMSReceiver @line 11
         * */
        registerReceiver(myBroadCastReceiver, new IntentFilter(SMSReceiver.SMS_FILTER));

        gestureDetector = new GestureDetector(this, new MyGestureDetector());

        myConstraint.setOnTouchListener(new View.OnTouchListener(){
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                gestureDetector.onTouchEvent(event);
                return true;
            }
            //            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                int action = event.getActionMasked();
//
//                switch (action){
//                    case MotionEvent.ACTION_DOWN:
//                        x_down = (int)event.getX();
//                        y_down = (int)event.getY();
//                        return true;
//                    case MotionEvent.ACTION_MOVE:
//                        return true;
//                    case MotionEvent.ACTION_UP:
//                        // if it is on upper part and it is a click action
//                        if(y_down <= 200 && y_down == (int)event.getY() && x_down == (int)event.getX()){
//                            // if it is at the right edge
//                            if(x_down >= 800){
//                                // add 50 for price
//                                if (!price.getText().toString().equals("")) {
//                                    int myPrice = parseInt(price.getText().toString()) + 50;
//                                    price.setText(myPrice + "");
//                                }
//                                // if empty set to 50
//                                else{
//                                    price.setText("50");
//                                }
//                            }// if it is at the left edge
//                            else if(x_down <= 40){
//                                // minus 50 for price
//                                if (!price.getText().toString().equals("")) {
//                                    int myPrice = parseInt(price.getText().toString());
//                                    if (myPrice >= 50) {
//                                        price.setText((myPrice - 50) + "");
//                                    }// min is 0 so set to 0
//                                    else {
//                                        price.setText("0");
//                                    }
//                                }// if empty set to 0
//                                else{
//                                    price.setText("0");
//                                }
//                            }
//                        }
//                        //horizontal line
//                        else if (Math.abs(y_down-event.getY())<40)
//                        {//if it is a horizontal line from left to right
//                            if(x_down-event.getX()<0){
//                                Car car = new Car(maker.getText().toString(), model.getText().toString(), year.getText().toString(), color.getText().toString(), seats.getText().toString(), price.getText().toString());
//                                mCarViewModel.insert(car);
//                                myRef.push().setValue(car);
//                                Toast.makeText(getApplicationContext(),"a new car added", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        //vertical line
//                        else if(Math.abs(x_down-event.getX())<40)
//                        {//if it is a vertical line from top to bottom
//                            if(y_down-event.getY()<0){
//                                int [] myArray = {R.id.inputMaker, R.id.inputModel, R.id.inputYear, R.id.inputColor, R.id.inputSeats, R.id.inputPrice};
//                                for(int i: myArray){
//                                    EditText myEdit = findViewById(i);
//                                    myEdit.getText().clear();
//                                }
//                                Toast.makeText(getApplicationContext(),"car field cleared", Toast.LENGTH_SHORT).show();
//                            }
//                        }
//                        return true;
//                    default:
//                        return false;
//                }
//            }
        });
    }

    class MyGestureDetector extends GestureDetector.SimpleOnGestureListener{
        @Override
        public boolean onSingleTapUp(MotionEvent e) {
            return super.onSingleTapUp(e);
        }

        @Override
        public void onLongPress(MotionEvent e) {
            int [] myArray = {R.id.inputMaker, R.id.inputModel, R.id.inputYear, R.id.inputColor, R.id.inputSeats, R.id.inputPrice};
            for(int i: myArray){
                EditText myEdit = findViewById(i);
                myEdit.getText().clear();
            }
            Toast.makeText(getApplicationContext(),"car field cleared", Toast.LENGTH_SHORT).show();
            super.onLongPress(e);
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            if(distanceX>0 && Math.abs(e2.getY()-e1.getY())<=20){
                if (!price.getText().toString().equals("")) {
                    int myPrice = parseInt(price.getText().toString()) + Math.round(distanceX);
                    if (myPrice <= 5000) {
                        price.setText((myPrice + ""));
                    }// min is 0 so set to 0
                    else {
                        price.setText("5000");
                    }
                }
                else{
                    price.setText(Math.round(distanceX));
                }
            }
            else if(distanceX<0 && Math.abs(e2.getY()-e1.getY())<=20){
                if (!price.getText().toString().equals("")) {
                    int myPrice = parseInt(price.getText().toString()) - (Math.round(distanceX)*-1);
                    if (myPrice >= 0) {
                        price.setText((myPrice +""));
                    }// min is 0 so set to 0
                    else {
                        price.setText("0");
                    }
                }
                else{
                    price.setText("0");
                }
            }
            return super.onScroll(e1, e2, distanceX, distanceY);
        }

        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            if (Math.abs(velocityX)>500 || Math.abs(velocityY)>500) {
                moveTaskToBack(true);
            }
            return super.onFling(e1, e2, velocityX, velocityY);
        }


        @Override
        public boolean onDoubleTap(MotionEvent e) {
            return super.onDoubleTap(e);
        }

        @Override
        public boolean onDoubleTapEvent(MotionEvent e) {
            maker.setText("Perodua");
            model.setText("Myvi");
            year.setText("2021");
            color.setText("Yellow");
            seats.setText("5");
            price.setText("50");
            return super.onDoubleTapEvent(e);
        }

        @Override
        public boolean onSingleTapConfirmed(MotionEvent e) {
            if (!seats.getText().toString().equals("")) {
                int myPrice = parseInt(seats.getText().toString()) + 1;
                seats.setText(myPrice + "");
            }
            else{
                seats.setText("1");
            }
            return super.onSingleTapConfirmed(e);
        }
    }

    class myNavListener implements NavigationView.OnNavigationItemSelectedListener{

        @Override
        public boolean onNavigationItemSelected(@NonNull MenuItem item) {
            int id = item.getItemId();

            switch(id){
                case R.id.menuAddCar:
//                    myList.add(maker.getText()+ " | " + model.getText());
//                    myAdapter.notifyDataSetChanged();
                    Car car = new Car(maker.getText().toString(), model.getText().toString(), year.getText().toString(), color.getText().toString(), seats.getText().toString(), price.getText().toString());
                    mCarViewModel.insert(car);
                    myRef.push().setValue(car);
                    break;
                case R.id.menuRemoveAllCars:
//                    if(myList.size()>0){
//                        myList.clear();
//                        myAdapter.notifyDataSetChanged();}
                    mCarViewModel.deleteAll();
                    myRef.removeValue();
                    break;
                case R.id.menuRemoveLastCar:
                    myList.remove(myList.size()-1);
                    myAdapter.notifyDataSetChanged();
                    break;
                case R.id.menuClose:
                    finish();
                case R.id.menuListAlltems:
//                    String dbStr = gson.toJson(myList);
//
//                    SharedPreferences sP = getSharedPreferences("f1", 0);
//                    SharedPreferences.Editor editor = sP.edit();
//                    editor.putString("KEY_LIST", dbStr);
//                    editor.apply();

                    Intent i = new Intent(self, MainActivity2.class);
                    startActivity(i);
            }
            drawer.closeDrawer(GravityCompat.START);
            return true;
        }
    }


    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.option_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();

        if(id == R.id.menuClearFields)
        {
            int [] myArray = {R.id.inputMaker, R.id.inputModel, R.id.inputYear, R.id.inputColor, R.id.inputSeats, R.id.inputPrice};
            for(int i: myArray){
                EditText myEdit = findViewById(i);
                myEdit.getText().clear();
            }
        }
        else if(id == R.id.menuTotal){
            Toast totalMessage = Toast.makeText(this, "The total number of cars in list: " + myList.size(), Toast.LENGTH_LONG);
            totalMessage.show();
        }

        return true;
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList("LIST",myList);
    }

    class MyBroadCastReceiver extends BroadcastReceiver {

        /*
         * This method 'onReceive' will get executed every time class SMSReceive sends a broadcast
         * */
        @Override
        public void onReceive(Context context, Intent intent) {
            /*
             * Retrieve the message from the intent
             * */
            String msg = intent.getStringExtra(SMSReceiver.SMS_MSG_KEY);
            /*
             * String Tokenizer is used to parse the incoming message
             * The protocol is to have the account holder name and account number separate by a semicolon
             * */

            StringTokenizer sT = new StringTokenizer(msg, ";");
            /*
             * Now, its time to update the UI
             * */
            String newMaker = sT.nextToken();
            String newModel = sT.nextToken();
            String newYear = sT.nextToken();
            String newColor = sT.nextToken();
            String newSeats = sT.nextToken();
            String newPrice = sT.nextToken();
            maker.setText(newMaker);
            model.setText(newModel);
            year.setText(newYear);
            color.setText(newColor);
            if (parseInt(newSeats) >= 4 && parseInt(newSeats) <= 8){
                seats.setText(newSeats);
            }
            else {
                seats.setText("Error: Must be 4-8");
            }

            price.setText(newPrice);

        }
    }


}