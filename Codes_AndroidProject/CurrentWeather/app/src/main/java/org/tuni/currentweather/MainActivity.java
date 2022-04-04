package org.tuni.currentweather;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;

import java.io.ByteArrayInputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Locale;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    public static String TAG = "ZZ MainActivity";
    public static String BASE_URL = "http://opendata.fmi.fi/wfs/fin?service=WFS&version=2.0.0&request=getFeature&storedquery_id=fmi::observations::weather::timevaluepair&place=";
    public static String END_QUERY = "&parameters=t2m&";
    public static String[] locations = new String[] {
        "AREA", "Helsinki", "Turku", "Pirkkala", "Joensuu", "Oulu", "Ivalo", "Imatra", "Kotka"
    };

    Spinner spinner;
    Button searchButton;
    TextView title, content;

    String query_place;
    boolean isSelected;
    List<String> datetime;
    List<String> degree;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        spinner = findViewById(R.id.spinner);
        searchButton = findViewById(R.id.button);
        title = findViewById(R.id.textView2);
        content = findViewById(R.id.textView3);

        isSelected = false;
        datetime = new ArrayList<>();
        degree = new ArrayList<>();

        spinner.setOnItemSelectedListener(this);

        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, locations);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        searchButton.setOnClickListener(view -> {
            if (isSelected && query_place != null) {
                String url = getFeatureQuery(query_place);
                Log.d(TAG, "url: " + url);
                getData(url);
                isSelected = false;
                title.setText(String.format(Locale.getDefault(), "5 Recent Temperature in %s", query_place));
            } else {
                Toast.makeText(this, "Area is not selected, please select region using spinner!", Toast.LENGTH_LONG).show();
            }
        });
    }


    private void getData(String url) {
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, url,
            response -> {
                try {
                    DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

                    for (int i=0; i< parse.getElementsByTagName("wml2:MeasurementTVP").getLength(); i++){
                        String key = parse.getElementsByTagName("wml2:time").item(i).getTextContent();
                        String value = parse.getElementsByTagName("wml2:value").item(i).getTextContent();
                        if (!key.isEmpty() && !value.isEmpty()) {
                            datetime.add(key.replaceAll("Z", "").replaceAll("T", " at "));
                            degree.add(value);
                        }
                        Log.d(TAG, "key: " + key + ", value: " + value );
                    }
                    getRecentFiveAndUpdate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            },
            error -> {
                //displaying the error in toast if occurrs
                Toast.makeText(getApplicationContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            });

        //creating a request queue
        RequestQueue requestQueue = Volley.newRequestQueue(this);

        //adding the string request to request queue
        requestQueue.add(stringRequest);
    }

    public void getRecentFiveAndUpdate() {
        Collections.reverse(datetime);
        Collections.reverse(degree);
        int number = Math.min(datetime.size(), 5);

        StringBuilder sb = new StringBuilder("Year-Month-Day time:\u0020\u0020\u0020 Temperature(Celsius)\n");
        for (int i=0; i< number; i++) {
            sb.append(datetime.get(i));
            sb.append("\u0020\u0020\u0020\u0020\u0020");
            sb.append(degree.get(i));
            sb.append(" °C\n");
        }

        content.setText(sb.toString());
    }

    public String getFeatureQuery(String location){
        return BASE_URL + location + END_QUERY;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
        if (i != 0) {
            isSelected = true;
            query_place = adapterView.getSelectedItem().toString();
            Log.d(TAG, "place: " + query_place);
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {
        Log.d(TAG, "nothing selected");
    }
}