package com.orbitz.weather.adapter;

import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import com.orbitz.weather.R;
import com.orbitz.weather.model.Forecast;
import android.content.Context;
import com.orbitz.weather.service.HttpRequestManager;

import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.joda.time.LocalDate;
import org.joda.time.format.DateTimeFormat;
import org.joda.time.format.DateTimeFormatter;

public class ForecastAdapter extends ArrayAdapter<Forecast> {

    private List<Forecast> forecasts;
    private int resourceId;
    private View view;
    private Context context;

    public ForecastAdapter(Context context, int resourceId, List<Forecast> forecasts) {
        super(context, resourceId, forecasts);
        this.resourceId = resourceId;
        this.forecasts = forecasts;
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(context);

        view = inflater.inflate(resourceId, null);

        TextView dateView = (TextView) view.findViewById(R.id.date);
        TextView description = (TextView) view.findViewById(R.id.description);
        TextView precipitation = (TextView) view.findViewById(R.id.precipitation);
        TextView temperature = (TextView) view.findViewById(R.id.temperature);
        ImageView icon = (ImageView) view.findViewById(R.id.icon);

        Forecast forecast = forecasts.get(position);



        final DateTimeFormatter formatter = DateTimeFormat.forPattern("yyyy-MM-dd");

        LocalDate date = formatter.parseLocalDate(forecast.getDate());

        final DateTimeFormatter shortDateFormatter = DateTimeFormat.forPattern("E, MM/dd");

        dateView.setText(shortDateFormatter.print(date));

        description.setText(forecast.getDescription());
        precipitation.setText(forecast.getPrecipitation());
        temperature.setText(forecast.getCurrentTemp());

        ForecastIconService service = new ForecastIconService();

        service.execute(forecast.getIconUrl());

        try {
            Bitmap bmp = service.get();
            icon.setImageBitmap(bmp);
        } catch (InterruptedException e) {
            //TODO
        } catch (ExecutionException e) {
            //TODO
        }

        return view;
    }


    private class ForecastIconService extends AsyncTask<String, Void, Bitmap> {

        @Override
        protected Bitmap doInBackground(String... url) {
            try {
                return new HttpRequestManager().getBitmapResponse(url[0]);
            } catch (IOException e)  {
                //TODO
                return null;
            }
        }
    }


}

