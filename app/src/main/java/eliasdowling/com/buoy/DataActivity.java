package eliasdowling.com.buoy;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Map;
import java.util.concurrent.ExecutionException;

public class DataActivity extends AppCompatActivity {
    TextView text;

    public static final String PREF_FAVORITE = "Favorites";
    public int count = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_data);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //this gets string from input and displays text entered
        Intent intent = getIntent();
        final String message = intent.getStringExtra(MainActivity.EXTRA_MESSAGE);
        final String value = message;
        TextView textView = new TextView(this);
        TextView text = new TextView(this);
        text.setTextSize(40);
        text.setText(message);

        RelativeLayout layout = (RelativeLayout) findViewById(R.id.content);
        layout.addView(text);


        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Added to favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Map<String,?> keys = prefs.getAll();
                count = keys.size();

                ////////for testing purposes/////
                Log.d("SIZE","size: "+count);
                int show = 0;
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    Log.d(Integer.toString(show),key+val);
                }
                ////////////////////////////////

                //this needs overhaul
                if(!keys.containsValue(value)){
                    editor.putString(Integer.toString(count+1),value).commit();
                }else Snackbar.make(view, "Already in favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton rem = (FloatingActionButton) findViewById(R.id.remove);
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
                    if(val.equals(message)) {
                        editor.remove(key).commit();
                        Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();
                        found = true;

                    }
                }
                if(!found){
                    Snackbar.make(view, "This buoy is not in your favorites", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        Data output[] = null;
        //Data past = null;

        //Async: pass: filename,Void,Data
        try {
            output = new RetrieveData().execute(message).get();
            //past = new RetrieveData().execute(message).get();

        }catch(java.lang.InterruptedException i){
            i.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        textView.setTextSize(20);
        textView.setText("\n\n\n"+output[0].toString());
        layout.addView(textView);




    }


    class RetrieveData extends AsyncTask<String,Void,Data[]> {

        private Exception exception;

        protected Data[] doInBackground(String... params) {
            Data[] dArr = new Data[2];
            Data buoy = new Data(params[0]);
            Data past = new Data(params[0]);
            buoy.setAll(buoy.retrieveCurrent(),false);
            dArr[0]=buoy;
            dArr[1]=past;
            return dArr;
        }

        protected void onPostExecute(Data data) {
            //text.setTextSize(20);
            //text.setText(data.toString());
        }
    }

}
