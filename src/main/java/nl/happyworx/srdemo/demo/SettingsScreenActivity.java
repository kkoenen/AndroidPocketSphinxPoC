package nl.happyworx.srdemo.demo;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.w3c.dom.Text;


public class SettingsScreenActivity extends Activity {

    private String TAG = "SRDemoLog";
    int wordsPerRound;
    String easterEgg_en;
    String easterEgg_nl;
    String easterEgg_en_mask;
    String easterEgg_nl_mask;
    Boolean showSpecialFeature;

    private TextView tv_easterEgg_en;
    private TextView tv_easterEgg_nl;

    public static final String MyPREFERENCES = "nl.happyworx.srdemo.prefs";
    private SharedPreferences prefs;
    static final int PICK_EASTER_EGG = 1;
    static final int PICK_EASTER_EGG_NL = 2;

    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        setContentView(R.layout.settings_screen);
        prefs = getSharedPreferences(MyPREFERENCES, Context.MODE_PRIVATE);
        wordsPerRound = prefs.getInt("wordsPerRound", 6);
        easterEgg_en = prefs.getString("easterEgg_en", "supercalifragilisticexpealidoshus");
        easterEgg_nl = prefs.getString("easterEgg_nl", "kattententoonstelling");
        showSpecialFeature = prefs.getBoolean("specialFeature", false);
        easterEgg_en_mask = easterEgg_en;
        easterEgg_nl_mask = easterEgg_nl;
        if (easterEgg_nl.equals("kattententoonstelling")) easterEgg_nl_mask = "******* (Default)";
        if (easterEgg_en.equals("supercalifragilisticexpealidoshus"))
            easterEgg_en_mask = "****** (Default)";

        // Defining control elements on the screen
        final TextView tv_wordsPerRound = (TextView) findViewById(R.id.sett_tv_numberOfWords);
        final SeekBar sb_wordsPerRound = (SeekBar) findViewById(R.id.sb_sett_wordsPerRound);
        tv_easterEgg_en = (TextView) findViewById(R.id.sett_tv_easterEgg_en);
        tv_easterEgg_nl = (TextView) findViewById(R.id.sett_tv_easterEgg_nl);
        Button btn_easterEgg_en = (Button) findViewById(R.id.sett_btn_setEasterEgg_en);
        Button btn_easterEgg_nl = (Button) findViewById(R.id.sett_btn_setEasterEgg_nl);
        Button btn_discardAndBack = (Button) findViewById(R.id.sett_btn_discardAndBack);
        Button btn_saveAndBack = (Button) findViewById(R.id.sett_btn_saveAndBack);
        Button btn_getDefaults = (Button) findViewById(R.id.sett_btn_getDefaults);
        // final CheckBox cb_showSpecialFeature = (CheckBox) findViewById(R.id.sett_cb_showSpecialFeature);

        // Setting the screen with current variables
        tv_wordsPerRound.setText("Number of words per round : " + wordsPerRound);
        sb_wordsPerRound.setMax(15);
        sb_wordsPerRound.setProgress(wordsPerRound);

        tv_easterEgg_en.setText("English easter egg word :\n " + easterEgg_en_mask);
        tv_easterEgg_nl.setText("Dutch easter egg word :\n " + easterEgg_nl_mask);

        // cb_showSpecialFeature.setChecked(showSpecialFeature);

        // Defining the controls procedures
        sb_wordsPerRound.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (progress < 2) {
                    sb_wordsPerRound.setProgress(2);
                    tv_wordsPerRound.setText("Words per round : 2 (min)");
                } else
                    tv_wordsPerRound.setText("Words per round : " + progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });

        btn_getDefaults.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                wordsPerRound = 6;
                easterEgg_en = "supercalifragilisticexpealidoshus";
                easterEgg_nl = "kattententoonstelling";
                easterEgg_en_mask = easterEgg_en;
                easterEgg_nl_mask = easterEgg_nl;
                easterEgg_nl_mask = "******* (Default)";
                easterEgg_en_mask = "****** (Default)";
                tv_wordsPerRound.setText("Number of words per round : " + wordsPerRound);
                sb_wordsPerRound.setMax(15);
                sb_wordsPerRound.setProgress(wordsPerRound);

                tv_easterEgg_en.setText("English easter egg word :\n " + easterEgg_en_mask);
                tv_easterEgg_nl.setText("Dutch easter egg word :\n " + easterEgg_nl_mask);
            }
        });

        btn_discardAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btn_saveAndBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Save all preferences and return to previous activity
                prefs.edit().putInt("wordsPerRound", sb_wordsPerRound.getProgress()).apply();
                prefs.edit().putString("easterEgg_nl", easterEgg_nl).apply();
                prefs.edit().putString("easterEgg_en", easterEgg_en).apply();
                // prefs.edit().putBoolean("specialFeature", cb_showSpecialFeature.isChecked()).apply();
                finish();
            }
        });

        btn_easterEgg_en.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsScreenActivity.this, SetEasterEggActivity.class);
                i.putExtra("wordlist", "wrd_lst_en.lst");
                i.putExtra("language", "en");
                startActivityForResult(i, PICK_EASTER_EGG);
            }
        });

        btn_easterEgg_nl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(SettingsScreenActivity.this, SetEasterEggActivity.class);
                i.putExtra("wordlist", "wrd_lst_nl.lst");
                i.putExtra("language", "nl");
                startActivityForResult(i, PICK_EASTER_EGG_NL);
            }
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // Check which request we're responding to
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == PICK_EASTER_EGG) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                easterEgg_en = data.getStringExtra("EASTEREGG");
                easterEgg_en_mask = easterEgg_en;
                if (easterEgg_en.equals("supercalifragilisticexpealidoshus"))
                    easterEgg_en_mask = "****** (Default)";
                tv_easterEgg_en.setText("English easter egg word :\n " + easterEgg_en_mask);
            } else {
                Toast.makeText(this, "Invalid input. Keeping current setting.",
                        Toast.LENGTH_LONG).show();
            }
        }
        if (requestCode == PICK_EASTER_EGG_NL) {
            // Make sure the request was successful
            if (resultCode == RESULT_OK) {
                easterEgg_nl = data.getStringExtra("EASTEREGG");
                easterEgg_nl_mask = easterEgg_nl;
                if (easterEgg_nl.equals("kattententoonstelling"))
                    easterEgg_nl_mask = "****** (Default)";
                tv_easterEgg_nl.setText("Dutch easter egg word :\n " + easterEgg_nl_mask);
            } else {
                Toast.makeText(this, "Geen correcte invoer. Oorspronkelijke easter egg gehouden",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}