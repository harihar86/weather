package com.orbitz.weather.activity;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import com.orbitz.weather.R;
import com.orbitz.weather.model.CurrentWeather;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

public class WeatherActivity extends Activity {

    private EditText locationInput;
    private TextView currentTemp;
    private ImageView icon;
    private TextView precipitation;
    private TextView description;


    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_activity);
        bindViews();
    }

    public void search(View view) {
        String location = locationInput.getText().toString();
        String url = "http://api.worldweatheronline.com/premium/v1/weather.ashx?q=" +
                location + "&format=json&fx=no&key=8cgvkzqjjcmk7wnsag822n3y";

        ConnectivityManager connMgr = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = connMgr.getActiveNetworkInfo();

        if (networkInfo != null && networkInfo.isConnected()) {
            new WeatherService().execute(url);
        }

    }

    private void bindViews() {
        locationInput = (EditText) findViewById(R.id.locationInput);
        currentTemp = (TextView) findViewById(R.id.currentTemp);
        icon = (ImageView) findViewById(R.id.weatherIcon);
        precipitation = (TextView) findViewById(R.id.precipitation);
        description = (TextView) findViewById(R.id.description);
    }

    private class WeatherService extends AsyncTask<String, Void, InputStream> {

        @Override
        protected InputStream doInBackground(String... url) {
            try {
                return getResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                return null;
            }
        }

        @Override
        protected void onPostExecute(InputStream result) {
            initializeView(result);
        }
    }

    private class WeatherIconService extends AsyncTask<String, Void, InputStream> {
        @Override
        protected InputStream doInBackground(String... url) {
            try {
                return getResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                return null;
            }
        }

        @Override
        protected void onPostExecute(InputStream result) {
            initializeIcon(result);
        }
    }

    private InputStream getResponse(String requestUrl) throws IOException {
        InputStream is = null;
        try {
            URL url = new URL(requestUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setReadTimeout(1000);
            connection.setConnectTimeout(15000);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Accept-Encoding", "");
            connection.setDoInput(true);
            connection.connect();

            is = connection.getInputStream();

            return is;

        } catch (Exception e) {
            return null;
        }
        finally {
            if (is != null) {
                is.close();
            }
        }
    }

    private void initializeView(InputStream is) {
        try {
            BufferedReader in = new BufferedReader(new InputStreamReader(is));

            StringBuilder builder = new StringBuilder();
            String line = null;
            while ((line = in.readLine()) != null) {
                builder.append(line);
            }


            JSONObject json = new JSONObject(builder.toString());
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
            precipitation.setText(precip + " mm");

            description.setText(weatherDesc);

        } catch (Exception e) {
            //TODO
        }
    }

    private void initializeIcon(InputStream is) {
        Bitmap bitmap = BitmapFactory.decodeStream(is);
        icon.setImageBitmap(bitmap);
    }
}
