package com.example.safetyapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.BatteryManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class MainActivity1 extends AppCompatActivity {
    private Button menuBtn,emergencyBtn;
    TextView tv;
    Button alert;
    static int battery_level=0;
    private TextView battery;
    double currLat,currLong;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main1);
        menuBtn = findViewById(R.id.menuBtn);
        menuBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent= new Intent(MainActivity1.this,MenuActivity.class);
                startActivity(intent);
            }
        });




        //battery = (TextView)this.findViewById(R.id.text1);
        batterylevel();
        tv = findViewById(R.id.loc);
        alert = findViewById(R.id.emergencyBtn);
        currLat= MainActivity.currLat;
        currLong= MainActivity.currLong;
        double homeLat=HomeLocation.homeLat;
        double homeLong=HomeLocation.homeLong;
        String diff = String.format("%.2f",distance(currLat,homeLat,currLong,homeLong));
        // tv.setText("Distance between home and your current location is "+diff+" km");

        // tv.setText("Distance between home and your current location is "+diff+" km");




        alert.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                final ArrayList<String> phone= new ArrayList<>();
                final ArrayList<String> name= new ArrayList<>();

                DataBaseHelper dataBaseHelper = new DataBaseHelper(MainActivity1.this);
                List<ContactModel> everyone = dataBaseHelper.getEveryone();
                System.out.println(everyone.toString());
                if(!everyone.isEmpty()) {
                    //-----Solved the problem of app crashing with less than 3 contacts saved
                    try {
                        for(int i=0; i<3; i++) {
                            if(everyone.size()>i) {
                                phone.add(everyone.get(i).getPhone());
                                name.add(everyone.get(i).getName());
                            }
                            else{
                                phone.add(null);
                                name.add(null);
                            }
                        }
//                        phone.add(everyone.get(1).getPhone());
//                        name.add(everyone.get(1).getName());
//                        phone.add(everyone.get(2).getPhone());
//                        name.add(everyone.get(2).getName());
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }

                String msg_temp="";
                String typed_msg ="Emergency";
                String loc = "https://maps.google.com/?q="+currLat+","+currLong;
                System.out.println("Typed msg: "+typed_msg);
                if(battery_level<=10)
                {
                    msg_temp="Sent from Student Emergency App." + typed_msg+" My battery is about to die (Automatic alert).\nBattery: "+battery_level+"%.\nCurrent location: "+loc;
                }
                else
                {
                    msg_temp="Name: Doreen Moraa." + typed_msg+" (C027-01-1383/2019).\nBattery: "+battery_level+"%.\nCurrent location: "+loc;
                }
                AlertModel alertModel = new AlertModel(-1,battery_level,loc,msg_temp,name.get(0),name.get(1),name.get(2),phone.get(0),phone.get(1),phone.get(2));
                boolean success = dataBaseHelper.addOneAlert(alertModel);
                String successMsg= success==true?"Added to database":"Error occurred";
                Toast.makeText(MainActivity1.this,successMsg,Toast.LENGTH_SHORT).show();
                SMS.sendSMS(phone,msg_temp);
                showMessageOKCancel("Message sent successfully to your trusted contacts. Stay safe \uD83D\uDE00");
            }
        });


    }
    private void batterylevel(){
        BroadcastReceiver mBatInfoReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                context.unregisterReceiver(this);
                int raw_level = intent.getIntExtra(BatteryManager.EXTRA_LEVEL,-1);
                int scale = intent.getIntExtra(BatteryManager.EXTRA_SCALE,-1);
                int level =-1;
                if(raw_level>=0 && scale>0){
                    level = (raw_level*100)/scale;
                }
                battery_level = level;
                //battery.setText(String.valueOf(level) + "%");
            }
        };
        IntentFilter batteryLevelFilter = new IntentFilter(Intent.ACTION_BATTERY_CHANGED);
        registerReceiver(mBatInfoReceiver, batteryLevelFilter);
    }

    public static double distance(double lat1,
                                  double lat2, double lon1,
                                  double lon2)
    {

        // The math module contains a function
        // named toRadians which converts from
        // degrees to radians.
        lon1 = Math.toRadians(lon1);
        lon2 = Math.toRadians(lon2);
        lat1 = Math.toRadians(lat1);
        lat2 = Math.toRadians(lat2);

        // Haversine formula
        double dlon = lon2 - lon1;
        double dlat = lat2 - lat1;
        double a = Math.pow(Math.sin(dlat / 2), 2)
                + Math.cos(lat1) * Math.cos(lat2)
                * Math.pow(Math.sin(dlon / 2),2);

        double c = 2 * Math.asin(Math.sqrt(a));

        // Radius of earth in kilometers. Use 3956
        // for miles
        double r = 6371;

        // calculate the result
        return(c * r);
    }

    private void showMessageOKCancel(String message) {
        new android.app.AlertDialog.Builder(MainActivity1.this)
                .setMessage(message)
                .setPositiveButton("OK",null)
                .create()
                .show();
    }



}