package org.tuni.intenttestapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.AlarmClock;
import android.widget.Button;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    Button buttonOpen, buttonSend, buttonTake, buttonSet, buttonWeb;
    TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        buttonOpen = findViewById(R.id.buttonMap);
        buttonSend = findViewById(R.id.buttonEmail);
        buttonTake = findViewById(R.id.buttonCamera);
        buttonSet = findViewById(R.id.buttonAlarm);
        buttonWeb = findViewById(R.id.buttonWeb);

        buttonOpen.setOnClickListener(v -> openMap());
        buttonSend.setOnClickListener(v -> sendEmail());
        buttonTake.setOnClickListener(v -> takePhoto());
        buttonSet.setOnClickListener(v-> setAlarm("WAKE UP!!", 6, 30));
        buttonWeb.setOnClickListener(v-> openWeb());
    }

    public void openMap() {
        Uri loc = Uri.parse("geo:0,0?q=Lauttasaarentie%2023%20Helsinki");
        showMap(loc);
    }
    public void showMap(Uri geoLocation) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(geoLocation);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void sendEmail() {
        String[] address = { "kim.pararre@google.com" };
        composeEmail(address, "hello", "there");
    }
    public void composeEmail(String[] addresses, String subject, String msg) {
        Intent intent = new Intent(Intent.ACTION_SENDTO);
        intent.setData(Uri.parse("mailto:")); // only email apps should handle this
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_TEXT, msg);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }
    public void composeEmail(String[] addresses, String subject, Uri attachment) {
        Intent intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.putExtra(Intent.EXTRA_EMAIL, addresses);
        intent.putExtra(Intent.EXTRA_SUBJECT, subject);
        intent.putExtra(Intent.EXTRA_STREAM, attachment);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


    public void takePhoto() {

    }


    public void setAlarm(String msg, int hour, int min) {
        createAlarm(msg, hour, min);
    }
    public void createAlarm(String message, int hour, int minutes) {
        Intent intent = new Intent(AlarmClock.ACTION_SET_ALARM)
                .putExtra(AlarmClock.EXTRA_MESSAGE, message)
                .putExtra(AlarmClock.EXTRA_HOUR, hour)
                .putExtra(AlarmClock.EXTRA_MINUTES, minutes);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }

    public void openWeb() {
        openWebPage("https://www.hs.fi");
    }
    public void openWebPage(String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(getPackageManager()) != null) {
            startActivity(intent);
        }
    }


}