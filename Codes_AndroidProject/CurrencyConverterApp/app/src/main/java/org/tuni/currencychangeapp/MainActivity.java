package org.tuni.currencychangeapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

public class MainActivity extends AppCompatActivity {

    public static String TAG = "ZZ MainActivity";

    public static String REFERENCE_URL = "https://www.ecb.europa.eu/stats/eurofxref/eurofxref-daily.xml";
    public static String[] CURRENCY_LIST = new String[] { "USD", "GBP", "JPY", "KRW", "CNY" };
    public static Map<String, String> UNIT_MAP = new HashMap<String, String>() {{
        put("USD", "$");
        put("GBP", "£");
        put("JPY", "¥");
        put("KRW", "₩");
        put("CNY", "¥");
    }};

    EditText editText;
    Button convertButton;
    TextView vUSD, vGBP, vJPY, vKRW, vCNY;

    Map<String, Float> map;
    float amountEuro;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        editText = findViewById(R.id.amount_euro);
        convertButton = findViewById(R.id.convertButton);

        vUSD = findViewById(R.id.textViewUSD);
        vGBP = findViewById(R.id.textViewGBP);
        vJPY = findViewById(R.id.textViewJPY);
        vKRW = findViewById(R.id.textViewKRW);
        vCNY = findViewById(R.id.textViewCNY);

        map = new HashMap<>();
        amountEuro = 0;

  //      getCurrencyRate();
        getDataUsingVolley();

        convertButton.setOnClickListener(view -> {
            if (!editText.getText().toString().isEmpty()) {

                InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                inputManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);

                amountEuro = Float.parseFloat(editText.getText().toString());
                updateView();
            }
        });
    }

    public void updateView() {
        List<String> tmp = new ArrayList<>();
        for (String s : CURRENCY_LIST) {
            if (map.get(s) == null) {
                Toast.makeText(this, "Internet connection is not stable, try again", Toast.LENGTH_LONG).show();
                return;
            }
            Log.d(TAG, "Currency: " + s + ", exchange rate to EURO: " + map.get(s) );
            tmp.add(String.format(Locale.getDefault(),
                    "%-4s (%s) %,.2f", s, UNIT_MAP.get(s), amountEuro * map.get(s) ));
        }

        vUSD.setText(tmp.get(0));
        vGBP.setText(tmp.get(1));
        vJPY.setText(tmp.get(2));
        vKRW.setText(tmp.get(3));
        vCNY.setText(tmp.get(4));
    }

    private void getDataUsingVolley() {
        //creating a string request to send request to the url
        StringRequest stringRequest = new StringRequest(Request.Method.GET, REFERENCE_URL,
                response -> {
                    Log.d("Data",response+"");

                    try {
                        DocumentBuilder newDocumentBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                        Document parse = newDocumentBuilder.parse(new ByteArrayInputStream(response.getBytes()));

                        for (int i=0; i< parse.getElementsByTagName("Cube").getLength(); i++){
                            Node node = parse.getElementsByTagName("Cube").item(i);

                            if (node.getNodeType() == Node.ELEMENT_NODE) {
                                Element el = (Element) node;
                                String key = el.getAttribute("currency");
                                String value = el.getAttribute("rate");
                                if (!key.isEmpty() && !value.isEmpty()) {
                                    map.put(key, Float.parseFloat(value));
                                }
                            }
                        }
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

    public void getCurrencyRate() {
        Thread thread = new Thread(() -> {
            try {
                DocumentBuilder db = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                Document xmlDoc = db.parse(REFERENCE_URL);

                xmlDoc.getDocumentElement().normalize();
                NodeList nodeList = xmlDoc.getDocumentElement().getElementsByTagName("Cube");
                Log.d(TAG, "length of nodelist " + nodeList.getLength());

                for (int i=0; i< nodeList.getLength(); i++) {
                    Node node = nodeList.item(i);
                    if (node.getNodeType() == Node.ELEMENT_NODE) {
                        Element el = (Element) node;

                        String key = el.getAttribute("currency");
                        String value = el.getAttribute("rate");
                        if (!key.isEmpty() && !value.isEmpty()) {
                            map.put(key, Float.parseFloat(value));
                        }
                    }
                }
                Log.d(TAG, "inside thread try block");
            } catch (ParserConfigurationException | IOException | SAXException e) {
                e.printStackTrace();
            }
        });
        thread.start();
    }

}