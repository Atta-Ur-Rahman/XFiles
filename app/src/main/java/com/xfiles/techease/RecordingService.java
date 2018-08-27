package com.xfiles.techease;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.RequiresApi;


import java.io.File;
import java.io.IOException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import static java.lang.Math.log10;

public class RecordingService extends Service {

    public static MediaRecorder mediaRecorder;
    String AudioSavePathInDevice = null;
    private static double mEMA = 0.0;
    static final private double EMA_FILTER = 0.6;

    Context context;


    boolean aBooleanRunner;
    Thread runner;
    final Runnable updater = new Runnable() {

        public void run() {
            if (aBooleanRunner) {
                updateSound();
            }
        }
    };

    final Handler mHandler = new Handler();

    public RecordingService() {


    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");


    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    public void onCreate() {
        super.onCreate();

        context = RecordingService.this;



        String out = new SimpleDateFormat("dd-MM-yyyy hh-mm-ss").format(new Date());
        File rootFile = new File(Environment.getExternalStorageDirectory().toString(), "XFiles");

        if (!rootFile.exists()) {
            rootFile.mkdir();
        }


        File sourceFile = new File(rootFile, out + "Audio.mp3");

        AudioSavePathInDevice = sourceFile.getAbsolutePath();

        Utilities.putValueInEditor(RecordingService.this).putString("test_recording_path",out+"Audio.mp3").commit();

        MediaRecorderReady();
        if (aBooleanRunner) {

            if (runner == null) {
                runner = new Thread() {
                    public void run() {
                        while (runner != null) {
                            try {
                                Thread.sleep(100);
                            } catch (InterruptedException e) {
                            }
                            ;
                            mHandler.post(updater);
                        }
                    }
                };
                runner.start();

            }
        }
        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            e.printStackTrace();
        }

        mediaRecorder.start();

    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public void MediaRecorderReady() {

        AudioManager am = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        String sampleRateStr = am.getProperty(AudioManager.PROPERTY_OUTPUT_SAMPLE_RATE);
        int sampleRate = Integer.parseInt(sampleRateStr);
        if (sampleRate == 0){
            sampleRate = 44100;
        }


        mediaRecorder = new MediaRecorder();
        mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_WB);

        mediaRecorder.setAudioEncodingBitRate(96000);
        mediaRecorder.setAudioSamplingRate(sampleRate);
        mediaRecorder.setOutputFile(AudioSavePathInDevice);
        Utilities.putValueInEditor(RecordingService.this).putString("recordingPath", AudioSavePathInDevice).commit();
        Utilities.putValueInEditor(RecordingService.this).putBoolean("playing", true).commit();
        aBooleanRunner = true;


    }


    private void updateSound() {

        double dNoise = soundDb(1);
        DecimalFormat decimalFormat = new DecimalFormat("###");
        String strNoiseDF = decimalFormat.format(dNoise);
        Utilities.putValueInEditor(this).putString("wave", strNoiseDF).commit();

        try {

        } catch (NumberFormatException nfe) {
            System.out.println("Could not parse " + nfe);
        }



    }

    public double soundDb(double ampl) {
        return 20 * log10(getAmplitudeEMA() / ampl);
    }

    public double getAmplitude() {
        if (mediaRecorder != null)
            return (mediaRecorder.getMaxAmplitude());
        else
            return 0;

    }

    public double getAmplitudeEMA() {
        double amp = getAmplitude();
        mEMA = EMA_FILTER * amp + (1.0 - EMA_FILTER) * mEMA;
        return mEMA;
    }

    @Override
    public void onDestroy() {

        Utilities.putValueInEditor(RecordingService.this).putBoolean("playing", false).commit();
        Utilities.putValueInEditor(RecordingService.this).putString("recordingPath", AudioSavePathInDevice);
        mediaRecorder.stop();
        aBooleanRunner = false;
    }


}

