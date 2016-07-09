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
                Log.d("SIZE","size: "+count);
                //this needs overhaul
                //keeps overwriting into third position
                /*while(count<4){

                }*/
                if(count==0) {
                    editor.putString("first", value);
                }else if(count==1&&!keys.containsKey("second")){
                    editor.putString("second",value);
                }else if(count+1==2){
                    editor.putString("third",value);
                }else{
                    Snackbar.make(view, "Maximum amount of favorites reached", Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }
                editor.apply();

            }
        });
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        FloatingActionButton rem = (FloatingActionButton) findViewById(R.id.remove);
        rem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Removed from favorites", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                SharedPreferences prefs = getSharedPreferences("Favorites",MODE_PRIVATE);
                SharedPreferences.Editor editor = prefs.edit();
                Map<String,?> keys = prefs.getAll();
                for(Map.Entry<String,?> entry : keys.entrySet()){
                    String key = entry.getKey();
                    Object val = entry.getValue();
                    if(val.equals(message)) {
                        editor.remove(key);
                        editor.apply();
                    }
                }
            }
        });

        Data output = null;

        //Async: pass: filename,Void,Data
        try {
            output = new RetrieveData().execute(message).get();
        }catch(java.lang.InterruptedException i){
            i.printStackTrace();
        }catch(ExecutionException e){
            e.printStackTrace();
        }

        textView.setTextSize(20);
        textView.setText("\n\n\n"+output.toString());
        layout.addView(textView);


    }


    class RetrieveData extends AsyncTask<String,Void,Data> {

        private Exception exception;

        protected Data doInBackground(String... params) {
            Data buoy = new Data(params[0]);
            buoy.setAll(buoy.retrieveCurrent());
            return buoy;
        }

        protected void onPostExecute(Data data) {
            //text.setTextSize(20);
            //text.setText(data.toString());
        }
    }

}
