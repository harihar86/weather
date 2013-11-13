package com.orbitz.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import com.orbitz.weather.R;
import com.orbitz.weather.service.HttpRequestManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class WeatherActivity extends Activity implements View.OnClickListener {

    private EditText locationInput;
    private TextView currentTemp;
    private ImageView icon;
    private TextView precipitation;
    private TextView description;
    public static final String EXTRA_LOCATION_INPUT = "locationInput";

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        bindViews();
        LinearLayout layout = (LinearLayout) findViewById(R.id.weatherDisplay);
        layout.setOnClickListener(this);
    }

    public void search(View view) {
        String location = locationInput.getText().toString();
        try {
            URI uri = new URI(
                    "http",
                    "api.worldweatheronline.com",
                    "/premium/v1/weather.ashx",
                    "q=" + location + "&format=json&fx=no&key=8cgvkzqjjcmk7wnsag822n3y",
                    null);


            ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

            if (networkInfo != null && networkInfo.isConnected()) {
                new WeatherService().execute(uri.toString());
            }
        } catch (URISyntaxException e) {
            //TODO
        }
    }

    private void bindViews() {
        locationInput = (EditText) findViewById(R.id.locationInput);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        icon = (ImageView) findViewById(R.id.weatherIcon);
        precipitation = (TextView) findViewById(R.id.precipitation);
        description = (TextView) findViewById(R.id.description);
    }

    @Override
    public void onClick(View v) {
        Intent intent = new Intent(this, ForecastActivity.class);
        String location = locationInput.getText().toString();
        intent.putExtra(EXTRA_LOCATION_INPUT, location);
        startActivity(intent);
    }

    private class WeatherService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                return new HttpRequestManager().getResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                String exception = e.getStackTrace().toString();
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            initializeView(result);
        }
    }

    private class WeatherIconService extends AsyncTask<String, Void, Bitmap> {
        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                return new HttpRequestManager().getBitmapResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                return null;
            }
        }

        @Override
        protected void onPostExecute(Bitmap result) {
            initializeIcon(result);
        }
    }

    private void initializeView(String data) {
        try {
            JSONObject json = new JSONObject(data);
            JSONObject jsonData = (JSONObject) json.get("data");
            JSONArray current =  (JSONArray) jsonData.get("current_condition");
            JSONObject currentJSON = current.getJSONObject(0);

            String temp_f = currentJSON.getString("temp_F");
            String precip = currentJSON.getString("precipMM");
            JSONArray weatherArray = currentJSON.getJSONArray("weatherDesc");
            JSONObject weatherObj = weatherArray.getJSONObject(0);
            String weatherDesc = weatherObj.getString("value");

            JSONArray weatherIconArray = currentJSON.getJSONArray("weatherIconUrl");
            JSONObject weatherIconObj = weatherIconArray.getJSONObject(0);
            String iconUrl = weatherIconObj.getString("value");

            currentTemp.setText(temp_f + " F");
            precipitation.setText("Precipitation: " + precip + " mm");

            description.setText(weatherDesc);

            new WeatherIconService().execute(iconUrl);



        } catch (Exception e) {
            //TODO
        }
    }

    private void initializeIcon(Bitmap bmp) {
        icon.setImageBitmap(bmp);
    }

}
