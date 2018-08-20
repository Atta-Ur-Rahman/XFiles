package com.xfiles.techease;


import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.audiofx.Visualizer;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;


import com.example.attaurrahman.recordapplication.R;

import java.io.File;
import java.io.IOException;

import ak.sh.ay.musicwave.MusicWave;
import br.com.joinersa.oooalertdialog.Animation;
import br.com.joinersa.oooalertdialog.OnClickListener;
import br.com.joinersa.oooalertdialog.OoOAlertDialog;
import me.itangqi.waveloadingview.WaveLoadingView;

import static android.Manifest.permission.RECORD_AUDIO;
import static android.Manifest.permission.WRITE_EXTERNAL_STORAGE;
import static java.lang.System.out;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private static final int CODE_DRAW_OVER_OTHER_APP_PERMISSION = 2084;

    String AudioSavePathInDevice = null;

    public static final int RequestPermissionCode = 0;
    MediaPlayer mediaPlayer;
    private Visualizer mVisualizer;
    private MusicWave musicWave;

    boolean aBooleanRunner = false;

    boolean aBooleanMediaPlayer;
    public static final int REQUEST_WRITE_STORAGE = 112;


    Thread runner;
    final Runnable updater = new Runnable() {

        public void run() {

            if (aBooleanRunner) {
                updateSound();
            }


        }
    };

    final Handler mHandler = new Handler();
    WaveLoadingView mWaveLoadingView;

    private static final int APP_OVERLAY_PERMISSION = 1000;


    Button btnStartRecording, btnStopRecording, btnPlayMediaPlayer;

    boolean aBooleanSaveRecording = false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        stopService(new Intent(MainActivity.this, FloatingViewService.class));

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O && !Settings.canDrawOverlays(this)) {
            Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + getPackageName()));
            startActivityForResult(intent, APP_OVERLAY_PERMISSION);
        }


        btnPlayMediaPlayer = findViewById(R.id.btn_play_media_player);
        btnStartRecording = findViewById(R.id.btn_start_record);
        btnStopRecording = findViewById(R.id.btn_stop_recording);

        btnStopRecording.setOnClickListener(this);
        btnStartRecording.setOnClickListener(this);
        btnPlayMediaPlayer.setOnClickListener(this);


        musicWave = findViewById(R.id.musicWave);


        mWaveLoadingView = findViewById(R.id.waveLoadingView);
        mWaveLoadingView.setShapeType(WaveLoadingView.ShapeType.CIRCLE);
        mWaveLoadingView.setBottomTitle("Scanning");
        mWaveLoadingView.setCenterTitleColor(Color.RED);
        mWaveLoadingView.setBottomTitleSize(18);
        mWaveLoadingView.setBorderWidth(3);
        mWaveLoadingView.setVisibility(View.GONE);


        boolean testButton = Utilities.getSharedPreferences(MainActivity.this).getBoolean("playButtonVisibility", true);

        if (testButton) {
            btnPlayMediaPlayer.setEnabled(false);
        }
        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                btnPlayMediaPlayer.setVisibility(View.VISIBLE);
                Utilities.putValueInEditor(MainActivity.this).putBoolean("PlayMediaPlayer", false).commit();

                finish();
                startActivity(new Intent(MainActivity.this, MainActivity.class));
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {

                btnPlayMediaPlayer.setVisibility(View.VISIBLE);
                Utilities.putValueInEditor(MainActivity.this).putBoolean("PlayMediaPlayer", false).commit();
                return false;
            }
        });

        aBooleanRunner = Utilities.getSharedPreferences(MainActivity.this).getBoolean("runner", false);
        if (aBooleanRunner) {

            mWaveLoadingView.setVisibility(View.VISIBLE);
            btnStartRecording.setVisibility(View.GONE);
            btnPlayMediaPlayer.setEnabled(false);
        }

        aBooleanMediaPlayer = Utilities.getSharedPreferences(MainActivity.this).getBoolean("PlayMediaPlayer", false);
        if (aBooleanMediaPlayer) {
            btnPlayMediaPlayer.setVisibility(View.GONE);
        }

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


        boolean hasPermission = (ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED);
        if (!hasPermission) {
            ActivityCompat.requestPermissions(MainActivity.this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    REQUEST_WRITE_STORAGE);
        }
    }


    private void requestPermission() {
        ActivityCompat.requestPermissions(MainActivity.this, new
                String[]{WRITE_EXTERNAL_STORAGE, RECORD_AUDIO}, RequestPermissionCode);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        switch (requestCode) {
            case RequestPermissionCode:
                if (grantResults.length > 0) {
                    boolean StoragePermission = grantResults[0] ==
                            PackageManager.PERMISSION_GRANTED;
                    boolean RecordPermission = grantResults[1] ==
                            PackageManager.PERMISSION_GRANTED;

                    if (StoragePermission && RecordPermission) {
                        Toast.makeText(MainActivity.this, "Permission Granted",
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_LONG).show();
                    }
                }
                break;

        }
    }

    public boolean checkPermission() {
        int result = ContextCompat.checkSelfPermission(getApplicationContext(),
                WRITE_EXTERNAL_STORAGE);
        int result1 = ContextCompat.checkSelfPermission(getApplicationContext(),
                RECORD_AUDIO);
        return result == PackageManager.PERMISSION_GRANTED &&
                result1 == PackageManager.PERMISSION_GRANTED;
    }


    private void prepareVisualizer() {

        mVisualizer = new Visualizer(mediaPlayer.getAudioSessionId());
        mVisualizer.setCaptureSize(Visualizer.getCaptureSizeRange()[1]);
        mVisualizer.setDataCaptureListener(
                new Visualizer.OnDataCaptureListener() {
                    public void onWaveFormDataCapture(Visualizer visualizer,
                                                      byte[] bytes, int samplingRate) {
                        musicWave.updateVisualizer(bytes);
                    }

                    public void onFftDataCapture(Visualizer visualizer,
                                                 byte[] bytes, int samplingRate) {
                    }
                }, Visualizer.getMaxCaptureRate() / 2, true, false);
        mVisualizer.setEnabled(true);
    }

    private void updateSound() {


        String string = Utilities.getSharedPreferences(MainActivity.this).getString("wave", "");
        try {
            mWaveLoadingView.setProgressValue(Integer.parseInt(string));
            mWaveLoadingView.setAmplitudeRatio(Integer.parseInt(string));
        } catch (NumberFormatException nfe) {
            out.println("Could not parse " + nfe);
        }


    }


    @Override
    protected void onPause() {
        super.onPause();

        aBooleanRunner = Utilities.getSharedPreferences(MainActivity.this).getBoolean("runner", false);

        if (aBooleanRunner) {

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M && !Settings.canDrawOverlays(this)) {

                Intent intent = new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION,
                        Uri.parse("package:" + getPackageName()));
                startActivityForResult(intent, CODE_DRAW_OVER_OTHER_APP_PERMISSION);
            } else {
                initializeView();
            }
        }

    }


    private void initializeView() {
        startService(new Intent(MainActivity.this, FloatingViewService.class));
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CODE_DRAW_OVER_OTHER_APP_PERMISSION) {
            if (resultCode == RESULT_OK) {
                initializeView();
            } else {
                Toast.makeText(this,
                        "Draw over other app permission not available. Closing the application",
                        Toast.LENGTH_SHORT).show();

                finish();
            }
        } else {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }


    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.btn_start_record:


                if (checkPermission()) {

                    Utilities.putValueInEditor(MainActivity.this).putBoolean("thread_runner", true);

                    if (validateMicAvailability()) {


                        Utilities.putValueInEditor(MainActivity.this).putBoolean("runner", true).commit();

                        startService(new Intent(MainActivity.this, RecordingService.class));
                        aBooleanRunner = true;
                        mWaveLoadingView.startAnimation();
                        mWaveLoadingView.setVisibility(View.VISIBLE);

                        btnStartRecording.setVisibility(View.GONE);
                        btnPlayMediaPlayer.setEnabled(false);
                    } else {
                        Toast.makeText(this, "Unable to start media recording. Other application already recording", Toast.LENGTH_LONG).show();
                    }


                } else {
                    requestPermission();
                }

                break;
            case R.id.btn_stop_recording:

                Utilities.putValueInEditor(MainActivity.this).putBoolean("thread_runner", false).commit();
                Utilities.putValueInEditor(MainActivity.this).putBoolean("savePath", true).commit();


                aBooleanSaveRecording = true;

                new OoOAlertDialog.Builder(MainActivity.this)
                        .setTitle(getString(R.string.app_name))
                        .setMessage("Do you want to save Recording?")
                        .setAnimation(Animation.ZOOM)
                        .setPositiveButton("Save", new OnClickListener() {
                            @Override
                            public void onClick() {

                                Toast.makeText(MainActivity.this, "Recording has been Saved", Toast.LENGTH_SHORT).show();
                                Utilities.putValueInEditor(MainActivity.this).putBoolean("savePath", false).commit();
                                aBooleanSaveRecording = false;

                                Utilities.putValueInEditor(MainActivity.this).putBoolean("playButtonVisibility", false).commit();


                            }
                        })
                        .setNegativeButton("Discard", new OnClickListener() {
                            @Override
                            public void onClick() {
                                DeleteRecordingFile(Utilities.getSharedPreferences(MainActivity.this).getString("recordingPath", ""));

                            }
                        }).build();


                btnStartRecording.setVisibility(View.VISIBLE);
                Utilities.putValueInEditor(MainActivity.this).putBoolean("runner", false).commit();

                mWaveLoadingView.setVisibility(View.GONE);
                aBooleanRunner = false;

                stopService(new Intent(MainActivity.this, RecordingService.class));

                btnPlayMediaPlayer.setEnabled(true);

                break;
            case R.id.btn_play_media_player:

                aBooleanSaveRecording = Utilities.getSharedPreferences(MainActivity.this).getBoolean("savePath", false);
                if (aBooleanSaveRecording) {
                    DeleteRecordingFile(Utilities.getSharedPreferences(MainActivity.this).getString("recordingPath", ""));
                }

                String string = Utilities.getSharedPreferences(MainActivity.this).getString("test_recording_path", "");
                DialogUtils.MediaPlayerDialog(getLayoutInflater(), this, string);

                break;
        }
    }


    private boolean validateMicAvailability() {
        Boolean available = true;
        AudioRecord recorder =
                new AudioRecord(MediaRecorder.AudioSource.MIC, 44100,
                        AudioFormat.CHANNEL_IN_MONO,
                        AudioFormat.ENCODING_DEFAULT, 44100);
        try {
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_STOPPED) {
                available = false;

            }

            recorder.startRecording();
            if (recorder.getRecordingState() != AudioRecord.RECORDSTATE_RECORDING) {
                recorder.stop();
                available = false;

            }
            recorder.stop();
        } finally {
            recorder.release();
            recorder = null;
        }

        return available;
    }


    private void DeleteRecordingFile(String strPath) {

        File file = new File(strPath);
        file.delete();
        if (file.exists()) {
            try {
                file.getCanonicalFile().delete();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (file.exists()) {
                getApplicationContext().deleteFile(file.getName());
            }

        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        aBooleanSaveRecording = Utilities.getSharedPreferences(MainActivity.this).getBoolean("savePath", false);
        if (aBooleanSaveRecording) {
            DeleteRecordingFile(Utilities.getSharedPreferences(MainActivity.this).getString("recordingPath", ""));
        }

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.action_show_files:

                aBooleanSaveRecording = Utilities.getSharedPreferences(MainActivity.this).getBoolean("savePath", false);
                if (aBooleanSaveRecording) {
                    DeleteRecordingFile(Utilities.getSharedPreferences(MainActivity.this).getString("recordingPath", ""));
                }

                if (aBooleanRunner) {

                } else {
                    startActivity(new Intent(MainActivity.this, Main2Activity.class));
                }


        }
        return super.onOptionsItemSelected(item);


    }

}


