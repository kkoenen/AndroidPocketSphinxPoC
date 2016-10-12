/* ====================================================================
 * Copyright (c) 2014 Alpha Cephei Inc.  All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met:
 *
 * 1. Redistributions of source code must retain the above copyright
 *    notice, this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright
 *    notice, this list of conditions and the following disclaimer in
 *    the documentation and/or other materials provided with the
 *    distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY ALPHA CEPHEI INC. ``AS IS'' AND
 * ANY EXPRESSED OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO,
 * THE IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR
 * PURPOSE ARE DISCLAIMED.  IN NO EVENT SHALL CARNEGIE MELLON UNIVERSITY
 * NOR ITS EMPLOYEES BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT
 * LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE,
 * DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY
 * THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE
 * OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *
 * ====================================================================
 */

package nl.happyworx.srdemo.demo;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.content.res.Configuration;
import android.media.MediaPlayer;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.widget.TextView;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import java.util.Scanner;

import edu.cmu.pocketsphinx.Assets;
import edu.cmu.pocketsphinx.Hypothesis;
import edu.cmu.pocketsphinx.RecognitionListener;
import edu.cmu.pocketsphinx.SpeechRecognizer;

import static edu.cmu.pocketsphinx.SpeechRecognizerSetup.defaultSetup;

