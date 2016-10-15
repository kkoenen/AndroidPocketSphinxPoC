package nl.happyworx.srdemo.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Locale;
import java.util.Scanner;

import edu.cmu.pocketsphinx.Assets;

/**
 * Created by Kees Koenen on 14-10-2016.
 * Enables user to set a easter egg word manually
 */

public class SetEasterEggActivity extends Activity {

    private AutoCompleteTextView actv;
    private ProgressDialog progress;
    private File assetDir;
    private String wordlist;
    private ArrayList<String> listForAutocomplete = new ArrayList<String>();
    static final int PICK_EASTER_EGG = 1;
    private String lang_value;
    private Boolean touchEnabled = false;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);
        touchEnabled = false;
        Intent intent = getIntent();
        wordlist = intent.getStringExtra("wordlist");
        lang_value = intent.getStringExtra("language");
        Locale locale = new Locale(lang_value);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());
        setContentView(R.layout.set_easteregg_screen);
        actv = (AutoCompleteTextView) findViewById(R.id.autoCompleteTextView1);
        actv.setEnabled(false);

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.preparingRecognizer_caption));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.setCancelable(false);
        progress.setCanceledOnTouchOutside(false);
        progress.show();


        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    if (Looper.myLooper() == null) {
                        Looper.prepare();
                    }

                    // Read wordlist into array
                    Assets assets = new Assets(SetEasterEggActivity.this);
                    assetDir = assets.syncAssets();
                    Log.v("PocketLog", "Start reading number of lines");
                    BufferedReader reader = new BufferedReader(new FileReader(assetDir + "/" + wordlist));
                    int lines = 0;
                    while (reader.readLine() != null) lines++;
                    reader.close();
                    Log.v("PocketLog", "Number of lines : " + lines);


                    progress.setMax(lines);
                    Scanner s = new Scanner(new File(assetDir + "/" + wordlist));
                    Log.v("PocketLog", "Start creating autocomplete list");
                    int jumpTime = 0;
                    while (s.hasNext()) {
                        String a = s.next();
                        listForAutocomplete.add(a);

                        jumpTime++;
                        progress.setProgress(jumpTime);
                    }
                    s.close();

                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {


                } else {
                    touchEnabled = true;
                    progress.hide();
                    progress.dismiss();
                    actv.setEnabled(true);
                }
            }
        }.execute();

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,android.R.layout.simple_dropdown_item_1line);
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.list_item_autocomplete, listForAutocomplete);
        actv.setAdapter(adapter);
        actv.setThreshold(1);

        actv.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                actv.showDropDown();
                return false;
            }
        });

        Button btn_back = (Button) findViewById(R.id.btn_ok_back);
        btn_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent returnIntent = new Intent();
                String newEasterEgg = actv.getText().toString();
                if (listForAutocomplete.contains(newEasterEgg)) {
                    Log.v("SetEasterEggActivity", "Word in list, word : " + newEasterEgg);
                    returnIntent.putExtra("EASTEREGG", newEasterEgg);
                    setResult(Activity.RESULT_OK, returnIntent);
                } else {
                    setResult(Activity.RESULT_CANCELED);
                    Log.v("SetEasterEggActivity", "No word or not in list");
                }
                finish();
            }
        });

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("SetEasterEggActivity", "onPause was called, going back to main menu");
        Intent returnIntent = new Intent();
        String newEasterEgg = actv.getText().toString();
        if (listForAutocomplete.contains(newEasterEgg)) {
            Log.v("SetEasterEggActivity", "Word in list, word : " + newEasterEgg);
            returnIntent.putExtra("EASTEREGG", newEasterEgg);
            setResult(Activity.RESULT_OK, returnIntent);
        } else {
            setResult(Activity.RESULT_CANCELED);
            Log.v("SetEasterEggActivity", "No word or not in list");
        }
        finish();
    }




}
