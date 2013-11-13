package com.orbitz.weather.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.widget.ListView;
import android.widget.TextView;
import com.orbitz.weather.R;
import com.orbitz.weather.adapter.ForecastAdapter;
import com.orbitz.weather.model.Forecast;
import com.orbitz.weather.service.HttpRequestManager;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

public class ForecastActivity extends Activity {

    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.forecast_list);
        Intent intent = getIntent();
        String location = intent.getStringExtra(WeatherActivity.EXTRA_LOCATION_INPUT);

        try {
            URI uri = new URI(
                    "http",
                    "api.worldweatheronline.com",
                    "/premium/v1/weather.ashx",
                    "q=" + location + "&format=json&num_of_days=7&tp=24&key=8cgvkzqjjcmk7wnsag822n3y",
                    null);

            new ForecastService().execute(uri.toString());

        } catch (URISyntaxException e) {
          //TODO
        }

    }

    private class ForecastService extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url) {
            try {
                return new HttpRequestManager().getResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                return null;
            }
        }

        @Override
        protected void onPostExecute(String result) {
            initializeView(result);
        }
    }



    private void initializeView(String data) {
        try {
            List<Forecast> forecasts = new ArrayList<Forecast>();

            JSONObject json = new JSONObject(data);
            JSONObject jsonData = (JSONObject) json.get("data");
            JSONArray weather = jsonData.getJSONArray("weather");

            for (int i = 0; i < weather.length(); i++) {
                Forecast forecast = new Forecast();

                JSONObject forecastJSON = weather.getJSONObject(i);
                String date = forecastJSON.getString("date");
                JSONObject hourly = forecastJSON.getJSONArray("hourly").getJSONObject(0);

                String precip = hourly.getString("precipMM");
                String tempF = hourly.getString("tempF");
                String description = hourly.getJSONArray("weatherDesc").getJSONObject(0).getString("value");
                String iconUrl = hourly.getJSONArray("weatherIconUrl").getJSONObject(0).getString("value");


                forecast.setDate(date);
                forecast.setCurrentTemp(tempF + " F");
                forecast.setPrecipitation(precip + " mm");
                forecast.setDescription(description);
                forecast.setIconUrl(iconUrl);

                forecasts.add(forecast);
            }

            ForecastAdapter adapter = new ForecastAdapter(this, R.layout.forecast_layout, forecasts);

            ListView forecastList = (ListView) findViewById(R.id.forecast_list);

            forecastList.setAdapter(adapter);
        } catch (JSONException e) {
            //TODO
        }
    }
}
