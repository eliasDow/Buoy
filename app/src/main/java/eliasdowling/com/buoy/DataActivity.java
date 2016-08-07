package eliasdowling.com.buoy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;


public class DataActivity extends AppCompatActivity {
    private TextView text;
    private int count = 0;
    public String val;
    private boolean inFav;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);/*
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);*/

        //this gets string from input and displays text entered
        Intent intent = getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        final String mapMes = intent.getStringExtra("MapAct");

        TextView text = (TextView)findViewById(R.id.name);

        //value to put into url
        if(mapMes!=null){
            HashMap map = MainActivity.map;
            String mapTitle = map.get(mapMes).toString();
            getSupportActionBar().setTitle(mapTitle);
            val=mapMes;
            text.setText(mapTitle);
        }else {
            getSupportActionBar().setTitle(message);
            val = message.substring(0,5);
            text.setText(message);
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Map<String,?> keys = prefs.getAll();
        count = keys.size();

        inFav=false;

        if(keys.containsValue(val)){
            fab.setImageResource(R.drawable.heartfilled);
            inFav=true;
        }else {
            fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            inFav=false;
        }

        /*fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Map<String,?> keys = prefs.getAll();
                count = keys.size();

                if(!keys.containsValue(value)){
                    Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG).show();
                    //fab.setImageDrawable(getResources().getDrawable(R.drawable.heartfilled));
                    editor.putString(Integer.toString(count+1),value).apply();
                }else if(favhold){
                    Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG).show();
                    editor.remove(value).apply();
                    fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
                }
            }
        });*/
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

       /* FloatingActionButton rem = (FloatingActionButton) findViewById(R.id.remove);
        rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Map<String,?> keys = prefs.getAll();
                boolean found = false;
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    //value is the buoy code
                    if(val.equals(value)) {
                        editor.remove(key).apply();
                        Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        found = true;
                        break;
                    }
                }
                if(!found){
                    Snackbar.make(view, "This buoy is not in your favorites", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });*/

        Data output[] = null;
        ArrayList<Data> past=new ArrayList<>();

        //Async: pass: filename,Void,Data
        try {
            output = new RetrieveData().execute(val).get();
            past = new RetrievePast().execute(val).get();
        }catch(java.lang.InterruptedException i){
            i.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        TextView textView = (TextView)findViewById(R.id.data);

        textView.setTextSize(20);
        //textView.setElevation(13);

        textView.setText(output[0].toString().substring(0,output[0].toString().lastIndexOf('\n')));

        /*TextView wind = (TextView)findViewById(R.id.wind);
        String windDat = "";
        for(int i=1;i<past.size();i++){
            System.out.println("DataActivity:"+past.get(i).getWindSpeed());
        }*/

        //wind.setText(windDat);


        //layout.addView(textView);

        //to pick what past data you want to see
        Spinner dropdown = (Spinner)findViewById(R.id.spinner);
        String[] items = new String[]{"Click for past data","Wind","Wave","Tide","Pressure","Temperature"};
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, items);
        dropdown.setAdapter(adapter);

        pastDataSelector(dropdown,past);


    }

    public void favorite(View v){
        FloatingActionButton fab = (FloatingActionButton)findViewById(R.id.fab);
        SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
        SharedPreferences.Editor editor = prefs.edit();
        Map<String,?> keys = prefs.getAll();
        count = keys.size();

        if(!keys.containsValue(val)){
            Snackbar.make(v, "Added to favorites", Snackbar.LENGTH_LONG).show();
            fab.setImageResource(R.drawable.heartfilled);
            editor.putString(Integer.toString(count+1),val).apply();
            keys = prefs.getAll();
        }else if(keys.containsValue(val)){
            Snackbar.make(v, "Removed from favorites", Snackbar.LENGTH_LONG).show();
            for(Map.Entry<String,?> entry : keys.entrySet()){
                String key = entry.getKey();
                Object entryVal = entry.getValue();
                //value is the buoy code
                if(entryVal.equals(val)) {
                    editor.remove(key).apply();
                    break;
                }
            }
            fab.setImageResource(R.drawable.ic_favorite_border_black_24dp);
            keys = prefs.getAll();
        }

    }

    private void pastDataSelector(Spinner d, ArrayList<Data> da){
        //wind wave tide pressure temp
        final ArrayList<Data> hold = da;

        d.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            String pastReturn="";

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                text = (TextView)findViewById(R.id.pastView);
                text.setText("");
                text.setMovementMethod(new ScrollingMovementMethod());
                switch (position) {
                    case 0:
                        text.setText("");
                        pastReturn="";
                        break;
                    case 1:
                        // Wind
                        text.setText("");
                        pastReturn="";
                        if(hold.get(0).getWindDir()!=null) {
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " " + d.getWindSpeed() + " mph " + d.getWindDir() + "\n";
                            }
                            text.setText(pastReturn);
                        }else text.setText("No wind data available");
                        break;
                    case 2:
                        // Wave
                        text.setText("");
                        pastReturn="";
                        if(hold.get(0).getWaveDir()!=null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " " + d.getWaveHgt() + " ft " + d.getWaveDir() + "\n";
                            }
                            text.setText(pastReturn);
                        }else text.setText("No wave data available");
                        break;
                    case 3:
                        // tide
                        text.setText("");
                        pastReturn="";
                        if(hold.get(0).getTide()!=null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " " + d.getTide() + " ft\n";
                            }
                            text.setText(pastReturn);
                        }else text.setText("No tide data available");
                        break;
                    case 4:
                        //pressure
                        text.setText("");
                        pastReturn="";
                        if(hold.get(0).getPressure()!=null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " " + d.getPressure() + " hPa\n";
                            }
                            text.setText(pastReturn);
                        }else text.setText("No pressure data available");
                        break;
                    case 5:
                        //both water and air available
                        text.setText("");
                        pastReturn="";
                        if(hold.get(0).getWaterTemp()!=null && hold.get(0).getAirTemp()!=null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " Water: " + d.getWaterTemp() + " \u2109 Air: "+d.getAirTemp()+" ℉\n";
                            }
                            text.setText(pastReturn);
                            //just water available
                        }else if(hold.get(0).getWaterTemp()!=null && hold.get(0).getAirTemp()==null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " Water: " + d.getWaterTemp() + " ℉\n";
                            }
                            text.setText(pastReturn);
                            //just air available
                        }else if(hold.get(0).getWaterTemp()==null && hold.get(0).getAirTemp()!=null){
                            for (Data d : hold) {
                                pastReturn += d.getDate().substring(10, d.getDate().length()) + " Air: " + d.getAirTemp() + " ℉\n";
                            }
                            text.setText(pastReturn);
                        }else text.setText("No temperature data available");
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

    }

    @Override
    public void onBackPressed() {
        Intent intent = getIntent();
        String checkFlag= intent.getStringExtra("flag");

        if(checkFlag.equals("home")) {
            startActivity(new Intent(this, MainActivity.class));
        }else if(checkFlag.equals("map")){
            super.onBackPressed();
        }else if(checkFlag.equals("fav")){
            super.onBackPressed();
        }
    }

    private class RetrieveData extends AsyncTask<String,Void,Data[]> {

        protected Data[] doInBackground(String... params) {
            Data[] dArr = new Data[2];
            Data buoy = new Data(params[0]);
            buoy.setAll(buoy.retrieveCurrent(),false);
            dArr[0]=buoy;
            return dArr;
        }
    }

    private class RetrievePast extends AsyncTask<String,Void,ArrayList<Data>> {

        protected ArrayList<Data> doInBackground(String... params) {
            PastObs past = new PastObs(params[0]);
            return past.pastObs();
        }
    }
}
