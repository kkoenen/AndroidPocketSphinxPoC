package nl.happyworx.srdemo.demo;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import java.util.Locale;


public class InfoScreenActivity extends Activity {

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.info_screen);

        Button btn_blog = (Button) findViewById(R.id.btn_blog);
        btn_blog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.happyworx.nl/blog"));
                startActivity(browserIntent);
            }
        });

        Button btn_en = (Button) findViewById(R.id.btn_info_en);
        btn_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("en");
                Log.v("PocketLog", "New locale");
                locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;

                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                restartActivity();
            }
        });

        Button btn_nl = (Button) findViewById(R.id.btn_info_nl);
        btn_nl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Locale locale = new Locale("nl");
                locale.setDefault(locale);
                Configuration config = new Configuration();
                config.locale = locale;
                getBaseContext().getResources().updateConfiguration(config,
                        getBaseContext().getResources().getDisplayMetrics());

                restartActivity();
            }
        });


    }

    private void restartActivity() {
        Intent intent = getIntent();
        finish();
        startActivity(intent);


    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // refresh your views here
        super.onConfigurationChanged(newConfig);
        getBaseContext().getResources().updateConfiguration(newConfig,
                getBaseContext().getResources().getDisplayMetrics());

        restartActivity();
    }
}
