package org.tuni.newsapp;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;
import android.widget.SearchView;

import com.android.volley.NetworkResponse;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.HttpHeaderParser;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {

    String TEST_URL = "https://raw.githubusercontent.com/saugkim/Tuni_2022spring/main/nokia.json";
    String TEST2 = "https://run.mocky.io/v3/d4bdf8e7-651c-427d-95c8-4717559afb78";

    private final static String TAG = "ZZ MainActivity";
    private final static String API_KEY = BuildConfig.API_KEY;
    private final static String DATE_FORMAT ="yyyy-MM-dd";
    public static String BASE_URL = "https://newsapi.org/v2/everything?q=";

    StringBuilder query = new StringBuilder();
    String url="";

    SearchView searchView;
    List<Article> articles = new ArrayList<>();

    ArticleAdapter adapter;
    RecyclerView recyclerView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        searchView = findViewById(R.id.search_view);
        searchView.setIconifiedByDefault(false);

        recyclerView = findViewById(R.id.recycler_view);
        adapter = new ArticleAdapter(articles);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String s) {
                query.setLength(0);
                query.append(BASE_URL);
                query.append(s).append("&from=");
                query.append(getYesterdayString()).append("&sortBy=publishedAt&apiKey=");
                query.append(API_KEY);
                query.append("&language=en");

                url = query.toString();
                Log.d(TAG, "final query: " + url);
                // getArticles();
                // doThis();
                // orDoThis();
                orUseVolley();
                return true;
            }
            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }
        });
    }

    public String getYesterdayString() {
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.DATE, -1);    //subtract 1 date from current date
        DateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT, Locale.getDefault());
        return dateFormat.format(calendar.getTime());
    }

    private NewsResult fetchNewsResult(String url){
        try {
            return JsonHandler.getNewsResult(url);
        } catch (JSONException | IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    void getArticles() {
        new Thread(() -> {
            // do background stuff here
            NewsResult finalNewsResult = fetchNewsResult(url);
            runOnUiThread(() -> {
                // OnPostExecute stuff here
                assert finalNewsResult != null;
                if (finalNewsResult.getStatus().equals("ok")) {
                    articles = finalNewsResult.getArticles();
                    Log.d(TAG, "number of articles: " + articles.size());
                    //adapter = new ArticleAdapter(articles);
                    adapter.setLocalDataset(articles);
                    recyclerView.setAdapter(adapter);
                    recyclerView.setLayoutManager(new LinearLayoutManager(this));
                }
            });
        }).start();
    }

    void doThis(){
        HandlerThread handlerThread = new HandlerThread("URLConnection");
        handlerThread.start();
        Handler mainHandler = new Handler(handlerThread.getLooper());
        Runnable myRunnable = () -> {
            NewsResult newsResult = fetchNewsResult(url);
            assert newsResult != null;
            Log.d(TAG, "NewsResult status"+ newsResult.getStatus() );
            if (newsResult.getStatus().equals("ok")) {
                articles = newsResult.getArticles();
                Log.d(TAG, "number of articles: " + newsResult.getArticles().size());
            }
        };
        mainHandler.post(myRunnable);
    }
    void orDoThis() {
        ExecutorService myExecutor = Executors.newFixedThreadPool(4);
        myExecutor.execute(() -> {
            NewsResult newsResult = fetchNewsResult(url);
            assert newsResult != null;
            Log.d(TAG, "NewsResult status"+ newsResult.getStatus() );
            if (newsResult.getStatus().equals("ok")) {
                articles = newsResult.getArticles();
                Log.d(TAG, "number of articles: " + newsResult.getArticles().size());
            }
        });
    }

    private void orUseVolley() {
        RequestQueue mRequestQueue = Volley.newRequestQueue(this);

        JsonObjectRequest mJsonRequest = new JsonObjectRequest(Request.Method.GET, url, null,
            response -> {
                try {
                    NewsResult newsResult = JsonHandler.getNewsResultFromJSONObject(response);
                    articles = newsResult.getArticles();
                    Log.d(TAG, "number of articles: " + articles.size());
                    adapter.setLocalDataset(articles);
                    recyclerView.setAdapter(adapter);
                } catch (JSONException e) {
                    e.printStackTrace();
                }},
            error -> Log.i(TAG, "Error :" + error.toString())) {

            @Override
            public Map<String, String> getHeaders() {
                Map<String, String> headers = new HashMap<>();
                headers.put("Upgrade-Insecure-Requests", "1");
                headers.put("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/83.0.4103.116 Safari/537.36");
                headers.put("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.9");
                return headers;
            }
            @Override
            protected Response<JSONObject> parseNetworkResponse(NetworkResponse response) {
                try {
                    String jsonString = new String(response.data,
                            HttpHeaderParser.parseCharset(response.headers));
                    //if (jsonString.length() == 0) jsonString = "{'status':'success'}";
                    return Response.success(new JSONObject(jsonString),
                            HttpHeaderParser.parseCacheHeaders(response));
                } catch (UnsupportedEncodingException | JSONException e) {
                    return Response.error(new ParseError(e));
                }
            }
            @Override
            protected void deliverResponse(JSONObject response) {
                super.deliverResponse(response);
            }
            @Override
            public void deliverError(VolleyError error) {
                super.deliverError(error);
            }
            @Override
            protected VolleyError parseNetworkError(VolleyError volleyError) {
                return super.parseNetworkError(volleyError);
            }
        };
        mRequestQueue.add(mJsonRequest);
    }
}
