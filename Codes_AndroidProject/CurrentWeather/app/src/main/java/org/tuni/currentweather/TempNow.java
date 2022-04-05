package org.tuni.currentweather;

import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.SharedPreferences;
import android.util.Log;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class TempNow extends AppWidgetProvider {

    public static String TAG = "ZZ widget";

    private static final String HEL_URL = "http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=Helsinki&parameters=t2m&";
    private static final String SHARED_PREF = "org.tuni.currentweather.sharedpref";
    private static final String PREF_KEY = "org.tuni.currentweather.shared_key";

    static void updateAppWidget(Context context, AppWidgetManager appWidgetManager,
                                int appWidgetId) {
        // Construct the RemoteViews object
        RemoteViews views = new RemoteViews(context.getPackageName(), R.layout.temp_now);
        getLastData(context);

        views.setTextViewText(R.id.appwidget_id, String.valueOf(appWidgetId));
        SharedPreferences prefs =
                context.getSharedPreferences(SHARED_PREF, 0);
        String temp = prefs.getString(PREF_KEY, "N/A") ;
        temp += " °C";
        Log.d(TAG, "saved temperature? " + temp);

        views.setTextViewText(R.id.appwidget_id, temp);
        appWidgetManager.updateAppWidget(appWidgetId, views);
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        // There may be multiple widgets active, so update all of them
        for (int appWidgetId : appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId);
        }
    }

    private static void getLastData(Context context) {
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, HEL_URL,
            response -> {
                SharedPreferences prefs =
                        context.getSharedPreferences(SHARED_PREF, 0);
                SharedPreferences.Editor prefEditor = prefs.edit();

                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

                    int elLength = parse.getElementsByTagName("wml2:MeasurementTVP").getLength();
                    String key = parse.getElementsByTagName("wml2:time").item(elLength-1).getTextContent();
                    String value = parse.getElementsByTagName("wml2:value").item(elLength-1).getTextContent();

                    Log.d(TAG, "value? " + value);
                    if (!key.isEmpty() && !value.isEmpty()) {
                        prefEditor.putString(PREF_KEY, value);
                        prefEditor.apply();
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },
            error -> {
                //displaying the error in toast if occurrs
                Toast.makeText(context, "Request failed for some reason", Toast.LENGTH_LONG).show();
            });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(context);
        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }
}