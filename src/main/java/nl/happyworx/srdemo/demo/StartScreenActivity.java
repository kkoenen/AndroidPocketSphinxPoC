package nl.happyworx.srdemo.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class StartScreenActivity extends Activity {

    public static final String MyPREFERENCES = "nl.happyworx.srdemo.prefs" ;
    private SharedPreferences prefs;
    int wordsPerRound;
    String easterEgg_nl;
    String easterEgg_en;
    Boolean showSpecialFeature;
    AdView mAdView;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        setContentView(R.layout.start_screen);

        mAdView = (AdView) findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        mAdView.loadAd(adRequest);

        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        wordsPerRound = prefs.getInt("wordsPerRound", 6);
        easterEgg_en = prefs.getString("easterEgg_en", "supercalifragilisticexpealidoshus");
        easterEgg_nl = prefs.getString("easterEgg_nl", "kattententoonstelling");
        showSpecialFeature = prefs.getBoolean("specialFeature", false);

        Button btn_sel_lang_en = (Button) findViewById(R.id.btn_sel_lang_en);
        Button btn_sel_lang_nl = (Button) findViewById(R.id.btn_sel_lang_nl);
        Button btn_settings = (Button) findViewById(R.id.btn_settings);
        Button btn_about = (Button) findViewById(R.id.btn_info);
        Button btn_specialFeature = (Button) findViewById(R.id.btn_specialFeature);

        if (!showSpecialFeature){
            Log.v("PocketLog", "Special Feature = false");
            btn_specialFeature.setVisibility(View.INVISIBLE);
        } else {
            Log.v("PocketLog", "Special Feature = false");
            btn_specialFeature.setVisibility(View.VISIBLE);
        }

        btn_sel_lang_nl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartScreenActivity.this, PocketSphinxActivity.class);
                i.putExtra("dictionary", "wrd_dict_nl.dic");
                i.putExtra("wordlist", "wrd_lst_nl.lst");
                i.putExtra("acousticModel", "nl-ptm");
                i.putExtra("language", "nl");
                i.putExtra("easterEgg", easterEgg_nl);
                i.putExtra("wordsPerRound", wordsPerRound);
                StartScreenActivity.this.startActivity(i);
            }
        });

        btn_sel_lang_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartScreenActivity.this, PocketSphinxActivity.class);
                i.putExtra("dictionary", "wrd_dict_en.dic");
                i.putExtra("wordlist", "wrd_lst_en.lst");
                i.putExtra("acousticModel", "en-us-ptm");
                i.putExtra("easterEgg", "supercalifragilisticexpealidoshus");
                i.putExtra("language", "en");
                i.putExtra("easterEgg", easterEgg_en);
                i.putExtra("wordsPerRound", wordsPerRound);
                StartScreenActivity.this.startActivity(i);
            }
        });

        btn_about.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartScreenActivity.this, InfoScreenActivity.class);
                StartScreenActivity.this.startActivity(i);
            }
        });

        btn_settings.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StartScreenActivity.this, SettingsScreenActivity.class);
                StartScreenActivity.this.startActivity(i);
            }
        });

    }

    @Override
    public void onResume(){
        super.onResume();
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        wordsPerRound = prefs.getInt("wordsPerRound", 6);
        easterEgg_en = prefs.getString("easterEgg_en", "supercalifragilisticexpealidoshus");
        easterEgg_nl = prefs.getString("easterEgg_nl", "kattententoonstelling");
        showSpecialFeature = prefs.getBoolean("specialFeature", false);
        Log.v("PocketLog", "Resumed!");
        if (!showSpecialFeature){
            Log.v("PocketLog","Special Feature = false");
            Button btn = (Button) findViewById(R.id.btn_specialFeature);
                    btn.setVisibility(View.INVISIBLE);
        } else {
            Button btn = (Button) findViewById(R.id.btn_specialFeature);
            btn.setVisibility(View.VISIBLE);
        }
    }
}
