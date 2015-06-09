package com.example.dimbler.webview;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Timer;
import java.util.TimerTask;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class MainActivity extends ActionBarActivity implements OnClickListener  {

    public Context context;
    public TextView errorText;
    private Timer myTimer;
    public static String access_token = null;
    public static int work_time = 0;
    public NumberPicker np;

    ToggleButton nasos_Button;
    /*
    //Получает токен авторизации с сервера
    public void GetAccessToken(){
        JSONObject jsonOnLogin = null;
        try {
            jsonOnLogin = new Login(this).execute().get();
        } catch (Exception e) {
            e.printStackTrace();
        }
        try {
            if (jsonOnLogin == null) {
                Toast toast = Toast.makeText(getApplicationContext(), "Отсутствует соединение с сервером", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
            }else {
                access_token = jsonOnLogin.getString("access_token");
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    */

    public void GetDataFromSendor(){

        JSONObject jsonFromSensor = null;

        if (NetworkExchange.isNetworkConnectedOrConnecting(this)) {
            try {
                if (access_token == null) {
                    access_token = new Login(this).execute().get();
                }
                jsonFromSensor = new GetSensorData(this).execute(access_token).get();
            } catch (Exception e) {
                e.printStackTrace();
            }

            try {
                if (jsonFromSensor == null) {
                    errorText.setText(R.string.error_get_sensor_data);
                /*
                Toast toast = Toast.makeText(getApplicationContext(), "Отсутствует соединение с сервером", Toast.LENGTH_LONG);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                */
                } else {
                    JSONArray array = jsonFromSensor.getJSONArray("assets");
                    for (int i = 0; i < array.length(); i++) {
                        JSONObject row = array.getJSONObject(i);
                        String id = row.getString("name");
                        JSONObject state = row.getJSONObject("state");
                        String value = state.getString("value");

                        if ("Почва влажная".equals(id)) {
                            Button pochvaButton = (Button) findViewById(R.id.act_main_tb_pochva);
                            ((ToggleButton) pochvaButton).setChecked(Boolean.valueOf(value));
                        }
                        if ("Полив".equals(id)) {

                            ((ToggleButton) nasos_Button).setChecked(Boolean.valueOf(value));
                        }
                        if ("Идет дождь".equals(id)) {
                            Button rainButton = (Button) findViewById(R.id.act_main_tb_rain);
                            ((ToggleButton) rainButton).setChecked(Boolean.valueOf(value));
                        }
                        if ("Влажность".equals(id)) {
                            Button humButton = (Button) findViewById(R.id.act_main_btn_hum);
                            humButton.setText(value);
                        }
                        if ("Температура".equals(id)) {
                            Button tempButton = (Button) findViewById(R.id.act_main_btn_temp);
                            tempButton.setText(value);
                        }

                        Log.d("JSON TSTR", id + value);
                        errorText.setText(null);
                    }
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }

        }else
        {
            errorText.setText(R.string.error_not_connected);
        }


    }

    private void TimerMethod()
    {
        //This method is called directly by the timer
        //and runs in the same thread as the timer.

        //We call the method that will work with the UI
        //through the runOnUiThread method.
        this.runOnUiThread(Timer_Tick);
    }

    private Runnable Timer_Tick = new Runnable() {
        public void run() {
            GetDataFromSendor();
            //This method runs in the same thread as the UI.

            //Do something to the UI thread here
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        nasos_Button = (ToggleButton) findViewById(R.id.toggleButton);

        if (getIntent().getBooleanExtra("EXIT", false)) {
            finish();
        }

        np = (NumberPicker) findViewById(R.id.numberPicker1);
        np.setMinValue(0);
        np.setMaxValue(30);
        np.setWrapSelectorWheel(true);

        np.setOnValueChangedListener(new OnValueChangeListener() {

            @Override
            public void onValueChange(NumberPicker picker, int oldVal, int newVal) {

                work_time = newVal;
            }
        });

        context = this.context;
        errorText = (TextView)findViewById(R.id.errorText);

        if (!NetworkExchange.isNetworkConnectedOrConnecting(this)){

            errorText.setText(R.string.no_network_connection);
        }

        /*
        WebView wv = (WebView) findViewById(R.id.webView);
        WebSettings webSettings = wv.getSettings();
        webSettings.setSaveFormData(true);
        webSettings.setJavaScriptEnabled(true);
        wv.loadUrl("http://api.thingspeak.com/channels/38705/charts/5?width=450&height=260&results=60&dynamic=true");

        /*
        JSONObject jsonOnLogin = null;
        try {
            jsonOnLogin = new Login().execute().get();
            //Log.d("JSON TSTR", jsonOnLogin.toString());
        } catch (Exception e) {
            e.printStackTrace();
        }
        */
        /*
        GetAccessToken();
        if (access_token != null) {
            GetDataFromSendor();
        }
        else
        {
            errorText.setText("Отсутвует соединение с сервером");
        }
        */
        Button button = (Button)findViewById(R.id.toggleButton);

        button.setOnClickListener(new OnClickListener() {
            public void onClick(View v) {
                boolean on = ((ToggleButton) v).isChecked();

                if (access_token != null) {
                    if (on) {
                        if (work_time == 0){
                            new ExecNasos().execute(access_token, "true");
                        }else {
                            new ExecNasos().execute(access_token, "true");
                            final Handler handler = new Handler();
                            handler.postDelayed(new Runnable() {
                                @Override
                                public void run() {
                                    //Do something after 100ms
                                    new ExecNasos().execute(access_token, "false");
                                    np.setValue(0);
                                    nasos_Button.setChecked(false);
                                }
                            }, work_time * 60000);

                        }
                    } else {
                        new ExecNasos().execute(access_token, "false");
                    }
                }
            }
        });

        Button tempButton = (Button)findViewById(R.id.act_main_btn_temp);
        tempButton.setOnClickListener(this);

        Button humButton = (Button)findViewById(R.id.act_main_btn_hum);
        humButton.setOnClickListener(this);



    }

    @Override
    protected void onResume(){
        super.onResume();
        myTimer = new Timer();
        myTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                TimerMethod();
            }

        }, 0, 30000);
        Log.d("Timer", "Create timer");
    }

    @Override
    protected void onStop() {
        super.onStop();
        myTimer.cancel();
        Log.d("Stop:", "MainActivity: onStop()");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);
        //return true;
        menu.add(0, 1, 0, R.string.exit);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == 1) {
            Log.d("Checked menu: ", String.valueOf(id));
            this.finish();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onClick(View view) {
        Intent intent = new Intent(this, Two.class);
        switch (view.getId()){
            case R.id.act_main_btn_temp:
                intent.putExtra("urlname", "http://api.thingspeak.com/channels/38705/charts/1?width=350&height=460&results=30&dynamic=true&type=spline");
                startActivity(intent);
                break;
            case R.id.act_main_btn_hum:
                intent.putExtra("urlname", "http://api.thingspeak.com/channels/38705/charts/2?width=350&height=460&results=30&dynamic=true&type=spline");
                startActivity(intent);
                break;
            default:
                break;
        }
    }
}