public class PocketSphinxActivity extends Activity implements
        RecognitionListener {

    private ArrayList<String> listForRecognition = new ArrayList<String>();
    private String wordsToAdd;
    private SpeechRecognizer recognizer;
    private File assetDir;
    private Context context;
    private String lang_value;
    static MediaPlayer mp = new MediaPlayer();
    private String dictionary;
    private String wordlist;
    private String acousticModel;
    private String easterEgg;
    private int wordsPerRound;
    private String previousRecognizedWord = "";
    private ProgressDialog progress;
    private AdView mAdView;


    @Override
    public void onCreate(Bundle state) {
        super.onCreate(state);

        Intent intent = getIntent();
        dictionary = intent.getStringExtra("dictionary");
        wordlist = intent.getStringExtra("wordlist");
        acousticModel = intent.getStringExtra("acousticModel");
        easterEgg = intent.getStringExtra("easterEgg");
        lang_value = intent.getStringExtra("language");
        wordsPerRound = intent.getIntExtra("wordsPerRound", 6);

        Locale locale = new Locale(lang_value);
        locale.setDefault(locale);
        Configuration config = new Configuration();
        config.locale = locale;
        getBaseContext().getResources().updateConfiguration(config,
                getBaseContext().getResources().getDisplayMetrics());

        context = this;
        setContentView(R.layout.main);
        ((TextView) findViewById(R.id.caption_text))
                .setText(getString(R.string.preparingRecognizer_caption));

        progress = new ProgressDialog(this);
        progress.setMessage(getString(R.string.preparingRecognizer_caption));
        progress.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progress.setIndeterminate(false);
        progress.setProgress(0);
        progress.show();


        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                try {
                    if (Looper.myLooper() == null)
                    {
                        Looper.prepare();
                    }
                    // Read wordlist into array
                    Assets assets = new Assets(PocketSphinxActivity.this);
                    assetDir = assets.syncAssets();

                    Log.v("PocketLog", "Start reading number of lines");
                    BufferedReader reader = new BufferedReader(new FileReader(assetDir + "/" + wordlist));
                    int lines = 0;
                    while (reader.readLine() != null) lines++;
                    reader.close();
                    Log.v("PocketLog", "Number of lines : " + lines);
                    progress.setMax(lines);


                    Scanner s = new Scanner(new File(assetDir + "/" + wordlist));
                    Log.v("PocketLog", "Start creating dictionary");
                    int jumpTime = 0;
                    while (s.hasNext()) {
                        String a = s.next();
                        listForRecognition.add(a);
                        jumpTime++;
                        progress.setProgress(jumpTime);
                    }
                    s.close();
                    Log.v("PocketLog", "Finished creating dictionary!");

                } catch (IOException e) {
                    return e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {

                    mAdView = (AdView) findViewById(R.id.adView);
                    mAdView.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            AdRequest adRequest = new AdRequest.Builder().build();
                            mAdView.loadAd(adRequest);
                        }
                    }, 500);

                    doRecognizerStuff();
                    progress.hide();
                }
            }
        }.execute();
    }


    public void doRecognizerStuff() {

        new AsyncTask<Void, Void, Exception>() {
            @Override
            protected Exception doInBackground(Void... params) {
                Log.v("PocketLog", "Start wordshuffle");
                Collections.shuffle(listForRecognition);
                Log.v("PocketLog", "End wordshuffle");
                wordsToAdd = "";
                String generate_lst = "#JSGF V1.0;\n" +
                        "\n" +
                        "grammar woordlijst;\n" +
                        "\n" +
                        "<item> = ";
                for (int i = 0; i < wordsPerRound; i++) {
                    wordsToAdd = wordsToAdd + listForRecognition.get(i) + "\n";
                    generate_lst = generate_lst + listForRecognition.get(i) + " |\n";
                }
                generate_lst = generate_lst + easterEgg + " ;\n\npublic <items> = <item>;";


                File file = new File(assetDir, "generated_list.gram");
                Log.v("PocketLog", "Saving file...");

                try {
                    FileOutputStream outputStream = new FileOutputStream(file);
                    outputStream.write(generate_lst.getBytes());
                    outputStream.close();
                    Log.v("PocketLog", "Saving file... done!");
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v("PocketLog", "Saving file... ERROR!");
                }

                Log.v("PocketLog", "Recognizer ready!");

                return null;
            }

            @Override
            protected void onPostExecute(Exception result) {
                if (result != null) {
                    ((TextView) findViewById(R.id.caption_text))
                            .setText("Failed to init recognizer " + result);
                } else {
                    switchSearch("woordherkenning");
                }
            }
        }.execute();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (recognizer != null) {
            recognizer.cancel();
            recognizer.shutdown();
        }
        finish();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.v("PocketLog", "onPause was called, going back to main menu");
        finish();
    }


    @Override
    public void onPartialResult(Hypothesis hypothesis) {
        if (hypothesis == null) return;
        String text = hypothesis.getHypstr();
        Log.v("PocketLog 129", "Partial result. I heared : " + text);
        //String curtext = ((TextView) findViewById(R.id.result_text)).getText().toString();
        //((TextView) findViewById(R.id.result_text)).setText(text);
        if (wordsToAdd.contains(text)) {
            recognizer.stop();
        } else {
            if (text.contains(easterEgg)) {
                Log.v("PocketLog", "Unleash the cats! ;)");
                recognizer.stop();
            }
        }
    }

    @Override
    public void onResult(Hypothesis hypothesis) {
        if (hypothesis != null) {
            String text = hypothesis.getHypstr();
            Log.v("PocketLog 129", "Full result. I heared : " + text);
            previousRecognizedWord = text;
            // Toast.makeText(PocketSphinxActivity.this, "" + text, Toast.LENGTH_SHORT).show();
            if (wordsToAdd.contains(text)) {
                Log.v("PocketLog", "Succes!! Moving to next wordgroup");

                if (!mp.isPlaying()) {
                    mp = new MediaPlayer();
                }
                try {
                    AssetFileDescriptor afd = getAssets().openFd("ding.mp3");
                    mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                    mp.prepare();
                    mp.start();

                } catch (Exception e) {
                    e.printStackTrace();
                }

                doRecognizerStuff();
            } else {
                if (hypothesis.getHypstr().contains(easterEgg)) {
                    Log.v("PocketLog", "Unleash the cats NOW!!!");
                    if (!mp.isPlaying()) {
                        mp = new MediaPlayer();
                    }
                    try {
                        AssetFileDescriptor afd = getAssets().openFd("kitten_sounds.mp3");
                        mp.setDataSource(afd.getFileDescriptor(), afd.getStartOffset(), afd.getLength());
                        mp.prepare();
                        mp.start();

                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    doRecognizerStuff();
                }
                Log.v("PocketLog", "Should't be here. Check what happened!");
            }
        }
    }

    @Override
    public void onBeginningOfSpeech() {
    }


    @Override
    public void onEndOfSpeech() {
    }

    private void switchSearch(String searchName) {
        if (recognizer != null) {
            recognizer.stop();
        }

        try {
            setupRecognizer(assetDir, "generated_list.gram");
        } catch (IOException e) {
            e.printStackTrace();
        }
        recognizer.startListening(searchName);

        ((TextView) findViewById(R.id.caption_text)).setText("Simon Says ;)\n\n" + wordsToAdd);
        ((TextView) findViewById(R.id.result_text)).setText(getString(R.string.I_heared_caption) + " " + previousRecognizedWord);
    }


    private void setupRecognizer(File assetsDir, String gramFile) throws IOException {
        // The recognizer can be configured to perform multiple searches
        // of different kind and switch between them


        recognizer = defaultSetup()
                .setAcousticModel(new File(assetsDir, acousticModel))
                .setFloat("-vad_threshold", 4.0)

                .setDictionary(new File(assetsDir, dictionary))
                .setKeywordThreshold(1e-25f)
                        // Use context-independent phonetic search, context-dependent is too slow for mobile
                .setBoolean("-allphone_ci", true)
                .getRecognizer();
        recognizer.addListener(this);

        File menuGrammar = new File(assetsDir, gramFile);
        recognizer.addGrammarSearch("woordherkenning", menuGrammar);
    }

    @Override
    public void onError(Exception error) {
        ((TextView) findViewById(R.id.caption_text)).setText(error.getMessage());
    }

    @Override
    public void onTimeout() {
        switchSearch("woordherkenning");
    }
}
